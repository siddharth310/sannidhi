# Design v6 — screen specs (Oct 2026)

Builds on the v3 look the team liked: Mandir base (maroon / gold / ivory), Alto's-style layered landscapes, big clear
icons, calm. If an older `design-system.md` exists in the repo, its tokens stay; this file wins where they differ.

---

## 1. Tokens

### Colour
| Token | Light | Use |
|---|---|---|
| `maroon` | `#7A1F2B` | primary, Coming-up card, headings |
| `maroonDeep` | `#5E1620` | pressed |
| `gold` | `#C79A3E` | **hairlines only**: dividers, progress arcs, small icons. Never a fill |
| `ivory` | `#FBF4E9` | page background |
| `ink` | `#2B1A1C` | body text |
| `inkSoft` | `#6B5559` | secondary text |
| `peacock` | `#1B3A4B` | links, Calendar accents |
| `tintPuja` | `#F3E1DF` | Puja card |
| `tintCalendar` | `#EFE6D8` | Calendar card |
| `tintSankalpam` | `#E3EEDB` | Sankalpam card |
| `tintStories` | `#DCE7EE` | Stories card |
| `skyTop` → `skyBottom` | `#F0C9B4` → `#F8E2C6` | Home sky |
| `hill1…hill4` | `#E7C2A6`, `#D9A58E`, `#B97A72`, `#8E2430` | layered hills |

Dark ("Rātri") keeps the same roles: background `#1A1214`, surfaces `#2A1D20`, text `#F3E6D8`, gold unchanged.
No red or "error" colour anywhere in practice or sādhana UI.

### Type
| Role | Font | Size / line |
|---|---|---|
| Display (greeting, page titles) | Cormorant Garamond SemiBold | 32 / 38 |
| Title (cards, sections) | Cormorant Garamond SemiBold | 22 / 28 |
| Body | Inter | 18 / 26 (never below 16) |
| Label | Inter Medium | 15 / 20 |
| Sacred text | Noto Serif Telugu / Devanagari; Cormorant for IAST | 24 / 36 (Read: 28 / 42) |

All fonts bundled. Respect system font scale up to 200%; layouts reflow, never truncate sacred text.

### Shape and touch
Card radius 24 dp, chips 999 dp, buttons 16 dp. Minimum touch target 56 dp. Primary buttons 56 dp tall, full width
on phones. Spacing scale 4 / 8 / 12 / 16 / 24 / 32. Page side margin 16 dp.

---

## 2. Navigation chrome

- **No bottom bar.** Remove it from every screen.
- **Top bar** on every inner screen, 64 dp: large **← back** (left, label "Back" next to the arrow for elders),
  screen title centred, **Home** button (house icon + "Home") right. System back = top-bar back.
- Home has no top bar; its header holds the language pill and the name button.

---

## 3. Home

```
┌──────────────────────────────┐
│ Sannidhi        [తెలుగు ▾] [Siddharth] │  sky header ≤ 25% of screen height
│ Good evening                            │  greeting: Display
│ Sun 11 Oct · Āśvayuja śukla pratipat · Hyderabad │  one info line, Body, inkSoft
│ ▁▂▃ layered hills ▃▂▁                    │  collapses to 72 dp on scroll
├──────────────────────────────┤
│ ┌──────── Coming up ────────┐ │  maroon card, 24 dp radius, observance image left (88 dp)
│ │ IN 3 DAYS · SAT 14 SEP     │ │  Label, gold-tinted caps
│ │ Vināyaka Chavithi          │ │  Title, ivory
│ │ Bhādrapada śukla chaturthī │ │  Body, ivory 80%
│ │ [ Prepare the puja → ]     │ │  "Start the puja" when today
│ └───────────────────────────┘ │
│ ┌ Puja ┐ ┌ Calendar ┐          │  2 × 2 cards, square, tint backgrounds,
│ └──────┘ └──────────┘          │  64 dp icon, script label (Title) + English label (Label)
│ ┌ Sankalpam ┐ ┌ Stories ┐      │
│ └───────────┘ └─────────┘      │
│ ── My sādhana · Gaṇeśa mūla mantra and 1 more   Open › │  bar, ivory surface, gold hairline top
└──────────────────────────────┘
```

- Info line built with `Labels.tithi` / `Labels.masa` from the calendar day; city from settings. No clock.
- Coming up = `ContentRepository.comingUp()`. Tap → Puja page of the first linked puja.
- My sādhana bar: active sankalpams from Room. Without any: "Begin a sankalpam ›". Never shows counts, gaps or
  "not done".
- Greeting by local hour: Good morning (4–12), Good afternoon (12–17), Good evening (17–21), Good night (21–4).

---

## 4. Puja

**Puja home**: Coming-up festival card (same as Home, smaller) → search field (56 dp, "Search a god or festival")
→ god grid, 2 columns, card 1 : 1.15, deity card image fills the top 75%, script name + English name below.

**God page**: hero image (deity hero, 45% height) → intro (Body) → symbols row (6 small round chips, tap shows
the one-line meaning in a bottom sheet) → "Pujas" list (title, kind chip, next date + tithi) → two hand-off rows:
"Stories of Gaṇeśa ›", "Chant Gaṇeśa's names ›".

**Puja page** — one scroll:
1. Header: title, next date · tithi, "about 75 min", **Guided mode** button (outlined, full width).
2. **Sticky stage bar** under the top bar: `Pre · During · Post` segmented control (48 dp). Tapping scrolls to the
   section; the section in view is selected. Gold hairline under the selected segment.
3. Steps are cards: number (gold hairline circle), IAST title (Title), image (16:12, rounded 16 dp) if present,
   body (Body), mantra blocks, chips for repeat ("× 21"), "optional" chip.
4. **Mantra block**: sacred text in the chosen text view (chip row `తెలుగు · संस्कृतम् · IAST` above the first mantra,
   remembered), meaning collapsed under "Meaning ›". Saṅkalpam fills `{placeholders}` from the calendar day and
   the user's saved name / gotra; empty fields are left out of the sentence, never shown as blanks.
5. **Samagri** step renders a checklist: item name, qty, "Instead: <alternative>" in inkSoft. Ticks are local and
   reset per puja day. Copy under the list: "Whatever you offer with love is enough."
6. **Text step** (`text:`) shows "Chant the 108 names" with **Read** and **Listen** buttons → opens Sankalpam
   practise for that text; finishing returns here. Puja itself logs nothing.
7. **Naivedyam** step shows naivedyam cards: image, "why" text, regional forms as chips with region.

**Guided mode**: full-screen, one step per page, horizontal pager, screen kept on, Body at 22 sp, mantra at 30 sp.
Bottom: ‹ Previous · step x of y · Next ›. Top: stage name and ✕. No timers.

---

## 5. Calendar

**Today panel** (top card): date · samvatsara · māsam (adhika marked) · tithi with end time · nakṣatra · sunrise /
sunset. Below a gold hairline: **Today for you** — rāśi name, the note (Body), optional "Chant <text> ›" link when
`suggest_text` is set. Chip row of all 12 rāśis to read another's note (today only). No rāśi saved: "Pick your rāśi
to see today's note" + chip row.

**Māsam view**: month grid by civil date, each cell shows date, a small moon (phase from `moon_pct` and paksha),
and dots for observances. Header shows the māsam(s) the civil month spans. Tap a day → bottom sheet day bubble
(tithi, nakṣatra, moon %, observances → each opens its observance page).

**Observance page**: image, title, date + tithi, sections Special / What families do / History / Regional notes,
then action rows: Puja ›, Story ›, Chant ›, Naivedyam ›.

---

## 6. Sankalpam

Top chips: **My sankalpams** · **Nāmalu** · **Discover** (Discover disabled with "Coming soon" until groups ship).

- My sankalpams: cards with text title, "Just me" or group name, total so far ("1,296 so far"), optional target
  ("of 10,000"), day N of M. No streaks, no today-count, no red.
- Nāmalu: god → texts list → **Practise**: segmented `Read · Listen · Count`, "Counts for" chip (none / a sankalpam).
  - Read: names one per row (number in gold hairline circle, sacred text), **Done** at the end → +1 reading.
  - Listen: player with the current name highlighted (audio timing from the text file); end → +1 reading.
  - Count: big round tap target (200 dp) with count, ring fills to 108, haptic tick, round counter.
- + New sankalpam: text → Just me / Start a group (group disabled until Phase 5) → optional target and days →
  optional intent → Begin.

---

## 7. Stories

Stories home: search + god grid (same component as Puja); "For today" card when today's observance links a story.
God → story cards (cover 4:3, title, "4 min · 4+").

**Player**: full-bleed beat image (4:3, top), text panel below on ivory. Tap the right two-thirds or swipe left →
next beat; tap the left third → previous. Start sheet asks once per story: "Little ones (4+)" / "Older children".
Older shows `more` under `short`. Language chips te / hi / en at the top-right. Progress as small dots.

Touch moments (the `touch.target` area is a separate image layer once art supports it; until then the whole image):
| effect | animation |
|---|---|
| glow | soft gold halo pulse, 2 cycles, 1.2 s |
| wiggle | ±4° rotation, 3 cycles, 0.6 s |
| hide | fade + slide down 12 dp, 0.5 s; tap again to return |
| appear | scale 0.8→1 + fade in, 0.4 s |
| bounce | 2 small hops (8 dp), 0.6 s |

A tiny hand hint pulses on beats with a touch moment until the child taps once.

Last beat: the takeaway line large (Title), then small parent links "Do the puja ›", "Chant the names ›".

---

## 8. Onboarding (once)

1. Language: three big tiles తెలుగు / हिन्दी / English.
2. Where you live: state, then city (from `master.cities`, search). Calendar MVP covers Indian cities, IST.
3. Your name (optional, used in greetings and saṅkalpam).
4. Your rāśi (optional): 12 chips + "I don't know". Explain: "Used only for a gentle note in the calendar."
No account, no permissions, no OTP.

## 9. Profile (from the name button)

Name, language, state / city, rāśi, nakṣatra, gotra (all on phone), text view default, "Delete my data".
No photo.

---

## 10. Missing images

`ContentRepository.imagePath()` returns null → show the deity symbol (for deity, puja, story) on its tint, else the
module icon on its tint. Never show a broken-image box or a grey placeholder.

## 11. Words never used in the UI

"missed", "streak", "behind", "incomplete", "you haven't", "0 today", "overdue", "reminder", "goal not met",
"bad day", "dosha", "remedy". Practice copy is past-tense and warm: "1,296 so far".
