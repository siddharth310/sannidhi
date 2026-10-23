# V1 intake report

Compared `_incoming/sannidhi-v1-complete/current-v1/` with the repo at tag `pre-v1-intake`.

## Counts

- NEW: 82
- SAME: 0
- DIFFERENT: 3

## DIFFERENT (not copied)

These live under `content/stories/` and stay in `_incoming/` for M2.

- `content/stories/ganesha/elephant-head.md` — pack adds `music` and `narration` front-matter, adds a `sound` on the touch target, and drops the `### short` sections still in the repo (incoming 6760 bytes, repo 6402).
- `content/stories/ganesha/mango.md` — same kind of change: music, narration, touch sound, and no `### short` sections (incoming 5359 bytes, repo 5239).
- `content/stories/ganesha/moon.md` — same kind of change: music, narration, touch sounds, and no `### short` sections (incoming 6080 bytes, repo 5841).

## Copied (all NEW)

- `START-HERE.md`
- `docs/**` from the pack (cursor prompts M1–M8, P2d2, P3a, P3b, V1-00, V1-MASTER; design; stories; v1 roadmap and release notes)
- `tests/**` (M1–M7 workbooks and `calendar-check.xlsx`)
- `content/source/ganesha_ashtottara.json` and `content/source/ganesha_ashtottara.md`
- `narration/**` (guide, Bulbul settings, pronunciations, nine story scripts)
- `tools/make_sacred_draft_audio.py`, `tools/make_narration.py`, `tools/make_placeholder_music.py`, `tools/sannidhi-audio.ipynb`

68 files copied. None overwrote an existing file.

## Held back in `_incoming/`

- `content/stories/**` (the three DIFFERENT story files)
- `content/music/**` (brief, licence, six placeholder tracks)
- `content/sfx/**` (licence and five placeholder effects)

17 files held. Nothing under `previous/` was copied. P2e and C1–C4 were not run.

## Archived

- `docs/cursor/P2c-sankalpam.md` → `docs/_archive/2026-10-24/docs/cursor/P2c-sankalpam.md`
- No `docs/cursor/P2e*` file was in the repo, so nothing else was archived.

## Ignored

`.gitignore` now ignores `_incoming/` and `*.zip`. `sannidhi-v1-complete.zip` was already committed on `pre-v1-intake`, so it stays tracked. The ignore rule covers new zip files only.

## Step 5

- Content check matches the baseline: 0 errors, 3 warnings (ashtottara names not imported, `ganesha.ashtottara.mp3` missing, `ganesha.mula-mantra.mp3` missing). `content/source/` added no new warning.
- Debug build passed.
- Home: same. Puja: same. Calendar: same. Sankalpam: same. Stories: same. Compared English, Hyderabad, debug date 14 Sep, ignoring only the status-bar clock.
- Sankalpam still shows the Groups placeholder. The 108 names were not imported.

`git diff --stat pre-v1-intake` after these copies lists only `.gitignore`, `START-HERE.md`, `docs/`, `tests/`, `content/source/`, `narration/`, and the four audio tool files. No app, resource, asset, Gradle, or manifest file changed.
