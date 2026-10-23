# Cursor — how to use these prompts

Run one prompt at a time, in order. Each is a single Cursor Agent task. Paste the whole file content into the Agent
chat (or type `@docs/cursor/<file>.md do this`). Don't start the next until the acceptance checks pass on a phone
or emulator.

| # | Prompt | Builds |
|---|---|---|
| 1 | `P1-foundation.md` | Theme, content layer wiring, settings, onboarding, Home hub, TopBar nav, profile. Removes the bottom bar. |
| 2 | `P2a-puja.md` | Puja home, god page, one-page puja with stage bar, guided mode |
| 3 | `P2b-calendar.md` | Today panel with rāśi note, māsam view, observance page |
| 4 | `P2c-sankalpam.md` | Nāmalu (Read / Listen / Count), My sankalpams (solo), practice data |
| 5 | `P2d-stories.md` | Stories home and the beat player with touch moments |

Groups (Firebase, Google sign-in, Discover globe) are Phase 5 and get their own prompt later.

Before prompt 1:
1. Copy this package into the repo root (`AGENTS.md`, `docs/`, `content/`, `tools/`, `app/src/main/...`).
2. `pip install -r tools/requirements.txt` then `python tools/build_content.py`. Commit the result.
3. If you have the verified aṣṭottaram JSON: `python tools/import_ashtottara.py <file> content/texts/ganesha.ashtottara.yaml`
   and build again. Until then the aṣṭottaram shows as "coming soon" and the mūla mantra works.

If Cursor proposes changing anything in `content/`, `assets/content/` or the files in `app/.../content/`, say no and
point it to `AGENTS.md` rule 1.
