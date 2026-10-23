# M2 — Stories: finish it (one telling, narration, music, touch sounds, pictures)

Run `docs/cursor/P2d2-stories-audio.md` (from stories-audio-kit) first; this prompt adds what is needed to call
the module done. Report back with every acceptance check and `tests/M2-stories.xlsx`.

READ FIRST:
- `docs/stories-audio.md`, `docs/cursor/P2d2-stories-audio.md`
- `content/stories/ganesha/*.md` (one telling), `content/audio/stories/**` (narration after you generate it)
- `content/images/manifest.csv` (story images)

STEP 0 — Bring in the held story files (M0 left them in `_incoming/`)
- From `_incoming/sannidhi-v1-complete/current-v1/`, copy `content/stories/**`, `content/music/**`, `content/sfx/**`
  into the repo. Before overwriting a file, move the old one to `docs/_archive/2026-10-24/<same path>`.
- Run `python tools/build_content.py --check`. Report anything new before going on.

STEP 1 — P2d2 in full (narration, music, touch sounds, Read to me, Music switches).

STEP 2 — Pictures
- Every beat uses `ImageOrFallback`: until an illustration exists, show the deity symbol on the story's soft
  colour, never a broken image or empty box.
- The build lists missing story images in `content_status.csv` with their brief from the manifest.

STEP 3 — Story list and ending
- Stories home: god grid; a god's stories as cards: cover, title, minutes. A "For today" card when today's
  observance links a story.
- Last beat: closing music; buttons "More stories of Gaṇeśa" and the related puja; no score, no "well done".

STEP 4 — Narration status
- A story whose narration files are missing for the current language hides Read to me for that language.
- Release builds require narration in te, hi and en for every published story (build error otherwise).

DO NOT:
- Change any story text. Run any voice model on the phone.

ACCEPTANCE:
1. All P2d2 acceptance checks pass.
2. Each of the 3 stories plays start to end in te, hi and en with narration, music and touch sounds, offline.
3. Missing illustrations show the symbol fallback; `content_status.csv` lists them.
4. On Vināyaka Chavithi (debug date 14 Sep 2026) the Stories home shows the "For today" card.
5. All of `tests/M2-stories.xlsx` passes.
