# Sannidhi v6 — UI/UX alignment spec for Cursor

Hand this file to Cursor together with `AGENTS.md`. It turns the 22 mockups in the "Sannidhi v6" zip into screen-by-screen build instructions for the existing app (package `app.sannidhi`, applicationId `com.smaranam.app`, project `~/Desktop/naamalu app creation`).

**Sources (all read, nothing modified):** the 22 mockup PNGs; `AGENTS.md`; `docs/design-v6.md`; `docs/app-flow.md`; `docs/content-architecture.md`; `content/README.md`; `content/_schema/*.schema.json`; `content/_templates/*`; `content/deities/ganesha.md`; `content/pujas/ganesha/vinayaka-chavithi.md`; `content/calendar/observances/vinayaka-chavithi.md`; `content/stories/ganesha/elephant-head.md`; `content/texts/ganesha.ashtottara.yaml`; `content/images/manifest.csv`; `tools/build_content.py` (header, `SIZES`, `build_images`, `main`); `tools/build_config.yaml`; `tools/requirements.txt`; `ui/theme/{Color,Type,Shape}.kt`; `ui/common/Common.kt` (component list); `ui/navigation/SannidhiNavHost.kt` (routes); `res/values*/strings.xml` (keys).
**Markers:** **[inferred]** = read off a mockup or reasoned, not found in a file. **[verify]** = Cursor must open the code to confirm. Code paths are relative to `app/src/main/java/com/smaranam/app/`.

> Precedence: `docs/design-v6.md` and the mockups agree on every token checked. Where a mockup shows sample text such as "[name 102]", the real content comes from `assets/content`. **Never copy sacred text from a mockup.**

---

## 0. Global rules that apply to every screen

| Rule | Value | Source |
|---|---|---|
| Design frame | 390 × 844 dp; support widths 360–430; reflow, never scale | spec sheet |
| Side margin | 16 dp; content in 1 column, or 2 with a 12 dp gap | spec sheet |
| Spacing scale | 4 / 8 / 12 / 16 / 24 / 32 | spec sheet, design-v6 §1 |
| Touch target | ≥ 56 dp hit area (chips look 44 dp but hit 56 dp) | spec sheet |
| Font scale | up to 200%; wrap, never truncate sacred text; no `maxLines` on sacred text | spec sheet |
| Gestures | every gesture also has a visible button | spec sheet, AGENTS #6 |
| Navigation | **No bottom bar.** Inner screens use `TopBar` (64 dp, "‹ Back" left, centred title, "⌂ Home" right, 56 dp hit). Home has no top bar. System back = Back. | spec sheet, design-v6 §2 |
| Insets | respect system insets; never draw a fake status bar | spec sheet |
| Banned words | missed · streak · behind · incomplete · you haven't · 0 today · overdue · reminder · goal not met · bad day · dosha · remedy | design-v6 §11 |
| Practice colour | no red or "error" colour in practice or sādhana UI | design-v6 §1 |
| Drafts | debug builds: small "draft" chip on draft content; release builds contain no drafts (`build_content.py --release`) | AGENTS #10 |
| Images | missing → fallback (§3.5); never a grey box, broken icon or spinner that stays | images sheet |
| Strings | UI chrome only in `res/values{,-te,-hi}/strings.xml`; content strings only from `ContentRepository` | AGENTS |
| Offline | every screen works offline (groups are Phase 5) | app-flow §0 |

**States used below.** *Loading*: content is local, so show nothing (keep the background colour) until the first frame is ready. Never a spinner that stays. *Empty*: warm copy, no blame. *Draft*: chip in debug only. *Offline*: same as online. Only "Discover" and "Start a group" are disabled, labelled "coming soon".

---

## 1. Screen-by-screen spec

Routes are the `@Serializable` routes in `ui/navigation/SannidhiNavHost.kt`. Screen functions are the ones found in each file.

### 1.1 `01 · Onboarding · language.png` → `OnboardingRoute` / `ui/onboarding/OnboardingScreen.kt :: OnboardingScreen`
- **Layout:** sky header with hills + sun (no Back on step 1); "Sannidhi" in Cormorant, maroon, centred → "Step 1 of 4" (Label, inkSoft) → title "Choose your language" (Display 32) → "You can change this any time." (Body) → three full-width tiles (surface #FFFDF8, radius 24, ≥ 72 dp tall **[inferred]**): `తెలుగు` / Telugu, `हिन्दी` / Hindi, `English` / "Names also shown in IAST". Selected tile: maroon 2 dp border + maroon round check on the right → bottom primary button "Continue" (56 dp, maroon, full width).
- **States:** nothing preselected **[inferred: mockup shows English selected]**. Default to the device locale if it is te, hi or en, else English. Continue is enabled once a tile is selected.
- **Nav:** Continue → step 2 (state/city, design-v6 §8) → step 3 (name, optional) → step 4.
- **Gap:** steps exist (`"Step "` found in OnboardingScreen.kt). **[verify]** the tile layout, the check mark and the sky header on step 1.

### 1.2 `02 · Onboarding · rāśi (step 4 of 4).png` → same screen, step 4
- **Layout:** "‹ Back" (no Home) → progress: 4 segments, gold for done, maroon for current, plus "Step 4 of 4" → title "Your rāśi" + "(optional)" (inkSoft, Cormorant) → "Used only for a gentle note in the calendar. It stays on your phone." → 3-column grid of 12 chips. Each chip has a Devanagari name (Body semibold) over IAST (Label): मेष Meṣa, वृषभ Vṛṣabha, मिथुन Mithuna, कर्कटक Karkaṭaka, सिंह Siṃha, कन्या Kanyā, तुला Tulā, वृश्चिक Vṛścika, धनुस् Dhanus, मकर Makara, कुम्भ Kumbha, मीन Mīna. Selected chip: maroon border. Bottom: outlined "I don't know · skip", then primary "Finish".
- **Data:** names come from `master.json` rāśis (`content/master/rashis.csv`), not from Kotlin. In te UI the script line is Telugu **[inferred]**.
- **Nav:** Finish or skip → `HomeRoute` (popUpTo Onboarding inclusive, as the code already does).
- **Gap:** string `i_dont_know` = "I don't know". The mockup reads "I don't know · skip", so add the "· skip" string. **[verify]** the segmented progress bar.

### 1.3 `03 · Home (hub).png` → `HomeRoute` / `ui/home/HomeScreen.kt :: HomeScreen`
- **Layout (top to bottom):**
  1. Sky header, ≤ 25% of height (211 dp; collapses to 72 dp on scroll), sky gradient SkyTop→SkyBottom, Hill1–4, sun, palm. Row: "Sannidhi" (Cormorant, maroon) · language pill "English ▾" · name pill "Siddharth" (both ivory pills, 56 dp hit).
  2. Greeting "Good evening" (Display 32/38). Greeting by hour: 4–12 morning, 12–17 afternoon, 17–21 evening, 21–4 night.
  3. Info line (Body, ink): `Fri 11 Sep · kṛṣṇa amāvāsyā · Śrāvaṇa · Hyderabad`. Date, tithi and māsam come from the calendar day via `Labels`; the city comes from settings. No clock.
  4. **Coming-up card**: maroon, radius 24, 88 dp deity/observance image left on an ivory tile. "IN 3 DAYS · MON 14 SEP" (Label caps, gold-tint) · "Vināyaka Chavithi" (Title, ivory) · "Bhādrapada śukla chaturthī" (Body, ivory 80%) · "Prepare the puja →" (or "Start the puja" on the day).
  5. 2 × 2 module cards (square, radius 24, tints): 64 dp module icon + script label (Title) + English label (Label). పూజ/Puja (#F3E1DF), పంచాంగం/Calendar (#EFE6D8), సంకల్పం/Sankalpam (#E3EEDB), కథలు/Stories (#DCE7EE).
  6. "My sādhana" bar: ivory surface, gold hairline, mālā icon, "My sādhana" (Label semibold) + "Gaṇeśa mūla mantra and 1 more", "Open ›" (peacock). With no sankalpam: "Begin a sankalpam ›" (string `begin_sankalpam` exists).
- **Nav out:** language pill → language picker (dropdown or sheet) **[inferred: the "▾" means it opens a choice; it does not toggle]**; name pill → `ProfileRoute`; card → `PujaIdRoute(firstLinkedPuja)`; tiles → `PujaRoute`/`CalendarRoute`/`SankalpamRoute`/`StoriesRoute`; sādhana bar → `SankalpamRoute`.
- **States:** no upcoming observance → hide the card, don't show an empty one. Draft observance → chip (`comingDraft` already computed at HomeScreen.kt:349).
- **Gaps:**
  - The pill label is currently `lang_te/hi/en` with no ▾ and no picker. It must read as the current language with a dropdown.
  - `HomeScreen.kt:318` re-derives "today". Use `settings.today()` (`AppSettingsStore.kt:50`).
  - Debug date override (`debugToday`): show a visible "Debug date: 14 Sep" chip in debug builds only.
  - Delete the orphan `tab_home/tab_puja/tab_japam/tab_naamalu/tab_saadhana` strings (v5 bottom nav, retired).

### 1.4 `04 · Profile.png` → `ProfileRoute` / `ui/profile/ProfileScreen.kt`
- **Layout:** TopBar "Profile" → card with rows (label in Label/inkSoft, value in Body semibold, chevron). Rows: Name · App language · Where you live ("Hyderabad, Telangana") · Rāśi · Nakṣatra "Add (optional)" · Gotra "Gotra · used in the saṅkalpam" "Add (optional)" → card "Show mantras and names in" with chips `తెలుగు` `संस्कृतम्` `IAST` (selected = maroon fill, ivory text) → footer copy "Everything here stays on your phone. No account is needed." → outlined maroon "Delete my data" (with a confirm dialog **[inferred]**).
- **Nav in:** Home name pill. **Out:** each row opens a picker or sheet; delete → `AppContainer.clearPersonalData()` → Onboarding (already wired).
- **Gaps:** add the strings "Show mantras and names in" and "Everything here stays on your phone…" (not found in strings.xml). Keep the debug-date section, but only under `BuildConfig.DEBUG`. **[verify]** that it is not shown in release.

### 1.5 `10 · Puja home.png` → `PujaRoute` / `ui/puja/PujaScreens.kt :: PujaHomeScreen`
- **Layout:** TopBar "Puja" → compact maroon festival card: 64 dp image, "NEXT FESTIVAL · MON 14 SEP", "Vināyaka Chavithi", "Prepare →" → search field (56 dp, radius 16, ivory surface, search icon, placeholder "Search a god or festival", string `search_god_or_festival` exists) → "Choose a god" (Title) → 2-column god grid, card ratio 1 : 1.15. The deity card image fills the top 75%; below it the script name (Devanagari / Telugu per text view) and "Gaṇeśa · 1 puja".
- **States:** a deity with no art shows a tint panel with the text "Art coming" **[inferred]**. This conflicts with the images sheet ("symbol on tint"), so use the symbol if one exists, else the "Art coming" label. A deity with no pujas shows "coming later" / "later" and is not tappable **[inferred: Lakṣmī/Veṅkaṭeśvara are not in content; show only content deities, never hard-code them]**.
- **Nav:** card → `PujaIdRoute`; god → `PujaGodRoute(deity)`.
- **Gap:** "Art coming" / "coming later" strings are missing.

### 1.6 `11 · God page · Gaṇeśa.png` → `PujaGodRoute` / `GodPageScreen`
- **Layout:** full-bleed deity hero (1200×1500 source, shown 390×380, top-anchored). Back and Home sit as **floating ivory pills** on top of the hero (no TopBar strip) → ivory sheet, top radius 24: `गणेश` (script, Title) + "Gaṇeśa" (Cormorant, inkSoft) → intro (Body, 3 lines) + "More" (peacock link, expands) → row of 6 round symbol chips (56 dp, ivory, gold hairline); tap opens a bottom sheet with label + one-line meaning → "Pujas" (Title) → puja row (tint Puja, radius 24): title + "Festival" kind chip + "Mon 14 Sep · Bhādrapada śukla chaturthī" + chevron → hand-off rows "Stories of Gaṇeśa ›", "Chant Gaṇeśa's names ›" (peacock, hairline dividers).
- **Nav:** puja row → `PujaIdRoute`; Stories → `StoriesGodRoute(deity)`; Chant → `SankalpamGodRoute(deity)`, or the text directly.
- **Gap:** **[verify]** floating pills over the hero vs the standard TopBar, and the "More" collapse. The strings `stories_of` and `chant_names_of` exist.

### 1.7 `12 · Puja page · Pre (samagri).png` → `PujaIdRoute` / `PujaPageScreen`
- **Layout:** TopBar "Puja" → title "Vināyaka Chavithi Puja" (Display) → "Mon 14 Sep · Bhādrapada śukla chaturthī · about 75 min" → outlined button "Guided mode · one step at a time" → **sticky stage bar** Pre · During · Post (48 dp, gold 2 dp hairline under the selected segment, maroon selected text) → step cards (ivory surface, radius 24). Step number sits in a gold hairline circle with an IAST title. Samagri step: header "5 Sāmagrī" + "2 of 21 ready" (right). Checklist rows: maroon checkbox, item (Body semibold) + "· qty" (inkSoft), optional chip "optional", "Instead: …" line. Footer inside the card: gold hairline + italic Cormorant "Whatever you offer with love is enough."
- **Rules:** ticks are local and reset per puja day. Puja writes no `PracticeEntry`. "2 of 21 ready" counts ticks only and must never say "19 missing".
- **Gap:** the "%d of %d ready" string is missing. **[verify]** the sticky bar scroll-sync.

### 1.8 `13 · Puja page · During (mantra).png` → same page, scrolled to During
- **Layout:** once scrolled, the TopBar title becomes the short puja name "Vināyaka Chavithi" and the stage bar stays sticky. Step "3 Prārthanā" → instruction (Body) → text-view chips `తెలుగు · संस्कृतम् · IAST` (shown above the first mantra; choice is remembered in settings) → mantra block (tint surface #F3EDE4 **[inferred]**, radius 16, Sacred 24/36, verse breaks preserved, daṇḍas ।/॥) → "Meaning ›" (peacock) → "+ 1 more mantra below (vakratuṇḍa mahākāya...)" (inkSoft). Step "14 Aṣṭottara pūjā": text + two buttons **Read** (filled maroon) and **Listen** (outlined), going to `SankalpamTextRoute`. Step "16 Vināyaka kathā" with "15 min" on the right.
- **Saṅkalpam:** fill `{placeholders}` from the calendar day and saved name / gotra. Leave empty fields out of the sentence; never show a blank.
- **Gap:** strings `read_text`, `listen_text` and `meaning_expand` exist. "+ %d more mantra below" is missing.

### 1.9 `14 · Guided mode.png` → `PujaGuidedRoute` / `GuidedPujaScreen`
- **Layout:** no TopBar. Header "DURING · 23 OF 32" (Label caps, maroon) + ✕ (56 dp) → gold progress line → title "Dhūpam, dīpam" (Display) → instruction (Body 22 sp) → mantra card (Sacred 30 sp) → "Screen stays on while guided mode is open." (Label, inkSoft) → bottom row: outlined "‹ Previous" + filled "Next ›" (56 dp, equal width).
- **Behaviour:** horizontal pager plus buttons; `keepScreenOn` (present in PujaScreens.kt); no timers. ✕ → back to the Puja page at the same step **[inferred]**.
- **Gap:** the counter string `guided_step` exists. Check its format is "STAGE · x OF y". The "Screen stays on…" string is missing.

### 1.10 `20 · Calendar · today and rāśi note.png` → `CalendarRoute` / `CalendarHomeScreen`
- **Layout:** TopBar "Calendar" → today card (surface, radius 24): "Today · Hyderabad" (Label) · "Friday, 11 September" (Display) · "Parābhava · Śrāvaṇa" (peacock) · a two-column table, label in inkSoft and value in Body semibold + "· till 8:57 am": Tithi / Nakṣatra / Sun "rises 6:03 am · sets 6:21 pm" → hairline → "☼ Today for you · Meṣa" (Label semibold) → rāśi profile line (italic Cormorant) → note (Body) → horizontal rāśi chip row (selected = peacock fill) → "Coming up" (Title) → observance rows (tint Calendar): day "MON" + date "14", title + tithi, chevron → outlined peacock "Open the month".
- **States:** no saved rāśi → "Pick your rāśi to see today's note" + chips (string `pick_rashi_note` exists). Picking another chip shows that note for today only, plus "Save as my rāśi" (exists). An adhika māsam is marked.
- **Nav:** row → `CalendarObsRoute`; Open the month → māsam view (same route, state) **[verify]**.
- **Gap:** none confirmed. Check the Coming-up rows and the "Open the month" button exist.

### 1.11 `21 · Calendar · māsam view + day bubble.png` → `CalendarRoute` month mode
- **Layout:** month header "‹ September 2026 ›" + "Śrāvaṇa → Bhādrapada" (the māsams the month spans). Week row SUN…SAT. Each cell: date number + small moon phase glyph (from `moon_pct` + paksha) + observance dot (maroon). Today has a gold hairline border; the selected day has a tint fill. Tapping a day opens a `ModalBottomSheet` "day bubble": "Bhādrapada · Parābhava" · "Monday, 14 September" (Display) · Tithi "śukla tṛtīyā till 7:08 am, then chaturthī" · Nakṣatra · Moon "9% lit · waxing" · Sun · "ON THIS DAY" → observance row (image, title, "Puja · story · names", chevron).
- **Nav:** the row opens `CalendarObsRoute`. Month arrows have 56 dp hits (`month_previous/next` exist). The data range ends with the bundled samvatsaras; beyond that, show `ugadi_update` (exists).
- **Gap:** "then <next tithi>" phrasing and "ON THIS DAY" **[verify]**.

### 1.12 `22 · Observance page.png` → `CalendarObsRoute` / `ObservanceScreen`
- **Layout:** observance image header (1200×800 → 390×260; until art exists, fall back to the deity card on the Calendar tint), Back/Home floating pills → "Vināyaka Chavithi" (Display) → "Mon 14 Sep · Bhādrapada śukla chaturthī" (peacock) → **4 action tiles** in a row (module tints, icon + label): Puja · Story · Chant · Naivedyam. Show only the tiles whose `links.*` are non-empty → "What makes it special" (Title + body from `## special`) → "What families do" (`## families-do`) → collapsible rows "History +", "Regional notes +".
- **Gap:** design-v6 describes "action rows"; the mockup shows tiles, so the mockup wins. `NaivedyamStubScreen` exists, which means Naivedyam is a stub.

### 1.13 `30 · Sankalpam · my sankalpams.png` → `SankalpamRoute` / `SankalpamHomeScreen`
- **Layout:** TopBar "Sankalpam" → chips: **My sankalpams** (Leaf green fill) · Nāmalu · "Discover · soon" (dashed outline, disabled) → cards (tint Sankalpam, radius 24): title (Title) + ⋮ menu (56 dp), "Just me · day 9 of 41" or "Just me · open-ended", big number "12,960" (Display, Leaf) + "so far · of 1,00,000", thin gold progress line (only when a target is set). For a reading unit: "12 readings so far" → help text "Practising without a sankalpam counts too. Open Nāmalu to read, listen or count any time." → extended FAB "+ New sankalpam" (Leaf, bottom-right, 56 dp).
- **Numbers:** Indian grouping (1,00,000) for te/hi/en-IN **[inferred from mockup]**.
- **Empty:** help text + FAB only. Never show "0".
- **Gap:** the chips exist (`chip_my_sankalpams`, `chip_naamalu`, `chip_discover`). Check the FAB, Indian grouping and the ⋮ menu (Leave / Mark complete **[inferred]**).

### 1.14 `31 · Practise · Count.png` → `SankalpamTextRoute` / `PractiseScreen` (Count)
- **Layout:** TopBar with the text title "Gaṇeśa mūla mantra" → segmented Read · Listen · Count (selected = Leaf fill; Listen is greyed when the text has no audio) → "Counts for: me + 1,00,000 sankalpam ▾" chip (Sankalpam tint) → mantra in Sacred (IAST or the chosen view) → 200 dp+ circle (ivory, shadow) with a gold ring filling to 108, "54" (Display 64 **[inferred]**), "of 108 · round 1" → "Tap anywhere on the circle" → bottom: "−1" (outlined) + "Finish and save" (Leaf). Background: sky gradient to warm sand.
- **Behaviour:** haptic tick per tap (`performHapticFeedback` present). Finish writes one `PracticeEntry(mode=count, count=n)`. Leaving without Finish asks nothing and saves nothing **[inferred, mirrors Read]**.
- **Gap:** strings exist. Check "Counts for: me + …" formatting.

### 1.15 `32 · Practise · Read.png` → same screen, Read
- **Layout:** segmented (Read selected) → "Counts for: me ▾" + compact text-view chips `తె` `सं` `IAST` → list, one name per row: number in a gold hairline circle + sacred line `oṃ <name> namaḥ` → at the end, "Done · adds 1 reading" (Leaf, full width) + "Shown at the end of the list. Leaving before Done adds nothing."
- **Data:** `texts/ganesha.ashtottara.json` names. The mockup placeholders "[name 102]" are not content.
- **Gap:** `done_reading` = "Done". The mockup says "Done · adds 1 reading", so add a string. The helper copy is missing.

### 1.16 `33 · New sankalpam.png` → `SankalpamNewRoute` / `NewSankalpamScreen`
- **Layout:** TopBar "New sankalpam" → "What will you chant?" + selected text card (tint) "Gaṇeśa mūla mantra" + "Change ›" → "With whom?": "Just me" (selected, Leaf border) | "Start a group · coming soon" (dashed, disabled) → "Total to reach (optional)" numeric field + presets 1,008 · 10,008 · 1,00,000 → "For how many days? (optional)": 11 · 21 · 41 · No end → "Your intent (optional, only you see it)" field, placeholder "For a peaceful home" → "Begin" (Leaf, full width, bottom).
- **Writes:** `Sankalpam(kind=solo, target, days, startDate=today, intent, intentVisible=false)`.
- **Gap:** "No end", the preset values and the intent placeholder are not in strings.xml. Add them as UI chrome. The presets are UI, not sacred content.

### 1.17 `40 · Stories home.png` → `StoriesRoute` / `StoriesHomeScreen`
- **Layout:** TopBar "Stories" → search "Search a god or a story" → **For today** card (tint Stories, radius 24; story cover, or symbol fallback such as the mūṣaka on tint): "FOR TODAY · VINĀYAKA CHAVITHI" (Label caps, peacock), "Ganesha and the Moon" (Title), "3 min · ages 4 to 12" → "Choose a god" → same god grid as Puja, with the subtitle "Gaṇeśa · 3 stories".
- **For today** appears only when today's (or the next **[inferred]**) observance links a story (`observances[].links.stories`).
- **Gap:** "For today", "%d stories" and "ages %s" strings are missing.

### 1.18 `41 · Story cover + start sheet.png` → `StoryRoute` / `StoryPlayerScreen` (cover)
- **Layout:** TopBar variant: Back · language chips `తె` `हि` `En` (selected = peacock fill) · Home → cover image (4:3) → title (Display), "3 min · 7 pages · ages 4 to 12", italic source "Retold from a popular Purāṇic telling" (the `source` field) → bottom sheet "Who is listening?": "Little ones (4+)" / "Short lines, big words" (tint) and "Older children" / "The full story on each page" (outlined).
- **Rule:** the sheet is asked once per story and then remembered. Older children show `more` under `short`.
- **Gap:** "Who is listening?" and the two sub-lines are missing.

### 1.19 `42 · Story beat (touch moment).png` → player, beat
- **Layout:** beat image (4:3 top, 390×292) with a pill "☝ Tap the moon" in the bottom-right (from `touch.target`) → text panel on ivory (Cormorant ≈ 24 sp **[inferred]**) → bottom: round outlined "‹" (56 dp), progress dots (current = wide pill), round filled peacock "›".
- **Behaviour:** tap the right two-thirds or swipe left → next. Touch effects glow / wiggle / hide / appear / bounce use the timings in design-v6 §7. A hand hint pulses until the first tap.
- **Gap:** the "Tap the %s" pill string is missing **[verify]**. Effects exist (glow and wiggle found).

### 1.20 `43 · Story last beat.png` → player, last beat
- **Layout:** image → takeaway line large (Title, peacock) → gold hairline → "FOR PARENTS" (Label caps) → rows "Do the Vināyaka Chavithi puja ›", "Chant Gaṇeśa's names ›" → dots → "Back to stories" (peacock, full width; `back_to_stories` exists).
- **Gap:** the "For parents" string is missing.

### 1.21 `00 · Spec sheet…png` and 1.22 `00 · Images, icons and fallbacks.png`
These are reference sheets, not screens. Their content is in §2 and §3.

---

## 2. Design system

All values below were found in `ui/theme/*.kt` **and** match the spec sheet and design-v6 §1, unless marked.

**Colour (`ui/theme/Color.kt`)**
| Token | Hex | Use |
|---|---|---|
| Maroon | #7A1F2B | primary, coming-up card, headings |
| MaroonDeep | #5E1620 | pressed |
| Gold | #C79A3E | hairlines, rings, number circles only. Never a fill |
| Ivory | #FBF4E9 | page |
| surface | #FFFDF8 | cards. **Missing from Color.kt: add `Surface`** |
| Ink / InkSoft | #2B1A1C / #6B5559 | text / secondary |
| Peacock | #1B3A4B | links, Calendar and Stories accents |
| Leaf | #3E6A33 | Sankalpam accent (in code; not on the spec sheet; it matches the green in the 30–33 mockups) |
| TintPuja / TintCalendar / TintSankalpam / TintStories | #F3E1DF / #EFE6D8 / #E3EEDB / #DCE7EE | one per module, always |
| SkyTop→SkyBottom, Hill1–4 | #F0C9B4→#F8E2C6; #E7C2A6 #D9A58E #B97A72 #8E2430 | Home sky and Count background |
| Rātri dark | #1A1214 page · #2A1D20 surface · #F3E6D8 text · gold unchanged | `darkColorScheme` exists in Theme.kt. **[verify]** that it follows the system setting (`isSystemInDarkTheme` not found) |
| Text on maroon | ivory #FBF4E9 or label #F3D9A4 | spec sheet. **#F3D9A4 missing from Color.kt** |

**Typography (`ui/theme/Type.kt`; fonts in `res/font/`: cormorant_garamond, inter_regular, inter_medium, noto_serif_telugu, noto_serif_devanagari)**
| Role | M3 slot in code | Font | sp |
|---|---|---|---|
| Display | displayLarge | Cormorant SemiBold | 32/38 |
| Title | headlineMedium | Cormorant SemiBold | 22/28 |
| Body | bodyLarge | Inter | 18/26 (never < 16; bodyMedium 16/24) |
| Label | labelLarge | Inter Medium | 15/20 |
| Sacred | **not defined as a style; add `SacredStyle(view)`** | Noto Serif Telugu / Devanagari; Cormorant for IAST | 24/36 · Read 28/42 · Guided 30 |
Sacred text is never below 20 sp and never truncated.

**Shape (`ui/theme/Shape.kt`)**: cards 24 dp (medium/large/extraLarge), buttons 16 dp (small), chips pill (999). Code has no pill shape: use `CircleShape` / `RoundedCornerShape(50)`. Sheets have a 24 dp top radius.
**Sizes**: TopBar 64 · buttons 56 tall, full width · stage bar 48 (sticky) · chips 44 visual / 56 hit · module icon 64 dp, stroke 2.5, rounded caps · symbol chips 36–64 · count circle ≥ 200.
**Iconography**: module glyphs in `ui/common/ModuleGlyph.kt` (puja bowl, calendar with moon, mālā, open book). Material Icons Extended for chevrons, search, house and ✕. Deity symbols are SVG on a 96 viewBox from content.
**Shared components (`ui/common/Common.kt`)**: `TopBar`, `ModuleCard`, `ComingUpCard`, `SacredText`, `ImageOrFallback`. AGENTS.md also lists `LangChips` and `StageBar`, which were not found in Common.kt. **[verify]** whether they sit inside feature files, and move them to `ui/common`. Also add: `GodGridCard`, `StepCard` (gold number circle), `HandOffRow`, `FloatingPillButton`, `SegmentedTabs`, `DraftChip` (debug only), `SearchField`.

---

## 3. Assets

### 3.1 Where they live
- **Source (you edit):** `content/images/<kind>/<image-id>.(svg|png)`, listed in `content/images/manifest.csv`. Kinds in use: `deity`, `symbol`, `puja`, `observance`, `naivedyam`, `samagri`, `story`.
- **Output (generated, never edit):** `app/src/main/assets/content/images/<image-id>.webp` + `images.json` (id → `{kind,status,alt,file}`; `file` is null when missing).
- **`res/drawable`:** only `ic_launcher_foreground.xml`. Keep app chrome (module glyphs are Compose code) out of content. Content images never go in `res/drawable`.
- **Audio:** source `content/audio/<text-id>.mp3` (folder currently empty). `res/raw/ganesha_ashtottara.wav` exists in the app **[verify]**. Under the content rules this should move to `content/audio/ganesha.ashtottara.mp3`, referenced by `audio.file` in the text yaml.
- **Fonts:** `res/font/` (bundled, OFL).

### 3.2 Naming
`img.<kind>.<deity>.<slug>`, for example `img.deity.ganesha.hero`, `img.deity.ganesha.card`, `img.symbol.ganesha.modaka`, `img.puja.ganesha.mandapam`. Exceptions: `img.obs.<observance-slug>`, `img.naivedyam.<slug>`, `img.samagri.<slug>`, `img.story.<deity>.<story>.cover|b<N>`. Lowercase ASCII; the id never changes once published.

### 3.3 manifest.csv columns
`id,kind,file,used_by,priority,status,alt_te,alt_hi,alt_en,brief`. `file` is relative to `content/images/`. `status` is `missing | supplied | approved`. `priority` is P0/P1. Real row: `img.deity.ganesha.card,deity,deity/img.deity.ganesha.card.svg,ganesha,P0,supplied,"వినాయకుడు, తామరపై కూర్చుని",कमल पर विराजमान गणेश जी,Ganesha seated on a lotus,"Same figure, head and shoulders, 600x600, plain haldi background."`

### 3.4 Sizes (`tools/build_content.py: SIZES`, matches the images sheet)
| Kind | Supply px | Ratio | Shown dp | Fallback |
|---|---|---|---|---|
| deity hero | 1200×1500 | 4:5 | 390×380 top-anchored | deity card on tint |
| deity card | 600×600 | 1:1 | 173×156 · 88×88 | symbol on tint |
| symbol | SVG 96 viewBox (rasterised 192×192) | 1:1 | 36–64 | module icon |
| puja step | 1200×900 | 4:3 | 326×245 · guided 390×292 | symbol on Puja tint |
| observance | 1200×800 | 3:2 | 390×260 | deity card |
| naivedyam | 800×800 | 1:1 | 96 · 326 | modaka symbol |
| samagri | 256×256, transparent | 1:1 | 40×40 | text only |
| story cover / beat | 1600×1200 (+ touch-layer PNG) | 4:3 | 358×268 / 390×292 | symbol on Stories tint |
Rules: PNG or SVG in, WebP out (SVG via cairosvg at SIZES, quality 90; PNG at quality 88). ≤ 400 KB each. Keep faces and key objects inside the centre 80%. No photos. Style: flat layered landscape, warm ritual palette, soft dawn/dusk sky. No density buckets are needed, because a single WebP per id is sized for the largest use and Compose scales it down.

### 3.5 How code references them
Code uses ids only. `ContentRepository.imagePath(id)` returns `"content/images/<id>.webp"` or null. Render through `ImageOrFallback(id, fallbackSymbol, tint)` (Common.kt:206). AGENTS lists Coil, but Coil is **not** in `app/build.gradle.kts`, so add `coil-compose` and load `file:///android_asset/<path>` **[inferred]**. Null → symbol on the module tint → module icon. Never a grey box.

---

## 4. Content recipes (worked from the real Ganesha files)

General rules (content-architecture §1–3, AGENTS #1–2):
- Write in `content/`; never in `assets/`.
- IDs are lowercase with dots between levels and never change. Retire with `status: retired`.
- Statuses: `draft → reviewed → published (→ retired)`.
- Localised field `L` = `{te, hi, en}`. Sanskrit field `S` = `{sa, iast, te}`. You write only `sa` (Devanagari); the pipeline makes `iast` and `te`.
- **Cursor never writes sacred text.** These recipes are for the content author.

### 4.1 Add a new god
1. Copy `content/_templates/deity.md` → `content/deities/<deity>.md`.
2. Fill the front-matter. Fields (output schema `deities.schema.json`; required: id, status, order, name, name_common, also_known_as, images, symbols, intro; `additionalProperties: false`):
   | Source field | Type | Req | → output |
   |---|---|---|---|
   | `id` | string, lowercase | yes | id |
   | `status` | draft/reviewed/published/retired | yes | status |
   | `name_sa` | Devanagari | yes | `name: S{sa,iast,te}` |
   | `name_common` | `{te,hi,en}` | yes | `name_common: L` |
   | `also_known_as_sa` | list of Devanagari | yes (may be []) | `also_known_as: [S]` |
   | `order` | int | yes | grid position |
   | `images` | `{hero, card}` image ids | yes (keys optional) | images |
   | `symbols` | list of image ids | yes | `symbols[{image,label,meaning:L}]` |
   | `art_brief` | text | no (artist only) | not exported |
   | `## intro` → `#### te/hi/en` | 60–80 words each | yes | `intro: L` |
   | `## symbols` → `### <image-id> \| <Label>` → `#### te/hi/en` one line | per symbol | yes | label + meaning |
   Worked example (actual `content/deities/ganesha.md`, shortened):
   ```
   ---
   id: ganesha
   status: draft
   name_sa: गणेशः
   name_common: { te: వినాయకుడు, hi: गणेश जी, en: Ganesha }
   also_known_as_sa: [विनायकः, विघ्नेश्वरः, गणपतिः, गजाननः, एकदन्तः, लम्बोदरः]
   order: 1
   images: { hero: img.deity.ganesha.hero, card: img.deity.ganesha.card }
   symbols: [img.symbol.ganesha.modaka, img.symbol.ganesha.mushika, img.symbol.ganesha.ankusha, img.symbol.ganesha.pasha, img.symbol.ganesha.ekadanta, img.symbol.ganesha.durva]
   art_brief: >
     Seated on a red lotus pedestal, four arms: …
   ---
   ## intro
   #### te
   వినాయకుడు విఘ్నాలను తొలగించే దేవుడు. …
   #### hi
   गणेश जी विघ्नों को दूर करने वाले देवता हैं। …
   #### en
   Ganesha removes obstacles. …

   ## symbols
   ### img.symbol.ganesha.modaka | Modaka
   #### te
   మోదకం అంటే ఆనందం. …
   #### hi
   मोदक यानी आनंद। …
   #### en
   Modaka means joy. Plain outside, sweet inside — like knowledge.
   ```
3. **Images:** add `deity/img.deity.<deity>.hero.(svg|png)` (1200×1500), `.card` (600×600) and each `symbol/img.symbol.<deity>.<slug>.svg` (96 viewBox), with one manifest.csv row each (alt_te/hi/en for hero and card).
4. **Mantras:** `content/mantras/<deity>.md` from `_templates/mantras.md`. Front-matter `deity, status, reviewed_by`. Then `## <deity>.mantra.<slug>` with `sa:` (Devanagari only), `use: [japa, puja]`, optional `count_default`, optional `review:` (blocks `reviewed`), and `#### te/hi/en` meaning. Upacāra lines go in a table under `prefix_sa`. Ganesha has 9 mantras + 21 upacāra + 21 patra lines (README).
5. **Nāmalu / ashtottara** (§4.5).
6. **Pujas** (§4.3), **festivals** (§4.2), **stories** (§4.4), **naivedyam** (`_templates/naivedyam.md`) and **samagri** (rows in `content/samagri/items.csv`, ids `sm.<slug>`).
7. Run `python tools/build_content.py --check`. Fix every error. Missing images are warnings, and the app shows fallbacks.
8. Purohit and native review, then set `status: reviewed` (and `reviewed_by`). Only then does it appear in `--release`.
9. App code: nothing to change. The god grid reads `deities.json` sorted by `order`. If a screen needs a new field, stop and extend `_schema` + `ContentModels.kt` together (AGENTS #1).

### 4.2 Add a festival (observance)
File `content/calendar/observances/<slug>.md` from `_templates/observance.md`.
| Field | Type | Req |
|---|---|---|
| `id` | `obs.<slug>` | yes |
| `kind` | festival / vratam / period / weekly / monthly | yes |
| `title` | L | yes |
| `rule.masam` | id from `master/masams.csv` (omit for monthly) | yes* |
| `rule.paksha` | shukla / krishna | yes |
| `rule.tithi` | 1–15 | yes |
| `rule.prevails_at` | sunrise / madhyahna / pradosha / nishita / chandrodaya | yes |
| `regions` | `[all]` or state codes | yes |
| `deity` | deity id | yes |
| `links` | `{pujas, texts, stories, naivedyam}` id lists (drive the Observance tiles and the "For today" card) | yes (may be empty) |
| `image` | `img.obs.<slug>` | yes (may be missing art) |
| `status` | | yes |
| sections | `## special`, `## families-do`, `## history`, `## regional-notes`, each `#### te/hi/en` | special required **[inferred]** |
Real example (`vinayaka-chavithi.md`): `rule: {masam: bhadrapada, paksha: shukla, tithi: 4, prevails_at: madhyahna}`; `links.pujas: [ganesha.puja.vinayaka-chavithi]`, `texts: [ganesha.ashtottara, ganesha.mula-mantra]`, `stories: [ganesha.story.elephant-head, ganesha.story.moon, ganesha.story.mango]`, `naivedyam: [nv.modaka, nv.phala]`.
Dates are computed by `tools/panchanga.py` for every city and samvatsara in `build_config.yaml`. If a computed date disagrees with your reference panchāṅgam, add a row to `content/calendar/overrides.csv`, then tick `calendar/check-<year>-<samvatsara>.csv` against the TTD panchāṅgam.

### 4.3 Add a puja
File `content/pujas/<deity>/<slug>.md` from `_templates/puja.md`. Front-matter: `id` (`<deity>.puja.<slug>`), `deity`, `kind` (festival/vratam/nitya), `observance` (omit for nitya), `title` L, `minutes`, `regions`, `status`, `reviewed_by`. Body: `## pre`, `## during`, `## post`. Each step is `### <slug> | <IAST title>` with optional `image`, `minutes`, `mantra` (id or list), `repeat`, `text` (nāmalu id → Read/Listen buttons), `fills: [name, gotra]` (saṅkalpam), `optional: yes`, `naivedyam: [nv.*]`, then `#### te/hi/en`. The samagri step is a table `| item | qty | required | alternatives |` using `sm.*` ids. Real example: Vinayaka Chavithi has `minutes: 75`, pre 8 / during 16 / post 8 steps, and first step `### clean | Sthala śuddhi` with `image: img.puja.ganesha.clean`, `minutes: 15`. Step ids are generated as `<puja-id>.<stage>.<slug>`.

### 4.4 Add a story
File `content/stories/<deity>/<slug>.md` from `_templates/story.md`. Front-matter: `id` (`<deity>.story.<slug>`), `deity`, `title` L, `ages` (e.g. 4-12), `minutes`, `feeling` list, `source` ("… retold gently"), `observances` list, `cover` (`img.story.<deity>.<slug>.cover`), `status`, `reviewed_by`. Beats: `## beat N` with `image: img.story.<deity>.<slug>.bN`, optional `touch: { target: <thing>, effect: glow|wiggle|hide|appear|bounce }`, `### short` (te/hi/en, ≤ 20 words) and optional `### more` (≤ 60 words). Real example: `ganesha.story.elephant-head`, 4 min, `feeling: [brave, safe]`, `source: Śiva Purāṇa (retold gently for children)`, beat 1 `touch: { target: boy, effect: glow }`. Add a cover row plus one row per beat to manifest.csv. The last beat's line is the takeaway.

### 4.5 Add a nāmalu text (ashtottara, mantra)
File `content/texts/<deity>.<type>.yaml` from `_templates/text.yaml`: `id`, `deity`, `type` (ashtottara / sahasranama / stotram / mantra), `unit` (reading | repetition), `title` L, `status`, `reviewed_by`, optional `audio: {file, intro_ms, slot_ms}`. Then `names: [{n, sa}]`, or for a mantra `mantra: <id>` + `round: 108`. Real `ganesha.ashtottara.yaml`: `unit: reading`, `audio: { file: ganesha.ashtottara.mp3, intro_ms: 8000, slot_ms: 3500 }`, `names: IMPORT`. Names are **never typed by hand**; run `python tools/import_ashtottara.py path/to/<deity>_ashtottara.json content/texts/<deity>.ashtottara.yaml`. Ganesha has 11 CORRECTED flags awaiting purohit review.

### 4.6 Sankalpam
A sankalpam is **user data, not content**. Users create it in the app (`NewSankalpamScreen` → Room `Sankalpam` entity: `id, textId, kind solo|group, groupId?, target?, days?, startDate, intent?, intentVisible, status active|completed|left`). The content side only needs a nāmalu text (§4.5). Practice is recorded as `PracticeEntry` (`textId, mode read|listen|count|manual, count, unit, startedAt, endedAt, localDate, sankalpamId?, addedByHand`). Groups arrive in Phase 5 (Firebase, after Google sign-in).

---

## 5. Validation and build commands
Run these from the project root on the Mac. `pip install -r tools/requirements.txt` installs pyyaml, jsonschema, indic-transliteration, ephem, pillow and cairosvg.
```
python tools/build_content.py --check        # validate only, prints report
python tools/build_content.py --no-calendar  # fast dev build while editing non-calendar content
python tools/build_content.py                # dev build (drafts included + marked)
python tools/build_content.py --release      # reviewed/published only, use before a release build
./gradlew :app:assembleDebug
./gradlew :app:lintDebug
./gradlew :app:testDebugUnitTest             # once tests exist (none today)
scripts/install_debug_emulator.sh            # install on emulator  [verify script args]
```
**Proposed CI checks [inferred, not present]:**
1. Every key in `values/strings.xml` exists in `values-te` and `values-hi`. Today 14 are missing: `debug_today_hint, lang_en/hi/te, tab_* ×5, text_devanagari, text_iast, view_devanagari/iast/telugu`. Delete the `tab_*` keys (retired). Mark language and script names `translatable="false"`.
2. Grep the UI strings for the banned words in §0.
3. `build_content.py --check` must exit 0.
4. Release APK contains no `"status":"draft"`.

---

## 6. Rules block for Cursor

```
YOU ARE EDITING: ~/Desktop/naamalu app creation (package app.sannidhi; applicationId stays com.smaranam.app).
READ FIRST: AGENTS.md, docs/app-flow.md, docs/design-v6.md, docs/content-architecture.md, this file.

DO
- Match each mockup in §1 exactly: order, labels, sizes, colours. The mockup wins over prose when they differ.
- Use theme tokens (Color.kt/Type.kt/Shape.kt). Add the missing tokens: Surface #FFFDF8, OnMaroonLabel #F3D9A4, SacredStyle, Pill shape.
- Put every UI string in res/values, values-te and values-hi. Use te/hi translations from the content team; if none is given, leave a TODO and report it. Do not invent sacred terms.
- Read content only via ContentRepository; images only via ImageOrFallback; "today" only via AppSettingsStore.today().
- Keep TopBar (Back + Home) on every inner screen; floating pill variant on hero screens (God page, Observance).
- 56 dp minimum hit target; test at 200% font scale in te, hi, en; test offline.
- Show the "draft" chip only when BuildConfig.DEBUG.
- Add a Compose preview + a screenshot test per screen (mockup file name in the test name).

DON'T
- No bottom navigation, no streaks/reminders/notifications/"0 today"/red states, none of the banned words.
- Never hard-code a deity, festival, mantra, name, story line, date or rāśi note in Kotlin.
- Never edit app/src/main/assets/content/** (generated) or any sacred text in content/.
- No new permissions, no network, no analytics, no account.
- Puja must never write PracticeEntry; only Sankalpam does.
- Don't ship res/raw audio for content; audio comes through content/audio + the text's audio field.

ACCEPTANCE (report pass/fail for each)
01 Onboarding language: 3 tiles, check mark, Continue disabled until chosen, choice persists.
02 Onboarding rāśi: 12 chips from master data, "I don't know · skip" and Finish both land on Home with back stack cleared.
03 Home: sky ≤25%, info line from calendar, Coming-up card hidden when none, 4 tiles open their modules, language pill opens a picker (not a toggle), no bottom bar.
04 Profile: all 6 rows + text-view chips persist; Delete my data clears Room + DataStore → Onboarding; debug-date section only in debug.
10 Puja home: festival card, search filters gods/festivals, grid from deities.json order, fallback never grey.
11 God page: hero + floating pills, More expands, symbol sheet, puja rows, both hand-offs navigate.
12 Puja Pre: sticky stage bar syncs with scroll, samagri ticks local + reset per day, "x of y ready", alternatives shown.
13 Puja During: text-view chips remembered, Meaning collapsible, saṅkalpam placeholders filled/omitted, Read/Listen open Practise.
14 Guided: one step per page, Prev/Next + swipe, keep-screen-on, ✕ returns to same step.
20 Calendar today: tithi/nakṣatra end times, sunrise/sunset, rāśi note today only, Save as my rāśi, Coming up, Open the month.
21 Māsam: moon glyphs, observance dots, today outlined, day sheet with "then <next tithi>", arrows bounded by bundled years.
22 Observance: only linked tiles shown; Special/Families-do open; History/Regional collapsible.
30 My sankalpams: cards with Indian-grouped totals, no counts of what wasn't done; Discover disabled; FAB.
31 Count: haptic per tap, ring to 108 + rounds, −1, Finish writes one PracticeEntry; leaving saves nothing.
32 Read: names from text JSON in chosen script; Done adds exactly 1 reading.
33 New sankalpam: presets, optional fields, group disabled, Begin writes Sankalpam and returns to list.
40 Stories home: For-today card only when an observance links a story; grid with story counts.
41 Cover: lang chips, source line, start sheet asked once per story.
42 Beat: tap zones + buttons, touch effect per design-v6 §7, hint until first tap, dots.
43 Last beat: takeaway, For parents links, Back to stories.
ALL: works offline, 200% font, te/hi/en, no strings missing in te/hi, build_content.py --check passes.
```
