# P2a — Puja: god grid, god page, one-page puja, guided mode

Read `docs/app-flow.md` §4 and `docs/design-v6.md` §4. Data: `deities()`, `pujasFor()`, `puja(id)`, `mantra(id)`,
`samagriItem(id)`, `naivedyamItem(id)`, `observance(id)`, `day()`/`comingUp()` for dates, `imagePath()`.

## Do
1. **Puja home**: Coming-up festival card (reuse `ComingUpCard`), search field filtering gods and pujas by any
   language or IAST, god grid (2 columns, big cards).
2. **God page**: hero, intro, symbols row (bottom sheet with meaning), pujas list with next date + tithi (find the
   first calendar day ≥ today whose observances contain the puja's `observance`), hand-off rows to Stories and
   Sankalpam (`textsFor(deity)`).
3. **Puja page**, one `LazyColumn`, three sections from `stages` in order, **sticky stage bar** that scrolls to a
   section and highlights the one in view.
   - Step card per design §4.3. Show `note` as a small italic line when present.
   - Mantras: `SacredText` in the chosen view with the text-view chips; "Meaning ›" expands `meaning`. For `lines`
     render each line on its own row. If `repeat` is set show "× n".
   - **Saṅkalpam**: replace `{samvatsara} {ayana} {ritu} {masa} {paksha} {tithi} {vasara} {nakshatra}` from today's
     calendar day + master names; `{gotra} {name}` from settings; `{desha_clause}` from the state's
     `desha_clause`. Any value missing → remove that word and its following word-suffix ("गोत्रः", "नामधेयः")
     cleanly. ayana: uttarāyaṇa when the sun is from Makara to Mithuna (use masa 10–3 as approximation), else
     dakṣiṇāyana; ritu from `master.masams[masa].ritu`. Show a small "edit details" link to Profile.
   - **Samagri**: checklist (ticks in DataStore keyed by puja id + date; cleared next day). Alternatives as
     "Instead: …". The line "Whatever you offer with love is enough." under the list.
   - **Text step**: Read / Listen buttons → `sankalpam/text/{textId}?return=puja` (Sankalpam screens come in P2c;
     link to a stub now).
   - **Naivedyam**: cards with image, why, regional-form chips; the user's state's form shown first.
4. **Guided mode**: full-screen pager over all steps of all stages, keep screen on, big type, stage name on top,
   Previous / Next buttons always visible.

## Don't
- No timers, no "complete" checkmarks on steps, nothing logged to practice.
- Don't reorder steps or merge stages.

## Acceptance checks
1. Puja → Gaṇeśa → Vināyaka Chavithi shows Pre 8, During 16, Post 8 steps in file order.
2. Stage bar: tapping During scrolls to it; scrolling into Post selects Post.
3. Text view chips switch every mantra between Telugu script, Devanagari and IAST; choice persists.
4. Saṅkalpam with name and gotra empty reads naturally (no "{gotra}", no blank words); with them set, both appear.
5. Samagri tick survives leaving and returning the same day; alternatives visible for dūrvā, kalaśa, panchāmṛtam.
6. Guided mode shows 32 pages, screen stays on, works in te/hi/en and at 200% font.
7. Missing step images show the Gaṇeśa symbol fallback, never a grey box.
