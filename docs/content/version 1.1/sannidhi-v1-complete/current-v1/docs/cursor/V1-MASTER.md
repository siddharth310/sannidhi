# Sannidhi v1 — master prompt for Cursor

First time only: run `V1-00-INTAKE.md` (M0). It brings the files in without changing the app.
After that, paste this whole file into Cursor at the start of every session, then add one line:
**"Run M1."** (or M2, M3 …). Cursor runs that one module, reports, and stops.

---

You are finishing Sannidhi v1: a South Indian Hindu devotional app for elders and for families living far from
home. v1 is the Gaṇeśa slice, offline, in Telugu, Hindi and English. Android first (Kotlin, Jetpack Compose,
Room, DataStore, Media3, WorkManager), then iPhone (Compose Multiplatform). Content is authored in `content/`
and compiled by `tools/build_content.py` into `app/src/main/assets/content/`.

## How we work

1. I name one module. You open its prompt from the table below and read everything under its READ FIRST.
2. Before you change code, tell me in five lines or fewer what you will change and anything that conflicts with
   these rules. Wait only if something conflicts; otherwise go ahead.
3. Work on a branch `v1/mN-<name>`. Small commits, each with a plain message.
4. Finish with the **report** below, then **stop**. Do not start the next module until I write
   "MN signed off".
5. If a step needs something only I can give (a file, a key, a recording, a decision), stop at that step, ask in one
   line, and carry on with the parts that don't depend on it.

## The modules, in order

| Run | Prompt | Test workbook | Needs from me |
|---|---|---|---|
| M0 Intake (once) | `docs/cursor/V1-00-INTAKE.md` | — | The zip in the repo root |
| M1 Sankalpam (solo) | `docs/cursor/M1-sankalpam-complete.md` (P3a must already be built; if not, run `P3a-sankalpam-solo.md` first) | `tests/M1-sankalpam.xlsx` | Names are in `content/source/ganesha_ashtottara.json`; draft audio from Colab |
| M2 Stories | `docs/cursor/P2d2-stories-audio.md`, then `M2-stories-complete.md` | `tests/M2-stories.xlsx` | Narration files from Colab; beat pictures when ready |
| M3 Puja | `docs/cursor/M3-puja-complete.md` | `tests/M3-puja.xlsx` | Images when ready |
| M4 Calendar | `docs/cursor/M4-calendar-complete.md` | `tests/M4-calendar.xlsx`, `tests/calendar-check.xlsx` | TTD panchāṅgam comparison |
| M5 Home, Profile, quality | `docs/cursor/M5-home-profile.md` | `tests/M5-home-profile.xlsx` | — |
| M6 Sankalpam groups (v1 or v1.1) | `docs/cursor/M6-sankalpam-groups.md` (builds on `P3b-sankalpam-groups.md`) | `tests/M6-groups.xlsx` | Firebase project, invite-link domain |
| M7 Android release | `docs/cursor/M7-release-android.md` and `docs/v1/release/*` | `tests/M7-release.xlsx` | Keystore (kept out of git), Play account |
| M8 iPhone | `docs/cursor/M8-iphone.md` (four stages; stop after each) | M1–M6 workbooks on iPhone | Apple Developer account, Mac or cloud build |

Design references for every module: `docs/design/sankalpam-design.md` (v7) and `docs/design/v7/` for Sankalpam;
`docs/stories-audio.md` for Stories; `docs/v1/ROADMAP.md` for what "done" means.

## Rules that hold in every module

**Sacred text**
- Never type, generate, translate or "fix" a mantra, a divine name or any sacred line. Sacred text comes only from
  files in `content/` that I supply; the aṣṭottaram comes only from `content/source/ganesha_ashtottara.json`
  through `tools/import_ashtottara.py`. If something looks wrong, list it for me; do not change it.
- Release builds ship only content marked `reviewed` or `published`, and no draft audio. Dev and internal builds
  may show drafts.
- Sacred audio in a release build is a human recording. Machine-made audio is for testing only.

**Free will, not tasks**
- No streaks, reminders, badges, "0 today", missed days, gaps, red, or guilt. Show what was done, never what wasn't.
- Good times only: the calendar shows auspicious windows, never warnings or negative periods.

**Navigation and look**
- Hub and spoke. No bottom navigation. TopBar with Back and Home on every inner screen.
- Shared components only: ModuleCard, ComingUpCard, SacredText, ImageOrFallback, the god grid and search.
- Mandir palette: maroon #7A1F2B, gold #C79A3E (hairlines only), ivory #FBF4E9, peacock #1B3A4B.
  Sankalpam uses tulasi green: #3E6A33, dark #2F4F27, tint #E3EEDB, border #B7CDA8. No other accent colours.
- Fonts: Cormorant Garamond, Inter, Noto Serif Telugu, Noto Serif Devanagari. Everything readable at 200% font.
- Regional names stay regional: show the person's own name for a dish or observance first (e.g. modak, undrallu),
  never rank one region's tradition over another.

**Language**
- Every visible string exists in te, hi and en. No hard-coded English in Telugu or Hindi screens.
- Sacred text has three views: the person's script, Devanagari and IAST.

**Privacy and accounts**
- Guest first. No phone numbers and no OTP, ever. Sign-in (Google; Apple on iPhone) only for groups, and groups are
  for adults.
- City-level location only, typed or picked; no GPS. A sankalpam's reason is private unless the person shares it.
- No network calls outside groups. No analytics, ads or crash SDKs. If you think one is needed, ask; it changes
  `docs/v1/release/data-safety.md`.
- Audience is adults (elders and parents). Stories are family content, listened to with a parent. Never add child
  accounts or child sign-in, and never word the app as "for kids".
- Keys and keystores (Sarvam key, signing keystore, Firebase secrets) never go into git.

**Files still in `_incoming/`**
- `content/stories/`, `content/music/` and `content/sfx/` wait in `_incoming/sannidhi-v1-complete/current-v1/`
  until M2 step 1, which copies them in (the old versions go to `docs/_archive/`). No other module takes files from
  `_incoming/`, and nothing is ever taken from `previous/`.
- A module changes only what its own prompt names. Screens outside that module must look exactly as they did.

**Content and data**
- Content IDs never change (`ganesha.ashtottara`, `ganesha.mantra.mula`, `nv.modaka`, `obs.vinayaka-chavithi` …).
- Edit content only in `content/`; never edit generated files in `assets/content/` by hand.
- `python tools/build_content.py --check` must pass before every report.
- Room migrations, never destructive. A person's practice history must survive every update.

## Report (end of every module)

```
Module: MN — <name>        Branch: v1/mN-<name>
Acceptance checks: 1 pass · 2 pass · 3 FAIL (why, and what is needed) …
Test workbook: tests/MN-….xlsx — rows I can run on the emulator: results filled; rows that need a real phone: listed
build_content.py --check: pass/fail (paste the summary and any flagged sacred lines)
Changed: <files, one line each>
Needs from you: <files, keys, recordings, decisions — or "nothing">
Not done / later: <anything left, with the reason>
```

Then stop and wait for "MN signed off".
