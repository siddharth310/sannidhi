# Naamalu Design System v3 — "Sandhya × Mandir"

Source of truth for every Compose screen. Structure and flows live in `naamalu-app-flow/SKILL.md`;
illustration specs live in `illustrations.md`. Reference mockups: the v3 row of the Naamalu design canvas.

This file specifies **what each component contains**, not just colours. If a screen needs something not
defined here, stop and ask — do not invent a default (the first build failed exactly there).

---

## 0. Why the app looks like this (the problem statement)

| Who / what | Design consequence |
|---|---|
| **Elders**, especially women's japa groups keeping collective counts (mannat) | Body ≥ 16sp, labels in script + English, touch ≥ 48dp, icons readable without text, tap-anywhere counting, no small grey text for anything important |
| **People living outside India** keeping pujas alive | Sky follows the user's local clock; tithi and festival dates shown **for their city**; every samagri item and dish has an alternative |
| **Belonging, not growth** | No streak guilt, no badges, no ranks, no leaderboards, no "share to social". Group counts add up together |
| **Every origin, as it is** | Names shown in their own script with their region; no tradition presented as the default or original; region chosen by the user, never assumed |

Four tests for every screen: **for elders · for those far from home · belonging not growth · every origin as it is.**

---

## 1. Visual concept

- **Alto's Adventure calm** — a layered landscape (hills, a gopuram, coconut palms, birds) under a sky that
  changes with the user's local time. Content sits on an ivory **sheet** that overlaps the landscape.
- **Deepstash clarity** — one idea per card, big type, colour-coded cards, a swipeable card stack for guided steps.
- **Mandir base** — ivory ground, kumkum maroon brand, gold only as a fine accent.

---

## 2. Colour

### 2.1 Base
| Token | Hex | Use |
|---|---|---|
| `ivory` | `#FBF4E9` | App background, sheet |
| `paper` | `#FFFDF8` | Floating nav, raised surfaces |
| `ink` | `#2A1A14` | Primary text |
| `inkSoft` | `#4A3A30` | Body prose |
| `inkMuted` | `#6B5A4E` | Secondary text (never below 14sp) |
| `line` | `#E8DCC8` | Hairlines, outlined cards |
| `gold` | `#C79A3E` | Accent only — hairlines, progress, small marks. Never a fill, never text |

### 2.2 Ritual colours — each owns one module (always the same meaning)
| Name | Tint (bg) | Mid | Deep (text/icon) | Owns |
|---|---|---|---|---|
| **Kumkum** | `#F7E3E1` | `#B23A48` | `#7A1F2B` | Puja, brand, primary CTA |
| **Haldi** | `#FBEFD0` | `#E0A93B` | `#8A5A12` | Naivedyam |
| **Tulasi** | `#E4EEDC` | `#6E9A5B` | `#3E5A34` | Japam, "alternative" cards |
| **Neela** | `#DCEBEF` | `#2F7188` | `#1B3A4B` | Naamalu, mantras |
| **Kamala** | `#FBE4EA` | `#E08BA0` | `#8C3B55` | Sankalpa (group) |
| **Chandan** | `#F4EAD9` | `#C9A67A` | `#6B4A2E` | Calendar, samagri |

Rules: a tile/card uses its module **tint** as background and **deep** for text and icon shapes. A full fill
(deep as background, ivory text) is reserved for **one hero card per screen**.

### 2.3 Sky palettes (header only, by local time)
| Sky | Local time | Gradient top → bottom | Hill layers far → near → ground |
|---|---|---|---|
| **Uṣā** (dawn) | 04:30–09:00 | `#F2C2B6` → `#F7D6C3` → `#FAE7D6` → `#FBF4E9` | `#EDC1B5` `#D39A96` `#A5646F` `#7A1F2B` |
| **Madhyāhna** (day) | 09:00–16:30 | `#CFE3E6` → `#E9EEE4` → `#F4EFE2` → `#FBF4E9` | `#B7C7C0` `#7F9A8E` `#4E6B4A` `#3E5A34` |
| **Sandhyā** (dusk) | 16:30–19:30 | `#5A2340` → `#A3485A` → `#E39A73` → `#EDB488` | `#9A4455` `#6E2C43` `#461C31` `#2A1220` |
| **Rātri** (night) | 19:30–04:30 | `#141726` → `#1E2238` → `#2B2338` | `#262A3F` `#1B1D2C` `#12131D` + moon, stars |

Sun: dawn `#F9D0A4` (+35% halo), dusk `#F6C48C`. Birds only at dawn/day.

```kotlin
enum class Sky { USHA, MADHYAHNA, SANDHYA, RATRI }
fun skyFor(local: LocalTime): Sky = when {
    local >= LocalTime.of(4, 30) && local < LocalTime.of(9, 0)   -> Sky.USHA
    local >= LocalTime.of(9, 0)  && local < LocalTime.of(16, 30) -> Sky.MADHYAHNA
    local >= LocalTime.of(16, 30) && local < LocalTime.of(19, 30) -> Sky.SANDHYA
    else -> Sky.RATRI
}
```
Fixed skies per screen: Puja overview = Sandhyā · Guided puja = Rātri · Japa = Sandhyā · Sankalpa = Madhyāhna
tinted kamala · Dish = haldi-tinted day. Home uses `skyFor(now)`.

### 2.4 Dark theme (Rātri)
bg `#14110F`, surface `#1F1A16`, line `#332B24`, ink `#EFE6DA`, inkMuted `#A39686`, primary `#E8913A`.
Module tints become their deep colour at 18% over the dark surface; icons keep mid/deep colours.

### 2.5 Contrast
Text ≥ 4.5:1 (≥ 3:1 at 24sp+). Allowed pairs: deep-on-tint for all six modules; ivory on kumkum deep;
ink on haldi mid. Never place `inkMuted` on a tint.

---

## 3. Typography

Bundled, OFL: **Fraunces** (Latin display + IAST italic), **Noto Serif Telugu / Devanagari / Tamil / Kannada**
(script display and sacred text), **Instrument Sans** (UI + Latin body), **Noto Sans Telugu / Devanagari**
(script UI + prose). Remove Inter and Cormorant.

| Style | Font | sp / line | Use |
|---|---|---|---|
| `greeting` | Noto Serif (script) 700 | 34 / 42 | Home greeting |
| `heroTitle` | Noto Serif (script) 700 | 34–36 / 46 | Puja title, dish title |
| `cardTitle` | Noto Serif (script) 700 or Fraunces 650 | 20–24 / 30 | Hero and section cards |
| `tileLabel` | Noto Serif (script) 700 | 16 / 20 | Module tile script label |
| `tileSub` | Instrument Sans 500 | 12 / 16 | Module tile English label |
| `mantraLarge` | Noto Serif (script) 700 | 32 / 46 | Guided card line 1 |
| `mantraBody` | Noto Serif (script) 500 | 22 / 33 | Guided card line 2 |
| `iast` | Fraunces italic 400 | 16–17 / 24 | Every IAST line |
| `body` | Instrument Sans / Noto Sans 400 | 16 / 25 | Prose, steps |
| `meta` | Instrument Sans 500 | 14–15 / 20 | Secondary info |
| `eyebrow` | Instrument Sans 600, +1sp tracking, caps | 13 / 16 | "IN 3 DAYS" |
| `count` | Fraunces 500 | 84 / 84 | Japa counter |
| `nav` | Instrument Sans 600 | 12 / 14 | Bottom nav |

Script font follows content language: `te` → Telugu, `hi` → Devanagari, `iast` → Fraunces italic.
Regional names on the dish screen use their own script's Noto font regardless of content language.

---

## 4. Space, shape, elevation

- 4dp grid. Screen gutter **20dp**. Card gap **12–18dp**.
- Radius: tile 26 · card 26–28 · sheet top 30 · floating nav 28 · buttons fully rounded.
- Touch: **48dp minimum**, primary CTA **60dp**, round icon buttons **44–56dp**.
- Elevation: only the floating nav (`y 6, blur 24, rgba(90,24,36,.12)`). Everything else is flat colour.

---

## 5. Icons (custom, filled, two-tone)

Each icon is a real object. 48×48 viewBox, filled shapes, module colours.
Sizes: **52dp on tiles · 60dp in hero cards · 26dp in nav** (nav = mono `#7E6B5E`, active = kumkum deep).
Convert these SVGs to `ImageVector` in `ui/icons/NaamaluIcons.kt`.

| Icon | Module | SVG body (viewBox 0 0 48 48) |
|---|---|---|
| **Kalaśam** | Puja | `<path d="M24 17C18 15 13 11 9 9C15 8 21 11 24 17Z" fill="#6E9A5B"/><path d="M24 17C30 15 35 11 39 9C33 8 27 11 24 17Z" fill="#6E9A5B"/><path d="M24 17C21 12 21 7 24 3C27 7 27 12 24 17Z" fill="#3E5A34"/><circle cx="24" cy="13" r="5" fill="#8A5A3A"/><rect x="19" y="16" width="10" height="5" rx="1.5" fill="#7A1F2B"/><path d="M13 28C13 40 35 40 35 28C35 23 30 20 24 20C18 20 13 23 13 28Z" fill="#B23A48"/><path d="M14 27H34" stroke="#E0A93B" stroke-width="2.2"/>` |
| **Tāḷapatra** | Naamalu | `<rect x="7" y="13" width="34" height="7" rx="3.5" fill="#2F7188"/><rect x="7" y="21" width="34" height="7" rx="3.5" fill="#9CC5D1"/><rect x="7" y="29" width="34" height="7" rx="3.5" fill="#2F7188"/><path d="M24 9V41" stroke="#E0A93B" stroke-width="2"/><circle cx="24" cy="9" r="2.6" fill="#E0A93B"/>` |
| **Mala** | Japam | 13 circles r=2.6 `#6E9A5B` evenly on a circle (cx 24, cy 20, r 13) · `<circle cx="24" cy="35" r="3.8" fill="#3E5A34"/><path d="M22 38L19.5 46H28.5L26 38Z" fill="#E0A93B"/>` |
| **Araṭi āku** | Naivedyam | `<path d="M3 32C10 19 38 19 45 32C38 41 10 41 3 32Z" fill="#6E9A5B"/><path d="M5 32H43" stroke="#3E5A34" stroke-width="1.4"/><circle cx="18" cy="27" r="6" fill="#E0A93B"/><circle cx="30" cy="26" r="6" fill="#E0A93B"/><circle cx="24" cy="32" r="6" fill="#C98A25"/>` |
| **Tithi** | Calendar | `<rect x="7" y="10" width="34" height="32" rx="7" fill="#FFFFFF"/><path d="M7 17a7 7 0 0 1 7-7h20a7 7 0 0 1 7 7v3H7z" fill="#C9A67A"/><rect x="15" y="6" width="3.4" height="9" rx="1.7" fill="#6B4A2E"/><rect x="29.6" y="6" width="3.4" height="9" rx="1.7" fill="#6B4A2E"/><circle cx="24" cy="31" r="7.5" fill="#E0A93B"/><circle cx="27.5" cy="28.5" r="6.5" fill="#FFFFFF"/>` |
| **Diyas** | Sankalpa | `<path d="M13 29C10 25 13 21 13 18C13 21 16 25 13 29ZM24 26C21 22 24 17 24 14C24 17 27 22 24 26ZM35 29C32 25 35 21 35 18C35 21 38 25 35 29Z" fill="#E0A93B"/><path d="M5 31C5 38 21 38 21 31ZM27 31C27 38 43 38 43 31Z" fill="#8C3B55"/><path d="M16 28C16 36 32 36 32 28Z" fill="#E08BA0"/>` |
| **Lotus** | Saadhana (nav) | `<path d="M24 38C18 32 18 20 24 12C30 20 30 32 24 38Z"/><path d="M24 38C16 36 10 28 10 20C18 22 22 30 24 38ZM24 38C32 36 38 28 38 20C30 22 26 30 24 38Z" fill-opacity=".6"/>` |
| **Mandapam** | Setup | `<path d="M6 40h36" stroke="#3E5A34" stroke-width="2.5" stroke-linecap="round"/><path d="M10 40V22h28v18" fill="#E4EEDC" stroke="#3E5A34" stroke-width="2.2"/><path d="M6 23L24 8L42 23Z" fill="#6E9A5B"/><circle cx="24" cy="32" r="4" fill="#B23A48"/>` |
| **Basket** | Samagri | `<path d="M8 20h32l-3 20a3 3 0 0 1-3 2H14a3 3 0 0 1-3-2z" fill="#C9A67A"/><path d="M14 20q2-8 10-8q8 0 10 8" fill="none" stroke="#6B4A2E" stroke-width="2.2"/><circle cx="18" cy="17" r="4" fill="#E08BA0"/><circle cx="26" cy="15" r="4.5" fill="#E0A93B"/>` |
| **Rings** | Mantras | `<circle cx="24" cy="24" r="18" fill="#2F7188"/><circle cx="24" cy="24" r="12" fill="none" stroke="#9CC5D1" stroke-width="2"/><circle cx="24" cy="24" r="6" fill="none" stroke="#9CC5D1" stroke-width="2"/><circle cx="24" cy="24" r="2.4" fill="#E0A93B"/>` |
| **Scroll** | Katha | `<rect x="12" y="10" width="24" height="28" fill="#FFF7F9"/><rect x="8" y="6" width="32" height="7" rx="3.5" fill="#8C3B55"/><rect x="8" y="35" width="32" height="7" rx="3.5" fill="#8C3B55"/><path d="M16 19h16M16 24h16M16 29h10" stroke="#E08BA0" stroke-width="2" stroke-linecap="round"/>` |

Home (nav, 24 viewBox): `<path d="M3 11l9-7 9 7v9a1 1 0 0 1-1 1h-5v-6h-6v6H4a1 1 0 0 1-1-1z"/>`.
Utility icons (back, close, play, chevron) = rounded 1.8dp strokes at 20–24dp.
**Rule: never render an empty icon container.** Icon parameters are non-null.

---

## 6. Components

All in `ui/components/`. Each: anatomy → sizes → behaviour → states.

### 6.1 `SkyHeader(sky, height, content)`
Vertical gradient (§2.3) · `LandscapeLayers(sky)` (4 hill paths; gopuram on layer 2; 1–2 palms on layer 3;
sun/moon/stars/birds per sky) · content slot padded by `WindowInsets.statusBars`.
Heights: Home 380dp · Puja 380dp · Sankalpa 420dp. The `Sheet` below overlaps its last 20–30dp.
Landscape shapes: `illustrations.md` §2 (one vector set, recoloured per sky).

### 6.2 `Sheet`
Ivory surface, top corners 30dp, overlaps the SkyHeader; all scrolling content lives inside.

### 6.3 `SkyTopBar`
Wordmark "నామాలు" (Noto Serif Telugu 700, 22sp, kumkum deep) · `LanguagePill` · `ProfileButton` (44dp, initial).
Control background: ivory @ 80% on dawn/day, `rgba(42,18,32,.35)` on dusk/night.

### 6.4 `LanguagePill`
Full language name: "తెలుగు ▾" / "हिन्दी ▾" / "IAST ▾". 44dp tall, 15sp 600. Opens a bottom sheet with three
64dp rows. The change applies immediately to all content on screen and persists.

### 6.5 `Greeting`
Script greeting by sky (శుభోదయం at dawn, etc.; strings from content pack) in `greeting`, colour = sky's deepest
hill tone. Meta line (16sp): "[tithi] · [city], [time]". City = the user's chosen location in Settings.

### 6.6 `FestivalHeroCard`
Kumkum deep fill, radius 28, padding 20. Left: 84dp kumkum-tint square (radius 26) holding the 60dp Kalaśam
or the festival's illustration. Right: eyebrow "IN N DAYS" (`#F2CF95`), name (`cardTitle`, ivory),
"Prepare the puja →" (15sp, `#F3D9D3`). Whole card → Puja overview.
On the day: "TODAY" + "Begin guided puja →". Nothing within 30 days: hide.

### 6.7 `ModuleTile(icon, scriptLabel, englishLabel, module)`
124dp tall, radius 26, module tint. Centered: 52dp icon · 6dp · script label (`tileLabel`, module deep) ·
English label (`tileSub`, module deep @ 80%). 3-column grid, 12dp gaps. **Every tile navigates.**
Pressed: scale 0.97, tint darkens 6%. No checkbox, no outline square, no chevron.
Home order: Puja · Naamalu · Japam · Naivedyam · Calendar (పండుగలు) · Group japa (సామూహిక జపం).

### 6.8 `SankalpaCard`
Kamala tint, radius 28. 76dp ring (track `#F3C6D2`, progress kamala deep, % label 17sp 600) ·
"Our group japa · [group]" · text name (Fraunces 20) · "62,400 of 1,00,000 together".
Not in a group → "Start or join a group japa" with the Diyas icon. Indian digit grouping (1,00,000).

### 6.9 `ContinueCard`
Neela tint: eyebrow, title (Fraunces 20, neela deep), 4dp progress bar, 56dp round play (neela deep).
**Hidden without history.** Never show "0 sessions" or "0 day streak".

### 6.10 `FloatingNavBar`
Paper, radius 28, 78dp tall, 12dp side margin, 16dp above `WindowInsets.navigationBars`.
Home · Puja · Naamalu · Japam · Saadhana. Icon 26dp over 12sp label. Active: 52×32 kumkum-tint pill behind a
kumkum-deep icon; label kumkum deep. Scroll content bottom padding ≥ 110dp.

### 6.11 `PujaHero`
SkyHeader(Sandhyā): back (44dp), `TraditionPill` ("Telugu tradition ▾"), `LanguagePill`; eyebrow
"FESTIVAL PUJA · [date] · [city]" (`#F4D39B`); title (`heroTitle`, ivory); IAST + tithi line.

### 6.12 `SignificanceCard`
Kumkum tint, radius 26: "WHY WE CELEBRATE", 1–2 lines Fraunces 20, "Read more →".

### 6.13 `SectionCard(icon, title, subtitle, module)`
176dp tall, 2-column grid, radius 26, module tint; 52dp icon top-left; title (Fraunces 21, 650) and subtitle (14sp) bottom.
Puja: **Setup** (tulasi) · **Samagri · N** (chandan) · **Mantras** (neela) · **Katha** (kamala).

### 6.14 `NaivedyamCard`
Haldi tint, full width: 72×56 dish illustration · "NAIVEDYAM" · dish name (script) · other names.

### 6.15 `VerseFooter`
Centered IAST (Fraunces italic 16) *patraṁ puṣpaṁ phalaṁ toyaṁ / yo me bhaktyā prayacchati* +
"A leaf, a flower, a fruit, water — offered with love. · Gītā 9.26".

### 6.16 `StickyCta(label, chip)`
Over a fade to ivory. 60dp, kumkum deep, label left, `#F4D39B` chip right ("N steps →" / "Resume at K →").

### 6.17 `SamagriItem`
56dp row: 28dp round check (ticked = kumkum deep + ivory tick; unticked = 2dp chandan-mid ring), name 16sp 500,
alternative in tulasi deep "or …". Whole row toggles. No red, no "missing", no disabled state.

### 6.18 `GuidedCardStack`
Rātri full screen. Top: close, "Step K of N" + step name, `LanguagePill`, 3dp gold progress.
Instruction 15sp (alternative line in `#F4D39B`). Two cards peek behind (+4°, −2°; `#3A3346`, `#D8C9AE`).
Front card ivory 342×340, radius 30: "1 OF 21" · line 1 (`mantraLarge`, kumkum deep) · line 2 (`mantraBody`) ·
40dp gold rule · IAST · offering hint. Controls: prev 48dp outline · play 60dp `#F4D39B` · next 48dp outline.
**Advance: tap card, swipe left, next, or volume keys.** Keep screen on. Small diya glow at the bottom.

### 6.19 `JapaRing`
The whole screen is the tap target. 300dp ring of 36 beads (12dp; lit `#F4D39B`, unlit ivory 22%) · center count
(`count`) + "of 108" · mantra above (script + IAST) · chip "Adds to our group · [group]" (kamala) when linked.
Haptic per bead, stronger at 108. Mala number in the top bar.

### 6.20 `SankalpaProgress` + `MemberRow`
190dp ring (kamala deep on `#F3C6D2`, inner gold hairline), total Fraunces 30, "of 1,00,000 together".
Intention card (kamala deep fill). "OFFERED TODAY": 40dp initial avatar · name · "3 malas · this morning".
**Sorted by most recent, never by amount. No ranks, no per-person totals, no badges.**
Footer: "Start japa" (kamala deep, 58dp) + "Add count" (outline, with a +108 quick option).
**Bubble pop-up:** each new contribution floats up from the bottom as a kamala-tint pill ("Lakshmi · +108"), 2.5s, max 3 on screen; reduced motion → a static toast.

### 6.21 `DishNamesPanel` + `RegionalNameChip`
Chandan tint, "KNOWN ACROSS HOMES AS", 2-column chips: name **in its own script** + "Name · Region".
Footer line: "Same offering, many kitchens — each recipe keeps its own way."
The user's region's name is the title; the others are equal chips, none marked "original".

### 6.22 Offering parts (Naivedyam is meaning, not a cookbook)
`WhyCard` (haldi tint: "Why this offering" — 2–4 sentences on why this item, for this deity, at this time) ·
`SymbolCard` (kamala tint: symbolism / story) · `SeasonCard` (tulasi tint: why these ingredients in this season) ·
`IngredientChips` (main ingredients as chips — no quantities, no steps) ·
`AlternativeCard` (tulasi deep fill: "Can't make it today?" / "Fruit, jaggery, or a cup of milk — offered with love, it is complete.") ·
`RecipeHint` (one quiet line: "For the recipe, ask family, search online, or ask an assistant.") · "Offered in: [puja links]".

---

## 7. Screen recipes (top → bottom)

- **Home:** SkyHeader(skyFor(now)) [SkyTopBar, Greeting] → Sheet [FestivalHeroCard · 3×2 ModuleTiles · SankalpaCard · ContinueCard] → FloatingNavBar.
- **Puja overview:** PujaHero → Sheet [SignificanceCard · 2×2 SectionCards · NaivedyamCard · VerseFooter] → StickyCta.
- **Guided puja:** GuidedCardStack.
- **Japa:** Sandhyā sky + landscape · JapaRing.
- **Sankalpa:** kamala day sky · SankalpaProgress → Sheet [intention · MemberRows] → footer buttons.
- **Offering (Naivedyam):** haldi sky + illustration → title + IAST → DishNamesPanel → WhyCard → SymbolCard → SeasonCard → IngredientChips → AlternativeCard → RecipeHint → Offered in.

---

## 8. Motion & haptics
Press 120ms scale 0.97. Guided swipe: card flies left 220ms, next rises from the stack.
Sky crossfades 1.5s when its time bucket changes. Japa haptics as §6.19. Reduced motion: instant swaps.

---

## 9. Never do (from build 1)
1. Content under the status bar — always apply `WindowInsets.statusBars` / `navigationBars`.
2. Home as identical eyebrow-title-chevron rows.
3. Empty rounded squares where icons belong (reads as a checkbox).
4. Tiles or cards that don't navigate.
5. Placeholder/debug text in UI ("మందిర · Ganesha", "arrive here", "For now…").
6. Zero states as numbers ("0 sessions · 0 day streak") — hide or invite instead.
7. Small grey text for anything an elder must read.
8. Ranking people, streak-loss warnings, red/error styling in practice or puja.
9. Photos, decorative gradients outside the sky, emoji, stock icon packs. Imagery comes only from `illustrations.md`.
