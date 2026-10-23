# P3a — Sankalpam v7: home, My japam, player, counting (offline, no groups yet)

Build the new Sankalpam module from scratch. The old Sankalpam code was removed; do not restore or reuse any of it.
Groups come in P3b. Report back at the end with every acceptance check as pass/fail.

READ FIRST:
- `AGENTS.md`
- `docs/design/v7/png/*.png` — reference screens; match them
- `docs/design/sankalpam-design.md` (v7) — all of it; the rule at the top: same components as Puja and Calendar, tulasi green as the module accent
- `docs/stories/sankalpam-user-stories.md` (v7) — US-01.1 to US-06.1
- `docs/design-v6.md` — TopBar, cards, StageBar, sheets, segmented, buttons, god grid and search in Puja home
- `docs/content-architecture.md` §6.3 (mantras) and §6.4 (texts)

STEP 1 — Content (recordings and timings)
- Texts: support `audio: { file, intro_ms, slot_ms }` and `audio: { file, names_ms: [...] }`.
- Mantras: add optional `audio: { file, length_ms, words_ms: [...] }` (one repetition, looped by the app).
- `tools/build_content.py`: validate (file exists; `names_ms` count = names; `words_ms` count = words in `sa`;
  values rising and inside the file length), copy files to the output, update `content/_schema/`.
- Add `ContentRepository.textsAndMantrasFor(deity)` returning both kinds with a `hasRecording` flag.

STEP 2 — Data (Room)
- `Sankalpam` and `PracticeEntry` exactly as design §7 (kind `japam` only in this prompt; keep `group` in the enum).
- Totals and "This month" are queries over `PracticeEntry` by `sankalpamId`. No other counter tables.
- On every app start: any active japam whose last day is before today becomes `completed` (completedAt = last day).
- Home sādhana bar reads active sankalpams from this table.

STEP 3 — S1 Sankalpam home (`sankalpam?tab=`)
- Three tabs in the Puja StageBar style: My japam · Groups · Completed. Remember the last tab.
- My japam and Completed per design S1. Groups tab: show only the empty state with the Join card and the two
  buttons disabled with the line "Groups are coming soon" (P3b replaces this).
- A card opens `sankalpam/play/{id}` directly. A completed card opens `sankalpam/done/{id}`.

STEP 4 — S2 Start a japam (`sankalpam/new`)
- Step 1 reuses the Puja home god grid and search component as is (no festival card).
- Step 2 lists `textsAndMantrasFor(deity)` like the god page puja list.
- Step 3 form per design S2: goal prefilled 108 (text) or 10,008 (mantra), days 41, Starts chips including an
  observance in the next 30 days, optional reason. Start creates the japam and opens the player.

STEP 5 — S3 Player (`sankalpam/play/{id}`)
- Layout, variants and behaviour exactly as design S3 and §4 (LitRow, Ring, PlayerControls).
- Audio: Media3 ExoPlayer in a `MediaSessionService` (lock screen and notification: name, count line, Play/Pause
  only). Speed via `setPlaybackParameters` (1.0, 1.25, 1.5, pitch kept). Mantra: `REPEAT_MODE_ONE`.
- Highlight: every 50 ms while playing, read the player position (media time, so it stays right at any speed) and
  light the last name or word whose start ≤ position. Script change re-renders the same index; never seek.
- Counting:
  - Text: on playback end add one `PracticeEntry` (count 1, unit reading, source play).
  - Mantra: on each automatic loop transition add one repetition to the session counter; persist the running
    session count every repetition so nothing is lost if the app is killed; write the entry on leave or pause.
  - Cut-off recitations add nothing. Ring touch adds one only when not playing.
  - Text without recording: Done at the end adds one reading.
- Audio focus: pause on loss and on becoming noisy; never auto-resume.
- Goal reached: show the line once per session when the total first reaches the goal; never stop counting.
- Keep screen on while the player is in front.

STEP 6 — Sheets and S7
- S4 I chanted outside the app: today and up to 7 days back, never before the start date; source `hand`.
- S5 About for a japam: reason, goal and days, This month (only days with chanting), Change goal or days, Mark
  complete.
- S6 Change goal or days: no limits; a last day before today completes it tomorrow.
- S7 Completed view with Start again (prefilled S2 step 3).

DO NOT:
- Use any accent other than tulasi green for Sankalpam, or add a component not listed in design §4.
- Show reminders, streaks, gaps, "0 today" or red.
- Add seek, skip or any speed other than 1×, 1.25×, 1.5×.
- Count a recitation that did not finish.

ACCEPTANCE (from US-01.1 to US-06.1):
1. Sankalpam shows My japam · Groups · Completed in the Puja StageBar style, with tulasi green as the accent.
2. Start a japam: Puja's god grid and search → Gaṇeśa → mūla mantra → prefilled 10,008 · 41 days → Start →
   player opens; My japam shows "0 so far · of 10,008 · Day 1 of 41".
3. Aṣṭottaram plays with names lit in step at 1×, 1.25× and 1.5×; no name lit during the opening invocation.
4. Switch Telugu → IAST at the 40th name: same name lit, no break in audio.
5. Aṣṭottaram plays to the end → "Today 1 · Total 1 of 108". Stop before the end → nothing added.
6. Mantra: 3 repetitions → ring 3 of 108; at 108 → round 2. Pause mid-repetition after 40 and go Back → 40 saved.
7. Touching the ring while playing adds nothing; while paused adds one.
8. Lock the phone 5 minutes while the mantra plays → every finished repetition counted; lock screen shows name,
   count and Play/Pause.
9. A phone call pauses; it stays paused after the call.
10. Text without a recording: no Play or Speed; Done adds one reading.
11. I chanted outside the app: 3 for yesterday → total +3, About shows yesterday "added by hand"; 8 days back is
    not offered.
12. Total passes the goal → "Goal reached · … · keep chanting" once; counting continues (1,062 of 1,008).
13. Goal changed below the total → saved with no warning.
14. Last day yesterday → card under Completed; Start again prefills the same text, goal and days with Today.
15. Five days without chanting → no card shows gaps or streaks.
16. Airplane mode: everything in this prompt works.
