# Naamalu Design System

Naamalu (నామాలు) is a South Indian Hindu devotional Android app centered on sacred
name-garlands (naamavalis). Stack: Kotlin + Jetpack Compose, Android-only. The design
is **reverent, restrained, and text-first**. The sacred name is the hero; UI chrome recedes.

Source of truth for exact token values is the `naamalu-app-design` skill. Values below
marked *(proposed)* are derivations to reconcile with that skill before shipping.

## Design principles (these override convenience and Material defaults)

1. **Name as hero.** The sacred name (Indic script) always gets the largest, highest-contrast,
   most prominent treatment on a screen. Never let controls, counters, or chrome compete
   visually with the name.
2. **Reverent, not gaudy.** No decorative gradients, glows, ornamental drop shadows, saturated
   fills, or visual clutter. Restraint reads as reverence. Prefer borders and hairlines over
   elevation shadows.
3. **Gold is restraint.** Gold (`#C79A3E`) is for 1dp hairlines, dividers, thin active
   indicators, and small accents ONLY. Never fill large areas, buttons, or backgrounds with gold.
4. **Script as identity.** Telugu, Tamil, Devanagari, and IAST transliteration are first-class.
   Never hardcode a single script. Every name-rendering composable takes the display script as
   input and selects the matching font family. See "Rendering names" below.
5. **Non-punitive.** A broken streak or missed day is NEVER shown in error/red styling. Lapsed
   states use the neutral/muted token. Reserve the error color strictly for genuine system
   failures (failed save, required network action).
6. **Sacred accuracy.** Divine names are never generated, altered, autocorrected, truncated,
   or ellipsized by UI logic. Render exactly what the data layer provides; wrap, don't clip.

## Color palette

Brand constants (fixed):

| Token        | Hex        | Role                                    |
|--------------|------------|-----------------------------------------|
| Maroon       | `#7A1F2B`  | Primary — filled actions, emphasis      |
| Gold         | `#C79A3E`  | Restraint accent — hairlines only       |
| Ivory        | `#FBF4E9`  | Light background / dark-mode text        |
| Peacock      | `#1B3A4B`  | Light-mode ink / dark-mode surface       |

Semantic tokens by theme. **Dina** = day/light, **Ratri** = night/dark.

### Dina (light) — *(extended values proposed)*
- `background`  `#FBF4E9`  (ivory)
- `surface`     `#FFFBF3`  (lifted paper) *(proposed)*
- `textPrimary` `#1B3A4B`  (peacock ink)
- `textEmphasis`/`primary` `#7A1F2B` (maroon)
- `hairline`    `#C79A3E`  (gold, ~40% opacity for dividers)
- `muted`       `#9A8F80`  lapsed/neutral state *(proposed)*
- `error`       `#B0463A`  rare only; must read distinct from maroon *(proposed)*

### Ratri (dark) — *(extended values proposed)*
- `background`  `#10222B`  (deep indigo-black, derived from peacock) *(proposed)*
- `surface`     `#1B3A4B`  (peacock)
- `textPrimary` `#FBF4E9`  (ivory)
- `textEmphasis`/`primary` `#B8515E`  (lightened maroon for dark contrast) *(proposed)*
- `hairline`    `#C79A3E`  (gold)
- `muted`       `#6E7F88`  lapsed/neutral state *(proposed)*
- `error`       `#C56A5C`  rare only *(proposed)*

Do NOT use raw maroon `#7A1F2B` for text on the Ratri background — contrast is insufficient.
Use it only for filled elements; use `textEmphasis` for dark-mode emphasis text.

## Typography

Three families, split by role. Bundle all as OFL fonts in `res/font`.

- **Cormorant Garamond** — Latin/IAST display: headings, transliterated names, section titles.
- **Noto Serif Telugu / Tamil / Devanagari** — the sacred script itself (the hero text).
  Select the family by the name's display script.
- **Inter** — all UI chrome: buttons, labels, nav, counts, metadata, timestamps.

Type scale *(proposed sp, reconcile with skill)*:

| Style        | Family                    | Size | Weight | Use                         |
|--------------|---------------------------|------|--------|-----------------------------|
| `nameHero`   | Noto Serif (by script)    | 34   | 600    | The name on detail/chant    |
| `nameList`   | Noto Serif (by script)    | 22   | 500    | Names in a list row         |
| `displayL`   | Cormorant Garamond        | 28   | 600    | Screen titles / deity name  |
| `headline`   | Cormorant Garamond        | 22   | 500    | Section headers             |
| `title`      | Inter                     | 18   | 500    | Card titles, tab labels     |
| `body`       | Inter (or Noto for names) | 16   | 400    | Body, meaning/translation   |
| `label`      | Inter                     | 14   | 500    | Buttons, chips              |
| `caption`    | Inter                     | 12   | 400    | Counts, meta, timestamps    |

Never render a sacred name in Inter. Never render UI chrome in a Noto Serif script face.

## Compose structure

Because tokens exceed Material3's `ColorScheme` (we add `hairline`, `muted`), define a
custom token set via `CompositionLocal`, and also map into a Material3 scheme for interop.

- `ui/theme/Color.kt` — raw `Color(0xFF…)` constants only.
- `ui/theme/NaamaluColors.kt` — `data class NaamaluColors(...)` with the semantic tokens above;
  `dinaColors()` and `ratriColors()` factory functions; a `staticCompositionLocalOf`.
- `ui/theme/Type.kt` — the three `FontFamily` declarations, a `notoSerifFor(script)` selector,
  and the `Typography`/custom text styles.
- `ui/theme/Theme.kt` — `NaamaluTheme(darkTheme: Boolean = isSystemInDarkTheme(), content)`
  that provides tokens + typography and wraps `MaterialTheme`.
- Access tokens through an `object NaamaluTheme` with `@Composable @ReadOnlyComposable`
  getters: `NaamaluTheme.colors.hairline`, `NaamaluTheme.typography.nameHero`.

Do not read colors off `MaterialTheme.colorScheme` for brand tokens — go through
`NaamaluTheme.colors` so light/dark and custom tokens stay correct.

## Rendering names (critical path)

A name carries its display script. Provide one composable that is the ONLY way names render:

```kotlin
@Composable
fun SacredName(
    text: String,
    script: Script,               // TELUGU, TAMIL, DEVANAGARI, IAST
    style: TextStyle = NaamaluTheme.typography.nameHero,
) {
    Text(
        text = text,
        style = style.copy(fontFamily = notoSerifFor(script)),
        color = NaamaluTheme.colors.textPrimary,
        softWrap = true,           // never clip a divine name
        maxLines = Int.MAX_VALUE,
    )
}
```

IAST/transliteration display uses Cormorant Garamond, not a Noto Serif face.

## Components

- **Buttons.** Primary = maroon fill, ivory label, 12dp radius. Secondary = transparent with a
  1dp gold border. Never a gold fill. Tertiary = text-only in `primary`.
- **Cards.** `surface` background, 16dp radius, NO drop shadow. Separate with a 1dp gold
  hairline (~40% opacity) or 1dp `muted` border; elevation `0.dp`.
- **Dividers / hairlines.** 1dp, gold at low opacity.
- **Japa counter.** Large, comfortable tap target. The count is legible but subordinate to the
  name above it. Idle/neutral by default; increment feedback is subtle (scale/opacity), never loud.
- **Saadhana / streaks.** Active day = maroon or gold accent. Lapsed/missed = `muted` neutral —
  never error color, never red, never a "broken" icon styled as a warning.
- **Bottom nav.** Four tabs (Home/Mandir, Library, Japa, Saadhana). Inter labels, active
  indicator is a thin gold mark; inactive = `muted`.

## Spacing, radius, elevation

- Spacing scale (dp): `4, 8, 12, 16, 24, 32`. Default screen padding `16`.
- Corner radius (dp): `8` (chips/small), `12` (buttons), `16` (cards/sheets).
- Elevation: prefer `0.dp` + hairline borders. If elevation is unavoidable, keep it minimal
  and shadowless where possible.

## Do NOT

- Do not use gold as a fill, background, or large surface.
- Do not style streaks, lapses, or missed days with red/error tokens.
- Do not render a sacred name in Inter, or clip/ellipsize a name.
- Do not hardcode a single script into a name composable.
- Do not generate, "fix," or autocomplete divine names in UI code.
- Do not add decorative gradients, glows, or heavy shadows.
- Do not pull brand colors from `MaterialTheme.colorScheme`; use `NaamaluTheme.colors`.
- Do not introduce new colors outside the palette without updating this file and the skill.
