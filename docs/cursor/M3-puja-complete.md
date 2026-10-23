# M3 — Puja: finish it (Naivedyam page, images, guided mode, review gates)

Report back with every acceptance check and `tests/M3-puja.xlsx`.

READ FIRST:
- `docs/app-flow.md` §4, `docs/design-v6.md` §4, `docs/content-architecture.md` §6.2, §6.5, §6.6
- `content/pujas/ganesha/vinayaka-chavithi.md`, `content/naivedyam/modaka.md`, `content/naivedyam/phala.md`,
  `content/samagri/items.csv`

STEP 1 — Naivedyam page (route `puja/naivedyam/{nvId}`), replacing the placeholder
- Opened from the Post section's naivedyam cards and from an observance page.
- Top: image (ImageOrFallback), name in the person's own region's form first (from Profile state), e.g. Undrallu
  for AP/TS, Modak for MH; the other regional forms follow as equal cards: name, region, one-line note.
  Never label one form "original" or "real".
- **Why this offering** — the `why` text in the app language.
- **Offered on** — linked observances and pujas.
- Line at the end: "Recipes differ in every home. Ask an elder, or search for '<name>'." The search button opens
  the browser only when online; hidden offline. No recipes inside the app.

STEP 2 — Puja page checks
- Pujas filtered by the person's state (`regions`), as built.
- Samagri: alternatives shown under each item ("No durva? Akshata is fine."); ticks saved per puja on the phone;
  a "Clear ticks" text button. Never a warning for unticked items.
- Each step's mantra uses SacredText with the text view switch (te script · संस्कृतम् · IAST).
- A step with a mantra and repeat count shows "Chant 21 times" and a small counter inside the step (touch adds
  one, shows "12 of 21"). It is not saved and resets when the person leaves the puja: Puja makes no practice entry.

STEP 3 — Guided mode
- Full screen, one step at a time, big text, screen stays on, swipe and ← → buttons, step N of M.
- Remembers the last step per puja for 12 hours ("Continue from step 9?").
- Leaving guided mode returns to the same step on the puja page.

STEP 4 — Images and review gates
- Every image goes through ImageOrFallback; missing images listed in `content_status.csv`.
- Release builds include only items with `status: reviewed` or `published`. Show me the list of puja steps,
  mantras and naivedyam still in `draft` in the build report so I can get them reviewed.

DO NOT:
- Save anything from Puja as practice. Show recipes or quantities of food. Rank regional forms.

ACCEPTANCE:
1. Profile state Telangana: the modaka page leads with Undrallu; Maharashtra leads with Modak; all 5 forms shown.
2. Naivedyam page works offline; the search button is hidden offline.
3. Guided mode: step 9 of N, leave the app, return within 12 hours → "Continue from step 9?".
4. Samagri ticks survive an app restart; Clear ticks empties them.
5. A 21-repeat step: the step counter reaches "21 of 21"; leaving the puja resets it; My japam is unchanged.
6. The build report lists every draft item; a release build excludes them.
7. All of `tests/M3-puja.xlsx` passes.
