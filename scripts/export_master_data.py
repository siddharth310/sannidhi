#!/usr/bin/env python3
"""Export docs/naamalu-master-data.xlsx → core/content/.../content/master/*.json"""

from __future__ import annotations

import json
import re
import zipfile
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
XLSX = ROOT / "docs" / "naamalu-master-data.xlsx"
OUT = ROOT / "core" / "content" / "src" / "main" / "assets" / "content" / "master"

NS = {"m": "http://schemas.openxmlformats.org/spreadsheetml/2006/main"}
RNS = "{http://schemas.openxmlformats.org/officeDocument/2006/relationships}"


def slug(text: str) -> str:
    s = text.strip().lower()
    s = re.sub(r"[^a-z0-9]+", "-", s)
    return s.strip("-")


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
    return [[rows[r].get(i, "") for i in range(max_col + 1)] for r in sorted(rows.keys())]


def rows_to_dicts(rows: list[list[str]]) -> list[dict[str, str]]:
    if len(rows) < 2:
        return []
    headers = [h.strip() for h in rows[0]]
    out: list[dict[str, str]] = []
    for row in rows[1:]:
        first = row[0].strip() if row else ""
        if not first or first in ("", "field", "code", "city_id", "state_ut", "rashi_id", "nakshatra_id"):
            continue
        obj = {headers[i]: (row[i].strip() if i < len(row) else "") for i in range(len(headers)) if headers[i]}
        out.append(obj)
    return out


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(XLSX) as z:
        wb = ET.fromstring(z.read("xl/workbook.xml"))
        sheets = {s.get("name"): s.get(RNS + "id") for s in wb.findall(".//m:sheet", NS)}
        rels = ET.fromstring(z.read("xl/_rels/workbook.xml.rels"))
        rid_to = {r.get("Id"): r.get("Target").lstrip("/") for r in rels if r.tag.endswith("Relationship")}

        def sheet(name: str) -> list[dict[str, str]]:
            return rows_to_dicts(read_sheet(z, rid_to[sheets[name]]))

        states_raw = sheet("States_UTs")
        states = []
        for s in states_raw:
            name = s.get("state_ut", "")
            states.append({
                "id": slug(name),
                "nameEn": name,
                "type": s.get("type", ""),
                "primaryLanguages": s.get("primary_languages", ""),
                "defaultAppLanguage": s.get("default_app_language", "te"),
                "monthSystem": s.get("month_system", "amanta"),
                "traditionId": s.get("tradition_id", ""),
            })

        state_name_to_id = {s["nameEn"]: s["id"] for s in states}

        cities_raw = sheet("Cities_India")
        cities = []
        for c in cities_raw:
            state_name = c.get("state_ut", "")
            cities.append({
                "id": c.get("city_id", ""),
                "nameEn": c.get("city", ""),
                "stateId": state_name_to_id.get(state_name, slug(state_name)),
                "lat": c.get("lat", ""),
                "lon": c.get("lon", ""),
                "timezone": c.get("timezone", "Asia/Kolkata"),
            })

        languages = sheet("Languages")
        rashis = sheet("Rashis")
        nakshatras = sheet("Nakshatras")

        (OUT / "states_ut.json").write_text(json.dumps(states, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        (OUT / "cities_india.json").write_text(json.dumps(cities, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        (OUT / "languages.json").write_text(json.dumps(languages, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        (OUT / "rashis.json").write_text(json.dumps(rashis, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        (OUT / "nakshatras.json").write_text(json.dumps(nakshatras, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        manifest = {"version": 1, "source": "docs/naamalu-master-data.xlsx", "stateCount": len(states), "cityCount": len(cities)}
        (OUT / "_manifest.json").write_text(json.dumps(manifest, indent=2) + "\n", encoding="utf-8")
        print(f"Exported {len(states)} states, {len(cities)} cities")


if __name__ == "__main__":
    main()
