# M4 — Calendar: finish it (verify a full year, observance pages, rāśi notes, good times)

Report back with every acceptance check, the exported CSV, and `tests/M4-calendar.xlsx`.

READ FIRST:
- `docs/app-flow.md` §5, `docs/content-architecture.md` §6.7, §6.8
- `sannidhi/calendar-good-times-user-stories.md` (good times and nakṣatra finder)
- `content/calendar/observances/*.md`, `content/rashi/*.md`, `tools/panchanga.py`

STEP 1 — Year export for checking against the TTD panchāṅgam
- `python tools/build_content.py --export-calendar <city> <year>` writes `build/check/<city>-<year>.csv` with one
  row per day: date, weekday, sunrise, sunset, tithi at sunrise (+ end time), paksha, māsam, nakṣatra (+ end
  time), moon rāśi, observances, good-time windows.
- Export Hyderabad and Vijayawada for 2026–27 (Parābhava) and send me the CSVs; I compare them with the TTD
  panchāṅgam in `tests/calendar-check.xlsx`. Fixes go into `content/calendar/overrides.csv`, never into code.

STEP 2 — Observance page (route `calendar/obs/{obsId}`)
- Sections from the content: What makes the day special · What families do · History · Regional notes, each
  only when present. Then links: its puja, story, nāmalu (Sankalpam start), naivedyam.
- Next date for the person's city, with tithi.

STEP 3 — Rāśi notes
- The build fails if any of the 12 profiles or 12 gochara notes is missing te, hi or en, or contains a word from
  the never-list (fear, loss, illness, death, money, remedy to buy; the good-times words).
- Today's note only; other rāśis by chips; "I don't know" shows the chips.

STEP 4 — Good times and nakṣatra finder (already built; verify)
- Never shows rāhu kālam, yamagaṇḍam, durmuhūrtam, varjyam, avoid, bad or inauspicious; no red.
- A day with no good window left hides the section.
- Finder: birth details discarded after the result; near a boundary both answers are shown with the note.

DO NOT:
- Hard-code any date. Show past or future rāśi notes.

ACCEPTANCE:
1. CSVs exported for both cities; Ugadi 19 Mar 2026, Vināyaka Chavithi 14 Sep 2026 and 11 Oct 2026 = Āśvayuja
   śukla pratipat appear correctly.
2. An override row moves an observance by one day without a code change.
3. Vināyaka Chavithi page shows all four sections and the four links.
4. Removing one hi gochara note fails the build with the note's ID.
5. Good-times section never shows a never-list word in te, hi or en (string scan over the built strings).
6. All of `tests/M4-calendar.xlsx` passes.
