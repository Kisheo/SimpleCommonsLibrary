#!/usr/bin/env python3
"""
Generate Kotlin files that emulate the deprecated Kotlin synthetic view imports for each layout.
This creates packages like: kotlinx.android.synthetic.main.item_filepicker_list.view
with extension properties for View, Activity and FragmentActivity.

Run: python tools/generate_synthetics_kotlin.py
"""
import os, re, xml.etree.ElementTree as ET

repo_root = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
layouts_dir = os.path.join(repo_root, 'commons', 'src', 'main', 'res', 'layout')
output_base = os.path.join(repo_root, 'commons', 'src', 'main', 'kotlin')

simple_map = {
    'View': 'android.view.View',
    'TextView': 'android.widget.TextView',
    'ImageView': 'android.widget.ImageView',
    'RelativeLayout': 'android.widget.RelativeLayout',
    'LinearLayout': 'android.widget.LinearLayout',
    'FrameLayout': 'android.widget.FrameLayout',
    'RecyclerView': 'androidx.recyclerview.widget.RecyclerView',
    'Button': 'android.widget.Button',
    'ImageButton': 'android.widget.ImageButton',
    'EditText': 'android.widget.EditText',
    'ConstraintLayout': 'androidx.constraintlayout.widget.ConstraintLayout',
    'ScrollView': 'android.widget.ScrollView',
    'CheckBox': 'android.widget.CheckBox',
    'Switch': 'android.widget.Switch',
    'RadioButton': 'android.widget.RadioButton',
    'RadioGroup': 'android.widget.RadioGroup',
    'ProgressBar': 'android.widget.ProgressBar',
    'SeekBar': 'android.widget.SeekBar',
}

if not os.path.isdir(layouts_dir):
    print('Layouts dir not found at', layouts_dir)
    raise SystemExit(1)

created = 0
for fn in os.listdir(layouts_dir):
    if not fn.endswith('.xml'):
        continue
    layout_name = fn[:-4]  # strip .xml
    path = os.path.join(layouts_dir, fn)
    try:
        with open(path, 'r', encoding='utf-8') as f:
            txt = f.read()
    except Exception as e:
        print('Failed to read', path, e)
        continue
    # strip leading lines that start with // (some files have injected comments)
    stripped = '\n'.join([line for line in txt.splitlines() if not line.strip().startswith('//')])
    try:
        root = ET.fromstring(stripped)
    except Exception as e:
        # fallback: try to remove XML comments
        cleaned = re.sub(r'<!--.*?-->', '', stripped, flags=re.S)
        try:
            root = ET.fromstring(cleaned)
        except Exception as e2:
            print('Failed to parse', path, 'skipping:', e2)
            continue
    ids = {}
    for el in root.iter():
        aid = el.get('{http://schemas.android.com/apk/res/android}id')
        if aid and aid.startswith('@+id/'):
            name = aid[len('@+id/'):]
            tag = el.tag
            if '}' in tag:
                tag = tag.split('}',1)[1]
            if '.' in tag:
                typ = tag
            else:
                typ = simple_map.get(tag, 'android.view.View')
            ids[name] = typ
    if not ids:
        continue
    # prepare output dir: kotlinx/android/synthetic/main/<layout>
    pkg_dir = os.path.join(output_base, 'kotlinx', 'android', 'synthetic', 'main', layout_name)
    os.makedirs(pkg_dir, exist_ok=True)
    out_file = os.path.join(pkg_dir, 'Synthetic.kt')
    with open(out_file, 'w', encoding='utf-8') as out:
        pkg = f'package kotlinx.android.synthetic.main.{layout_name}.view\n\n'
        out.write(pkg)
        out.write('import android.app.Activity\nimport android.view.View\nimport androidx.fragment.app.FragmentActivity\nimport com.dpsoftapps.commons.R\n\n')
        for name, typ in sorted(ids.items()):
            # Activity extension
            out.write(f'val Activity.{name}: {typ}\n')
            out.write(f'    get() = findViewById(R.id.{name})\n\n')
            # FragmentActivity extension
            out.write(f'val FragmentActivity.{name}: {typ}\n')
            out.write(f'    get() = findViewById(R.id.{name})\n\n')
            # View extension
            out.write(f'val View.{name}: {typ}\n')
            out.write(f'    get() = findViewById(R.id.{name})\n\n')
    created += 1
    print('Created synthetic compat for', layout_name, '->', out_file)

print(f'Created {created} synthetic compat files under {output_base}/kotlinx/android/synthetic/main/')
