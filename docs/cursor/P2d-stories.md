# P2d — Stories: home and the beat player

Read `docs/app-flow.md` §7 and `docs/design-v6.md` §7. Data: `storyIndex()`, `storiesFor()`, `story(id)`,
`deities()`, `day()` + `observance()` for "For today", `imagePath()`.

## Do
1. **Stories home**: search, god grid (reuse Puja's component), "For today" card when today's observances link a
   story.
2. **God → stories**: cover, title, minutes, ages label ("4+ to 12").
3. **Player**: one beat per page, image top (4:3), text below. Start sheet "Little ones (4+)" / "Older children"
   (remember per story). Older shows `more` under `short`. Language chips te / hi / en (default app language).
   Tap right two-thirds / swipe → next; left third → previous; visible ‹ › buttons too.
4. **Touch moments** from `touch.effect` with the animations in design §7, applied to the whole image until art
   ships separate layers. A pulsing hand hint until first tap on that beat.
5. **Last beat**: takeaway large; parent links "Do the puja ›" (first puja linked by the story's observance) and
   "Chant the names ›".
6. Show `source` in small text on the cover page ("Retold from Śiva Purāṇa").

## Don't
- No narration audio, no autoplay, no timers, no scores or rewards.

## Acceptance checks
1. Gaṇeśa shows 3 stories; "How Ganesha Got His Elephant Head" has 6 beats, "Ganesha and the Moon" 7.
2. Little ones mode shows only short lines; Older children adds the longer lines where present.
3. Moon story beat 5 (hide) hides the image on tap and brings it back on second tap.
4. Switching language mid-story keeps the same beat.
5. On 14 Sep 2026 Stories home shows a "For today" card.
6. Missing beat images show the deity symbol on the Stories tint, not a grey box.
