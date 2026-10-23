# Sannidhi v1 — complete bundle (24 Oct 2026)

## Use this

**`current-v1/`** is the latest pack. Don't copy it by hand: the intake prompt brings it in step by step.

- **First, once:** follow `V1-00-INTAKE-cursor-prompt.md`. Cursor unpacks this zip into `_incoming/`, copies
  in only the files that cannot change the app, and proves the screens look the same as before.
- **Then, every session:** paste `V1-MASTER-cursor-prompt.md` into Cursor, then write "Run M1." Cursor does one
  module, reports back and stops until you reply "M1 signed off".
- **Module prompts:** `current-v1/docs/cursor/` (M1–M8, plus P2d2, P3a and P3b).
- **108 names:** `current-v1/content/source/ganesha_ashtottara.json` is the file M1 imports.
  `ganesha_ashtottara.md` is the same list as a sheet for the purohit to check (14 flagged lines).
- **Roadmap:** `current-v1/docs/v1/ROADMAP.md`.
- **Release documents:** `current-v1/docs/v1/release/` (checklist, privacy policy, store listing, data safety).
- **Tests:** `current-v1/tests/` holds one workbook per module, plus `calendar-check.xlsx`.
- **Audio:** `current-v1/tools/sannidhi-audio.ipynb` runs in Google Colab.

## Earlier versions (reference only; `current-v1` replaces them)

| Folder | What it is | Status |
|---|---|---|
| `previous/sankalpam-v7/` | Sankalpam v7 rewrite: design (tulasi green), HTML screens, 14 user stories, P3a/P3b | Included in current-v1 |
| `previous/sankalpam-v6.2/` | Sankalpam v6.2 and the P2e chant-along prompt | Replaced by v7; do not run P2e |
| `previous/stories-audio-kit/` | Story narration scripts, Bulbul settings, music placeholders, P2d2 | Included in current-v1 |
| `previous/calendar-good-times/` | Good-times spreadsheet and the calendar user stories | Built into M4 |
| `previous/content-portal-ON-HOLD/` | Content portal spec and the C1–C4 prompts | On hold; do not run |

## Changes in current-v1 since the last pack

- `docs/cursor/V1-00-INTAKE.md` (M0) is new: it brings the files in without changing the app.
- M2 now starts by copying in the story, music and sound files that M0 holds back.
- `docs/cursor/V1-MASTER.md` is new: the module order, the rules for every module and the report format.
- `content/source/` is new: the 108 names, exactly as in the 14 Sep dataset, plus the review sheet.
- 14 lines are flagged for review, not 11: 9 corrected spellings, 2 recension variants and 3 ళ/ल script lines.
- `tools/make_sacred_draft_audio.py` now reads the names that the import step writes. It found none before this fix.
- The target audience is decided: adults, with Stories described as family content. This is applied across the
  checklist, privacy policy, data safety, store listing (en, te, hi), M7 and `tests/M7-release.xlsx`.
- M1, `tests/M1-sankalpam.xlsx` and the roadmap now point to `content/source/ganesha_ashtottara.json`.
