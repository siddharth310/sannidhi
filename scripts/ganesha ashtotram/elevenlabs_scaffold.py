#!/usr/bin/env python3
"""
elevenlabs_scaffold.py — build a placeholder Ganesha Ashtottara track on the fixed slot grid.

SCAFFOLD ONLY. Use this to develop and test the player. Do not ship it.
ElevenLabs has no Sanskrit voice; a Hindi multilingual voice reading Devanagari is the
closest approximation and it will still get retroflexes and vowel length wrong in places.

    pip install requests pydub
    export ELEVENLABS_API_KEY=...
    python3 elevenlabs_scaffold.py --names names_dev.txt --voice <voice_id>

Produces takes/001.mp3 ... 108.mp3 and ganesha_scaffold.wav, with every name onset at
exactly 8000 + i*3500 ms. Reports any take that overruns its slot.
"""

import argparse, os, sys, requests
from pydub import AudioSegment

INTRO_MS, SLOT_MS = 8000, 3500
VOICE_MAX_MS = 2800  # utterance must fit inside the slot with pause to spare

API = "https://api.elevenlabs.io/v1/text-to-speech/{vid}"


def synth(text, voice_id, key, out_path):
    r = requests.post(
        API.format(vid=voice_id),
        headers={"xi-api-key": key, "Content-Type": "application/json"},
        json={
            "text": text,
            "model_id": "eleven_multilingual_v2",
            "voice_settings": {
                "stability": 0.85,        # high: we want flat, even, non-dramatic delivery
                "similarity_boost": 0.75,
                "style": 0.0,             # zero: no emotive interpretation of sacred text
                "use_speaker_boost": True,
            },
        },
        timeout=60,
    )
    r.raise_for_status()
    open(out_path, "wb").write(r.content)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--names", default="names_dev.txt")
    ap.add_argument("--voice", required=True, help="ElevenLabs voice_id (pick a calm male Hindi voice)")
    ap.add_argument("--out", default="ganesha_scaffold.wav")
    ap.add_argument("--drone", help="optional tanpura loop wav, mixed at -22dB")
    a = ap.parse_args()

    key = os.environ.get("ELEVENLABS_API_KEY")
    if not key:
        sys.exit("set ELEVENLABS_API_KEY")

    names = [l.strip() for l in open(a.names, encoding="utf-8") if l.strip()]
    if len(names) != 108:
        sys.exit(f"expected 108 names, got {len(names)}")

    os.makedirs("takes", exist_ok=True)
    track = AudioSegment.silent(duration=INTRO_MS + 108 * SLOT_MS)
    overruns = []

    for i, name in enumerate(names):
        p = f"takes/{i+1:03d}.mp3"
        if not os.path.exists(p):
            synth(name, a.voice, key, p)
            print(f"  {i+1:3d}/108  {name}")
        clip = AudioSegment.from_file(p)
        if len(clip) > VOICE_MAX_MS:
            overruns.append((i + 1, name, len(clip)))
        track = track.overlay(clip, position=INTRO_MS + i * SLOT_MS)

    if a.drone:
        d = AudioSegment.from_file(a.drone) - 22
        while len(d) < len(track):
            d += d
        track = track.overlay(d[: len(track)])

    track = track.fade_in(2000).fade_out(3000)
    track.export(a.out, format="wav")
    print(f"\nwrote {a.out}  ({len(track)} ms, expected {INTRO_MS + 108*SLOT_MS})")

    if overruns:
        print(f"\n{len(overruns)} take(s) exceed the {VOICE_MAX_MS} ms budget:")
        for idx, nm, ln in overruns:
            print(f"  {idx:3d}  {ln} ms  {nm}")
        print(f"\nRaise SLOT_MS to {((max(o[2] for o in overruns) + 700)//500 + 1) * 500} "
              f"and regenerate NaamaTimings.kt, or re-synth these at a faster rate.")
    else:
        print("All takes fit the slot. Timings formula holds.")


if __name__ == "__main__":
    main()
