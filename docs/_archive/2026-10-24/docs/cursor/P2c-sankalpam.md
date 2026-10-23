# P2c — Sankalpam (solo): Nāmalu Read / Listen / Count, My sankalpams

Read `docs/app-flow.md` §6, §9 and `docs/design-v6.md` §6. Data: `textIndex()`, `textsFor()`, `text(id)`,
`mantra(id)`, `deities()`; Room entities in `practice/PracticeModels.kt`.

## Do
1. Room database with `PracticeEntry` and `Sankalpam` (as given), DAOs, `PracticeRepository`.
2. **Sankalpam home**: chips My sankalpams · Nāmalu · Discover (Discover disabled: "Coming soon").
3. **Nāmalu**: god grid → texts for that god → **Practise** screen with `Read · Listen · Count` and a
   "Counts for" chip (None or one of my active sankalpams for this text).
   - Read: names list (`names`), or the mantra text for `type: mantra`. **Done** at the end → entry
     `mode=read, count=1, unit=reading`.
   - Listen: Media3 player on `audio.file` if present; highlight name i at `intro_ms + i*slot_ms`. Reaching the end
     → `mode=listen, count=1`. No audio file → hide Listen.
   - Count: tap target, haptic, ring to `round` (default 108); leaving the screen with count > 0 saves
     `mode=count, count=taps, unit=repetition`. No "reset" without confirm.
   - Text with no names yet (aṣṭottaram before import): show title and "Names coming soon"; Count still works.
4. **+ New sankalpam**: text → Just me (group option visible but disabled "Groups coming soon") → optional target
   and days → optional intent → Begin. Saves `Sankalpam(kind=solo)`.
5. **My sankalpams**: cards with total so far (sum of entries with that sankalpamId), optional "of target",
   "day N of M" when days set. Tap → Practise with Counts for preselected.
6. **Manual add** (from a sankalpam card menu): count + date up to 7 days back, `addedByHand=true`.
7. Home **My sādhana bar** now reads active sankalpams: "My sādhana · <first title> and n more · Open" → My sankalpams.
8. Puja text-step links (`?return=puja`) return to the puja after Done / end of Listen.

## Don't
- No daily target, no "today" count, no streaks, no reminders, no notifications, no red.
- Don't upload anything. No Firebase in this phase.

## Acceptance checks
1. Gaṇeśa → mūla mantra → Count 108 taps → leave → My practice total shows 108.
2. Create "Just me" sankalpam for mūla mantra, target 10,000 → Count with Counts for set → card shows the new total.
3. Read Done adds exactly 1 reading; backing out before Done adds nothing.
4. Home sādhana bar shows the sankalpam and opens My sankalpams.
5. Manual add 8 days back is not allowed; 7 days back works and shows "added by hand".
6. Nowhere in the module are the words in `docs/design-v6.md` §11.
