#!/usr/bin/env python3
"""Turn recordings that have arrived into timed audio for one puja.

    python3 tools/prepare_audio.py --puja ganesha.puja.vinayaka-chavithi

Reads wav files from _incoming/audio-from-sarvam/. Writes m4a and a draft
timing sidecar under content/audio/. Does not invent a recording. A list whose
pauses do not match its names is reported and gets no timings.
"""
from __future__ import annotations

import json
import os
import re
import shutil
import subprocess
import sys
import unicodedata

sys.path.insert(0, os.path.dirname(__file__))
from export_sarvam_text import blocks_for  # noqa: E402

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
INCOMING = os.path.join(ROOT, "_incoming", "audio-from-sarvam")
AUDIO = os.path.join(ROOT, "content", "audio")


def weight(text: str) -> int:
    count = 0
    for ch in text:
        if ch.isalpha() or unicodedata.category(ch).startswith("M"):
            count += 1
    return max(count, 1)


def marks(parts: list[str], length: int) -> list[int] | None:
    """Start time of each part, from its share of the syllables. Each stays under length."""
    if not parts or length < 1:
        return None
    weights = [weight(part) for part in parts]
    total = sum(weights) or 1
    cursor = 0
    rough = []
    for i, share in enumerate(weights):
        rough.append(0 if i == 0 else cursor)
        cursor += max(1, int(length * share / total))
    fixed = []
    previous = -1
    for i, mark in enumerate(rough):
        room = length - (len(parts) - i)
        if room <= previous:
            return None
        at = min(max(mark, previous + 1), room - 1)
        if at < 0 or at >= length:
            return None
        fixed.append(at)
        previous = at
    return fixed


def spans(parts: list[str], length: int) -> list[tuple[int, int]] | None:
    starts = marks(parts, length)
    if starts is None:
        return None
    out = []
    for i, start in enumerate(starts):
        end = starts[i + 1] if i + 1 < len(starts) else length
        if end <= start:
            return None
        out.append((start, end))
    return out


def duration_ms(path: str) -> int | None:
    try:
        out = subprocess.check_output(["afinfo", path], text=True, stderr=subprocess.DEVNULL)
        for line in out.splitlines():
            if "estimated duration" in line.lower():
                return int(float(line.split(":")[-1].split()[0]) * 1000)
    except (OSError, subprocess.CalledProcessError, ValueError, IndexError):
        pass
    try:
        out = subprocess.check_output(
            ["ffprobe", "-v", "error", "-show_entries", "format=duration", "-of", "csv=p=0", path],
            text=True, stderr=subprocess.DEVNULL,
        )
        return int(float(out.strip()) * 1000)
    except (OSError, subprocess.CalledProcessError, ValueError):
        return None


def convert(src: str, dest: str) -> bool:
    if shutil.which("ffmpeg") is None:
        print(f"ffmpeg is not installed; left untouched: {os.path.basename(src)}")
        return False
    os.makedirs(os.path.dirname(dest), exist_ok=True)
    done = subprocess.run(
        ["ffmpeg", "-y", "-i", src, "-af", "loudnorm=I=-16:TP=-1.5:LRA=11",
         "-ar", "44100", "-c:a", "aac", "-b:a", "96k", dest],
        stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL,
    )
    if done.returncode != 0 or not os.path.isfile(dest):
        print(f"could not convert {os.path.basename(src)}")
        if os.path.isfile(dest):
            os.remove(dest)
        return False
    return True


def name_starts(path: str, length: int) -> list[int]:
    """Start of each spoken name, from the silences between them."""
    if shutil.which("ffmpeg") is None:
        return []
    done = subprocess.run(
        ["ffmpeg", "-i", path, "-af", "silencedetect=noise=-35dB:d=0.25", "-f", "null", "-"],
        stdout=subprocess.DEVNULL, stderr=subprocess.PIPE, text=True,
    )
    silences = []
    pending = None
    for kind, raw in re.findall(r"silence_(start|end): ([0-9.]+)", done.stderr):
        if kind == "start":
            pending = float(raw)
        elif pending is not None:
            silences.append((pending, float(raw)))
            pending = None
    if silences and silences[0][0] < 0.2:
        first = int(silences[0][1] * 1000)
        rest = silences[1:]
    else:
        first = 0
        rest = silences
    starts = [first] + [int(end * 1000) for _start, end in rest]
    return [at for at in starts if 0 <= at < length - 150]


def write_json(stem: str, payload: dict):
    os.makedirs(AUDIO, exist_ok=True)
    with open(os.path.join(AUDIO, stem + ".json"), "w", encoding="utf-8") as handle:
        json.dump(payload, handle, ensure_ascii=False, indent=2)
        handle.write("\n")


def stem_of(file_name: str, kind: str) -> str:
    base = file_name[:-4] if file_name.endswith(".wav") else file_name
    if kind == "mantra":
        return base[len("mantra."):]
    if kind == "list":
        return base[len("mantra."):]  # already ends with .list
    return base


def prepare(puja_id: str) -> int:
    _heading, blocks = blocks_for(puja_id)
    present = []
    for block in blocks:
        src = os.path.join(INCOMING, block.file_name)
        if os.path.isfile(src):
            present.append((block, src))
    if not present:
        print(f"No recordings in {INCOMING} for {puja_id}.")
        return 0
    written = 0
    for block, src in present:
        stem = stem_of(block.file_name, block.kind)
        dest = os.path.join(AUDIO, stem + ".m4a")
        if not convert(src, dest):
            continue
        length = duration_ms(dest)
        if not length or length < 1:
            print(f"no duration, removed: {os.path.basename(dest)}")
            os.remove(dest)
            continue
        if block.kind == "list":
            starts = name_starts(dest, length)
            if len(starts) != len(block.parts):
                print(f"list pauses do not match the names, no timings: {block.file_name}")
                os.remove(dest)
                continue
            write_json(stem, {
                "status": "draft",
                "length_ms": length,
                "names_ms": starts,
            })
        elif block.kind == "mantra":
            words = marks(block.parts, length)
            if words is None:
                print(f"could not place word starts, no timings: {block.file_name}")
                os.remove(dest)
                continue
            write_json(stem, {
                "status": "draft",
                "length_ms": length,
                "words_ms": words,
                "words_estimated": True,
            })
        else:
            pieces = spans(block.parts, length)
            if pieces is None:
                print(f"could not place sentence starts, no timings: {block.file_name}")
                os.remove(dest)
                continue
            write_json(stem, {
                "status": "draft",
                "length_ms": length,
                "sentences": [
                    {"start_ms": start, "end_ms": end, "text": text}
                    for (start, end), text in zip(pieces, block.parts)
                ],
            })
        written += 1
        print(f"draft  {stem}.m4a")
    print(f"Prepared {written} of {len(present)} recordings.")
    return written


def main():
    if len(sys.argv) != 3 or sys.argv[1] != "--puja":
        raise SystemExit("usage: python3 tools/prepare_audio.py --puja <puja-id>")
    prepare(sys.argv[2])


if __name__ == "__main__":
    main()
