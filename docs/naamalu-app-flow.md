# Naamalu — App Flow & Information Architecture

Naamalu (నామాలు, "the names") is a South Indian Hindu devotional app built around sacred
name-garlands — Ashtottara Shatanamavali (108 names) and Sahasranamam (1000 names). But the app
is **not just a naamavali reader**: it is a daily *saadhana* (practice) companion. The core loop
is chant → the app counts it → your practice visibly grows over time. Everything else exists to
serve that loop.

This document defines the screens, how the user moves between them, and — most importantly — the
**tracking spine that interconnects them all**. Read it before building or changing any screen so
that a read in the library, a listen in the player, and a japa session all feed one consistent
record of practice.

Design language, tokens, palette, typography, and theming are owned by `naamalu-app-design`;
canonical name content is owned by `deity-naamavali`. This doc owns *structure and flow* and defers
to those two for look and content.

---

## 1. Core concepts (the domain model)

Everything in the app is built from a small set of nouns. Keep these names stable across UI, data
model, and analytics.

- **Deity** — a devata the user follows (Ganesha is the seed; Lakshmi and Venkateswara are next).
  One deity is the user's **ishta devata** (chosen at onboarding), which personalizes Home.
- **Naamavali** — one name-garland belonging to a deity. Has a *type* (`ashtottara` = 108,
  `sahasranama` = 1000) and carries audio and (optionally) video.
- **Naama** — a single line within a naamavali, rendered in archana form **"oṁ [name] namaḥ"**,
  shown in the user's chosen script(s) with an optional meaning.
- **Recitation** — one act of engaging a naamavali or naama: a *read*, a *listen*, or a *japa*
  session. **This is the atomic unit of tracking.** Every recitation is one row in the ledger.
- **Japa** — repetitive count of a single name or mantra on a 108-bead mala. Produces recitations.
- **Saadhana** — the user's accumulated practice: counts, streaks, history, milestones.

A useful mental model: *deity → naamavali → naama* is the content tree; *recitation → saadhana* is
the practice tree. The Naama Player and Japa counter are the two engines that turn content into
recitations.

---

## 2. The tracking spine (recitation ledger) — read this first

This is the single most important architectural idea in the app, and the answer to "how is it all
interconnected." **Do not build tracking per-screen.** There is one ledger.

Every meaningful act of practice writes one **recitation event**:

```
Recitation {
  id, userId,
  deityId, naamavaliId,
  mode: "read" | "listen" | "japa",
  count,                 // 1 for a completed read/listen; bead count for japa
  naamaId?,              // set when japa is on a specific name
  startedAt, completedAt,
  source                 // "player" | "reader" | "japa" | future: "puja"
}
```

Everything else in the app is a **read-model derived from this ledger** — never a separately
maintained counter:

- **Library card badges** ("read 12× · listened 30×") = aggregate over `naamavaliId`.
- **Home streak & today's count** = ledger grouped by day.
- **Saadhana stats** (per-deity totals, japa malas, time spent) = ledger rollups.
- **Practice calendar & day detail** = ledger grouped by calendar date; tap a day → that day's
  recitation list.
- **Practice history list** = ledger ordered by `completedAt`, newest first.
- **"Continue" on Home** = the most recent incomplete or last-touched recitation.

This mirrors the content architecture principle already in the project — *single canonical source,
derived outputs.* The ledger is the practice-side equivalent of storing names once in IAST and
deriving every script. One event updates the Home streak, the library badge, and the Saadhana chart
at once. That consistency **is** the interconnection the app promises.

**Rules of the spine**
- A read/listen logs a recitation when the user completes it (or crosses a completion threshold,
  see the Player). Partial engagement does not silently inflate counts.
- Streaks are **non-punitive**: a day with practice lights up; a missed day is simply neutral, never
  shown in error styling, never guilt-tripped.
- Later modules (Puja) also write recitations with `source: "puja"`, so they flow into the same
  Saadhana without new plumbing. Design the ledger module-agnostic now.

---

## 3. Navigation map (information architecture)

Stage-1 navigation is a **4-destination bottom bar** plus a profile entry in the top bar. Lean now,
with a defined growth path so adding modules doesn't force a re-architecture.

```
┌─────────────────────────────────────────────┐
│  Home        Library      Japa      Saadhana │   ← bottom nav (Stage 1)
│ (Mandir)   (Naamalu)   (counter)  (progress) │
└─────────────────────────────────────────────┘
        Top bar: script/theme quick-toggle · profile/settings
```

- **Home (Mandir)** — personalized devotional landing; resume practice, streak, quick japa, daily
  naama, your deities.
- **Library (Naamalu)** — browse the full content tree: deities → naamavalis → the reader/player.
- **Japa** — the counter, as a first-class practice, reachable in one tap from anywhere.
- **Saadhana** — the progress/tracking home: streaks, history, totals, milestones.

**Growth path (do not build yet, but leave room):** when Puja/Samagri/AI arrive, the 5th slot
becomes **Explore** (a hub that holds Puja, Samagri, festivals/panchangam, AI guide) rather than
cramming a 5th peer tab. Home surfaces the newest module via a card; Explore holds the full set.
Never let future modules push Japa or Saadhana out of the primary bar — daily practice stays
one tap away.

Script toggle (Telugu / Tamil / Devanagari / transliteration) and theme (Dina light / Ratri dark)
are **first-class and global**, surfaced in the top bar and inside the reader/player — not buried in
settings.

---

## 4. Screen-by-screen flows

### 4.0 Onboarding & auth

Goal: get the user to a *personalized* Home with the least friction, and capture the one preference
that makes Home feel like theirs (ishta devata).

1. **Splash** — brand mark, optional invocatory line. Routes to Home if a session exists, else auth.
2. **Auth** — phone number + password only (sign up / log in). **No OTP.** Keep this screen calm and
   short.
3. **Choose your ishta devata** — pick a preferred deity from the available set (Ganesha now; more as
   content lands). This drives Home personalization. Allow "decide later" → defaults to a general
   Home.
4. **Set your scripts & theme** (optional, skippable) — default reading script(s) and Dina/Ratri.
   Pre-select sensible defaults so a skip still works.
5. → **Home.**

First-run Home should show a one-time gentle nudge toward the first practice ("Begin with
[ishta devata]'s Ashtottara") so the empty state is never truly empty.

### 4.1 Home (Mandir / Today)

The shrine you return to daily. It answers three questions instantly: *where did I leave off, how
is my practice, what should I do now.* Everything here is derived from the ledger + ishta devata.

Composition, top to bottom:

- **Masthead** — time-of-day aware greeting with a restrained ishta-devata motif. Feels reverent,
  not gaudy; gold only as a hairline/accent.
- **Continue** — resume the last naamavali (read or listen) with one tap. Reads the ledger's last
  touched recitation. Skip the card if there's no history.
- **Today's practice** — today's recitation count and the **streak**, shown gently (a thin gold
  progress arc, non-punitive). Tapping it deep-links into Saadhana.
- **Quick Japa** — start a japa session immediately on the ishta devata's mantra (or last-used),
  without going to the Japa tab first.
- **Naama of the day** — a suggested naamavali or single naama (rotating, ishta-devata-weighted) to
  give a reason to return and something to chant right now.
- **Your deities** — ishta devata plus recently opened deities; shortcut into their naamavalis.
- **Explore the library** — doorway into Library.
- **(Later) placeholders** — festival/panchangam card, "Puja of the day," etc., slot in here first
  before they earn a tab. Leave vertical room in the layout.

Answering the original question directly — Home holds, *besides* naamavalis: resume, today's
tracking + streak, quick japa, a daily suggestion, your deities, a library doorway, and (later)
festival/puja cards. It is the tracking mechanism's front porch.

### 4.2 Library (Naamalu)

Browse the content tree. Two levels before the reader.

1. **Deity list / grid** — all available deities. Each shows a small practice badge from the ledger
   ("chanted 40×"). Ishta devata pinned or highlighted.
2. **Deity detail** — the deity's naamavalis, grouped by type (Ashtottara 108, Sahasranama 1000,
   and later other chants). Each naamavali card shows: title, type, duration (if audio), and its
   ledger badge ("read 12× · listened 30×"). This badge is the visible interconnection — the same
   number that Home and Saadhana compute.
3. Tap a naamavali → **Naamavali detail** (4.3).

Search/filter (by deity, by type, by script availability) is a Stage-1 nicety if cheap, else Stage 2.

### 4.3 Naamavali detail (Read mode)

The text-first experience. This is where reading (not listening) happens and where a *read*
recitation originates.

- **Header** — naamavali title, deity, type, source citation (from `deity-naamavali`).
- **Script toggle** — first-class control to switch/stack Telugu / Tamil / Devanagari /
  transliteration. Persists to the user's default.
- **Numbered rows** — each naama in archana form **"oṁ [name] namaḥ"**, optional meanings column.
- **Primary actions**: **Listen** (→ opens the Naama Player, 4.4) and **Read** (guided read mode
  that scrolls/paces the rows). Completing a read logs a `read` recitation.
- **Per-row action**: **Japa this name** → opens the Japa counter pre-loaded with that naama (4.5).
  This is the tie between the library and the counter.

### 4.4 Naama Player (synced lyrics) — the "Spotify for chanting"

The signature experience. **Media on top, synced lyrics below**, so the user can *follow along and
chant*, not just passively listen.

Layout:

- **Top — media stage.** Audio-only naamavali → a devotional visual (deity image / yantra / ambient
  motif) with the transport. Video naamavali → the video plays here. (Design the stage to hold
  either; Stage-1 content is mostly audio, video-capable.)
- **Bottom — synced lyric view.** The current **"oṁ [name] namaḥ"** line is highlighted and the list
  auto-scrolls, karaoke-style. Tap any line to **seek** to it. Script toggle lives here too; tapping
  a line can reveal its meaning without leaving the flow.

Transport & practice controls:

- Play / pause, scrubber, **previous/next name**.
- **Playback speed** — essential for chanting; let users slow down to learn, speed up when fluent.
- **Repeat** — loop the whole naamavali, or an **A–B loop** over a range of names for memorization.
- **Sleep timer** — for meditative/night use (pairs with Ratri theme).
- **Count-on-complete** — finishing a playthrough (or crossing a completion threshold, e.g. 90%)
  logs a `listen` recitation; a looped playthrough counts each full pass. This is where most
  "times listened" numbers come from, so make the threshold explicit and honest.

The player must feel calm and reverent — restrained controls, generous whitespace, gold only as a
thin accent on the active state.

### 4.5 Japa counter

Repetitive name/mantra count on a 108-bead mala. A distinct meditative mode from reading a full
garland.

- **Big, forgiving tap target** — tap anywhere on the bead area (and/or volume keys) to increment;
  make it usable eyes-closed.
- **Mala model** — 108 beads per round; a subtle haptic/chime at 108; track rounds (malas) beyond
  one.
- **Subject** — what's being japa'd: the ishta devata's mantra by default, or a specific naama
  passed in from the reader ("Japa this name"). Show it prominently.
- **Gentle target** — optional goal (108 / 1008), framed as intention, never a hard fail state.
- **Logging** — a session writes a `japa` recitation (with `naamaId` when applicable) on completion
  or at each mala, feeding Saadhana and per-name/per-deity totals.

### 4.6 Saadhana (Progress / tracking)

The tracking mechanism made visible. Entirely derived from the ledger.

- **Streak** — current and longest, **non-punitive** (missed days neutral, never red/error styling).
- **Practice calendar** — a heatmap of days practiced. **Tappable:** each day opens **Day detail**
  (4.6.1) so the user can see exactly what they did that day.
- **Practice history** — a scrollable log of past recitations across all days (4.6.1).
- **Totals** — lifetime recitations, split by mode (read / listen / japa), per-deity breakdown, japa
  malas completed, time spent.
- **Per-naamavali history** — times read/listened and last practiced (the same badges seen in
  Library, here in detail).
- **Milestones** — reverent, not gamified: e.g. "108 recitations of [naamavali]" acknowledged
  quietly and beautifully. No aggressive streak-shaming, leaderboards, or loss-aversion mechanics —
  this is devotion, not a habit-hacking app.

### 4.6.1 Daily practice log & day detail

Users need to see not just *that* they practiced on a day, but *what* they did and *how much*.
Both views are **read-models over the recitation ledger** — never a separately maintained log.

**Day detail (calendar tap)**

Tapping a day on the practice calendar opens a **Day detail** screen for that calendar date.

- **Day header** — date, total recitations that day, split by mode (e.g. "2 listened · 1 read ·
  324 japa beads").
- **Recitation list** — every completed recitation logged that day, ordered chronologically
  (default: most recent first). Each row shows:
  - time of completion
  - mode (read / listen / japa)
  - deity + naamavali title (or specific naama/mantra label for japa when `naamaId` is set)
  - count (1 for a completed read/listen; bead count for japa)
  - duration, when available
- **Empty days** — days with no practice show neutral copy ("No practice logged this day"). Never
  guilt language, never error styling. Unpracticed days on the calendar stay visually muted.
- **Today** — selectable like any other day; shows everything completed so far today (feeds the same
  data as Home's "Today's practice").

**Practice history list**

A scrollable **History** section on Saadhana — below the calendar or as a segment within the
screen — lists recitations across all days, **newest first**. Same row shape as Day detail.

- Answers "what have I done lately?" without picking a calendar day first.
- Paginate or lazy-load by month; do not cap history artificially in Stage 1.
- **Tap a row** → deep-link to the relevant destination: naamavali detail, Naama Player resume
  point, or Japa counter with that subject pre-loaded, as appropriate.

**Filters (Stage 1, lightweight)**

Optional filter chips above the history list (and optionally scoped within Day detail):

- by **mode** (read / listen / japa)
- by **deity**
- by **naamavali** (enabled once a deity is selected)

Filters narrow the visible list only; underlying totals elsewhere (badges, streaks) always come
from the full ledger.

**Ledger queries (implementation note)**

- Day detail = recitations where `completedAt` falls on the user's local calendar date.
- History = all recitations ordered by `completedAt` descending.
- Day header totals = group/sum by `mode` for that date.
- One ledger row → one history row. No duplicate counting in UI aggregates.

### 4.7 Settings & profile

- **Account** — phone/password management.
- **Reading defaults** — default script(s), meanings on/off.
- **Appearance** — Dina / Ratri (with the meditation-friendly dark), auto/manual.
- **Practice** — chime/haptic preferences, japa defaults, notification/reminder (a single gentle
  daily nudge, opt-in).
- **About / sources** — content citations and credits.

---

## 5. End-to-end journeys (how the pieces connect)

**Daily returning user.** Opens app → Home shows streak + "Continue: Ganesha Ashtottara" → taps
Continue → Naama Player resumes, synced lyrics follow → finishes a pass → `listen` recitation logs →
Home streak, Library badge, and Saadhana totals all update from that one event. This single trace is
the whole thesis of the app.

**Learner memorizing a garland.** Library → Ganesha → Ashtottara → Listen → sets speed to 0.75× and
A–B loops names 1–18 → later switches to Read mode with transliteration + meanings → over days,
Saadhana shows rising reads/listens on that naamavali.

**Japa practitioner.** Home → Quick Japa on ishta devata's mantra → completes 3 malas → `japa`
recitations log → Saadhana per-deity japa total climbs. Or, from a naama row in the reader, "Japa
this name" launches the counter pre-loaded on that name.

**Reviewing practice history.** Saadhana → tap yesterday on the calendar → Day detail shows
"1 listened (Ganesha Ashtottara), 324 japa beads (Om Gam Ganapataye)" with timestamps. Or scroll
the History list, filter by deity = Ganesha, and tap a row to reopen that naamavali in the player.

**First run.** Splash → phone+password → choose Ganesha as ishta devata → skip script/theme (defaults
applied) → Home with a one-time "Begin with Ganesha's Ashtottara" nudge → first recitation seeds the
ledger and lights the first streak day.

---

## 6. Stage 1 (known scope) vs later stages

**Stage 1 — build now:**
- Onboarding & auth (phone + password, no OTP; ishta devata; script/theme defaults).
- Library: deity list → deity detail → naamavali detail, with ledger badges. Ganesha seeded;
  Lakshmi and Venkateswara next.
- Naamavali detail / Read mode: multi-script archana rows, optional meanings, citations.
- Naama Player: audio synced lyrics (video-capable stage), speed, repeat/A–B, sleep timer,
  count-on-complete.
- Japa counter with mala model and logging.
- The recitation ledger + Saadhana (streaks, calendar, day detail, practice history list, totals,
  per-naamavali history, lightweight filters).
- Settings (scripts, theme, account, gentle reminder).
- Companion **web admin dashboard** for content CRUD (deities, naamavalis, audio, users) — the
  content pipeline behind the app.

**Stage 2+ — architected for, not built:**
- **Puja module** — guided puja/vidhi, sankalpa, step-by-step ritual flow; writes recitations with
  `source: "puja"` into the same ledger.
- **Samagri** — checklist + commerce for puja materials (introduces cart/checkout concerns; keep it
  out of the practice ledger).
- **More chant types** — stotram, suprabhatam, ashtakam under the same naamavali/reader/player model.
- **Festivals / panchangam** — tithi-aware Home cards, festival calendar.
- **AI real-time experiences** (the "real time" the product wants): live pronunciation feedback while
  chanting; "explain this name" on tap; a conversational guru/priest assistant; adaptive audio
  (preferred voice/speed); personalized practice guidance and suggestions driven by the ledger.

Keep Stage-1 code honest about these seams (Section 7) but do not build ahead of need.

---

## 7. Extension points (so growth doesn't mean a rewrite)

- **Ledger is module-agnostic.** `mode` and `source` are open enums; Puja adds a source, not a new
  tracking system. Saadhana rolls up whatever writes to the ledger.
- **Content model generalizes.** A naamavali is one *practice content type*; stotram/suprabhatam are
  new types reusing the reader + player. The player's "synced lyric" component is content-type
  agnostic.
- **Nav has a reserved slot.** Home cards are the staging ground for new modules; the 5th bar slot
  becomes an **Explore** hub (Puja, Samagri, festivals, AI), never a shove of Japa/Saadhana.
- **AI hooks, not AI dependencies.** Stage 1 leaves obvious insertion points — a per-name "explain"
  affordance, a player state stream that a future pronunciation model can consume, ledger-derived
  signals a recommender can read — without any Stage-1 feature depending on AI to function.

---

## 8. Open design decisions (confirm or override)

These are the genuine forks I chose a default for. Flag any you want changed and the flow updates
around them.

1. **Bottom nav = Home / Library / Japa / Saadhana**, with Explore reserved for later. (Alt: fold
   Japa into Home/reader and use the 4th slot for Explore now.)
2. **Japa as its own tab** vs. only launched contextually from Home + naama rows.
3. **Video in Stage 1?** I designed the player video-capable but assumed Stage-1 *content* is mostly
   audio. Confirm whether video naamavalis ship in Stage 1.
4. **Count-on-complete threshold** for logging a listen (I assumed ~90% of a pass, and each full loop
   counts). Set the exact rule you want, since it defines every "times listened" number.
5. **Home naming** — "Mandir" vs "Today" vs "Home" for the first tab's label and feel.

---

## 9. Alignment with existing skills

- **`naamalu-app-design`** owns palette (maroon / gold / ivory / peacock indigo, gold as restraint),
  Dina/Ratri themes, and typography. This doc references those but never redefines them.
- **`deity-naamavali`** owns canonical name content (IAST → all scripts), archana form
  "oṁ [name] namaḥ", meanings, and citations that the reader and player display.
- This skill owns **structure, navigation, screen flow, and the recitation ledger**. When these three
  meet on a screen, look here for *where things go and how they connect*, to the design skill for
  *how they look*, and to the content skill for *what they say*.
