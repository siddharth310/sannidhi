# Design v6 — reference screens

Source of truth for how each screen looks. Specs in words: `docs/design-v6.md`. Content: `docs/content-architecture.md`.

- `png/` — one image per screen at 2× (phone screens are 780 × 1688 px = 390 × 844 dp).
- `html/` — the same screens as plain HTML. Open any file in a browser; links between screens work.
  Fonts and images are bundled, so it works offline. Inspect any element for exact sizes and colours.

All mockups use Hyderabad on Fri 11 Sep 2026 (Stories home and Observance show 14 Sep, the festival day),
app language English, rāśi Meṣa. Values come from the built calendar. Grey-blue or pink picture slots are the
missing-image fallback, not final art. `[name 102]` in Read is a placeholder: real names come from the verified
aṣṭottaram import — never type sacred names into code.

| Screen | Files | Built in prompt |
|---|---|---|
| Spec sheet: frame, grid, colour, type, components | 00a-spec-sheet | all |
| Images, icons, fallbacks | 00b-images-icons | all |
| Onboarding · language | 01-onboarding-language | P1 |
| Onboarding · rāśi (step 4) | 02-onboarding-rashi | P1 |
| Home | 03-home | P1 |
| Profile | 04-profile | P1 |
| Puja home | 10-puja-home | P2a |
| God page · Gaṇeśa | 11-god-ganesha | P2a |
| Puja page · Pre (samagri) | 12-puja-pre-samagri | P2a |
| Puja page · During (mantra, text step) | 13-puja-during-mantra | P2a |
| Guided mode | 14-puja-guided | P2a |
| Calendar · today + rāśi note | 20-calendar-today | P2b |
| Calendar · month + day bubble | 21-calendar-month-day | P2b |
| Observance page | 22-observance | P2b |
| My sankalpams | 30-sankalpam-mine | P2c |
| Practise · Count | 31-practise-count | P2c |
| Practise · Read | 32-practise-read | P2c |
| New sankalpam | 33-sankalpam-new | P2c |
| Stories home | 40-stories-home | P2d |
| Story cover + start sheet | 41-story-start | P2d |
| Story page with touch moment | 42-story-beat | P2d |
| Story last page | 43-story-last | P2d |

## Line to add to each Cursor prompt

> Match `docs/design/v6/png/<screen>.png` exactly. For sizes, colours and spacing read
> `docs/design/v6/html/<screen>.html`. Do not copy the HTML into the app; rebuild it in Compose with the theme tokens.
