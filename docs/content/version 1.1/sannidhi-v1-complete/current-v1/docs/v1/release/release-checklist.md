# Release checklist — v1 (Gaṇeśa)

Tick each line before moving to production. Owners: **S** = Siddharth, **C** = Cursor, **R** = reviewer.

## Content (critical path: release builds ship only reviewed content)
- [ ] S/R — Purohit review of all Gaṇeśa mantras and the lines marked REVIEW; `reviewed_by` filled.
- [ ] S/R — Purohit review of the 11 CORRECTED aṣṭottaram names.
- [ ] S/R — Native read of all Telugu and Hindi (puja steps, naivedyam, observances, rāśi notes, stories).
- [ ] S — Human recordings: aṣṭottaram (with name timings) and mūla mantra (one repetition).
- [ ] S — Story narration approved in te, hi, en (Bulbul or human).
- [ ] S — Music: commissioned or licensed, licence files beside each track (placeholders are acceptable only if
      you are happy with them).
- [ ] S — Illustrations: P0 images at least (deity hero and card, puja steps, story beats).
- [ ] S/C — Calendar check sheet compared with the TTD panchāṅgam for Hyderabad and Vijayawada; overrides added.

## Build and quality
- [ ] C — M1–M5 test workbooks all pass on three phones.
- [ ] C — Release build excludes draft content and draft audio.
- [ ] C — 200% font, TalkBack, offline, monkey test, no network calls.
- [ ] S — 2–3 elders and one child tried the app; changes made.

## Store and legal
- [ ] S — App name checked on Play Store and App Store; trademark search in India (Class 9).
- [ ] S — Icon artwork; listing text read by native speakers; screenshots in te, hi, en.
- [ ] S — Privacy policy hosted at a stable URL (and in the app).
- [x] S — **Decision (24 Oct 2026): target audience is adults.** Play Console → Target audience: 18 and over only
      (no age group under 13). Stories are described as family content, enjoyed with a parent. Store text and
      screenshots must not present the app as made for children (no "for kids", no child-only imagery).
      Apple: do not choose the Kids category; age rating 4+ is fine.
- [ ] S — On submission day, re-read Google Play's Families policy and target-audience form; confirm nothing has
      changed that affects this decision.
- [ ] S — Play developer account; internal testing → closed testing → production.

## Groups (only if M6 ships in v1)
- [ ] S — Firebase project on Blaze with a budget alert; invite-link domain.
- [ ] S — Who reviews reports, and how fast.
- [ ] C — M6 workbook passes with two phones; account deletion works.
