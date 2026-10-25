# Audio

Audio is made outside this repo, on Sarvam’s website, by the person who keeps the content.

Nothing in the repo generates a recording. The narration notes and the old audio tools live in
`docs/_archive/2026-10-24/`. Do not run them, and do not add a new generator.

The files arrive at the end of v1, in `_incoming/audio-from-sarvam/`. Do not wait for them, and do not
invent a stand-in. Until a recording is in the pack, a text shows Done and a mantra shows the ring.

Bulbul audio, including the chants, ships in a release build once it is marked `reviewed`. A release
build still ships only content marked `reviewed` or `published`.

Vināyaka Chavithi paste text:

    python3 tools/export_sarvam_text.py ganesha.puja.vinayaka-chavithi

That writes `_incoming/SARVAM-ONLINE/5-puja-vinayaka-chavithi.md` and `5-checklist.csv`. When the wav
files are in `_incoming/audio-from-sarvam/`:

    python3 tools/prepare_audio.py --puja ganesha.puja.vinayaka-chavithi

Sentence and word starts are estimated. List name starts are measured from the pauses, and a mismatch
writes no timings. Every timing file is `draft` until it is marked reviewed.
