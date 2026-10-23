# P1 — Foundation: theme, content layer, onboarding, Home hub

Read `AGENTS.md`, `docs/app-flow.md` §0–3, §8–9 and `docs/design-v6.md` §1–3, §8–10 first.

## Do
1. **Remove the bottom navigation** and every route only it used. Remove Home tiles Naamalu, Japam, Naivedyam.
2. **Theme** in `ui/theme/` from `docs/design-v6.md` §1 (colours light + dark, type with bundled fonts, shapes).
3. **Content layer**: add kotlinx.serialization and use `content/ContentRepository.kt` and `ContentModels.kt` as
   given (provide it as a singleton). Do not change their fields.
4. **Settings** in DataStore: `lang` (te/hi/en), `textView` (Script/Devanagari/Iast), `stateCode`, `cityId`, `name`,
   `rashi` (1–12 or null), `nakshatra`, `gotra`, `onboarded`. App UI language follows `lang` (per-app locale).
5. **Onboarding** per design §8 (4 steps, all skippable except language and city). City list from `master().cities`
   filtered by the chosen state, with search.
6. **Shared components** in `ui/common/`: `TopBar(title, onBack, onHome)`, `ModuleCard`, `ComingUpCard`,
   `SacredText(sacred, view, lang)`, `ImageOrFallback(imageId, fallbackSymbolId, tint)`.
7. **Home** per design §3:
   - Sky header ≤ 25% height, collapsing on scroll. Greeting by hour. Info line = date · `Labels.tithi` ·
     `Labels.masa` (only if not obvious, keep one line; drop māsa if it overflows) · city name. No clock.
   - Coming up card from `comingUp(cityId, today)`. Tap → `puja/{pujaId}` route (screen stub for now).
   - 2 × 2 module cards: Puja, Calendar, Sankalpam, Stories → their routes (stubs with TopBar for now).
   - My sādhana bar: reads active sankalpams (empty for now) → "Begin a sankalpam ›".
8. **Profile** from the name button (design §9), including "Delete my data" (clears DataStore + Room, confirm dialog).
9. Navigation: type-safe routes `home, onboarding, profile, puja, puja/god/{deity}, puja/{pujaId}, calendar,
   calendar/obs/{obsId}, sankalpam, sankalpam/text/{textId}, stories, stories/{storyId}`. All inner screens use
   `TopBar`; Home button pops to `home`.

## Don't
- Don't hard-code any tithi, festival, deity or date. The screenshot "Kṛṣṇa dvādaśī" on 11 Oct 2026 was wrong:
  that day is Āśvayuja śukla pratipat. Values must come from the calendar file.
- No bottom bar, no avatar or photo, no notifications permission.

## Acceptance checks (report each)
1. Fresh install → onboarding → Home. Kill and reopen → Home directly.
2. With city Hyderabad on 11 Oct 2026 the info line reads (en) "Sun 11 Oct · śukla pratipat · Āśvayuja · Hyderabad"
   or the agreed one-line equivalent; in te it uses Telugu script.
3. On 11 Sep 2026 Coming up shows Vināyaka Chavithi "In 3 days · Mon 14 Sep"; on 14 Sep it says "Today" and
   "Start the puja".
4. Sky header is ≤ 25% of screen height on a 6.1" phone and collapses on scroll.
5. No bottom bar anywhere. Every non-Home screen has Back and Home; Home returns to Home from 3 levels deep.
6. Switching language in Profile changes Home immediately; 200% font scale has no clipped text.
7. Delete my data → onboarding again.
