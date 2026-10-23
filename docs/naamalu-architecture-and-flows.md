# Naamalu — Architecture & Flows (v2, Sept 2026)

Companion docs: `design-system.md` (look & components) · `naamalu-app-flow/SKILL.md` (screen-level flows) ·
`illustrations.md` (imagery). The flow file (v4) matches this document.

---

## 1. Purpose and users

Naamalu gives a **sense of belonging** and keeps cultural roots alive. It is not built for growth: no DAU/MAU goals,
no ads, no campaigns, no engagement tricks.

| | Elders in japa groups | Families living abroad |
|---|---|---|
| Opens the app for | Daily japa, group count, today's observance | Upcoming festival, how to do the puja, what to cook |
| Must-haves | Large type, app in their language, manual count, listen-first, zero scam surface | Local dates, alternatives for every item, katha to explain to children, share with family |
| Main flows | Japam · Group japa · Calendar | Calendar · Puja · Naivedyam |

**Principles:** for elders · for those far from home · belonging, not growth · every origin as it is ·
**static and simple** — the fewer moving parts, the fewer ways anyone can be harmed.

---

## 2. System overview

```
┌──────────────────────── ANDROID APP (does almost everything) ────────────────────────┐
│  Bundled content (pujas, calendar, dishes, naamavalis, audio, illustrations)          │
│  On-device data: DataStore (preferences) · Room (ledger, puja progress)               │
│  Personal backup: Android Auto Backup → user's own Google Drive (no server)           │
└───────────────────────────────┬──────────────────────────────────────────────────────┘
                                │ only for Group japa, only after Google sign-in
                     ┌──────────▼──────────┐
                     │  FIREBASE (Google)  │  Auth (Google sign-in) · Firestore (groups, counts)
                     └─────────────────────┘
  Static web page (GitHub Pages / Google Sites): privacy policy · account deletion request
  Content pipeline (Python, off-device): transliteration, validation, calendar computation → bundled into the app
  Admin dashboard: later phase (not needed while content is bundled)
```

**What needs the internet:** only Group japa. Everything else works offline, like a calculator.

---

## 3. Identity and safety

### 3.1 Guest first
The app opens and works fully with **no account**: puja, calendar, naivedyam, naamalu, japa, Saadhana.

### 3.2 Sign in with Google — only to join or create a group
- One tap via Android Credential Manager; the phone already has a Google account.
- No password to create or forget. **No OTP anywhere.** Recovery and suspicious-login protection are Google's.
- Stored on device: only the Firebase session (managed by the Firebase SDK). No password is ever stored.
- Stored on server: Google user ID, chosen display name, group memberships and counts. **No phone number, no email shown to anyone.**

### 3.3 Anti-scam rules (non-negotiable)
1. The app **never asks** for money, codes, OTPs, passwords, bank or card details.
2. No payments, no donations, no purchase links.
3. No chat, no private messages, no photos, no member-posted links.
4. Members see only display names — never phone numbers or emails.
5. The only free text is a group's title and intention, written by its creator (length-limited, word-filtered).
6. A permanent line in Settings and on every group screen: **"Naamalu will never ask for money, codes, or passwords."**
7. Notifications never contain links or requests for action other than opening the app.

### 3.4 Personal backup
Android Auto Backup saves the ledger and preferences to the user's own Google Drive. Restores automatically on a new
phone signed into the same Google account. No Naamalu server involved.

---

## 4. App architecture (Android)

Stack: Kotlin · Jetpack Compose · Room · DataStore · Media3/ExoPlayer · WorkManager · Firebase Auth + Firestore (group module only).

```
:app
:core:design      tokens, icons, landscape, components (design-system.md)
:core:data        Room DB (ledger, puja progress), DataStore (prefs), repositories
:core:content     bundled content loader, link graph, calendar lookup
:core:i18n        app strings te / hi / en, script fonts
:feature:onboarding   :feature:home       :feature:calendar
:feature:puja         :feature:naivedyam  :feature:naamalu   :feature:player
:feature:japa         :feature:saadhana   :feature:group     :feature:settings
```

Rules:
- Only `:feature:group` depends on Firebase. Removing it must leave the rest of the app working.
- Puja writes nothing to the ledger. Practice (naamalu, player, japa) writes to the ledger. Group receives contributions.
- All sacred text comes from bundled content; none is typed into code.
- Permanent IDs for every entity (e.g. `ganesha.ashtottara.047`, `puja.vinayaka-chavithi`, `dish.undrallu`).

---

## 5. Language and one-time setup

### 5.1 Two separate controls
| Control | Changes | Where |
|---|---|---|
| **App language** | Menus, labels, buttons, prose (katha, steps, recipes) | Onboarding, then Settings only |
| **Text view pill** | Only sacred text (mantras, names): *My language · संस्कृतम् · IAST* | On every mantra / name screen |

Sanskrit (Devanagari) and IAST are always available on top of the one chosen language.
Launch app languages: తెలుగు · हिन्दी · English.

### 5.2 Onboarding (under a minute, big buttons, all skippable)
1. **Language** — buttons in their own script; tapping switches the app instantly.
2. **State / tradition** — sets puja variants, dish names, month system (amānta / pūrṇimānta) in one choice.
3. **City** — sets festival dates and the sky. MVP: Indian cities, dates in IST.
4. **Ishta devata** (optional) → Home.

No sign-in during onboarding.

---

## 6. Flows

### 6.1 Smaranam / Japam
Entry: Naamalu tile · today's observance · a deity page · a puja's mantra · a group.
- **Naamalu:** deity → naamavali → Read (text view pill) or Listen (synced lyrics) → completion writes a Recitation → Saadhana.
- **Japam:** choose mantra / name, or free count → tap anywhere → 108-bead mala with haptics → end → saved to ledger.
- If the user is in a group for that practice: "Add these to our group?" (Yes / Not this time; remembered per group).
- **Add count by hand** for japa done on a physical mala.

### 6.2 Puja
Entry: Calendar · Home festival card · Puja tab.
Overview → **Prepare** (samagri with alternatives, what to cook) → **Guided puja** (card stack, audio, tap/swipe) →
quiet completion → suggestion "Chant the deity's naamavali" (a hand-off, not a log).

**Naivedyam is meaning, not a cookbook:** each offering page explains why this item, for this deity, at this time of year;
its symbolism and season; regional names; main ingredients; simple alternatives. For the recipe the app says
"Ask family, search online, or ask an assistant" — no recipes are hosted.

### 6.3 Calendar with meaning
| Level | Shows |
|---|---|
| Year | 12 māsams (+ adhika māsa when present) |
| Māsam page | Significance, what families traditionally do, its festivals and vratams |
| Day | Tithi, paksha, māsam — dated for the user's city (no full panchangam) |
| Festival page | Story and history, significance, what is done, regional sections ("In Telugu homes…", "In Maharashtra…"), links to puja, dishes, naamavalis |
| Recurring observances | Ekādaśī, Saṅkaṣṭī Chaturthī, Pūrṇimā, Pradoṣa |

### 6.4 How everything connects
Two hubs: **Calendar day** and **Deity**. Every content entity carries links: `deityIds, festivalIds, pujaIds, dishIds, naamavaliIds, mantraIds`.
Every detail screen ends with a **Connected** row. Home's *Today* card is driven by the calendar.

```
Calendar day ─► Festival / observance ─┬─► Puja vidhi ──► Naivedyam (dishes)
                     ▲                 ├─► Naivedyam
         Deity ──────┘─────────────────┴─► Naamavali / mantra ─► Japam ─┬─► Saadhana (on phone)
                                                    ▲                    └─► Group japa (synced)
                         Puja mantra ── hand-off ───┘
```

### 6.5 Additional flows
- **Gentle reminders** (opt-in): day before a festival with prep notes; daily japa time; group start day.
- **Share with family:** a puja guide or recipe as an image/text card — no tracking links.
- **Save for offline:** a festival's audio and images downloaded ahead (only relevant once content is hosted).
- **Listen-first mode:** puja steps and katha as audio with large controls.

---

## 7. Group japa (virtual gathering)

### 7.1 Scope — just this much
**Create:** title · intention · what is chanted (mantra or naamavali) · start date · number of days or open-ended ·
optional daily reminder time · open to anyone with the link, or invite-only by code.
**Join:** invite link or 6-character code → Google sign-in (first time only) → display name.
**Counts:** simple repetitions; a mala = 108; no unit conversions.
**Group screen:** day N of M and "since [start date]" · consolidated total · **Today:** who read and how many times (most
recent first) · **bubble pop-up** when someone adds ("Lakshmi · +108") · Start japa · Add count.
**Controls:** anyone can leave; the creator can remove a member or close the group.
**Not included:** chat, photos, private messages, member links, phone numbers, rankings, per-person leaderboards.

### 7.2 Data model (Firestore)
```
groups/{groupId}
  title, intention, practiceId,
  startDate, days (null = open), reminderTime?, visibility (link|code),
  inviteCode, creatorUid, status (active|closed), total, createdAt
groups/{groupId}/members/{uid}
  displayName, joinedAt, role (creator|member), lastContributionAt
groups/{groupId}/contributions/{contributionId}
  uid, displayName, count, source (japa|read|listen|manual), localDate, createdAt
```
- `total` updated in a transaction with each contribution.
- "Today" = contributions where `localDate` = group's current day.
- Bubble pop-up = realtime listener on new contributions.

### 7.3 Security rules (summary)
- Read a group: members only. Join: anyone with the valid code/link while `status = active`.
- Write a contribution: only for your own `uid`, only in groups you belong to, `count` within sane bounds (1–10,000 per write).
- Membership capped at 200.
- Edit title/intention, remove members, close: creator only.
- No field accepts URLs; text fields length-limited and filtered.

---

## 8. Data needed

| Dataset | Contents | Source / method | Needed for |
|---|---|---|---|
| Regions & traditions | State → languages, month system, default puja variants | Authored | Onboarding, everything |
| Cities | Name, lat/long — MVP: Indian cities, IST only | Open datasets | Dates, sky |
| Calendar | Per year per city: date → tithi, paksha, māsam, observances | Pipeline (Swiss Ephemeris–based Python), checked against published panchangams | Calendar, Home |
| Māsam & festival texts | Significance, traditions, history, regional notes | Written from sources, human-reviewed | Calendar |
| Pujas | Per puja × region: steps, mantras (Sanskrit → IAST → script), samagri + alternatives, setup, katha, audio | Authored + pipeline transliteration, reviewed | Puja |
| Naivedyam | Regional names (script + region), why this offering at this time, symbolism, season, main ingredients, alternatives, festival links — **no recipes** | Authored (content templates) | Naivedyam |
| Naamavalis & mantras | Names, IDs, audio + timing (Ganesha done) | Existing pipeline | Practice |
| Link graph | Deity ↔ festival ↔ puja ↔ dish ↔ naamavali ↔ mantra | Authored in content | Connections |
| App strings | te / hi / en | Translated, reviewed | Everything |
| Illustrations | Per `illustrations.md` | Owner | Everything |
| Group data | Groups, members, contributions | Firestore (§7.2) | Group japa |
| On device | Preferences, ledger, puja progress | DataStore, Room | Everything |

---

## 9. Hosting and Play Store

| Need | Solution | Cost |
|---|---|---|
| App distribution | Google Play (internal testing → closed → production) | One-time developer fee |
| Group japa | Firebase Auth + Firestore | Free tier at this scale |
| Privacy policy (required) | Static page — GitHub Pages or Google Sites | Free |
| Account deletion (required once sign-in exists) | Static page with request form/email + in-app "Delete my group account" | Free |
| Content updates | Bundled; ship with app releases | — |
| Admin dashboard | Later, only if content moves off-bundle | — |

Play Console also needs: Data safety form (declare Google sign-in, display name, group counts), content rating, store listing assets.

---

## 10. Release phases

| Phase | Scope | Needs internet? |
|---|---|---|
| **1 — Foundation** | Design system, onboarding (language, state, city), Home, Settings, i18n | No |
| **2 — Smaranam** | Naamalu library, reader, player, Japam, Saadhana, ledger, Auto Backup | No |
| **3 — Calendar** | Māsam, day, festival pages, recurring observances, reminders | No |
| **4 — Puja** | Puja tab, overview, sections, guided puja, Naivedyam, link graph, share cards | No |
| **5 — Group japa** | Google sign-in, Firebase, create/join, group screen, bubbles, deletion page | Yes (group only) |
| **6 — Polish & release** | QA, accessibility, Play Store listing, production rollout | — |

Each phase ends on the Play Store internal testing track.

---

## 11. Next steps
1. Confirm this document; update `naamalu-app-flow/SKILL.md` to match (Google sign-in, group japa scope, app language vs text view).
2. Write the **data contract spec**: content schema, permanent IDs, ledger event, Firestore model.
3. Build the **module-wise workbook** (test-case-workbook format) tagged by phase and status.
4. Start **Phase 1** in Cursor; set up Play Console and the privacy page.
5. In parallel: one year of calendar data, three launch pujas with dishes, P0 illustrations.

## 12. Decisions (resolved Sept 2026)
1. **Deities:** depicted in form, in the app's illustration style — conveying nature, power, stature, wisdom and intellect (see `illustrations.md`).
2. **Content:** owner fills the content templates workbook (calendar, puja format, naivedyam meanings).
3. **Calendar MVP:** Indian locations, IST. Diaspora cities later.
4. **Group counts:** simple 108-based repetitions, no unit conversion.
5. **Group size:** up to 200 members.
