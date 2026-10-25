#!/usr/bin/env python3
"""Paste kit for one puja. Copies the instruction and mantra text; it does not speak.

    python3 tools/export_sarvam_text.py ganesha.puja.vinayaka-chavithi

Writes _incoming/SARVAM-ONLINE/5-puja-<slug>.md and 5-checklist.csv.
Nothing under content/ is changed.
"""
from __future__ import annotations

import csv
import glob
import os
import re
import sys
from dataclasses import dataclass, field

sys.path.insert(0, os.path.dirname(__file__))
from mdparse import parse, slug_title  # noqa: E402

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
LANGS = ("te", "hi", "en")
VOICE = {
    "te": ("te-IN", "kavitha", "0.85", "0.45"),
    "hi": ("hi-IN", "ritu", "0.85", "0.45"),
    "en": ("en-IN", "priya", "0.88", "0.45"),
}
MANTRA_VOICE = ("hi-IN", "anand", "0.8", "0.3")


@dataclass
class Block:
    file_name: str
    paste_from: str
    what: str
    language: str
    speaker: str
    pace: str
    temperature: str
    text: str
    kind: str  # instruction | mantra | list
    parts: list[str] = field(default_factory=list)


def as_list(value):
    if value is None or value == "":
        return []
    return value if isinstance(value, list) else [value]


def find_puja(puja_id: str) -> str:
    for path in glob.glob(os.path.join(ROOT, "content", "pujas", "*", "*.md")):
        fm, _ = parse(path)
        if fm.get("id") == puja_id:
            return path
    raise SystemExit(f"no puja with id {puja_id}")


def load_mantras() -> dict:
    found = {}
    for path in glob.glob(os.path.join(ROOT, "content", "mantras", "*.md")):
        _, root = parse(path)
        for sec in root.children:
            if sec.level != 2:
                continue
            if sec.title.lower().startswith("upac"):
                keys = sec.keys(path)
                prefix = str(keys.get("prefix_sa") or "")
                for row in sec.table():
                    mid = row.get("id", "")
                    action = next((v for k, v in row.items() if k.startswith("sa")), "")
                    action = action.replace("(REVIEW)", "").strip()
                    spoken = f"{prefix} {action}".strip() if prefix else action
                    found[mid] = {"sa": spoken, "lines": []}
                continue
            keys = sec.keys(path)
            lines = [str(line) for line in as_list(keys.get("sa_lines"))]
            found[sec.title] = {"sa": keys.get("sa") or "", "lines": lines}
    return found


def sentences(text: str) -> list[str]:
    """Split for lighting. The pieces, joined, are the same letters as the source."""
    if not text.strip():
        return []
    holder = "\uE000"
    protected = text.replace("॥", holder)
    raw = re.split(r"(?<=[।.!?])\s+", protected)
    parts = [piece.replace(holder, "॥").strip() for piece in raw if piece.strip()]
    if not parts:
        parts = [text.strip()]
    source = re.sub(r"\s+", "", text)
    joined = re.sub(r"\s+", "", "".join(parts))
    if joined != source:
        return [text.strip()]
    return parts


def blocks_for(puja_id: str) -> tuple[str, list[Block]]:
    path = find_puja(puja_id)
    fm, root = parse(path)
    mantras = load_mantras()
    slug = puja_id.split(".")[-1]
    paste = f"5-puja-{slug}.md"
    out: list[Block] = []
    used: list[str] = []
    for stage in ("pre", "during", "post"):
        sec = root.child(stage)
        for step in (sec.children if sec else []):
            if step.level != 3:
                continue
            step_slug, _title = slug_title(step.title)
            keys = step.keys(path)
            for mid in as_list(keys.get("mantra")):
                if mid not in used:
                    used.append(mid)
            langs = step.langs()
            for lang in LANGS:
                body = langs.get(lang) or ""
                if not str(body).strip():
                    continue
                code, speaker, pace, temperature = VOICE[lang]
                name = f"puja.{puja_id}.{stage}.{step_slug}.{lang}.wav"
                out.append(Block(
                    file_name=name, paste_from=paste, what=f"{stage} {step_slug} {lang}",
                    language=code, speaker=speaker, pace=pace, temperature=temperature,
                    text=str(body).strip(), kind="instruction", parts=sentences(str(body).strip()),
                ))
    for mid in used:
        mantra = mantras.get(mid)
        if mantra is None:
            raise SystemExit(f"{puja_id} uses {mid}, which is not in content/mantras")
        sa = str(mantra["sa"] or "").strip()
        lines = [line.strip() for line in mantra["lines"] if str(line).strip()]
        if sa:
            code, speaker, pace, temperature = MANTRA_VOICE
            out.append(Block(
                file_name=f"mantra.{mid}.wav", paste_from=paste, what=mid,
                language=code, speaker=speaker, pace=pace, temperature=temperature,
                text=sa, kind="mantra", parts=sa.split(),
            ))
        if lines:
            code, speaker, pace, temperature = MANTRA_VOICE
            out.append(Block(
                file_name=f"mantra.{mid}.list.wav", paste_from=paste, what=f"{mid} list",
                language=code, speaker=speaker, pace=pace, temperature=temperature,
                text="\n".join(lines), kind="list", parts=lines,
            ))
    title = fm.get("title") or {}
    heading = title.get("en") if isinstance(title, dict) else str(title)
    return heading or puja_id, out


def write_kit(puja_id: str) -> int:
    heading, blocks = blocks_for(puja_id)
    slug = puja_id.split(".")[-1]
    folder = os.path.join(ROOT, "_incoming", "SARVAM-ONLINE")
    os.makedirs(folder, exist_ok=True)
    md_path = os.path.join(folder, f"5-puja-{slug}.md")
    csv_path = os.path.join(folder, "5-checklist.csv")
    lines = [
        f"# 5 · {heading}",
        "",
        "Paste one block at a time. Save it with the file name shown. The words inside the fence",
        "are copied from the content files.",
        "",
        "Instructions, one file per step per language:",
        "",
        "| Language | Code | Speaker | Pace | Temperature |",
        "|---|---|---|---|---|",
        "| Telugu | te-IN | kavitha | 0.85 | 0.45 |",
        "| Hindi | hi-IN | ritu | 0.85 | 0.45 |",
        "| English | en-IN | priya | 0.88 | 0.45 |",
        "",
        "Mantras and the name list, from the Devanagari:",
        "",
        "| Language | Code | Speaker | Pace | Temperature |",
        "|---|---|---|---|---|",
        "| Hindi | hi-IN | anand | 0.8 | 0.3 |",
        "",
    ]
    groups = [
        ("instruction", "te", "Instructions · Telugu"),
        ("instruction", "hi", "Instructions · Hindi"),
        ("instruction", "en", "Instructions · English"),
        ("mantra", "hi", "Mantras · Sanskrit, spoken in Hindi"),
        ("list", "hi", "Names, one per line"),
    ]
    for kind, lang, title in groups:
        chosen = [b for b in blocks if b.kind == kind and b.file_name.endswith(f".{lang}.wav" if kind == "instruction" else ".wav")]
        if kind != "instruction":
            chosen = [b for b in blocks if b.kind == kind]
        if not chosen:
            continue
        lines.append(f"## {title}")
        lines.append("")
        for block in chosen:
            lines.append(f"**{block.what}** → `{block.file_name}`")
            lines.append("")
            fence = "~~~~" if "```" in block.text else "```"
            lines.append(fence)
            lines.append(block.text)
            lines.append(fence)
            lines.append("")
    with open(md_path, "w", encoding="utf-8") as handle:
        handle.write("\n".join(lines).rstrip() + "\n")
    with open(csv_path, "w", encoding="utf-8", newline="") as handle:
        writer = csv.writer(handle)
        writer.writerow(["file_name", "paste_from", "what", "language", "speaker", "pace", "temperature", "done"])
        for block in blocks:
            writer.writerow([
                block.file_name, block.paste_from, block.what, block.language,
                block.speaker, block.pace, block.temperature, "",
            ])
    print(f"Wrote {len(blocks)} files")
    print(md_path)
    print(csv_path)
    return len(blocks)


def main():
    if len(sys.argv) != 2:
        raise SystemExit("usage: python3 tools/export_sarvam_text.py <puja-id>")
    write_kit(sys.argv[1])


if __name__ == "__main__":
    main()
