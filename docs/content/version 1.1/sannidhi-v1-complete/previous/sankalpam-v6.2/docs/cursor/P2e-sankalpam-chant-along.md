# P2e — Sankalpam: restore Chant along for mantras, one count for japa and sankalpam

Bring back guided mantra japa: the recording plays, the mantra is on screen with the current word lit, and every
recitation adds one. Make japa and sankalpam one count. Report back at the end with every acceptance check.

READ FIRST:
- `AGENTS.md`
- `docs/design/sankalpam-design.md` (v6.2): §1b, §2 routes, §4 MantraStage / Ring · compact / StopAfterChips /
  ChantPlayerBar, §5 screens 31, 31c, 32b, 34, §7, §8, §9, §10
- `docs/stories/sankalpam-user-stories.md` (v6.2): US-01.3, US-01.2, US-02.1
- `docs/content-architecture.md` §6.3 (mantras), `content/mantras/ganesha.md`

STEP 1 — Content
- Add the optional `audio` field to mantras exactly as design §10 (`file`, `length_ms`, `words_ms`).
- `tools/build_content.py`: validate it (file exists, `words_ms` count = words in `sa`, rising, < `length_ms`);
  copy the file to the output; add it to `content/_schema/` and `mantras.json`.
- For testing only, add a placeholder recording for `ganesha.mantra.mula` marked `status: draft` (dev builds only).
  The real recording comes from me.

STEP 2 — One count
- `PracticeEntry.mode` adds `chant` (Room migration; existing rows unchanged).
- Remove any separate japa counter, table or total if one exists. My practice, sankalpam totals and group totals
  all read `PracticeEntry` only.
- Nāmalu chip: a god's texts and its mantras with `use: japa` in one list. New sankalpam picker: the same list.

STEP 3 — Routes and modes
- Mantras move to `sankalpam/mantra/{mantraId}?for=&mode=chant|count&stop=&return=puja`; texts stay on
  `sankalpam/text/{textId}`. Update every caller (Puja steps, Sankalpam page, group page).
- Mantra with a recording: Segmented Chant along | Count, Chant along first and default. Without: Count only, no
  Segmented. Texts: Read · Listen · Count as today. Listen is never offered for a mantra.
- Puja step with `mantra` and `repeat` → Chant along with `stop={repeat}` and `return=puja`.

STEP 4 — Screen 31c Chant along
- Layout and components exactly as design §4 and §5 31c.
- Player: Media3 ExoPlayer, `REPEAT_MODE_ONE`. Count +1 on each automatic loop transition
  (`onPositionDiscontinuity` with `DISCONTINUITY_REASON_AUTO_TRANSITION`) and on the final end when Stop after is
  reached. A pause mid-recitation never counts; resuming continues the same recitation.
- Stop after: stop at the chosen count with a 600 ms volume fade; default `count_default`; puja `stop` preset.
- Word highlight: poll position every 50 ms while playing and highlight the word whose start ≤ position; without
  `words_ms`, pulse the line per recitation.
- Background and lock screen: a `MediaSessionService` with a notification showing the mantra name, "{n} of {target}"
  and Play/Pause only. Counting continues while locked.
- Audio focus: pause on loss (calls, other audio) and on becoming noisy (headphones out). Never auto-resume.
- Saving: Back or Finish and save writes one `PracticeEntry` (mode `chant`, count = recitations finished, Counts for
  as chosen). Nothing finished → nothing saved. Process death while playing: save what was finished
  (persist the running count every recitation).
- Keep screen on while 31c is in front.

STEP 5 — 32b Listen
- Limit to texts. Make sure playback also continues with the screen locked through the same MediaSessionService.

DO NOT:
- Count a recitation that did not play to its end.
- Show any number of repetitions "left" or "missed", or any reminder.
- Generate or synthesise mantra audio.
- Change the Count screen's behaviour.

ACCEPTANCE (all from US-01.3, US-01.2, US-02.1):
1. Gaṇeśa mūla mantra opens on Chant along; Segmented shows Chant along | Count; no Listen.
2. Play: the ring adds one at the end of each recitation and the current word is highlighted in step.
3. Stop after 108 with 107 done: the next recitation ends, the ring is full, audio fades and stops, "108 offered".
4. Stop after 11: stops at 11 with "11 offered" and Finish and save.
5. Keep going to 216: round 3, still counting.
6. Pause part-way through recitation 41 and go Back: My practice +40.
7. Back before the first recitation ends: nothing saved.
8. Lock the phone for 5 minutes while playing: every recitation finished while locked is counted; lock screen shows
   name, count and Play/Pause.
9. A phone call mid-recitation: paused after the call, count unchanged, cut-off recitation not added.
10. Switch text view to IAST while playing: no break in audio or count.
11. From a Vināyaka Chavithi puja step with repeat 21: stops at 21, +21 to My practice, back on the same step.
12. A mantra with no recording: Count only.
13. Chant along 108 on Just my practice, then Count 54 for a mūla mantra sankalpam: sankalpam shows 54, My practice
    shows 162 today.
14. Listen on the Gaṇeśa aṣṭottaram to the end adds one reading with no confirmation.
