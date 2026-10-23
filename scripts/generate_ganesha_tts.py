#!/usr/bin/env python3
"""Generate bundled Ganesha demo audio via macOS `say` (TTS).

For a real recording you uploaded, use:
  python3 scripts/bundle_naamavali_audio.py

TTS is dev-only. Re-run after changing lines; copy start_times_ms into SeedContent.kt.

Usage:
  python3 scripts/generate_ganesha_tts.py
  python3 scripts/generate_ganesha_tts.py --voice Geeta
"""

from __future__ import annotations

import argparse
import json
import os
import struct
import subprocess
import wave

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RAW = os.path.join(ROOT, "app", "src", "main", "res", "raw")
WORK = os.path.join(ROOT, "scripts", "tts_work")

DEFAULT_LINES = [
    "Om sumukha namaha",
    "Om durmodara namaha",
    "Om jnanin ishvara namaha",
    "Om bala prabhu namaha",
    "Om poorna namaha",
    "Om varada namaha",
    "Om ekadanta namaha",
    "Om heramba namaha",
    "Om lambodara namaha",
    "Om sura graja namaha",
    "Om vighna raja namaha",
    "Om vighna harta namaha",
]


def say_to_wav(text: str, out_wav: str, voice: str, rate: int) -> None:
    aiff = out_wav + ".aiff"
    subprocess.run(["say", "-v", voice, "-r", str(rate), "-o", aiff, text], check=True)
    subprocess.run(["afconvert", "-f", "WAVE", "-d", "LEI16", aiff, out_wav], check=True)
    os.remove(aiff)


def read_wav(path: str) -> tuple[int, int, int, bytes]:
    with wave.open(path, "rb") as w:
        return w.getnchannels(), w.getsampwidth(), w.getframerate(), w.readframes(w.getnframes())


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--voice", default="Rishi", help="macOS say voice (Rishi, Lekha, Geeta, Vani, …)")
    parser.add_argument("--rate", type=int, default=150)
    parser.add_argument("--gap-ms", type=int, default=400)
    args = parser.parse_args()

    os.makedirs(WORK, exist_ok=True)
    os.makedirs(RAW, exist_ok=True)

    segment_paths: list[str] = []
    start_times: list[int] = []
    cursor_ms = 0

    for i, line in enumerate(DEFAULT_LINES, start=1):
        seg = os.path.join(WORK, f"seg_{i:02d}.wav")
        say_to_wav(line, seg, args.voice, args.rate)
        segment_paths.append(seg)
        start_times.append(cursor_ms)
        nch, sw, fr, frames = read_wav(seg)
        dur_ms = int(len(frames) / sw / nch * 1000 / fr)
        cursor_ms += dur_ms + args.gap_ms

    nch, sw, fr, _ = read_wav(segment_paths[0])
    gap_samples = int(fr * args.gap_ms / 1000)
    gap_chunk = struct.pack("<h", 0) * gap_samples * nch

    out_path = os.path.join(RAW, "ganesha_ashtottara.wav")
    with wave.open(out_path, "wb") as out:
        out.setnchannels(nch)
        out.setsampwidth(sw)
        out.setframerate(fr)
        for idx, seg in enumerate(segment_paths):
            _, _, _, frames = read_wav(seg)
            out.writeframes(frames)
            if idx < len(segment_paths) - 1:
                out.writeframes(gap_chunk)

    kotlin = "listOf(" + ", ".join(f"{t}L" for t in start_times) + ")"
    meta = {
        "voice": args.voice,
        "start_times_ms": start_times,
        "kotlin": kotlin,
        "output": out_path,
    }
    with open(os.path.join(WORK, "ganesha_tts_meta.json"), "w", encoding="utf-8") as f:
        json.dump(meta, f, indent=2)

    print(f"Wrote {out_path}")
    print("Paste into SeedContent.kt ganeshaTtsStartTimesMs:")
    print(kotlin)


if __name__ == "__main__":
    main()
