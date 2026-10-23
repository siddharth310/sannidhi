#!/usr/bin/env python3
"""Copy naamavali audio into res/raw and estimate per-name start times.

Usage:
  # Your uploaded recording (default path):
  python3 scripts/bundle_naamavali_audio.py

  # Custom file + name count (108 for full ashtottara, 12 for current seed):
  python3 scripts/bundle_naamavali_audio.py --input scripts/multi-speaker.mp3 --names 12

  # Use exact timings from JSON (array of start_ms integers):
  python3 scripts/bundle_naamavali_audio.py --timings scripts/ganesha_timings.json

Then paste the printed Kotlin list into SeedContent.kt (ganeshaTtsStartTimesMs).
Rebuild the app.
"""

from __future__ import annotations

import argparse
import json
import math
import os
import shutil
import struct
import wave

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RAW = os.path.join(ROOT, "app", "src", "main", "res", "raw")
DEFAULT_INPUT = os.path.join(ROOT, "scripts", "multi-speaker.mp3")
META_OUT = os.path.join(ROOT, "scripts", "tts_work", "imported_audio_meta.json")


def read_wav(path: str) -> tuple[int, int, int, bytes]:
    with wave.open(path, "rb") as w:
        return w.getnchannels(), w.getsampwidth(), w.getframerate(), w.readframes(w.getnframes())


def detect_starts_ms(
    path: str,
    *,
    rms_threshold: int = 600,
    silence_ms: int = 200,
    min_gap_sec: float = 2.0,
    target_count: int | None = None,
) -> list[int]:
    nch, sw, fr, raw = read_wav(path)
    if sw != 2:
        raise ValueError("Only 16-bit PCM WAV is supported")
    samples = struct.unpack("<" + "h" * (len(raw) // 2), raw)
    win = int(fr * 0.05)
    rms: list[float] = []
    for i in range(0, len(samples), win):
        chunk = samples[i : i + win]
        if chunk:
            rms.append(math.sqrt(sum(s * s for s in chunk) / len(chunk)))

    starts = [0]
    in_speech = rms[0] > rms_threshold
    silent_run = 0
    minsil = int(fr * silence_ms / 1000)

    for idx, val in enumerate(rms[1:], start=1):
        frame = idx * win
        if val > rms_threshold:
            if not in_speech:
                starts.append(frame)
                in_speech = True
            silent_run = 0
        else:
            silent_run += win
            if in_speech and silent_run >= minsil:
                in_speech = False

    merged = [starts[0]]
    min_gap = int(fr * min_gap_sec)
    for s in starts[1:]:
        if s - merged[-1] >= min_gap:
            merged.append(s)

    starts_ms = [int(s * 1000 / fr) for s in merged]

    if target_count is not None and len(starts_ms) > target_count:
        starts_ms = starts_ms[:target_count]
    elif target_count is not None and len(starts_ms) < target_count:
        # Even split fallback for missing detections
        duration_ms = int(len(samples) * 1000 / fr)
        step = duration_ms // target_count
        starts_ms = [i * step for i in range(target_count)]

    return starts_ms


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", default=DEFAULT_INPUT, help="Source audio (WAV or WAV-in-.mp3)")
    parser.add_argument("--resource", default="ganesha_ashtottara", help="res/raw base name (no extension)")
    parser.add_argument("--names", type=int, default=12, help="Number of names in SeedContent for this naamavali")
    parser.add_argument("--timings", help="JSON file: [0, 1234, ...] start times in ms")
    parser.add_argument("--min-gap-sec", type=float, default=2.0, help="Merge detections (≈2.0 → ~108 for full archana)")
    parser.add_argument(
        "--fixed-slot",
        action="store_true",
        help="Use assemble_master grid: intro 8000 ms + index×3500 ms (108 names)",
    )
    args = parser.parse_args()

    if not os.path.isfile(args.input):
        raise SystemExit(f"Input not found: {args.input}")

    os.makedirs(RAW, exist_ok=True)
    os.makedirs(os.path.dirname(META_OUT), exist_ok=True)

    dest = os.path.join(RAW, f"{args.resource}.wav")
    shutil.copy2(args.input, dest)

    nch, sw, fr, raw = read_wav(dest)
    duration_ms = int(len(raw) / sw / nch * 1000 / fr)

    if args.fixed_slot:
        intro_ms, slot_ms, count = 8_000, 3_500, 108
        starts_ms = [intro_ms + i * slot_ms for i in range(count)]
        args.names = count
    elif args.timings:
        with open(args.timings, encoding="utf-8") as f:
            starts_ms = json.load(f)
    else:
        starts_ms = detect_starts_ms(
            dest,
            min_gap_sec=args.min_gap_sec,
            target_count=args.names,
        )

    kotlin = "listOf(" + ", ".join(f"{t}L" for t in starts_ms) + ")"
    meta = {
        "input": args.input,
        "bundled": dest,
        "duration_ms": duration_ms,
        "name_count": args.names,
        "start_times_ms": starts_ms,
        "kotlin": kotlin,
        "note": "Auto-detected timings are approximate. Prefer hand-checked or Whisper timings for production.",
    }
    with open(META_OUT, "w", encoding="utf-8") as f:
        json.dump(meta, f, indent=2)

    print(f"Bundled → {dest}")
    print(f"Duration: {duration_ms} ms ({duration_ms/1000:.1f}s)")
    print(f"Start times ({len(starts_ms)}): {starts_ms}")
    print("\nPaste into SeedContent.kt:")
    print(kotlin)
    print(f"\nMeta: {META_OUT}")


if __name__ == "__main__":
    main()
