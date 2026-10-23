---
name: naamalu-app-flow
description: Canonical user-flow, navigation, and information-architecture reference for the Naamalu (నామాలు) South Indian Hindu devotional app. Consult this whenever building, wiring, discussing, or scoping any Naamalu screen or feature — Puja Vidhi (festival and vratam pujas, samagri, mantras, katha, guided puja mode), Naivedyam (why each offering is made), the festival calendar and māsam meanings, Group japa, the naamavali library, the synced-lyrics Naama Player, the japa counter, the recitation ledger, Home, Saadhana, onboarding, language, sign-in, or navigation. Use it to keep screens and navigation consistent, to decide what belongs in each release phase, and to keep the Puja, Practice and Group modules correctly separated — even when the user doesn't explicitly say "the flow."
---

# Naamalu — App Flow & Information Architecture (v4)

System-level decisions (hosting, sign-in, data model, phases) live in `naamalu-architecture-and-flows.md`.
Look and components → `design-system.md`. Images → `illustrations.md`. Names content → `deity-naamavali`.
This file owns screens, navigation, flows and module boundaries.

---

## 0. Purpose and users

Naamalu gives a **sense of belonging** and keeps cultural roots alive — not growth. No ads, no campaigns, no engagement tricks.
Keep it **static and simple**: fewer moving parts, fewer ways anyone can be harmed.

1. **Elders, especially women in japa groups**, keeping collective counts toward a vow (mannat).
2. **Families living away from home** who want to do pujas properly, with alternatives and meaning.

Principles: for elders · for those far from home · belonging, not growth · every origin as it is.

---

## 1. Modules and boundaries

| Module | Contains | Writes |
|---|---|---|
| **Puja** (standalone) | Calendar, pujas, samagri, mantras, katha, naivedyam meanings, guided mode | Nothing to the ledger. Local convenience state only (samagri ticks, guided resume) |
| **Practice** | Naamalu library, reader, Naama Player, Japam, Saadhana | Recitations to the on-device, append-only ledger |
| **Group japa** | Create / join a group, shared count, today's list, bubbles | Contributions to Firebase (the only synced data) |

Shared: deity · state/tradition · app language · text view · city · design system.
Cross-links are hand-offs, not logs ("Japa this mantra" opens Japam; only that session logs).

---

## 2. Identity

- **Guest first.** Everything except Group japa works with no account, fully offline.
- **Sign in with Google** — one tap, only when creating or joining a group. **No OTP, no password, no phone number anywhere.**
- Personal backup: Android Auto Backup to the user's own Google Drive. No Naamalu server.
- Always visible in Settings and group screens: **"Naamalu will never ask for money, codes, or passwords."**

---

## 3. Language

| Control | Changes | Where |
|---|---|---|
| **App language** (తెలుగు · हिन्दी · English) | Menus, labels, prose (katha, steps, meanings) | Onboarding; afterwards Settings only |
| **Text view pill** (*My language · संस्कृतम् · IAST*) | Only sacred text: mantras, names | On every mantra/name screen |

Sanskrit (Devanagari) and IAST are always available on top of the one chosen language.
Missing translation → show IAST with "Not yet in [language]". All transliteration happens in the content pipeline.

---

## 4. Navigation

Floating bottom nav: **Home · Puja · Naamalu · Japam · Saadhana**.
Group japa: Home tile + Home card + the chip on Japam.
Home tiles (fixed order): Puja · Naamalu · Japam · Naivedyam · Calendar · Group japa.

Two content hubs connect everything: **Calendar day** and **Deity**. Every content entity carries
`deityIds, festivalIds, pujaIds, dishIds, naamavaliIds, mantraIds`; every detail screen ends with a **Connected** row.

---

## 5. Screen flows

### 5.0 Onboarding (under a minute, big buttons, skippable)
1. **Language** — buttons in their own script; tapping switches the app instantly.
2. **State / tradition** — sets puja variants, regional dish names, month system (amānta / pūrṇimānta).
3. **City** — MVP: Indian cities, dates in IST.
4. **Ishta devata** (optional) → Home. No sign-in here.

### 5.1 Home
SkyHeader (local time) with greeting + "[tithi] · [city]" → **Today card** (today's observance, or next festival within 30 days) →
6 module tiles → Group japa card (progress, or "Start or join a group japa") → Continue card (hidden without history).

### 5.2 Calendar
| Screen | Shows |
|---|---|
| Year | 12 māsams (+ adhika māsa when present) |
| **Māsam** | Significance, deity focus, what families traditionally do, festivals and vratams in it |
| **Day** | Tithi, paksha, māsam (no full panchangam) + any observance |
| **Festival / observance** | Story and history, significance, what is done, regional sections ("In Telugu homes…"), Connected row: puja, naivedyam, naamavalis |
| Recurring | Ekādaśī, Saṅkaṣṭī Chaturthī, Pūrṇimā, Pradoṣa |
Optional reminder the day before (with preparation note).

### 5.3 Puja
Entry: Calendar · Today card · Puja tab.
**Overview** (significance, Setup · Samagri · Mantras · Katha cards, Naivedyam card, Gītā 9.26 footer) →
**Samagri** (checklist, every item with an alternative, never "missing") →
**Guided puja** (card stack on the night sky; one step or one name per card; audio; tap / swipe / volume keys; screen stays on; resume) →
quiet completion card → suggestion "Chant [deity]'s naamavali" (hand-off only).
Tradition pill switches the regional variant for the view; permanent change in Settings.

### 5.4 Naivedyam — meaning, not a cookbook
Library (by festival or deity) → **Offering page**:
- Name in the user's region + "Known across homes as" chips (own script + region; none marked original)
- **Why this offering** — why this item for this deity, at this time of year
- **Symbolism / story** and **season** (why these ingredients now)
- Main ingredients (a short list, not a recipe)
- "Can't make it today?" simple alternatives (fruit, jaggery, milk)
- "Offered in" links
- **For the recipe:** "Ask family, search online, or ask an assistant" — the app does not host recipes.

### 5.5 Naamalu & Player (unchanged)
Deity → naamavali → Read (text view pill) or Listen (synced lyrics) → completion logs a Recitation → Saadhana.

### 5.6 Japam
Choose mantra / name, or free count → whole screen is the tap target → 108-bead mala, haptics → end → ledger.
If in a group for that practice: "Add these to our group?" (Yes / Not this time; remembered per group).
**Add count by hand** for japa on a physical mala (+108 quick button).

### 5.7 Saadhana (unchanged)
Calm heatmap and totals from the ledger; no streak guilt, no badges.

### 5.8 Group japa
- **Create:** title · intention · what is chanted · start date · number of days or open · optional daily reminder · open link or invite code.
- **Join:** link or 6-character code → Google sign-in (first time) → display name.
- **Group screen:** day N of M, "since [date]", consolidated total, **Today** (who, how many, most recent first), **bubble pop-up** on each new count ("Lakshmi · +108"), Start japa, Add count.
- **Counts:** simple repetitions; a mala = 108. No unit conversions.
- **Limits:** up to 200 members. Anyone can leave; the creator can remove a member or close the group.
- **Never:** chat, photos, private messages, member links, phone numbers, rankings.

### 5.9 Settings
App language · State/tradition · City · Theme · Practice (haptics, japa defaults) · Reminders ·
Group japa (sign-in, groups, delete my group account) · Backup status · About, sources, privacy.

---

## 6. Release phases

| Phase | Scope |
|---|---|
| 1 — Foundation | Design system, onboarding, Home, Settings, app language |
| 2 — Smaranam | Naamalu, Player, Japam, Saadhana, ledger, Auto Backup |
| 3 — Calendar | Year, māsam, day, festival pages, recurring observances, reminders (India, IST) |
| 4 — Puja | Puja tab, overview, samagri, guided puja, Naivedyam meanings, Connected rows, share cards |
| 5 — Group japa | Google sign-in, Firebase, create/join, group screen, bubbles, account deletion |
| 6 — Release | QA, accessibility, Play listing, production |

Later: diaspora cities and time zones · Tamil / Kannada / Marathi · puja lifecycle (Before → Day → After) · hosted content packs · admin dashboard.
**Never:** ads, payments, leaderboards, public profiles, chat, OTP.
