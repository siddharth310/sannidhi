# M1 — Sankalpam (solo): finish it

Close the gaps in My japam and the player so the module is fully usable. P3a is built; this prompt finishes it.
Report back with every acceptance check as pass/fail and the test workbook `tests/M1-sankalpam.xlsx` filled in.

READ FIRST:
- `docs/design/sankalpam-design.md` (v7) and `docs/design/v7/png/s3a.png`, `s3b.png`, `s3c.png`
- `docs/stories/sankalpam-user-stories.md` (v7) US-01.1 to US-06.1
- `content/texts/ganesha.ashtottara.yaml`, `content/texts/ganesha.mula-mantra.yaml`, `content/mantras/ganesha.md`
- `content/source/ganesha_ashtottara.json` (the 108 names; `ganesha_ashtottara.md` is the same list as a review sheet)
- `tools/import_ashtottara.py`, `tools/make_sacred_draft_audio.py`

STEP 1 — Import the aṣṭottaram names (content, not code)
- Run `python tools/import_ashtottara.py content/source/ganesha_ashtottara.json content/texts/ganesha.ashtottara.yaml`.
  Never type, generate or "fix" a name yourself. The names come only from that file. Each `sa` is the full archana
  line (`ॐ … नमः`); the player shows it as it is, in the chosen script.
- 14 lines carry a `flag` for the purohit: 9 CORRECTED, 2 VARIANT, 3 SCRIPT. The importer keeps them; the build
  report lists them. Do not remove a flag in code; flags go only when the source JSON is updated after review.
- `python tools/build_content.py --check` passes with 108 names; `ganesha.ashtottara` stays `status: draft`
  until `reviewed_by` is filled.

STEP 2 — Audio timings from sidecar files
- The build reads `content/audio/<text-id>.json` when present:
  texts `{status, names_ms[], length_ms}`, mantras `{status, words_ms[], length_ms, words_estimated}`.
  It copies `.m4a` + timings into the pack (`texts/<id>.json` → `audio: {file, namesMs | wordsMs, draft}`).
- `names_ms` count must equal the number of names; `words_ms` count must equal the words in `sa`; values rising
  and below `length_ms`; otherwise the build fails.
- **Draft audio** (`status: draft`) is included in dev and internal builds only. Release builds drop it, and the
  player falls back exactly as if there were no recording (Done for texts, the ring for mantras).
- Remove the silent placeholder for the mūla mantra.

STEP 3 — Player checks against the design
- Text with audio: the lit row is the last name whose start ≤ position; follows at 1×, 1.25×, 1.5×.
- Mantra with audio: loops; each finished loop adds one; current word lit from `wordsMs`.
- Script switch keeps the same index lit; never seeks or restarts audio.
- Lock screen shows name, count line and Play/Pause only; playback and counting continue while locked.
- Audio focus loss and headphones out pause; never auto-resume.

STEP 4 — Small finishes
- Home sādhana bar: first active sankalpam title and "and N more"; opens Sankalpam.
- "Goal reached · … · keep chanting" appears once per japam, the first time only (persist the flag).
- Completed japam opens S7 read-only with Start again.
- About sheet "This month" lists only days with chanting, newest first, "added by hand" tags.

DO NOT:
- Type or alter any sacred name or mantra text.
- Ship draft audio in a release build.
- Count a recitation that did not finish.

ACCEPTANCE:
1. `build_content.py --check` passes with 108 aṣṭottaram names; the 14 flagged lines are listed in the build report.
2. With the draft aṣṭottaram audio: names light in step at all three speeds; switching to IAST at name 40 keeps 40 lit.
3. Aṣṭottaram played to the end adds 1 reading; stopping at name 107 adds nothing.
4. Mūla mantra with draft audio: speaks, loops, ring counts each finished repetition; touching the ring while
   playing adds nothing, while paused adds one.
5. Release build: no draft audio inside the APK; the aṣṭottaram shows Done; the mantra shows the ring only.
6. Lock screen 5 minutes during the mantra: every finished repetition counted.
7. All of `tests/M1-sankalpam.xlsx` passes.
