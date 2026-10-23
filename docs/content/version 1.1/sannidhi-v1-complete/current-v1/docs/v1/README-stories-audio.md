# Stories audio kit — 23 Oct 2026

Unzip at the repo root. Everything for the three Gaṇeśa stories with narration, music and touch sounds.

| Path | What |
|---|---|
| `content/stories/ganesha/elephant-head.md`, `moon.md`, `mango.md` | The stories, one telling each (short and more joined, words unchanged), with music, narration and touch sounds wired in. Replace the old files. |
| `narration/NARRATION-GUIDE.md` | Voice, tone, pace, pitch, pronunciation, steps and checklist for Sarvam Bulbul |
| `narration/bulbul-settings.json` | Model, voice per language, pace, temperature, pauses |
| `narration/scripts/*.md` | The exact lines Bulbul reads, per story and language (9 files) |
| `narration/pronunciations.csv` | Names to listen for, with respellings |
| `tools/make_narration.py` | Generates all narration files with sentence timings (needs your Sarvam key) |
| `content/music/` | Placeholder beds (full and low) for each story, licence note, `MUSIC-BRIEF.md` for a musician |
| `content/sfx/` | Placeholder touch sounds: glow, bounce, wiggle, appear, hide |
| `tools/make_placeholder_music.py` | How the placeholders were made |
| `docs/stories-audio.md` | Player behaviour and story format |
| `docs/cursor/P2d2-stories-audio.md` | Cursor prompt to build it |

Order: 1) audition voices and generate narration (NARRATION-GUIDE §3), 2) run P2d2 in Cursor, 3) brief a
musician with MUSIC-BRIEF.md and swap in the real music when ready.
