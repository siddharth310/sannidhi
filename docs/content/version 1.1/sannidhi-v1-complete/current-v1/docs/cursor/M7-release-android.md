# M7 — Android release: internal testing, then Play Store

Prepare a signed release and the Play listing. Report back with each step done and anything you need from me.

READ FIRST:
- `docs/v1/release/release-checklist.md`, `store-listing.md`, `data-safety.md`, `privacy-policy.md`

BEFORE YOU START — ask me for: final app name (store check done), package name if it must change (normally keep
`applicationId`), the app icon artwork, and confirmation that the content review is complete.

STEP 1 — Build
- `versionCode`/`versionName` from one place; release build type with R8 shrinking and resource shrinking.
- Release builds contain only reviewed or published content and no draft audio (build fails otherwise).
- Adaptive icon (foreground on the ivory background) and a calm splash (ivory, gold hairline mark, no text).
- Baseline profile for Home, Puja page and the player (faster first open).

STEP 2 — Signing
- Play App Signing: generate an upload key; keep the keystore and passwords outside the repo and write down
  where they are stored for me. Never commit them.

STEP 3 — Checks before upload
- `./gradlew lint` clean of errors; all unit and screenshot tests pass; M1–M5 workbooks pass.
- APK/AAB size reported (target under 60 MB with the Gaṇeśa pack and audio).
- Test on three phones: a low-end Android 9–10, a mid-range Android 13–14, and the newest you have.

STEP 4 — Play Console (with me)
- Internal testing track first: upload the AAB, add testers (family and elders' group emails).
- Store listing in te, hi and en from `store-listing.md`; 4–8 phone screenshots per language.
- Data safety and content rating from `data-safety.md`; privacy policy URL (hosted copy of `privacy-policy.md`).
- Target audience: **18 and over only** (decided). Stories are family content. Use the answers in
  `docs/v1/release/data-safety.md`; never tick an age group under 13.
- In the app: Profile → About says "Made for elders and families. Stories are for listening together." No
  child sign-in prompt anywhere; sign-in screens say groups are for adults.

ACCEPTANCE:
1. Signed AAB builds from a clean checkout with one command.
2. Release AAB has no draft content or draft audio (build report).
3. Internal testing install works on all three test phones; every module works offline.
4. Listing, data safety, content rating and privacy policy submitted without warnings.
