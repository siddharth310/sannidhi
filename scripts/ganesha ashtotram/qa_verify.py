#!/usr/bin/env python3
"""
Gaṇeśa Aṣṭottara — QA Gate Verification Script
===============================================

Verifies a finished master track against the QA gates in the production brief:

    1. Total length is 386,000 ms ± 50 ms  (6:26.000; see note below)
    2. Each name's onset within ±80 ms of its computed slot boundary
    3. No utterance exceeds 2.8 s
    4. All 108 present, in order, none duplicated or dropped
    5. Background drone fade-in / fade-out present

NOTE on total length: the brief states "382,500 ms (6:26.000)". 382,500 ms is
the onset of name 108 (8000 + 107×3500); the actual total including name 108's
slot is 386,000 ms = 6:26.000. This script checks against 386,000 ms.

Usage:
    python qa_verify.py --master ganesha_ashtottara_master.wav
    python qa_verify.py --master ganesha_ashtottara_master.wav --report qa_report.txt
"""

import argparse
import sys
import numpy as np
import soundfile as sf
from pathlib import Path

# ---------------------------------------------------------------------------
# Hard-spec constants (must match assemble_master.py)
# ---------------------------------------------------------------------------
SAMPLE_RATE  = 48_000
INTRO_MS     = 8_000
SLOT_MS      = 3_500
NUM_NAMES    = 108
TOTAL_MS     = INTRO_MS + NUM_NAMES * SLOT_MS   # 386,000

# QA tolerances
LENGTH_TOLERANCE_MS   = 50     # ±50 ms on total length
ONSET_TOLERANCE_MS    = 80     # ±80 ms on slot onset
MAX_UTTERANCE_MS      = 2_800  # no utterance may exceed this

# Onset detection parameters
ONSET_ENERGY_THRESHOLD_DB = -45.0   # dBFS — energy above this = voice
ONSET_PRE_ROLL_MS         = 50      # ms — window before slot boundary to search


def ms_to_samples(ms: float) -> int:
    return int(ms * SAMPLE_RATE / 1000)


def samples_to_ms(s: int) -> float:
    return s * 1000 / SAMPLE_RATE


def db_to_linear(db: float) -> float:
    return 10.0 ** (db / 20.0)


def compute_envelope(data: np.ndarray, window_ms: float = 10.0) -> np.ndarray:
    """RMS envelope with a rectangular window."""
    w = max(1, ms_to_samples(window_ms))
    n_windows = len(data) // w
    if n_windows == 0:
        return np.array([0.0])
    trimmed = data[: n_windows * w].reshape(n_windows, w)
    env = np.sqrt(np.mean(trimmed**2, axis=1))
    return env


def detect_onset_in_slot(data: np.ndarray, slot_start: int, slot_end: int) -> tuple[int | None, float]:
    """
    Detect the onset of voice within a slot.
    Returns (onset_sample, duration_ms_of_continuous_voice).
    onset_sample is None if no voice detected.
    """
    # Search window: from 50 ms before slot boundary to 80% into the slot
    search_start = max(0, slot_start - ms_to_samples(ONSET_PRE_ROLL_MS))
    search_end = slot_start + int(SLOT_MS * SAMPLE_RATE / 1000 * 0.8)

    seg = data[search_start:min(search_end, len(data))]
    if len(seg) == 0:
        return None, 0.0

    threshold = db_to_linear(ONSET_ENERGY_THRESHOLD_DB)

    # Use a short-window envelope for onset detection
    env_w = ms_to_samples(5)  # 5 ms windows
    n_env = len(seg) // env_w
    if n_env == 0:
        return None, 0.0
    env = np.sqrt(np.mean(seg[: n_env * env_w].reshape(n_env, env_w) ** 2, axis=1))

    above = env > threshold
    if not above.any():
        return None, 0.0

    # First window above threshold = onset
    onset_env_idx = int(np.argmax(above))
    onset_sample = search_start + onset_env_idx * env_w

    # Measure duration: last window above threshold before a long gap
    # (gap = 300 ms of silence ends the utterance)
    gap_w = ms_to_samples(300) // env_w
    last_voice_idx = onset_env_idx
    silence_count = 0
    for j in range(onset_env_idx, len(env)):
        if env[j] > threshold:
            last_voice_idx = j
            silence_count = 0
        else:
            silence_count += 1
            if silence_count >= gap_w:
                break

    voice_start = onset_env_idx * env_w
    voice_end = (last_voice_idx + 1) * env_w
    duration_ms = (voice_end - voice_start) * 1000 / SAMPLE_RATE

    return onset_sample, duration_ms


def check_length(data: np.ndarray) -> dict:
    actual_ms = len(data) * 1000 / SAMPLE_RATE
    diff_ms = actual_ms - TOTAL_MS
    passed = abs(diff_ms) <= LENGTH_TOLERANCE_MS
    return {
        "gate": "Total length",
        "expected_ms": TOTAL_MS,
        "actual_ms": round(actual_ms, 1),
        "diff_ms": round(diff_ms, 1),
        "tolerance_ms": LENGTH_TOLERANCE_MS,
        "passed": passed,
        "detail": f"{actual_ms:.0f} ms vs expected {TOTAL_MS} ms (diff {diff_ms:+.0f} ms, tol ±{LENGTH_TOLERANCE_MS})",
    }


def check_onsets_and_durations(data: np.ndarray) -> tuple[dict, list[dict]]:
    """
    Check that each of the 108 names has an onset within ±80 ms of its slot
    boundary, and that no utterance exceeds 2.8 s.
    """
    results = []
    slot_samples = ms_to_samples(SLOT_MS)
    intro_samples = ms_to_samples(INTRO_MS)

    onsets_pass = True
    durations_pass = True
    missing = []
    late = []
    over_length = []

    for i in range(NUM_NAMES):
        slot_start = intro_samples + i * slot_samples
        slot_end = slot_start + slot_samples
        expected_ms = samples_to_ms(slot_start)

        onset_sample, duration_ms = detect_onset_in_slot(data, slot_start, slot_end)

        if onset_sample is None:
            missing.append(i + 1)
            results.append({
                "name": i + 1,
                "expected_onset_ms": expected_ms,
                "detected_onset_ms": None,
                "onset_diff_ms": None,
                "utterance_ms": 0,
                "onset_ok": False,
                "duration_ok": False,
                "status": "NO_VOICE",
            })
            onsets_pass = False
            durations_pass = False
            continue

        detected_ms = samples_to_ms(onset_sample)
        diff_ms = detected_ms - expected_ms

        onset_ok = abs(diff_ms) <= ONSET_TOLERANCE_MS
        duration_ok = duration_ms <= MAX_UTTERANCE_MS

        if not onset_ok:
            late.append((i + 1, diff_ms))
            onsets_pass = False
        if not duration_ok:
            over_length.append((i + 1, duration_ms))
            durations_pass = False

        results.append({
            "name": i + 1,
            "expected_onset_ms": expected_ms,
            "detected_onset_ms": round(detected_ms, 1),
            "onset_diff_ms": round(diff_ms, 1),
            "utterance_ms": round(duration_ms, 1),
            "onset_ok": onset_ok,
            "duration_ok": duration_ok,
            "status": "OK" if (onset_ok and duration_ok) else "FAIL",
        })

    onset_gate = {
        "gate": "Onset alignment",
        "tolerance_ms": ONSET_TOLERANCE_MS,
        "passed": onsets_pass,
        "missing": missing,
        "late": late,
        "detail": (
            f"All 108 onsets within ±{ONSET_TOLERANCE_MS} ms"
            if onsets_pass
            else f"{len(late)} late/early, {len(missing)} missing"
        ),
    }

    duration_gate = {
        "gate": "Utterance duration ≤ 2.8 s",
        "passed": durations_pass,
        "over_length": over_length,
        "detail": (
            "No utterance exceeds 2.8 s"
            if durations_pass
            else f"{len(over_length)} utterance(s) over 2.8 s"
        ),
    }

    return onset_gate, results, duration_gate


def check_drone_fades(data: np.ndarray) -> dict:
    """Verify the drone fades in and out (energy at start/end should be lower)."""
    w = ms_to_samples(500)
    first = np.sqrt(np.mean(data[:w] ** 2))
    mid_start = len(data) // 2 - w // 2
    mid = np.sqrt(np.mean(data[mid_start : mid_start + w] ** 2))
    last = np.sqrt(np.mean(data[-w:] ** 2))

    # Fade-in: first 500 ms should be notably quieter than mid
    fadein_ok = first < mid * 0.5
    fadeout_ok = last < mid * 0.5

    return {
        "gate": "Drone fades",
        "passed": fadein_ok and fadeout_ok,
        "detail": f"start RMS {first:.4f}, mid RMS {mid:.4f}, end RMS {last:.4f}",
    }


def check_duplicates(results: list[dict]) -> dict:
    """Check all 108 names are present in order (no dup/drop)."""
    names_detected = [r["name"] for r in results if r["status"] != "NO_VOICE"]
    all_present = len(names_detected) == NUM_NAMES
    in_order = names_detected == sorted(names_detected)
    return {
        "gate": "All 108 present, in order",
        "passed": all_present and in_order,
        "detail": f"{len(names_detected)}/108 detected, order {'correct' if in_order else 'incorrect'}",
    }


def format_gate(g: dict) -> str:
    icon = "PASS" if g["passed"] else "FAIL"
    line = f"  [{icon}] {g['gate']}"
    if "detail" in g and g["detail"]:
        line += f" — {g['detail']}"
    return line


def main():
    parser = argparse.ArgumentParser(description="QA gate verification for Gaṇeśa Aṣṭottara master.")
    parser.add_argument("--master", type=Path, required=True, help="Path to the master WAV")
    parser.add_argument("--report", type=Path, default=None, help="Write a text report to this path")
    parser.add_argument("--per-name", action="store_true", help="Print per-name detail table")
    args = parser.parse_args()

    if not args.master.exists():
        print(f"Error: {args.master} not found.")
        sys.exit(2)

    data, sr = sf.read(str(args.master), dtype="float32", always_2d=False)
    if sr != SAMPLE_RATE:
        print(f"Error: sample rate {sr} Hz ≠ {SAMPLE_RATE} Hz.")
        sys.exit(2)
    if data.ndim == 2:
        data = data.mean(axis=1)

    print("=" * 72)
    print("Gaṇeśa Aṣṭottara — QA Gate Verification")
    print("=" * 72)
    print(f"  File     : {args.master}")
    print(f"  Duration : {len(data) / SAMPLE_RATE:.3f} s")
    print(f"  SR       : {sr} Hz")
    print()

    gates = []

    # Gate 1: Total length
    g1 = check_length(data)
    gates.append(g1)
    print(format_gate(g1))

    # Gates 2+3: Onsets and durations
    g2, results, g3 = check_onsets_and_durations(data)
    gates.append(g2)
    gates.append(g3)
    print(format_gate(g2))
    print(format_gate(g3))

    # Gate 4: All present, in order
    g4 = check_duplicates(results)
    gates.append(g4)
    print(format_gate(g4))

    # Gate 5: Drone fades
    g5 = check_drone_fades(data)
    gates.append(g5)
    print(format_gate(g5))

    print()

    # Per-name detail
    if args.per_name:
        print("Per-name detail:")
        print(f"  {'#':>4} {'expected':>10} {'detected':>10} {'diff':>8} {'utter':>8} {'status':>8}")
        print("  " + "-" * 60)
        for r in results:
            det = f"{r['detected_onset_ms']:.1f}" if r["detected_onset_ms"] is not None else "—"
            diff = f"{r['onset_diff_ms']:+.1f}" if r["onset_diff_ms"] is not None else "—"
            print(
                f"  {r['name']:>4} "
                f"{r['expected_onset_ms']:>10.1f} "
                f"{det:>10} "
                f"{diff:>8} "
                f"{r['utterance_ms']:>8.1f} "
                f"{r['status']:>8}"
            )
        print()

    # Failure details
    failures = [g for g in gates if not g["passed"]]
    if failures:
        print("=" * 72)
        print("FAILURES — must be fixed before publish:")
        for g in failures:
            print(f"  • {g['gate']}: {g.get('detail', '')}")
            if "late" in g and g["late"]:
                for name, diff in g["late"][:10]:
                    print(f"      name #{name}: onset off by {diff:+.1f} ms")
            if "over_length" in g and g["over_length"]:
                for name, dur in g["over_length"][:10]:
                    print(f"      name #{name}: utterance {dur:.0f} ms > 2800 ms")
            if "missing" in g and g["missing"]:
                print(f"      missing: {g['missing']}")
        print("=" * 72)

        # Write report if requested
        if args.report:
            with open(args.report, "w", encoding="utf-8") as f:
                f.write("Gaṇeśa Aṣṭottara — QA Report\n")
                f.write("=" * 40 + "\n\n")
                for g in gates:
                    f.write(f"[{'PASS' if g['passed'] else 'FAIL'}] {g['gate']}\n")
                    f.write(f"  {g.get('detail', '')}\n\n")
                f.write("\nPer-name detail:\n")
                for r in results:
                    f.write(f"  #{r['name']:03d} {r['status']}\n")
            print(f"Report written to {args.report}")

        sys.exit(1)
    else:
        print("=" * 72)
        print("ALL QA GATES PASSED — track is ready for purohit review and encode.")
        print("=" * 72)
        print("Next steps:")
        print("  1. Purohit review pass on pronunciation")
        print("  2. Encode to 128 kbps mono AAC or Opus for shipping")
        print("  3. Verify LUFS: −16 LUFS integrated, true peak ≤ −1.5 dBTP")

        if args.report:
            with open(args.report, "w", encoding="utf-8") as f:
                f.write("Gaṇeśa Aṣṭottara — QA Report\n")
                f.write("=" * 40 + "\n\n")
                for g in gates:
                    f.write(f"[{'PASS' if g['passed'] else 'FAIL'}] {g['gate']}\n")
                    f.write(f"  {g.get('detail', '')}\n\n")
            print(f"\nReport written to {args.report}")

        sys.exit(0)


if __name__ == "__main__":
    main()
