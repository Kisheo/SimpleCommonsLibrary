#!/usr/bin/env python3
"""
Simple helper to find usages of Kotlin synthetic view imports and suggest ViewBinding replacements.
Run from the repo root: python tools/migrate_synthetics.py
It lists files importing 'kotlinx.android.synthetic' and suggests the binding class name derived from layout file.
"""
import os
import re

root = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
pattern = re.compile(r'import\s+kotlinx\.android\.synthetic\.(?:main\.)?([\w_\.]+)')

results = []
for dirpath, dirnames, filenames in os.walk(root):
    if 'build' in dirpath.split(os.sep):
        continue
    for fn in filenames:
        if fn.endswith('.kt') or fn.endswith('.java'):
            fp = os.path.join(dirpath, fn)
            try:
                with open(fp, 'r', encoding='utf-8') as f:
                    txt = f.read()
            except Exception:
                continue
            for m in pattern.finditer(txt):
                layout = m.group(1)
                results.append((fp, layout))

if not results:
    print('No kotlinx.android.synthetic imports found.')
else:
    print('Found kotlinx.android.synthetic imports:')
    for fp, layout in results:
        # derive binding class: layout file name -> PascalCase + Binding
        base = os.path.basename(layout)
        parts = base.split('_')
        binding = ''.join(p.capitalize() for p in parts) + 'Binding'
        print(f'- {fp}\n  layout: {layout}\n  suggested binding: {binding}\n  hint: In the containing class inflate via {binding}.inflate(layoutInflater) or use ViewBinding in adapters by inflating with LayoutInflater.from(parent.context)')

print('\nNext steps:\n - Enable viewBinding (done in commons/build.gradle).\n - For each file above, replace synthetic imports and direct view access with the corresponding binding class.\n - For Activities/Fragments: use binding = ActivityXyzBinding.inflate(layoutInflater) or val binding = FragmentXyzBinding.bind(view).\n - For Adapter view holders: create a ViewHolder that holds the binding type: val binding = ItemXyzBinding.inflate(LayoutInflater.from(parent.context), parent, false)')
