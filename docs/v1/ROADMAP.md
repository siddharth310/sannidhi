# Sannidhi v1 — module-by-module completion (24 Oct 2026)

Goal: a fully working app for the Gaṇeśa slice, offline, in Telugu, Hindi and English, with reviewed content,
on Android first, then iPhone.

## Where we are

| Module | Built | Missing for "done" |
|---|---|---|
| Home | yes | Final pass (M5) |
| Puja | yes | Real Naivedyam page, guided-mode resume, review gates, images (M3) |
| Calendar | yes, with good times and nakṣatra finder | Year checked against TTD, fuller observance page, rāśi note checks (M4) |
| Sankalpam solo | yes | Aṣṭottaram names, recordings and timings (M1) |
| Stories | yes, text only | Narration, music, touch sounds, pictures (M2) |
| Sankalpam groups | disabled | All of it (M6) |
| Release | — | M7 |
| iPhone | — | M8 |

## Definition of done (every module)

1. Its Cursor prompt's acceptance checks pass.
2. Its test workbook in `tests/` passes on a real phone.
3. Works in airplane mode, in te, hi and en, and at 200% font size.
4. Content is real and reviewed; no placeholders in a release build.
5. Two elders (and a child, for Stories) have used it and their problems are fixed.
6. You sign it off; it is marked done in the project.

## Order and what each needs

| # | Module | Cursor prompt | Test workbook | Needs from you before or during |
|---|---|---|---|---|
| M1 | Sankalpam (solo) | `docs/cursor/M1-sankalpam-complete.md` | `tests/M1-sankalpam.xlsx` | Names are in `content/source/` (have them reviewed); Sarvam key for draft audio (Colab); a reciter for the release recordings |
| M2 | Stories | `docs/cursor/P2d2-stories-audio.md` then `M2-stories-complete.md` | `tests/M2-stories.xlsx` | Narration made and approved (Colab); illustrator for beat pictures |
| M3 | Puja | `docs/cursor/M3-puja-complete.md` | `tests/M3-puja.xlsx` | Purohit review list (the build prints it); images |
| M4 | Calendar | `docs/cursor/M4-calendar-complete.md` | `tests/M4-calendar.xlsx`, `tests/calendar-check.xlsx` | A printed TTD panchāṅgam to compare against |
| M5 | Home, Profile, quality | `docs/cursor/M5-home-profile.md` | `tests/M5-home-profile.xlsx` | Three test phones |
| M6 | Sankalpam groups | `docs/cursor/M6-sankalpam-groups.md` | `tests/M6-groups.xlsx` | Firebase project (Blaze, budget alert), invite-link domain, who reviews reports |
| M7 | Android release | `docs/cursor/M7-release-android.md` | `tests/M7-release.xlsx`, `docs/v1/release/*` | Name check, icon, Play account (audience decided: adults, Stories as family content) |
| M8 | iPhone | `docs/cursor/M8-iphone.md` | Same M1–M6 workbooks on iPhone | Apple Developer account; Mac or cloud build |

M6 can ship in v1 or in v1.1. Shipping solo first is safer: groups need a server, sign-in and moderation.

## Critical path (start these today, they take the longest)

Release builds ship only **reviewed** content, so these gate the launch, not the code:
1. **Purohit review**: all Gaṇeśa mantras, the REVIEW lines, the 14 flagged aṣṭottaram lines (review sheet:
   `content/source/ganesha_ashtottara.md`).
2. **Native read**: Telugu and Hindi across puja, naivedyam, observances, rāśi notes, stories.
3. **Recordings**: aṣṭottaram (with name timings) and mūla mantra by a person.
4. **Illustrations**: the P0 list in `content/images/manifest.csv`.
5. **Music**: brief a musician with `content/music/MUSIC-BRIEF.md`.

## Audio, without installing anything

`tools/sannidhi-audio.ipynb` — open in Google Colab, upload a zip of `content/`, `narration/`, `tools/`, paste
the Sarvam key, Run all. It makes the story narration and the draft aṣṭottaram and mūla mantra recordings
(drafts are for testing; release builds leave them out) and downloads them as one zip.

## Each module, every time

1. Run the module's Cursor prompt; Cursor reports each acceptance check.
2. You run the test workbook on a phone and fill the result column.
3. Elders try it; note what confused them; send me the notes and I write the fix prompt.
4. Sign off → I mark the module done in the project.
