#!/usr/bin/env python3
"""
Gaṇeśa Aṣṭottara — Master Track Assembly Script
================================================

Reads 108 per-name takes from ``takes/`` and a drone file, then assembles
the master track by placing each take at its exact slot boundary:

    start_ms(i) = 8000 + i * 3500      (i = 0..107)

No hand-nudging. No slot widening. Takes longer than 2.8 s are flagged
for re-record, never stretched.

Outputs (into --out-dir):
    ganesha_ashtottara_master.wav   — voice + drone, mixed
    stems/voice_dry.wav             — voice only, no drone
    stems/drone.wav                 — drone only, with fades

All audio is 48 kHz / 24-bit WAV, mono.

Usage:
    python assemble_master.py \\
        --takes-dir takes \\
        --drone drone.wav \\
        --out-dir output

    # Intro is optional — supply a WAV for "śuklāmbaradharaṁ" or omit
    # for a silent intro (8 s of drone only).
    python assemble_master.py \\
        --takes-dir takes \\
        --drone drone.wav \\
        --intro intro.wav \\
        --out-dir output

Requirements:
    pip install soundfile numpy
"""

import argparse
import sys
import numpy as np
import soundfile as sf
from pathlib import Path

# ---------------------------------------------------------------------------
# Hard-spec constants — the app's timings are derived from these. Do NOT change.
# ---------------------------------------------------------------------------
SAMPLE_RATE      = 48_000        # Hz
BIT_DEPTH        = "PCM_24"      # 24-bit WAV
CHANNELS         = 1             # mono

INTRO_MS         = 8_000         # ms — silence (or intro) before name 1
SLOT_MS          = 3_500         # ms — slot per name
NUM_NAMES        = 108
MAX_TAKE_MS      = 2_800         # ms — QA gate: no utterance may exceed this
MIN_VOICE_MS     = 1_400         # ms — minimum voice occupancy per slot

# Total length: intro + 108 slots.
# NOTE: The brief states "382,500 ms (6:26.000)". 382,500 ms = 6:22.500, which is
# actually the *onset of name 108*, not the total length. The timecode 6:26.000
# = 386,000 ms is the correct total (intro + 108 full slots). We use 386,000.
TOTAL_MS         = INTRO_MS + NUM_NAMES * SLOT_MS   # 386,000 ms = 6:26.000
# For reference: onset of name 108 = 382,500 ms (the number in the brief).

# Drone / background
DRONE_RELATIVE_DB = -22.0        # dB relative to voice
DRONE_FADEIN_MS   = 2_000        # ms
DRONE_FADEOUT_MS  = 3_000        # ms

# Derived sample counts
INTRO_SAMPLES  = int(INTRO_MS  * SAMPLE_RATE / 1000)   # 384,000
SLOT_SAMPLES   = int(SLOT_MS  * SAMPLE_RATE / 1000)     # 168,000
TOTAL_SAMPLES  = INTRO_SAMPLES + NUM_NAMES * SLOT_SAMPLES  # 18,528,000
MAX_TAKE_SAMPLES = int(MAX_TAKE_MS * SAMPLE_RATE / 1000)    # 134,400


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def ms_to_samples(ms: int) -> int:
    return int(ms * SAMPLE_RATE / 1000)


def samples_to_ms(s: int) -> float:
    return s * 1000 / SAMPLE_RATE


def db_to_linear(db: float) -> float:
    return 10.0 ** (db / 20.0)


def load_mono(path: Path) -> np.ndarray:
    """Load an audio file, convert to mono float32 at the project sample rate."""
    data, sr = sf.read(str(path), dtype="float32", always_2d=False)
    if sr != SAMPLE_RATE:
        raise ValueError(
            f"{path.name}: sample rate {sr} Hz ≠ required {SAMPLE_RATE} Hz. "
            f"Resample before assembly."
        )
    if data.ndim == 2:
        data = data.mean(axis=1)  # fold to mono
    return data.astype(np.float32)


def normalize_peak(data: np.ndarray, target_dbfs: float = -1.0) -> np.ndarray:
    """Normalize so the peak hits target_dbfs."""
    peak = np.max(np.abs(data))
    if peak < 1e-9:
        return data
    target = db_to_linear(target_dbfs)
    return data * (target / peak)


def apply_fades(data: np.ndarray, fade_in_ms: int, fade_out_ms: int) -> np.ndarray:
    """Apply linear fades in-place."""
    out = data.copy()
    fi = min(ms_to_samples(fade_in_ms), len(out) // 2)
    fo = min(ms_to_samples(fade_out_ms), len(out) // 2)
    if fi > 0:
        out[:fi] *= np.linspace(0.0, 1.0, fi, dtype=np.float32)
    if fo > 0:
        out[-fo:] *= np.linspace(1.0, 0.0, fo, dtype=np.float32)
    return out


def build_drone_track(drone_raw: np.ndarray, total_samples: int) -> np.ndarray:
    """
    Loop the drone to fill the entire track, then apply fade-in and fade-out.
    The loop is crossfaded at the seam to avoid clicks.
    """
    if len(drone_raw) == 0:
        raise ValueError("Drone file is empty.")

    # Crossfade length for seamless looping (50 ms)
    xfade = ms_to_samples(50)

    # If the drone is shorter than the total track, loop it
    if len(drone_raw) < total_samples:
        loop_unit = drone_raw.copy()
        # Prepare crossfaded loop: append a crossfaded copy to create one
        # seamless unit, then tile.
        if len(loop_unit) > xfade:
            xfade_region = np.linspace(1.0, 0.0, xfade, dtype=np.float32)
            tail = loop_unit[-xfade:] * xfade_region
            head = loop_unit[:xfade] * xfade_region[::-1]
            seamless_tail = tail + head
            loop_extended = np.concatenate([loop_unit[:-xfade], seamless_tail])
        else:
            loop_extended = loop_unit

        # Tile to fill
        reps = int(np.ceil(total_samples / len(loop_extended)))
        drone_full = np.tile(loop_extended, reps)[:total_samples]
    else:
        drone_full = drone_raw[:total_samples].copy()

    # Trim to exact total
    drone_full = drone_full[:total_samples]

    # Apply fades
    drone_full = apply_fades(drone_full, DRONE_FADEIN_MS, DRONE_FADEOUT_MS)

    return drone_full


def build_voice_track(takes_dir: Path) -> tuple[np.ndarray, list[dict]]:
    """
    Build the dry voice track: silence for the intro, then each take placed
    at its slot boundary. Returns (voice_track, take_log).
    """
    voice = np.zeros(TOTAL_SAMPLES, dtype=np.float32)
    take_log = []

    for i in range(NUM_NAMES):
        slot_idx = i
        name_num = i + 1
        start_sample = INTRO_SAMPLES + slot_idx * SLOT_SAMPLES
        end_sample = start_sample + SLOT_SAMPLES

        take_path = takes_dir / f"{name_num:03d}.wav"
        if not take_path.exists():
            take_log.append({
                "name": name_num,
                "status": "MISSING",
                "start_ms": samples_to_ms(start_sample),
                "duration_ms": 0,
                "message": f"{take_path} not found",
            })
            continue

        take = load_mono(take_path)

        # QA gate: flag takes longer than 2.8 s
        take_dur_ms = len(take) * 1000 / SAMPLE_RATE
        status = "OK"
        message = ""

        if len(take) > MAX_TAKE_SAMPLES:
            status = "OVER_LENGTH"
            message = (
                f"Take {name_num:03d} is {take_dur_ms:.0f} ms "
                f"(> {MAX_TAKE_MS} ms). RE-RECORD — do not widen the slot."
            )
            # Truncate to max — the take will be placed but flagged
            take = take[:MAX_TAKE_SAMPLES]
        elif take_dur_ms < MIN_VOICE_MS:
            status = "SHORT"
            message = (
                f"Take {name_num:03d} is {take_dur_ms:.0f} ms "
                f"(< {MIN_VOICE_MS} ms). May be too short."
            )

        # Place take at slot boundary, no overlap into next slot
        slot_end = min(start_sample + len(take), end_sample)
        place_len = slot_end - start_sample
        voice[start_sample:slot_end] = take[:place_len]

        take_log.append({
            "name": name_num,
            "status": status,
            "start_ms": samples_to_ms(start_sample),
            "duration_ms": take_dur_ms,
            "message": message,
        })

    return voice, take_log


def mix_tracks(voice: np.ndarray, drone: np.ndarray, voice_gain_db: float = 0.0) -> np.ndarray:
    """Mix voice and drone. Voice is at unity (0 dB), drone at DRONE_RELATIVE_DB."""
    drone_gain = db_to_linear(DRONE_RELATIVE_DB + voice_gain_db)
    mixed = voice + drone * drone_gain

    # Prevent clipping
    peak = np.max(np.abs(mixed))
    if peak > 0.99:
        mixed = mixed * (0.99 / peak)
        print(f"  [WARN] Mix peaked above -0.1 dBFS; normalized to -0.1 dBFS.")

    return mixed


def write_wav(path: Path, data: np.ndarray):
    """Write mono float32 audio as 24-bit WAV at 48 kHz."""
    path.parent.mkdir(parents=True, exist_ok=True)
    sf.write(str(path), data, SAMPLE_RATE, subtype=BIT_DEPTH)


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main():
    parser = argparse.ArgumentParser(
        description="Assemble Gaṇeśa Aṣṭottara master track from per-name takes."
    )
    parser.add_argument(
        "--takes-dir", type=Path, required=True,
        help="Directory containing 001.wav … 108.wav"
    )
    parser.add_argument(
        "--drone", type=Path, required=True,
        help="Drone / tanpura WAV file (will be looped seamlessly)"
    )
    parser.add_argument(
        "--intro", type=Path, default=None,
        help="Optional intro WAV (śuklāmbaradharaṁ or praṇava). "
             "Must resolve to silence by 7800 ms. If omitted, intro is silent."
    )
    parser.add_argument(
        "--out-dir", type=Path, default=Path("output"),
        help="Output directory (default: output)"
    )
    args = parser.parse_args()

    takes_dir: Path = args.takes_dir
    out_dir: Path  = args.out_dir

    print("=" * 72)
    print("Gaṇeśa Aṣṭottara — Master Assembly")
    print("=" * 72)
    print(f"  Sample rate : {SAMPLE_RATE} Hz")
    print(f"  Bit depth    : 24-bit PCM")
    print(f"  Intro        : {INTRO_MS} ms")
    print(f"  Slot/name    : {SLOT_MS} ms")
    print(f"  Names        : {NUM_NAMES}")
    print(f"  Total length : {TOTAL_MS} ms ({TOTAL_MS // 60000}:{TOTAL_MS % 60000 // 1000:03d}.{TOTAL_MS % 1000:03d})")
    print(f"  Max take     : {MAX_TAKE_MS} ms")
    print(f"  Drone level  : {DRONE_RELATIVE_DB} dB rel. voice")
    print()

    # --- Validate takes directory ---
    print("[1/5] Checking takes…")
    missing = []
    for i in range(1, NUM_NAMES + 1):
        p = takes_dir / f"{i:03d}.wav"
        if not p.exists():
            missing.append(i)
    if missing:
        print(f"  MISSING {len(missing)} takes: {missing[:10]}{'…' if len(missing) > 10 else ''}")
        print("  Cannot proceed without all 108 takes.")
        sys.exit(1)
    print(f"  All 108 takes present.")

    # --- Build voice track ---
    print("[2/5] Building voice track…")
    voice, take_log = build_voice_track(takes_dir)

    issues = [t for t in take_log if t["status"] != "OK"]
    if issues:
        print(f"  {len(issues)} take(s) flagged:")
        for t in issues:
            print(f"    #{t['name']:03d} [{t['status']}] {t['message']}")
    else:
        print("  All takes within spec.")

    # --- Handle intro ---
    if args.intro:
        print(f"[3/5] Loading intro: {args.intro.name}")
        intro_data = load_mono(args.intro)
        intro_len = min(len(intro_data), INTRO_SAMPLES)
        voice[:intro_len] = intro_data[:intro_len]
        print(f"  Intro placed: {samples_to_ms(intro_len):.0f} ms")
    else:
        print("[3/5] No intro file — 8 s silent intro (drone only).")

    # --- Build drone track ---
    print("[4/5] Building drone track…")
    drone_raw = load_mono(args.drone)
    drone = build_drone_track(drone_raw, TOTAL_SAMPLES)
    print(f"  Drone looped to {len(drone)} samples ({samples_to_ms(len(drone)):.0f} ms)")

    # --- Mix and write outputs ---
    print("[5/5] Mixing and writing outputs…")
    master = mix_tracks(voice, drone)

    master_path = out_dir / "ganesha_ashtottara_master.wav"
    voice_path   = out_dir / "stems" / "voice_dry.wav"
    drone_path   = out_dir / "stems" / "drone.wav"

    write_wav(master_path, master)
    write_wav(voice_path, voice)
    write_wav(drone_path, drone)

    print()
    print("Outputs written:")
    print(f"  {master_path}")
    print(f"  {voice_path}")
    print(f"  {drone_path}")
    print()

    # --- Summary ---
    total_dur_ms = len(master) * 1000 / SAMPLE_RATE
    print(f"Master duration : {total_dur_ms:.0f} ms "
          f"({total_dur_ms // 60000:.0f}:{total_dur_ms % 60000 // 1000:02d}.{total_dur_ms % 1000:03d})")
    print(f"Expected        : {TOTAL_MS} ms")
    diff = total_dur_ms - TOTAL_MS
    if abs(diff) > 50:
        print(f"  [WARN] Duration differs by {diff:+.0f} ms (tolerance ±50 ms)")
    else:
        print(f"  OK (within ±50 ms tolerance)")

    if issues:
        print()
        print(f"[ACTION REQUIRED] {len(issues)} take(s) need re-recording.")
        sys.exit(1)

    print()
    print("Assembly complete. Run qa_verify.py on the master before publish.")
    print("Then encode to 128 kbps mono AAC or Opus for shipping.")


if __name__ == "__main__":
    main()
