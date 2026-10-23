---
name: "naamalu-app-flow"
description: Canonical user-flow, navigation, and information-architecture reference for the devotional app (working name Sannidhi, earlier Naamalu / Smaranam). Consult this whenever building, wiring, discussing, or scoping any screen or feature — Home hub, Puja (god grid, pre / during / post on one page, guided mode, naivedyam), Calendar (māsam view, today's panel, daily rāśi note), Sankalpam (nāmalu read / listen / count, my sankalpams, groups, discover), Stories (interactive god stories for ages 4–12), profile, onboarding, language, sign-in, navigation, or phases. Use it to keep screens consistent and modules correctly separated, even when the user doesn't say "the flow."
---

# App Flow & Information Architecture (v6, Oct 2026)

Working name **Sannidhi** (store and trademark checks pending). IDs, folders and code never use the app name.
Content contract: `docs/content-architecture.md`. Screen specs: `docs/design-v6.md`. Kotlin contract: `app/src/main/java/app/sannidhi/content/`.

---

## 0. Purpose and principles

Belonging, not growth. No ads, payments, ranks, streaks, chat, OTP or phone numbers.
Users: (1) elders, especially women in japa groups keeping collective counts; (2) families living away from home,
and their children.

Principles:
1. **For elders.** Large type, script + English labels, few steps, nothing hidden in menus.
2. **For those far from home.** Local dates, an alternative for every item, stories children can read.
3. **Free will, not tasks.** The app never shows what you *didn't* do. No reminders, no "0 today", no unfinished
   badges, no done-for-today checklists. If you practised, it's counted; if not, nothing is said.
4. **No fear.** The rāśi note never frightens, predicts loss, or sells a remedy.
5. **Every origin as it is.** Regional forms sit side by side; none is "the original".
6. **Static and simple.** One hub, short paths, everything offline except groups.

---

## 1. Modules

| Module | Job | Writes |
|---|---|---|
| **Home** | The hub: today, what's coming, entry to everything, my sādhana | Nothing |
| **Puja** | Pick a god → pick a puja → prepare, perform, finish (one page) | Local only: samagri ticks, saṅkalpam fields |
| **Calendar** | Māsam, tithi, moon, observances; today's rāśi note | Nothing |
| **Sankalpam** | Read / listen / count nāmalu; my sankalpams (solo and groups); discover groups | Practice entries (phone); group contributions (Firebase, only in a group) |
| **Stories** | Interactive god stories for ages 4–12 | Nothing |

**Retired from v5:** bottom navigation; Nāmalu as a tab (now inside Sankalpam); separate Japam / Saadhana tabs;
Naivedyam tile on Home (now inside Puja).

Cross-links are hand-offs, never duplicates: an observance opens its puja; a puja step opens a nāmalu in Sankalpam;
a festival day offers its story.

---

## 2. Navigation — hub and spoke

- **No bottom navigation.** Home is the hub; every module opens from it.
- Every inner screen has a top bar: **← Back** (left, large) and a **Home** button (right). System back works too.
- Keep paths short: no screen is more than three taps from Home.
- Settings and profile open from the name button on Home.

---

## 3. Home (top to bottom)

1. **Sky header, at most 25% of the screen.** Greeting, then one info line:
   `Sun, 11 Oct · Āśvayuja kṛṣṇa dvādaśī · Vijayawada`. No clock (the phone shows it). Language pill and the
   user's name (no photo) at top right. Collapses to a slim header on scroll.
2. **Coming up — one card** (merges "today" and "upcoming puja"):
   - Today is an observance → `Today · Vināyaka Chavithi` · tithi · **Start the puja**.
   - Otherwise the next observance that has a puja → `In 3 days · Sat 14 Oct · Bhādrapada śukla chaviti` · **Prepare**.
   - The card always has something; monthly observances (e.g. Saṅkaṣṭi) fill quiet weeks.
3. **Four large cards (2 × 2):** Puja · Calendar · Sankalpam · Stories. Icon, script label, English label.
4. **My sādhana bar** (bottom of Home):
   - With sankalpams: `My sādhana · Gaṇeśa mūla mantra and 2 more · Open` → My sankalpams (§6).
   - Without: `Begin a sankalpam` → Sankalpam create.
   - Never shows progress gaps, "not done" states, or zero counts.

---

## 4. Puja

### 4.1 Puja home
- **Festival CTA on top:** next festival that has a puja, with date and tithi → opens that puja.
- **Search box:** "Search a god or festival" (type-ahead, script or English).
- **God grid:** two big cards per row, scrolls; image, script name, English name.

### 4.2 God page
- Hero image and a short intro.
- **Pujas for this god:** festival · vratam · daily, each with its next date.
- Hand-offs: *Stories of Gaṇeśa* → Stories; *Chant Gaṇeśa's names* → Sankalpam.

### 4.3 Puja page — one scrolling page, three sections
Header: title, next date and tithi, about how long it takes.
A **sticky stage bar `Pre · During · Post`** jumps to each section and highlights the one in view.

| Section | Contains |
|---|---|
| **Pre** | Setup (image + short steps, simplest acceptable version first); samagri list with alternatives (tickable, local only); optional saṅkalpam details (name, gotra, nakṣatra, rāśi — on phone) |
| **During** | Step cards in order: title, image, what to do, mantra (in the chosen text view), repeat count. Katha appears as a step **only in pujas that have one**. |
| **Post** | Naivedyam (meaning cards with regional forms), harati, udvāsana and after-care (eco-friendly first) |

**Guided mode** (button on the page): full-screen, one step at a time, big text, screen stays on, swipe or
tap to move. Same content, no new data. Puja logs nothing to practice.

---

## 5. Calendar

- **Today panel (top):** date, tithi, nakṣatra, māsam, samvatsara; **today's rāśi note** for my rāśi.
  A chip row lets you read other rāśis' notes. **Today only**; no past or future notes.
  No rāśi saved → "Pick your rāśi to see today's note" (optional; "I don't know" just shows the chips).
- **Māsam view:** moon phase per day, tide band, observance dots; tap a day → day bubble (tithi, moon %,
  observances, actions) → observance → its puja / story / nāmalu.
- **Year view:** samvatsara, Ugadi to Ugadi, 12 māsams (+ adhika).
- **Source:** TTD Āsthāna Siddhānti panchāṅgam (Relangi Tangirala). Dates are computed, checked against it every
  Ugadi, and corrected with overrides (see `content-architecture.md` §6.7). Rules in amānta; labels converted for
  pūrṇimānta users.

---

## 6. Sankalpam (nāmalu + japa + groups)

Top chips: **My sankalpams** (default) · **Nāmalu** · **Discover**.

- **My sankalpams:** everything I started for myself and every group I joined, one list. Card: text, solo or
  group, total so far, day N of M for groups. Tap → Practise. Opened directly by the Home sādhana bar.
- **Nāmalu:** God → texts (aṣṭottaram, sahasranāmam, mantra, stotram) → **Practise** with Read · Listen · Count.
  Practising without a sankalpam is fine; it still counts as my practice.
- **+ New sankalpam:** pick text → **Just me** or **Start a group** → optional target and days → optional intent.
  Solo lives on the phone. A group needs Google sign-in and lives in Firebase.
- **Discover:** globe of open groups; join with display name and optional intent (shown to the group or private,
  member's choice). Invite code for invite-only groups.

Counting rules (unchanged from v5): Read to the end + Done = 1 reading; Listen to the end = 1 reading;
Count = taps, 108 taps = 1 round. Every entry goes to my practice; if a sankalpam is selected in the
"Counts for" chip, it also goes there. Manual add up to 7 days back, marked "added by hand".

Group rules (unchanged): up to 200 members; anyone can leave; organiser can remove or close; Report on every
public card; never chat, photos, private messages, phone numbers or rankings.

---

## 7. Stories

- **Stories home:** search + the same god grid as Puja; a *For today* story when today's observance links one.
- **God → stories:** cover, minutes, age label (4+ / 8+).
- **Story player:** one **beat** per screen. The illustration changes as the story moves; tap or swipe to go on.
  Some beats have a small touch moment (tap the mouse and it scurries; tap the moon and it hides).
  - Every beat has a **short line** (for 4+, big type). Beats may add a **more** paragraph, shown when "Older
    children" is chosen at the start of the story. One story serves 4–12.
  - Language switch inside the story (te / hi / en).
  - Last beat: one line the child can hold on to; for parents, small links to the puja or the names.
- No narration audio in MVP (planned later as "Read to me").
- One telling per story with its source noted. Where regions tell it differently, the other telling is a
  separate story, not a correction.

---

## 8. Profile

Display name (no photo), app language, state and city, rāśi / nakṣatra / gotra (phone only), Google account
(only if in a group), delete my data. To be revisited after v6 ships.

---

## 9. Identity and data

- Guest first. **Sign in with Google** only to start or join a group. No OTP, password or phone.
- App language te / hi / en. Text view for sacred text: my language script · संस्कृतम् · IAST.
- On the phone: preferences, practice entries, sankalpams, puja ticks, saṅkalpam fields, rāśi — never uploaded.
- Firebase (groups only): group, membership (display name, city, intent + visibility), contributions.
- Never collected: surname, age, date of birth, phone, email, gender, GPS, photos.

Data model:
- **PracticeEntry** — textId, mode (read | listen | count | manual), count, unit (reading | repetition),
  startedAt, endedAt, sankalpamId?
- **Sankalpam** (phone) — id, textId, kind (solo | group), groupId?, target?, days?, startDate, intent?,
  status (active | completed | left)
- **Group**, **Membership** — as in v5.

---

## 10. Phases (vertical slice: Gaṇeśa first)

| Phase | Scope |
|---|---|
| 1 — Foundation | Design system, onboarding (language, state/city, optional rāśi), Home hub, top-bar navigation, profile |
| 2 — Gaṇeśa slice | Puja (Vināyaka Chavithi, full), Sankalpam solo (aṣṭottaram + mūla mantra), Stories (Gaṇeśa set), Calendar with Gaṇeśa observances and the rāśi note — all from `content/` |
| 3 — Calendar full | Full samvatsara, all observances, Ugadi refresh process |
| 4 — More gods | Next deities through the same content templates |
| 5 — Groups | Google sign-in, Firebase, create / join / discover, report, account deletion |
| 6 — Release | QA, accessibility, Play Store and App Store listings |
