#!/usr/bin/env python3
"""
Scan Android layout XML files and generate simple Kotlin extension stubs that mimic
kotlinx.android.synthetic accessors. This is useful if you temporarily need the
synthetic properties for other projects. Generated files are placed under:

  commons/src/main/kotlin/kotlinx/android/synthetic/main/<layout>/Synthetic.kt

Run from project root (Windows cmd.exe):
  python scripts\generate_synthetics.py

This script is safe and only generates files for layouts that contain id attributes.
"""
import os
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LAYOUTS_DIR = ROOT / 'commons' / 'src' / 'main' / 'res' / 'layout'
OUT_BASE = ROOT / 'commons' / 'src' / 'main' / 'kotlin' / 'kotlinx' / 'android' / 'synthetic' / 'main'

ID_RE = re.compile(r"@\+id/([A-Za-z0-9_]+)")

created = 0

if not LAYOUTS_DIR.exists():
    print('Layouts directory not found:', LAYOUTS_DIR)
    raise SystemExit(1)

OUT_BASE.mkdir(parents=True, exist_ok=True)

for fn in sorted(os.listdir(LAYOUTS_DIR)):
    if not fn.endswith('.xml'):
        continue
    layout = fn[:-4]
    ids = set()
    path = LAYOUTS_DIR / fn
    with open(path, 'r', encoding='utf-8') as f:
        txt = f.read()
        ids.update(ID_RE.findall(txt))
    if not ids:
        continue

    pkg_dir = OUT_BASE / layout
    pkg_dir.mkdir(parents=True, exist_ok=True)
    out_file = pkg_dir / 'Synthetic.kt'
    with open(out_file, 'w', encoding='utf-8') as out:
        out.write(f'package kotlinx.android.synthetic.main.{layout}.view\n\n')
        out.write('import android.app.Activity\n')
        out.write('import android.view.View\n')
        out.write('import androidx.fragment.app.FragmentActivity\n')
        out.write('import com.dpsoftapps.commons.R\n\n')
        for idn in sorted(ids):
            out.write(f'val Activity.{idn}: View\n')
            out.write(f'    get() = findViewById(R.id.{idn})\n\n')
            out.write(f'val FragmentActivity.{idn}: View\n')
            out.write(f'    get() = findViewById(R.id.{idn})\n\n')
            out.write(f'val View.{idn}: View\n')
            out.write(f'    get() = findViewById(R.id.{idn})\n\n')
    created += 1
    print('created', out_file)

print('total', created)

