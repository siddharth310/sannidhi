# M8 — iPhone: same app on iOS with Compose Multiplatform

Do this in stages; each stage leaves Android working. Report back at the end of each stage.

BEFORE YOU START — ask me to confirm: Apple Developer Program membership, and how we build for iOS (a Mac with
Xcode, or a cloud macOS build such as GitHub Actions or Codemagic). Stop until I answer.

STAGE 1 — Shared core (no UI change)
- Create `shared/` (Kotlin Multiplatform: android, iosArm64, iosSimulatorArm64).
- Move content models, `ContentRepository`, JSON parsing (kotlinx.serialization), practice and sankalpam data
  (Room KMP), settings (DataStore KMP) and the calendar/good-times logic into `shared/`.
- Android app depends on `shared/`. All Android tests still pass.

STAGE 2 — Shared screens
- Move screens to Compose Multiplatform in `shared/`; resources and fonts via Compose resources
  (Cormorant Garamond, Inter, Noto Serif Telugu, Noto Serif Devanagari).
- Android looks and behaves exactly as before (screenshot tests unchanged).

STAGE 3 — iPhone-only pieces (expect/actual)
- Audio: AVPlayer with background audio mode, Now Playing info and remote commands limited to play/pause;
  speed 1×, 1.25×, 1.5× with pitch kept; looping for mantras.
- Keep screen on (`isIdleTimerDisabled`), haptics, share sheet, keyboard.
- Universal Links for group invites; Sign in with Apple beside Google for groups.
- Content bundled in the app; same offline behaviour.

STAGE 4 — iOS app and TestFlight
- `iosApp/` Xcode project embedding the shared framework; app icon and launch screen to match Android.
- Signing via the Apple Developer account; upload to TestFlight from the chosen build route.

ACCEPTANCE:
1. Android: every M1–M5 test still passes after each stage.
2. iPhone (TestFlight): the same M1–M5 workbooks pass, including locked-screen audio and counting.
3. Telugu and Devanagari render correctly on iPhone at the largest text size.
