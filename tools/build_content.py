#!/usr/bin/env python3
"""Content build: content/  →  app/src/main/assets/content/

    python tools/build_content.py --check      # validate only, print report
    python tools/build_content.py              # dev build (drafts included, marked)
    python tools/build_content.py --release    # only reviewed / published content

Rules live in docs/content-architecture.md. This script is the only thing that writes the assets folder.
"""
from __future__ import annotations

import argparse
import csv
import datetime as dt
import glob
import hashlib
import json
import os
import re
import shutil
import subprocess
import sys
from collections import defaultdict

import yaml
from indic_transliteration import sanscript

sys.path.insert(0, os.path.dirname(__file__))
import panchanga as P  # noqa: E402
from mdparse import LANGS, ContentError, parse, slug_title  # noqa: E402

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
C = os.path.join(ROOT, "content")
CONFIG = yaml.safe_load(open(os.path.join(ROOT, "tools", "build_config.yaml"), encoding="utf-8"))

STATUSES = {"draft", "reviewed", "published", "retired"}
ID = {
    "deity": r"^[a-z][a-z0-9-]*$",
    "puja": r"^[a-z][a-z0-9-]*\.puja\.[a-z0-9-]+$",
    "mantra": r"^[a-z][a-z0-9-]*\.mantra\.[a-z0-9-]+$",
    "text": r"^[a-z][a-z0-9-]*\.(ashtottara|sahasranama|stotram|mula-mantra|[a-z0-9-]+)$",
    "obs": r"^obs\.[a-z0-9-]+$",
    "nv": r"^nv\.[a-z0-9-]+$",
    "sm": r"^sm\.[a-z0-9-]+$",
    "story": r"^[a-z][a-z0-9-]*\.story\.[a-z0-9-]+$",
    "img": r"^img\.[a-z]+(\.[a-z0-9-]+)+$",
}
DEVANAGARI_OK = re.compile(r"^[ऀ-ॿ᳐-᳿꣠-ꣿ\s।॥\-'’,.{}a-z_0-9()]*$")
FEAR_WORDS = re.compile(r"\b(danger|dangerous|bad luck|misfortune|death|die|accident|illness|disease|loss|"
                        r"dosha|remedy|curse|evil|enemy|enemies|beware|warning)\b", re.I)
KALAS = P.KALAS
TOUCH_EFFECTS = {"glow", "wiggle", "hide", "appear", "bounce"}


# ---------------------------------------------------------------- report

class Report:
    def __init__(self):
        self.errors: list[str] = []
        self.warnings: list[str] = []
        self.notes: list[str] = []

    def err(self, where, msg):
        self.errors.append(f"ERROR  {where}: {msg}")

    def warn(self, where, msg):
        self.warnings.append(f"warn   {where}: {msg}")

    def note(self, where, msg):
        self.notes.append(f"flag   {where}: {msg}")

    def print(self):
        for line in self.notes + self.errors + self.warnings:
            print(line)
        print(f"\n{len(self.errors)} errors, {len(self.warnings)} warnings")


R = Report()


def rel(path):
    return os.path.relpath(path, ROOT)


# ---------------------------------------------------------------- helpers

def sacred(sa: str | None, where: str) -> dict | None:
    if not sa:
        return None
    sa = " ".join(str(sa).split())
    if not DEVANAGARI_OK.match(sa):
        R.err(where, f"sacred text must be Devanagari only: {sa[:40]}…")
    keep = {}
    def protect(m):  # keep {placeholders} untouched
        k = f"QQ{len(keep)}QQ"
        keep[k] = m.group(0)
        return k
    raw = re.sub(r"\{[a-z_]+\}", protect, sa)
    out = {
        "sa": sa,
        "iast": sanscript.transliterate(raw, sanscript.DEVANAGARI, sanscript.IAST),
        "te": sanscript.transliterate(raw, sanscript.DEVANAGARI, sanscript.TELUGU),
    }
    for k, v in keep.items():
        out["iast"], out["te"] = out["iast"].replace(k, v), out["te"].replace(k, v)
    return out


def localized(d, where: str, status: str, required: bool = True) -> dict | None:
    if not d:
        if required:
            (R.err if status in ("reviewed", "published") else R.warn)(where, "missing te / hi / en text")
        return None
    if not isinstance(d, dict):
        R.err(where, "expected {te, hi, en}")
        return None
    missing = [l for l in LANGS if not str(d.get(l, "")).strip()]
    if missing and required:
        (R.err if status in ("reviewed", "published") else R.warn)(where, f"missing language(s): {', '.join(missing)}")
    return {l: str(d[l]).strip() for l in LANGS if str(d.get(l, "")).strip()}


def check_id(kind, value, where):
    if not value or not re.match(ID[kind], str(value)):
        R.err(where, f"bad {kind} id '{value}'")


def check_status(fm, where, reviewed_needed=False):
    st = fm.get("status")
    if st not in STATUSES:
        R.err(where, f"status must be one of {sorted(STATUSES)}, got '{st}'")
        return "draft"
    if st in ("reviewed", "published") and reviewed_needed and not fm.get("reviewed_by"):
        R.err(where, "reviewed / published sacred content needs reviewed_by")
    return st


def as_list(v):
    if v is None or v == "":
        return []
    return v if isinstance(v, list) else [v]


def read_csv(path):
    with open(path, encoding="utf-8") as f:
        return [{k: (v or "").strip() for k, v in row.items()} for row in csv.DictReader(f)]


def yes(v):
    return str(v).strip().lower() in ("yes", "true", "1", "y")


# ---------------------------------------------------------------- loaders

def load_master():
    m = os.path.join(C, "master")
    out = {
        "masams": [{"n": int(r["n"]), "id": r["id"], "name": sacred(r["sa"], "master/masams"), "en": r["en"], "ritu": r["ritu"]}
                   for r in read_csv(f"{m}/masams.csv")],
        "tithis": [{"n": int(r["n"]), "id": r["id"], "name": sacred(r["sa"], "master/tithis"), "en": r["en"]}
                   for r in read_csv(f"{m}/tithis.csv")],
        "nakshatras": [{"n": int(r["n"]), "id": r["id"], "name": sacred(r["sa"], "master/nakshatras"), "en": r["en"]}
                       for r in read_csv(f"{m}/nakshatras.csv")],
        "rashis": [{"n": int(r["n"]), "id": r["id"], "name": sacred(r["sa"], "master/rashis"), "en": r["en"], "symbol_en": r["symbol_en"]}
                   for r in read_csv(f"{m}/rashis.csv")],
        "samvatsaras": [{"n": int(r["n"]), "id": r["id"], "name": sacred(r["sa"], "master/samvatsaras"), "cycle_year": int(r["first_ugadi_year"])}
                        for r in read_csv(f"{m}/samvatsaras.csv")],
        "states": [{"code": r["code"], "name": {"te": r["name_te"], "hi": r["name_hi"], "en": r["name_en"]},
                    "month_system": r["month_system"], "desha_clause": sacred(r["desha_clause_sa"], "master/states")}
                   for r in read_csv(f"{m}/states.csv")],
        "cities": [{"id": r["id"], "name": {"te": r["name_te"], "hi": r["name_hi"], "en": r["name_en"]},
                    "state": r["state"], "lat": float(r["lat"]), "lon": float(r["lon"])}
                   for r in read_csv(f"{m}/cities.csv")],
    }
    codes = {s["code"] for s in out["states"]}
    for c in out["cities"]:
        if c["state"] not in codes:
            R.err("master/cities.csv", f"{c['id']}: unknown state {c['state']}")
    if len(out["samvatsaras"]) != 60:
        R.err("master/samvatsaras.csv", "must list all 60 samvatsaras")
    return out


def load_images():
    rows = read_csv(os.path.join(C, "images", "manifest.csv"))
    out = {}
    for r in rows:
        where = f"images/manifest.csv:{r['id']}"
        check_id("img", r["id"], where)
        if r["id"] in out:
            R.err(where, "duplicate image id")
        src = os.path.join(C, "images", r["file"]) if r["file"] else None
        exists = bool(src and os.path.exists(src))
        if r["status"] in ("supplied", "approved") and not exists:
            R.err(where, f"marked {r['status']} but file missing: {r['file']}")
        out[r["id"]] = {"kind": r["kind"], "src": src if exists else None, "status": r["status"] if exists else "missing",
                        "used_by": r["used_by"], "priority": r["priority"],
                        "alt": {l: r.get(f"alt_{l}", "") for l in LANGS if r.get(f"alt_{l}")}}
    return out


def load_deities():
    out = []
    for path in sorted(glob.glob(f"{C}/deities/*.md")):
        w = rel(path)
        fm, root = parse(path)
        st = check_status(fm, w)
        check_id("deity", fm.get("id"), w)
        intro = root.child("intro")
        symbols = []
        sym_sec = root.child("symbols")
        for s in (sym_sec.children if sym_sec else []):
            img, label = slug_title(s.title)
            symbols.append({"image": img, "label": label, "meaning": localized(s.langs(), f"{w} {img}", st)})
        out.append({
            "id": fm["id"], "status": st, "order": int(fm.get("order", 99)),
            "name": sacred(fm.get("name_sa"), w),
            "name_common": localized(fm.get("name_common"), f"{w} name_common", st),
            "also_known_as": [sacred(x, w) for x in as_list(fm.get("also_known_as_sa"))],
            "images": fm.get("images", {}), "symbols": symbols,
            "intro": localized(intro.langs() if intro else None, f"{w} intro", st),
        })
    return out


def read_sidecar(stem: str):
    """Timings written beside a recording: content/audio/<id>.json."""
    path = os.path.join(C, "audio", f"{stem}.json")
    if not os.path.isfile(path):
        return None
    try:
        data = json.load(open(path, encoding="utf-8"))
    except json.JSONDecodeError as e:
        R.err(f"audio/{stem}.json", f"sidecar is not valid JSON: {e}")
        return None
    if not isinstance(data, dict):
        R.err(f"audio/{stem}.json", "sidecar must be an object")
        return None
    return data


def companion_audio(stem: str):
    for ext in (".m4a", ".mp3", ".wav"):
        name = stem + ext
        if os.path.isfile(os.path.join(C, "audio", name)):
            return name
    return None


def rising_marks(values, length, where: str, label: str):
    if not isinstance(values, list):
        R.err(where, f"{label} must be a list")
        return None
    previous = -1
    marks = []
    for mark in values:
        try:
            at = int(mark)
        except (TypeError, ValueError):
            R.err(where, f"{label} values must be numbers")
            return None
        if at <= previous or (length is not None and at >= length):
            R.err(where, f"{label} must rise and stay under length_ms")
        previous = at
        marks.append(at)
    return marks


def sidecar_length(side, where: str):
    try:
        return int(side["length_ms"])
    except (KeyError, TypeError, ValueError):
        R.err(where, "sidecar needs length_ms")
        return None


def draft_flag(side):
    status = side.get("status")
    if status == "draft":
        return True
    if status not in (None, "reviewed", "published"):
        R.err("audio", f"sidecar status must be draft, reviewed or published, not {status}")
    return False


def mantra_audio(audio, sa: str, where: str, mid: str):
    """Optional one-repetition recording. Absent is fine. Never synthesised here."""
    if not audio:
        return None
    if not isinstance(audio, dict) or not audio.get("file") or not audio.get("length_ms"):
        R.err(f"{where} {mid}", "audio needs file and length_ms")
        return None
    file_name = str(audio["file"])
    disk = os.path.join(C, "audio", file_name)
    if not os.path.isfile(disk):
        R.err(f"{where} {mid}", f"audio file missing: {file_name}")
    try:
        length = int(audio["length_ms"])
    except (TypeError, ValueError):
        R.err(f"{where} {mid}", "length_ms must be a number")
        return None
    words = audio.get("words_ms") or []
    if not isinstance(words, list):
        R.err(f"{where} {mid}", "words_ms must be a list")
        return None
    spoken = [part for part in str(sa).split() if part]
    if words and len(words) != len(spoken):
        R.err(f"{where} {mid}", f"words_ms has {len(words)} marks for {len(spoken)} words")
    previous = -1
    marks = []
    for mark in words:
        try:
            at = int(mark)
        except (TypeError, ValueError):
            R.err(f"{where} {mid}", "words_ms values must be numbers")
            return None
        if at <= previous or at >= length:
            R.err(f"{where} {mid}", "words_ms must rise and stay under length_ms")
        previous = at
        marks.append(at)
    return {"file": file_name, "length_ms": length, "words_ms": marks}


def mantra_from_sidecar(side, sa: str, where: str, mid: str):
    """One repetition's timings. Draft audio is marked so a release build can leave it out."""
    where = f"audio/{mid}.json"
    file_name = companion_audio(mid)
    if not file_name:
        R.err(where, f"audio file missing for {mid}")
        file_name = f"{mid}.m4a"
    length = sidecar_length(side, where)
    if length is None:
        return None
    spoken = [part for part in str(sa).split() if part]
    words = side.get("words_ms") or []
    if len(words) != len(spoken):
        R.err(where, f"words_ms has {len(words)} marks for {len(spoken)} words")
    marks = rising_marks(words, length, where, "words_ms")
    if marks is None:
        return None
    out = {"file": file_name, "length_ms": length, "words_ms": marks}
    if draft_flag(side):
        out["draft"] = True
    if side.get("words_estimated"):
        R.note(where, "word timings are estimated")
    return out


def media_length_ms(path: str):
    """Duration of an audio file, when the platform can read it."""
    try:
        import subprocess
        out = subprocess.check_output(["afinfo", path], text=True, stderr=subprocess.DEVNULL)
    except (OSError, subprocess.CalledProcessError):
        return None
    for line in out.splitlines():
        if "estimated duration" in line.lower():
            try:
                return int(float(line.split(":")[-1].split()[0]) * 1000)
            except (TypeError, ValueError, IndexError):
                return None
    return None


def text_audio(audio, name_count: int, where: str, status: str):
    """Equal slots, or one start time per name. Absent is fine."""
    if not audio:
        return None
    if not isinstance(audio, dict) or not audio.get("file"):
        R.err(where, "audio needs a file")
        return None
    file_name = str(audio["file"])
    disk = os.path.join(C, "audio", file_name)
    if not os.path.isfile(disk):
        (R.err if status in ("reviewed", "published") else R.warn)(where, f"audio file missing: {file_name}")
    length = media_length_ms(disk) if os.path.isfile(disk) else None
    names_ms = audio.get("names_ms")
    if names_ms is not None:
        if not isinstance(names_ms, list):
            R.err(where, "names_ms must be a list")
            return None
        if name_count and len(names_ms) != name_count:
            R.err(where, f"names_ms has {len(names_ms)} marks for {name_count} names")
        previous = -1
        marks = []
        for mark in names_ms:
            try:
                at = int(mark)
            except (TypeError, ValueError):
                R.err(where, "names_ms values must be numbers")
                return None
            if at <= previous or (length is not None and at >= length):
                R.err(where, "names_ms must rise and stay under the file length")
            previous = at
            marks.append(at)
        out = {"file": file_name, "names_ms": marks}
        if audio.get("draft"):
            out["draft"] = True
        return out
    intro = audio.get("intro_ms")
    slot = audio.get("slot_ms")
    out = {"file": file_name}
    if intro is not None:
        out["intro_ms"] = int(intro)
    if slot is not None:
        out["slot_ms"] = int(slot)
    return out


def text_from_sidecar(side, text_id: str, name_count: int, status: str):
    where = f"audio/{text_id}.json"
    file_name = companion_audio(text_id)
    if not file_name:
        (R.err if status in ("reviewed", "published") else R.warn)(where, f"audio file missing: {text_id}.m4a")
        file_name = f"{text_id}.m4a"
    length = sidecar_length(side, where)
    names_ms = side.get("names_ms")
    if not isinstance(names_ms, list):
        R.err(where, "names_ms must be a list")
        return None
    if name_count and len(names_ms) != name_count:
        R.err(where, f"names_ms has {len(names_ms)} marks for {name_count} names")
    if length is None:
        return None
    marks = rising_marks(names_ms, length, where, "names_ms")
    if marks is None:
        return None
    out = {"file": file_name, "names_ms": marks}
    if draft_flag(side):
        out["draft"] = True
    return out


def load_mantras():
    out = []
    for path in sorted(glob.glob(f"{C}/mantras/*.md")):
        w = rel(path)
        fm, root = parse(path)
        file_status = check_status(fm, w, reviewed_needed=True)
        deity = fm.get("deity")
        for sec in root.children:
            if sec.level != 2:
                continue
            if sec.title.lower().startswith("upac"):
                prefix = sec.keys(w).get("prefix_sa", "")
                for row in sec.table():
                    mid = row.get("id", "")
                    sa_line = next((v for k, v in row.items() if k.startswith("sa")), "")
                    review = "REVIEW" in sa_line
                    sa_line = sa_line.replace("(REVIEW)", "").strip()
                    check_id("mantra", mid, w)
                    out.append({"id": mid, "deity": deity, "status": file_status, "kind": "upachara",
                                "text": sacred(f"{prefix} {sa_line}", f"{w} {mid}"), "lines": [], "use": ["puja"],
                                "count_default": None, "meaning": None, "fills": None, "needs_review": review})
                continue
            mid = sec.title
            check_id("mantra", mid, f"{w} '{mid}'")
            k = sec.keys(w)
            review = bool(k.get("review"))
            if review and file_status in ("reviewed", "published"):
                R.err(f"{w} {mid}", "still marked review: remove after purohit check")
            side = read_sidecar(mid)
            out.append({
                "id": mid, "deity": deity, "status": file_status, "kind": "mantra",
                "text": sacred(k.get("sa"), f"{w} {mid}"),
                "lines": [sacred(x, f"{w} {mid}") for x in as_list(k.get("sa_lines"))],
                "use": as_list(k.get("use")), "count_default": k.get("count_default"),
                "meaning": localized(sec.langs(), f"{w} {mid}", file_status),
                "fills": k.get("fills"), "needs_review": review,
                "audio": (mantra_from_sidecar(side, k.get("sa") or "", w, mid) if side
                          else mantra_audio(k.get("audio"), k.get("sa") or "", w, mid)),
            })
            if not k.get("sa") and not k.get("sa_lines"):
                R.err(f"{w} {mid}", "mantra has no sa: or sa_lines:")
    return out


def load_texts():
    out = []
    for path in sorted(glob.glob(f"{C}/texts/*.yaml")):
        w = rel(path)
        d = yaml.safe_load(open(path, encoding="utf-8"))
        st = check_status(d, w, reviewed_needed=True)
        check_id("text", d.get("id"), w)
        names = d.get("names")
        entry = {"id": d["id"], "deity": d.get("deity"), "type": d.get("type"), "unit": d.get("unit"),
                 "status": st, "title": localized(d.get("title"), f"{w} title", st),
                 "audio": None, "round": d.get("round"), "mantra": d.get("mantra"), "names": []}
        if d.get("unit") not in ("reading", "repetition"):
            R.err(w, "unit must be reading or repetition")
        if names == "IMPORT":
            (R.err if st in ("reviewed", "published") else R.warn)(w, "names not imported yet (run tools/import_ashtottara.py)")
        elif isinstance(names, list):
            for i, n in enumerate(names, 1):
                if int(n.get("n", 0)) != i:
                    R.err(w, f"name numbering broken at position {i}")
                entry["names"].append({"id": f"{d['id']}.{i:03d}", "n": i, "text": sacred(n.get("sa"), f"{w} #{i}"),
                                       "flag": n.get("flag")})
                if n.get("flag"):
                    R.note(w, f"name {i} flagged {n.get('flag')}")
            if d.get("type") == "ashtottara" and len(entry["names"]) != 108:
                R.err(w, f"aṣṭottaram must have 108 names, has {len(entry['names'])}")
        side = read_sidecar(d["id"])
        entry["audio"] = text_from_sidecar(side, d["id"], len(entry["names"]), st) if side else text_audio(
            d.get("audio"), len(entry["names"]), w, st)
        out.append(entry)
    return out


def load_pujas():
    out = []
    for path in sorted(glob.glob(f"{C}/pujas/*/*.md")):
        w = rel(path)
        fm, root = parse(path)
        st = check_status(fm, w, reviewed_needed=True)
        check_id("puja", fm.get("id"), w)
        stages = []
        for stage in ("pre", "during", "post"):
            sec = root.child(stage)
            steps = []
            for s in (sec.children if sec else []):
                if s.level != 3:
                    continue
                slug, title = slug_title(s.title)
                k = s.keys(w)
                where = f"{w} {stage}.{slug}"
                samagri = [{"item": r.get("item"), "qty": r.get("qty"), "required": yes(r.get("required")),
                            "alternatives": [a.strip() for a in r.get("alternatives", "").split(",") if a.strip()]}
                           for r in s.table()]
                langs = s.langs()
                steps.append({
                    "id": f"{fm['id']}.{stage}.{slug}", "slug": slug, "title_iast": title,
                    "body": localized(langs, where, st, required=not samagri),
                    "note": localized(k.get("note"), where + " note", st, required=False),
                    "image": k.get("image"), "minutes": k.get("minutes"),
                    "mantras": as_list(k.get("mantra")), "repeat": k.get("repeat"), "text": k.get("text"),
                    "fills": as_list(k.get("fills")), "naivedyam": as_list(k.get("naivedyam")),
                    "optional": yes(k.get("optional", "no")), "samagri": samagri,
                })
            if not steps:
                R.warn(w, f"stage '{stage}' has no steps")
            stages.append({"stage": stage, "steps": steps})
        out.append({"id": fm["id"], "deity": fm.get("deity"), "kind": fm.get("kind"), "observance": fm.get("observance"),
                    "title": localized(fm.get("title"), f"{w} title", st), "minutes": fm.get("minutes"),
                    "regions": as_list(fm.get("regions")) or ["all"], "status": st, "stages": stages})
    return out


def load_naivedyam():
    out = []
    for path in sorted(glob.glob(f"{C}/naivedyam/*.md")):
        w = rel(path)
        fm, root = parse(path)
        st = check_status(fm, w)
        check_id("nv", fm.get("id"), w)
        why = root.child("why")
        out.append({"id": fm["id"], "offered_to": as_list(fm.get("offered_to")), "occasions": as_list(fm.get("occasions")),
                    "image": fm.get("image"), "status": st, "why": localized(why.langs() if why else None, f"{w} why", st),
                    "regional_forms": [{"name": localized(f.get("name"), f"{w} form", st), "region": as_list(f.get("region")),
                                        "note": localized(f.get("note"), f"{w} form note", st, required=False)}
                                       for f in as_list(fm.get("regional_forms"))]})
    return out


def load_samagri():
    out = []
    for r in read_csv(os.path.join(C, "samagri", "items.csv")):
        w = f"samagri/items.csv:{r['id']}"
        check_id("sm", r["id"], w)
        note = {l: r[f"note_{l}"] for l in LANGS if r.get(f"note_{l}")}
        out.append({"id": r["id"], "name": localized({l: r[f"name_{l}"] for l in LANGS}, w, "reviewed"),
                    "image": r["image"] or None,
                    "alternatives": [a.strip() for a in r["alternatives"].split(",") if a.strip()],
                    "note": note or None})
    return out


def load_observances(master):
    masa_ids = {m["id"]: m["n"] for m in master["masams"]}
    out = []
    for path in sorted(glob.glob(f"{C}/calendar/observances/*.md")):
        w = rel(path)
        fm, root = parse(path)
        st = check_status(fm, w)
        check_id("obs", fm.get("id"), w)
        rule = fm.get("rule") or {}
        if rule.get("masam") and rule["masam"] not in masa_ids:
            R.err(w, f"unknown masam '{rule['masam']}'")
        if rule.get("paksha") not in ("shukla", "krishna"):
            R.err(w, "rule.paksha must be shukla or krishna")
        if not (1 <= int(rule.get("tithi", 0)) <= 15):
            R.err(w, "rule.tithi must be 1–15")
        if rule.get("prevails_at") not in KALAS:
            R.err(w, f"rule.prevails_at must be one of {sorted(KALAS)}")
        if fm.get("kind") != "monthly" and not rule.get("masam"):
            R.err(w, "only kind: monthly may omit rule.masam")
        sections = {}
        for key in ("special", "families-do", "history", "regional-notes"):
            s = root.child(key)
            sections[key.replace("-", "_")] = localized(s.langs() if s else None, f"{w} {key}", st,
                                                        required=key in ("special", "families-do"))
        out.append({"id": fm["id"], "kind": fm.get("kind"), "title": localized(fm.get("title"), f"{w} title", st),
                    "rule": {"masam": rule.get("masam"), "masa_n": masa_ids.get(rule.get("masam")),
                             "paksha": rule.get("paksha"), "tithi": int(rule.get("tithi", 0)),
                             "prevails_at": rule.get("prevails_at")},
                    "regions": as_list(fm.get("regions")) or ["all"], "deity": fm.get("deity"),
                    "links": {k: as_list(v) for k, v in (fm.get("links") or {}).items()},
                    "image": fm.get("image"), "status": st, "sections": sections})
    return out


def load_stories():
    out = []
    for path in sorted(glob.glob(f"{C}/stories/*/*.md")):
        w = rel(path)
        fm, root = parse(path)
        st = check_status(fm, w, reviewed_needed=True)
        check_id("story", fm.get("id"), w)
        beats = []
        for b in root.children:
            if not b.title.startswith("beat"):
                continue
            k = b.keys(w)
            short, more = b.child("short"), b.child("more")
            where = f"{w} {b.title}"
            s_l = localized(short.langs() if short else None, where + " short", st)
            m_l = localized(more.langs() if more else None, where + " more", st, required=False) if more else None
            for l, txt in (s_l or {}).items():
                if l != "te" and len(txt.split()) > 20:
                    R.warn(where, f"short ({l}) is {len(txt.split())} words; keep ≤ 20")
            for l, txt in (m_l or {}).items():
                if l != "te" and len(txt.split()) > 60:
                    R.warn(where, f"more ({l}) is {len(txt.split())} words; keep ≤ 60")
            touch = k.get("touch")
            if touch and touch.get("effect") not in TOUCH_EFFECTS:
                R.err(where, f"touch.effect must be one of {sorted(TOUCH_EFFECTS)}")
            beats.append({"n": len(beats) + 1, "image": k.get("image"), "touch": touch, "short": s_l, "more": m_l})
        if not 4 <= len(beats) <= 8:
            R.err(w, f"stories need 4–8 beats, has {len(beats)}")
        out.append({"id": fm["id"], "deity": fm.get("deity"), "title": localized(fm.get("title"), f"{w} title", st),
                    "ages": str(fm.get("ages", "4-12")), "minutes": fm.get("minutes"), "feeling": as_list(fm.get("feeling")),
                    "source": fm.get("source"), "observances": as_list(fm.get("observances")), "cover": fm.get("cover"),
                    "status": st, "beats": beats})
    return out


def load_rashi():
    fm_g, g = parse(os.path.join(C, "rashi", "gochara.md"))
    fm_p, p = parse(os.path.join(C, "rashi", "profiles.md"))
    st_g, st_p = check_status(fm_g, "rashi/gochara.md"), check_status(fm_p, "rashi/profiles.md")
    houses = []
    for sec in g.children:
        m = re.match(r"gochara\.h(\d\d)$", sec.title)
        if not m:
            continue
        k = sec.keys("rashi/gochara.md")
        variants = sec.langs(variants=True)
        for l in LANGS:
            if not variants.get(l):
                (R.err if st_g != "draft" else R.warn)(f"rashi/gochara.md {sec.title}", f"missing {l}")
        for txt in variants.get("en", []):
            if FEAR_WORDS.search(txt):
                R.err(f"rashi/gochara.md {sec.title}", f"fear / remedy word: '{FEAR_WORDS.search(txt).group(0)}'")
        if k.get("tone") not in ("open", "steady", "gentle"):
            R.err(f"rashi/gochara.md {sec.title}", "tone must be open, steady or gentle")
        houses.append({"house": int(m.group(1)), "tone": k.get("tone"), "suggest_text": k.get("suggest_text"),
                       "variants": variants})
    if sorted(h["house"] for h in houses) != list(range(1, 13)):
        R.err("rashi/gochara.md", "need exactly houses h01–h12")
    profiles = {sec.title: localized(sec.langs(), f"rashi/profiles.md {sec.title}", st_p) for sec in p.children}
    return {"status": "draft" if "draft" in (st_g, st_p) else st_g, "profiles": profiles,
            "gochara": sorted(houses, key=lambda h: h["house"])}


# ---------------------------------------------------------------- references

def check_refs(data, images):
    ids = defaultdict(set)
    for k in ("deities", "mantras", "texts", "pujas", "naivedyam", "samagri", "observances", "stories"):
        for item in data[k]:
            if item["id"] in ids["all"]:
                R.err(k, f"duplicate id {item['id']}")
            ids["all"].add(item["id"])
            ids[k].add(item["id"])

    def need(kind, ref, where):
        if ref and ref not in ids[kind]:
            R.err(where, f"unknown {kind[:-1] if kind.endswith('s') else kind} '{ref}'")

    def img(ref, where):
        if ref and ref not in images:
            R.err(where, f"image '{ref}' not in images/manifest.csv")

    for d in data["deities"]:
        for v in d["images"].values():
            img(v, d["id"])
        for s in d["symbols"]:
            img(s["image"], d["id"])
    for p in data["pujas"]:
        need("deities", p["deity"], p["id"])
        need("observances", p["observance"], p["id"])
        for stage in p["stages"]:
            for s in stage["steps"]:
                for m in s["mantras"]:
                    need("mantras", m, s["id"])
                for n in s["naivedyam"]:
                    need("naivedyam", n, s["id"])
                need("texts", s["text"], s["id"])
                img(s["image"], s["id"])
                for row in s["samagri"]:
                    need("samagri", row["item"], s["id"])
                    for a in row["alternatives"]:
                        need("samagri", a, s["id"])
    for t in data["texts"]:
        need("deities", t["deity"], t["id"])
        need("mantras", t["mantra"], t["id"])
    for n in data["naivedyam"]:
        img(n["image"], n["id"])
        for o in n["occasions"]:
            need("observances", o, n["id"])
    for s in data["samagri"]:
        img(s["image"], s["id"])
        for a in s["alternatives"]:
            need("samagri", a, s["id"])
    for o in data["observances"]:
        img(o["image"], o["id"])
        need("deities", o["deity"], o["id"])
        for kind, refs in o["links"].items():
            for r in refs:
                need({"pujas": "pujas", "texts": "texts", "stories": "stories", "naivedyam": "naivedyam"}[kind], r, o["id"])
    for s in data["stories"]:
        img(s["cover"], s["id"])
        need("deities", s["deity"], s["id"])
        for b in s["beats"]:
            img(b["image"], f"{s['id']} beat {b['n']}")
        for o in s["observances"]:
            need("observances", o, s["id"])
    for h in data["rashi"]["gochara"]:
        need("texts", h["suggest_text"], f"gochara.h{h['house']:02d}")
    for r in data["master"]["rashis"]:
        if r["id"] not in data["rashi"]["profiles"]:
            R.err("rashi/profiles.md", f"missing profile for {r['id']}")


def drop_draft_audio(data):
    """A release build ships no machine draft. The player then behaves as if there is no recording."""
    for group in ("texts", "mantras"):
        for item in data[group]:
            audio = item.get("audio")
            if isinstance(audio, dict) and audio.get("draft"):
                item["audio"] = None


def prune_for_release(data):
    """Drop draft / retired items and strip links to anything dropped."""
    keep = lambda x: x.get("status") in ("reviewed", "published")
    for k in ("deities", "mantras", "texts", "pujas", "naivedyam", "observances", "stories"):
        data[k] = [x for x in data[k] if keep(x)]
    live = {x["id"] for k in ("deities", "mantras", "texts", "pujas", "naivedyam", "observances", "stories") for x in data[k]}
    for o in data["observances"]:
        o["links"] = {k: [r for r in v if r in live] for k, v in o["links"].items()}
    for p in data["pujas"]:
        for stage in p["stages"]:
            for s in stage["steps"]:
                s["mantras"] = [m for m in s["mantras"] if m in live]
                s["naivedyam"] = [m for m in s["naivedyam"] if m in live]
                if s["text"] not in live:
                    s["text"] = None
    if data["rashi"]["status"] not in ("reviewed", "published"):
        data["rashi"] = {"status": "draft", "profiles": {}, "gochara": []}


# ---------------------------------------------------------------- calendar

def build_calendar(master, observances, out_dir):
    years = CONFIG["samvatsaras"]
    first, last = min(years), max(years)
    months = P.lunar_months(dt.date(first, 1, 1), dt.date(last + 1, 6, 30))
    sam_by_cycle = {s["cycle_year"] % 60: s for s in master["samvatsaras"]}
    overrides = read_csv(os.path.join(C, "calendar", "overrides.csv"))
    cities = master["cities"] if CONFIG.get("cities", "all") == "all" else [c for c in master["cities"] if c["id"] in CONFIG["cities"]]
    check_rows, index = [], []
    first_city_of_state = {}
    for c in cities:
        first_city_of_state.setdefault(c["state"], c["id"])
    for city in cities:
        place = P.Place(city["id"], city["lat"], city["lon"], city["state"])
        days = P.build_days(place, months, dt.date(first, 3, 1), dt.date(last + 1, 4, 30))
        ugadis = P.resolve_observance(place, days, months, 1, "shukla", 1, "sunrise")
        for o in observances:
            if o["regions"] != ["all"] and city["state"] not in o["regions"]:
                continue
            r = o["rule"]
            for d in P.resolve_observance(place, days, months, r["masa_n"], r["paksha"], r["tithi"], r["prevails_at"]):
                days[d].observances.append(o["id"])
        for ov in overrides:   # samvatsara, observance, region, date, reason
            if ov["region"] not in ("all", city["state"]):
                continue
            target = dt.date.fromisoformat(ov["date"])
            for d in days.values():
                if ov["observance"] in d.observances and d.samvatsara_year == int(ov["samvatsara"]):
                    d.observances.remove(ov["observance"])
            if target in days:
                days[target].observances.append(ov["observance"])
        for y in years:
            start = next(u for u in ugadis if u.year == y)
            end = next(u for u in ugadis if u.year == y + 1) - dt.timedelta(days=1)
            sam = sam_by_cycle[y % 60]
            recs = []
            d = start
            while d <= end:
                x = days[d]
                recs.append({
                    "date": d.isoformat(), "sunrise": P.hhmm(x.sunrise), "sunset": P.hhmm(x.sunset),
                    "tithi": x.tithi, "tithi_ends": P.iso(x.tithi_end) if x.tithi_end else None,
                    "nakshatra": x.nakshatra, "nakshatra_ends": P.iso(x.nakshatra_end) if x.nakshatra_end else None,
                    "masa": x.masa, "adhika": x.adhika, "moon_rashi": x.moon_rashi, "moon_pct": x.moon_pct,
                    "observances": x.observances,
                })
                if first_city_of_state.get(city["state"]) == city["id"]:
                    for o in x.observances:
                        check_rows.append([y, sam["id"], o, city["state"], city["id"], d.isoformat(), ""])
                d += dt.timedelta(days=1)
            rel_path = f"calendar/{city['id']}/{y}-{sam['id']}.json"
            write_json(os.path.join(out_dir, rel_path), {
                "city": city["id"], "samvatsara": {"year": y, "id": sam["id"], "n": sam["n"]},
                "start": start.isoformat(), "end": end.isoformat(), "days": recs})
            if city is cities[0]:
                index.append({"year": y, "id": sam["id"], "start": start.isoformat(), "end": end.isoformat()})
    for y in years:
        sam = sam_by_cycle[y % 60]
        rows = sorted(r for r in check_rows if r[0] == y)
        with open(os.path.join(C, "calendar", f"check-{y}-{sam['id']}.csv"), "w", newline="", encoding="utf-8") as f:
            w = csv.writer(f)
            w.writerow(["samvatsara", "observance", "state", "reference_city", "computed_date", "ttd_date_if_different"])
            w.writerows([r[1:] for r in rows])
    return index


# ---------------------------------------------------------------- images

SIZES = {"deity": {"hero": (1200, 1500), "card": (600, 600)}, "symbol": (192, 192), "puja": (1200, 900),
         "observance": (1200, 800), "story": (1600, 1200), "naivedyam": (800, 800), "samagri": (256, 256)}


def image_size(kind, iid):
    size = SIZES.get(kind)
    if isinstance(size, dict):
        size = size.get(iid.rsplit(".", 1)[-1], (600, 600))
    return size or (600, 600)


def rasterize_svg(src, tmp, size):
    """Write a PNG. Cairo is used when it is installed; otherwise resvg renders the same drawing."""
    try:
        import cairosvg
        cairosvg.svg2png(url=src, write_to=tmp, output_width=size[0], output_height=size[1])
        return
    except Exception as cairo_error:
        script = os.path.join(ROOT, "tools", "svg_to_png.mjs")
        node = shutil.which("node")
        if not node or not os.path.isfile(script):
            raise cairo_error
        proc = subprocess.run([node, script, src, tmp, str(size[0]), str(size[1])], capture_output=True, text=True)
        if proc.returncode != 0:
            detail = (proc.stderr or proc.stdout or "").strip()
            raise RuntimeError(f"{cairo_error}; resvg: {detail}") from cairo_error


def build_images(images, out_dir):
    out = {}
    for iid, im in images.items():
        entry = {"kind": im["kind"], "status": im["status"], "alt": im["alt"], "file": None}
        if im["src"]:
            dst_rel = f"images/{iid}.webp"
            dst = os.path.join(out_dir, dst_rel)
            os.makedirs(os.path.dirname(dst), exist_ok=True)
            try:
                from PIL import Image
                if im["src"].endswith(".svg"):
                    size = image_size(im["kind"], iid)
                    tmp = dst + ".png"
                    rasterize_svg(im["src"], tmp, size)
                    image = Image.open(tmp).convert("RGBA")
                    if image.size != tuple(size):
                        image = image.resize(tuple(size), Image.Resampling.LANCZOS)
                    image.save(dst, "WEBP", quality=90)
                    os.remove(tmp)
                else:
                    Image.open(im["src"]).save(dst, "WEBP", quality=88)
                entry["file"] = dst_rel
            except Exception as e:  # noqa: BLE001
                R.err(f"images/{iid}", f"could not convert: {e}")
        out[iid] = entry
    return out


# ---------------------------------------------------------------- output

def write_json(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(obj, f, ensure_ascii=False, separators=(",", ":"))


def validate_outputs(out_dir):
    import jsonschema
    sdir = os.path.join(C, "_schema")
    pairs = [("master.schema.json", ["master.json"]), ("deities.schema.json", ["deities.json"]),
             ("mantras.schema.json", ["mantras.json"]), ("samagri.schema.json", ["samagri.json"]),
             ("naivedyam.schema.json", ["naivedyam.json"]), ("observances.schema.json", ["observances.json"]),
             ("rashi.schema.json", ["rashi.json"]), ("images.schema.json", ["images.json"]),
             ("manifest.schema.json", ["manifest.json"]),
             ("puja.schema.json", glob.glob(f"{out_dir}/pujas/*.json")),
             ("text.schema.json", glob.glob(f"{out_dir}/texts/*.json")),
             ("story.schema.json", glob.glob(f"{out_dir}/stories/*.json")),
             ("calendar.schema.json", glob.glob(f"{out_dir}/calendar/*/*.json"))]
    for schema_name, files in pairs:
        schema = json.load(open(os.path.join(sdir, schema_name), encoding="utf-8"))
        for f in files:
            path = f if os.path.isabs(f) else os.path.join(out_dir, f)
            try:
                jsonschema.validate(json.load(open(path, encoding="utf-8")), schema)
            except jsonschema.ValidationError as e:
                R.err(rel(path), f"schema {schema_name}: {e.message} at {list(e.absolute_path)}")


def status_report(data, images):
    rows = []
    for k in ("deities", "pujas", "texts", "mantras", "naivedyam", "observances", "stories"):
        for x in data[k]:
            rows.append([k, x["id"], x["status"], "yes" if x.get("needs_review") else ""])
    rows.append(["rashi", "rashi", data["rashi"]["status"], ""])
    for iid, im in images.items():
        rows.append(["images", iid, im["status"], im["priority"]])
    with open(os.path.join(C, "content_status.csv"), "w", newline="", encoding="utf-8") as f:
        w = csv.writer(f)
        w.writerow(["type", "id", "status", "needs_review / priority"])
        w.writerows(rows)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--check", action="store_true", help="validate only")
    ap.add_argument("--release", action="store_true", help="include only reviewed / published")
    ap.add_argument("--no-calendar", action="store_true", help="skip calendar computation (faster)")
    args = ap.parse_args()

    try:
        master = load_master()
        images = load_images()
        data = {"master": master, "deities": load_deities(), "mantras": load_mantras(), "texts": load_texts(),
                "pujas": load_pujas(), "naivedyam": load_naivedyam(), "samagri": load_samagri(),
                "observances": load_observances(master), "stories": load_stories(), "rashi": load_rashi()}
    except ContentError as e:
        R.err("parse", str(e))
        R.print()
        sys.exit(1)

    check_refs(data, images)
    for o in data["observances"]:
        for pid in o["links"].get("pujas", []):
            p = next((p for p in data["pujas"] if p["id"] == pid), None)
            if p and p["observance"] != o["id"]:
                R.warn(o["id"], f"links puja {pid} whose observance is {p['observance']}")

    if args.check:
        status_report(data, images)
        R.print()
        sys.exit(1 if R.errors else 0)
    if R.errors:
        R.print()
        print("\nFix the errors above, then build again.")
        sys.exit(1)

    if args.release:
        prune_for_release(data)
        drop_draft_audio(data)

    out_dir = os.path.join(ROOT, CONFIG["assets_out"])
    if os.path.isdir(out_dir):
        keep_cal = args.no_calendar and os.path.isdir(os.path.join(out_dir, "calendar"))
        for name in os.listdir(out_dir):
            if keep_cal and name == "calendar":
                continue
            p = os.path.join(out_dir, name)
            shutil.rmtree(p) if os.path.isdir(p) else os.remove(p)
    os.makedirs(out_dir, exist_ok=True)

    write_json(f"{out_dir}/master.json", master)
    write_json(f"{out_dir}/deities.json", sorted(data["deities"], key=lambda d: d["order"]))
    write_json(f"{out_dir}/mantras.json", data["mantras"])
    write_json(f"{out_dir}/samagri.json", data["samagri"])
    write_json(f"{out_dir}/naivedyam.json", data["naivedyam"])
    write_json(f"{out_dir}/observances.json", data["observances"])
    write_json(f"{out_dir}/rashi.json", data["rashi"])
    for t in data["texts"]:
        write_json(f"{out_dir}/texts/{t['id']}.json", t)
    write_json(f"{out_dir}/texts.json", [{k: v for k, v in t.items() if k != "names"} | {"count": len(t["names"])}
                                         for t in data["texts"]])
    for p in data["pujas"]:
        write_json(f"{out_dir}/pujas/{p['id']}.json", p)
    write_json(f"{out_dir}/pujas.json", [{k: p[k] for k in ("id", "deity", "kind", "observance", "title", "minutes", "regions", "status")}
                                         for p in data["pujas"]])
    for s in data["stories"]:
        write_json(f"{out_dir}/stories/{s['id']}.json", s)
    write_json(f"{out_dir}/stories.json", [{k: s[k] for k in ("id", "deity", "title", "ages", "minutes", "feeling", "observances", "cover", "status")}
                                           | {"beats": len(s["beats"])} for s in data["stories"]])
    write_json(f"{out_dir}/images.json", build_images(images, out_dir))

    if args.no_calendar and os.path.isdir(os.path.join(out_dir, "calendar")):
        cal_index = json.load(open(os.path.join(out_dir, "calendar", "index.json"), encoding="utf-8"))
    else:
        cal_index = build_calendar(master, data["observances"], out_dir)
        write_json(f"{out_dir}/calendar/index.json", cal_index)
    draft_stems = set()
    for side_path in glob.glob(f"{C}/audio/*.json"):
        side = read_sidecar(os.path.splitext(os.path.basename(side_path))[0])
        if side and side.get("status") == "draft":
            draft_stems.add(os.path.splitext(os.path.basename(side_path))[0])
    for pattern in ("*.mp3", "*.m4a", "*.wav"):
        for audio in glob.glob(f"{C}/audio/{pattern}"):
            stem = os.path.splitext(os.path.basename(audio))[0]
            if args.release and stem in draft_stems:
                continue
            os.makedirs(f"{out_dir}/audio", exist_ok=True)
            shutil.copy(audio, f"{out_dir}/audio/")

    files = {}
    for path in sorted(glob.glob(f"{out_dir}/**/*", recursive=True)):
        if os.path.isfile(path):
            files[os.path.relpath(path, out_dir)] = hashlib.sha256(open(path, "rb").read()).hexdigest()[:16]
    now = dt.datetime.now(P.IST)
    write_json(f"{out_dir}/manifest.json", {
        "version": now.strftime("%Y%m%d.%H%M"), "built_at": now.isoformat(timespec="seconds"),
        "mode": "release" if args.release else "dev", "samvatsaras": cal_index,
        "counts": {k: len(data[k]) for k in ("deities", "pujas", "texts", "mantras", "naivedyam", "observances", "stories")},
        "files": files})
    validate_outputs(out_dir)
    status_report(data, images)
    R.print()
    print(f"\nBuilt {len(files)} files into {rel(out_dir)}")
    sys.exit(1 if R.errors else 0)


if __name__ == "__main__":
    main()
