#!/usr/bin/env python3
"""Export docs/naamalu-calendar-core.xlsx → core/content/.../content/calendar/*.json"""

from __future__ import annotations

import json
import re
import zipfile
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
XLSX = ROOT / "docs" / "naamalu-calendar-core.xlsx"
OUT_DIR = ROOT / "core" / "content" / "src" / "main" / "assets" / "content" / "calendar"

NS = {"m": "http://schemas.openxmlformats.org/spreadsheetml/2006/main"}
RNS = "{http://schemas.openxmlformats.org/officeDocument/2006/relationships}"


def cell_value(cell: ET.Element) -> str:
    t = cell.get("t")
    if t == "inlineStr":
        is_el = cell.find("m:is", NS)
        if is_el is None:
            return ""
        return "".join((x.text or "") for x in is_el.findall(".//m:t", NS))
    v = cell.find("m:v", NS)
    return v.text if v is not None else ""


def col_letters(ref: str) -> str:
    return re.match(r"([A-Z]+)", ref).group(1)  # type: ignore[union-attr]


def col_index(col: str) -> int:
    n = 0
    for ch in col:
        n = n * 26 + (ord(ch) - ord("A") + 1)
    return n - 1


def read_sheet(z: zipfile.ZipFile, path: str) -> list[list[str]]:
    root = ET.fromstring(z.read(path))
    rows: dict[int, dict[int, str]] = {}
    for row in root.findall(".//m:sheetData/m:row", NS):
        r_idx = int(row.get("r", "0"))
        for c in row.findall("m:c", NS):
            ref = c.get("r", "A1")
            c_idx = col_index(col_letters(ref))
            rows.setdefault(r_idx, {})[c_idx] = cell_value(c)
    if not rows:
        return []
    max_col = max(max(cols.keys()) for cols in rows.values())
    out: list[list[str]] = []
    for r in sorted(rows.keys()):
        line = [rows[r].get(i, "") for i in range(max_col + 1)]
        out.append(line)
    return out


def rows_to_objects(rows: list[list[str]]) -> list[dict[str, str]]:
    if len(rows) < 2:
        return []
    headers = [h.strip() for h in rows[0]]
    objects: list[dict[str, str]] = []
    for row in rows[1:]:
        if not any(cell.strip() for cell in row):
            continue
        first = row[0].strip() if row else ""
        if first in ("ID", "1–15 / 30", ""):
            continue
        obj: dict[str, str] = {}
        for i, h in enumerate(headers):
            if not h:
                continue
            val = row[i].strip() if i < len(row) else ""
            obj[h] = val
        if not first:
            continue
        objects.append(obj)
    return objects


def sheet_export_name(sheet: str) -> str:
    mapping = {
        "Maasams": "maasams.json",
        "Observances": "observances.json",
        "Tithis": "tithis.json",
        "Pakshas": "pakshas.json",
        "Varas": "varas.json",
        "Ritus": "ritus.json",
        "Samvatsaras": "samvatsaras.json",
    }
    return mapping.get(sheet, "")


def main() -> None:
    if not XLSX.exists():
        raise SystemExit(f"Missing workbook: {XLSX}")
    OUT_DIR.mkdir(parents=True, exist_ok=True)

    with zipfile.ZipFile(XLSX) as z:
        wb = ET.fromstring(z.read("xl/workbook.xml"))
        sheets = [(s.get("name"), s.get(RNS + "id")) for s in wb.findall(".//m:sheet", NS)]
        rels = ET.fromstring(z.read("xl/_rels/workbook.xml.rels"))
        rid_to = {
            r.get("Id"): r.get("Target").lstrip("/")
            for r in rels
            if r.tag.endswith("Relationship")
        }
        exported: list[str] = []
        for name, rid in sheets:
            out_name = sheet_export_name(name or "")
            if not out_name:
                continue
            rows = read_sheet(z, rid_to[rid or ""])
            data = rows_to_objects(rows)
            path = OUT_DIR / out_name
            path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
            exported.append(out_name)
            print(f"Wrote {path.name}: {len(data)} rows")

    manifest = {
        "version": 1,
        "source": "docs/naamalu-calendar-core.xlsx",
        "exportedSheets": exported,
        "datesFile": "dates-2026-27.json",
        "datesStatus": "FIXTURE",
    }
    (OUT_DIR / "_manifest.json").write_text(
        json.dumps(manifest, indent=2) + "\n", encoding="utf-8",
    )
    print("Wrote _manifest.json")


if __name__ == "__main__":
    main()
