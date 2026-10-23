# P2b — Calendar: today panel, rāśi note, māsam view, observance page

Read `docs/app-flow.md` §5 and `docs/design-v6.md` §5. Data: `calendarYear()`, `day()`, `master()`,
`observances()`, `rashi()`, `rashiNote()`.

## Do
1. **Today panel**: date, samvatsara (from the calendar file's `samvatsara.n` → `master.samvatsaras`), māsam with
   adhika, tithi with end time ("till 9:32 pm"), nakṣatra with end time, sunrise / sunset.
2. **Today for you**: `rashiNote(settings.rashi, today, lang)`; show rāśi name, profile line once (from
   `rashi().profiles["rashi.<id>"]`), the note, and "Chant <text title> ›" when `suggestText` is set (→ Sankalpam).
   Rāśi chips (12) let the user read any rāśi's note **for today only**. No rāśi saved → prompt + chips; choosing a
   chip here does not save it unless the user taps "Save as my rāśi".
3. **Māsam view**: civil-month grid; each cell: date number, moon glyph from `moon_pct` + paksha, observance dots
   (max 2). Swipe for months, but only within the carried samvatsaras (`manifest().samvatsaras`); beyond that show
   "Dates for the next year arrive with the Ugadi update."
4. **Day bubble** (bottom sheet): tithi, nakṣatra, moon %, observances list → observance page.
5. **Observance page**: image, title, date + tithi, sections (special, families_do, history, regional_notes —
   skip empty), action rows for each link type (pujas, stories, texts, naivedyam).

## Don't
- No rāśi note for any date other than today. No "good / bad day" labels or colours. No tithi computed in Kotlin.

## Acceptance checks
1. Hyderabad, 11 Oct 2026: Āśvayuja, śukla pratipat till 9:32 pm, nakṣatra 14 (Chitrā) till 10:32 pm,
   sunrise 06:07, sunset 17:57, samvatsara Parābhava.
2. May–June 2026 shows "Adhika Jyeṣṭha" for 17 May–15 Jun.
3. 14 Sep 2026 cell has a dot; its bubble lists Vināyaka Chavithi → observance page → Puja opens the puja.
4. Rāśi Meṣa (1) on a day with moon_rashi 8 shows the house-8 note (gentle tone) with "Chant … ›".
5. Navigating past 26 Mar 2028 (end of the last carried samvatsara) shows the Ugadi-update message, not a crash.
6. All labels switch with language; tithi names in the chosen text view.
