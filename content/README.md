# Content — v1.2 (draft)

Master tables (`master/`), rāśi notes (`rashi/`), templates (`_templates/`), output schemas (`_schema/`) and the
first deity pack (Gaṇeśa). Check and build with `tools/build_content.py`.

## Gaṇeśa pack

First complete deity slice, written to `content-architecture.md` v1.1. Drop this `content/` folder in the repo root.

| Area | Files | State |
|---|---|---|
| Deity + symbols | `deities/ganesha.md`, `images/deity/*.svg` (card, hero), `images/symbol/*.svg` (6) | Drafted; deity and symbols drawn |
| Puja vidhi | `pujas/ganesha/vinayaka-chavithi.md` — pre 8, during 16, post 8 steps | Drafted |
| Mantras | `mantras/ganesha.md` — 9 mantras + 21 upacāra lines + 21 patra lines | Drafted; REVIEW marks |
| Nāmalu | `texts/ganesha.ashtottara.yaml` (import from verified dataset), `texts/ganesha.mula-mantra.yaml` | Header ready |
| Naivedyam | `naivedyam/modaka.md` (5 regional forms), `naivedyam/phala.md` | Drafted |
| Samagri | `samagri/items.csv` — 24 items | Drafted |
| Calendar | `calendar/observances/vinayaka-chavithi.md`, `sankashti-chaturthi.md`; `calendar/overrides.csv`; computed `check-*.csv` | Drafted; dates computed for 2026–27 and 2027–28 |
| Rāśi | `rashi/gochara.md` (12 houses), `rashi/profiles.md` (12 rāśis) | Drafted, one variant each |
| Master | `master/` māsams, tithis, nakṣatras, rāśis, 60 samvatsaras, 12 states, 26 cities | Drafted; `desha_clause_sa` empty for purohit |
| Stories | `stories/ganesha/elephant-head.md`, `moon.md`, `mango.md` | Drafted, te / hi / en |
| Images | `images/manifest.csv` — 68 entries with briefs | 8 supplied, 60 to draw |

## Before status can move to `reviewed`
1. Purohit review of everything in `mantras/ganesha.md`, especially lines marked REVIEW (achamanam, kalaśa,
   saṅkalpam wording and regional clause, the 21 patra names, udvāsana line).
2. Import the 108 names; clear the 11 CORRECTED flags.
3. Native read of all Telugu and Hindi prose (draft quality, not final).
4. Story review: tone and the moon-story source note.

## Still missing for a fully shippable slice
- Saṅkaṣṭi puja (short form) — observance points to none yet.
- Rāśi: purohit / native read; add a second and third variant per house so notes don't repeat.
- `master/states.csv` `desha_clause_sa` for each state (saṅkalpam).
- Tick `calendar/check-*.csv` against the TTD panchāṅgam.
- 60 illustrations (P0 first: puja steps, story beats).
- Audio: aṣṭottaram and mūla mantra recordings.
