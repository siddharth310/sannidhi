# M5 — Home, Profile and app-wide quality

The last pass before release: Home, Profile, language, accessibility, offline and stability across the app.
Report back with every acceptance check and `tests/M5-home-profile.xlsx`.

READ FIRST:
- `docs/app-flow.md` §0–3, §8, §9; `docs/design-v6.md`
- `docs/v1/release/privacy-policy.md` (the in-app copy of the policy)

STEP 1 — Home
- Greeting by name and time of day; one info line: date · tithi · city (no clock).
- Coming up card: today's observance with Start the puja, else the next one with Prepare.
- Four cards; sādhana bar: active sankalpams ("<first> and N more") or "Begin a sankalpam".
- Language pill te / hi / en switches the whole app immediately.

STEP 2 — Profile
- Name, language, state and city, rāśi, nakṣatra (with Find my nakṣatra), gotra, default text view.
- **About**: app version, content pack version, "Made with care for families", credits.
- **Licences**: fonts (SIL Open Font License: Cormorant Garamond, Inter, Noto Serif Telugu, Noto Serif
  Devanagari), music and recordings credits from `content/**/<file>.licence.txt`, open-source libraries
  (generated list).
- **Privacy**: the policy text in the app language, readable offline.
- **Delete my data**: confirm → clears DataStore, Room and cached files → onboarding. (Groups data deletion
  comes with M6.)

STEP 3 — App-wide quality
- 200% font: no clipped text on any screen (screenshot test of every route).
- TalkBack: every icon-only control has a label; sacred text is announced once, not per letter.
- Contrast: body text ≥ 4.5:1 on its background in light and dark.
- Offline: every module works in airplane mode from a fresh install.
- Cold start under 2 s on a mid-range phone; no ANR; no crash in a 30-minute monkey run.
- No analytics, ads, tracking SDKs or network calls except groups (later) and the store's own.

ACCEPTANCE:
1. te → hi on Home: every label in every module changes without restart.
2. Delete my data from a phone with japams and samagri ticks → onboarding; nothing left (check app storage).
3. Licences and Privacy open offline.
4. Screenshot test at 200% passes for all routes; TalkBack walk of Home → Puja → step passes.
5. `adb shell monkey` 30 minutes: no crash.
6. Network inspector during a full walk-through: no requests.
7. All of `tests/M5-home-profile.xlsx` passes.
