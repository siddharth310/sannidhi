# Content Architecture — v1.2 (Oct 2026)

How every piece of content (calendar, rāśi, deities, puja vidhi, mantras, naivedyam, samagri, nāmalu texts,
stories, images, audio) is written by you, checked, and delivered to the app.

App name: working name Sannidhi (checks pending). Nothing here depends on the name: folders and IDs never use it.

---

## 1. The one rule

You write content in plain `.md` and `.csv` files under `content/`. A build script checks it and turns it into
JSON that the app bundles. Cursor writes code that **reads** that JSON; Cursor never writes or edits content.
Nothing reaches the app without passing the check.

```
content/            →   tools/build_content.py              →   app/src/main/assets/content/
(you write)             check · transliterate · compute          (app reads; never edited by hand)
                        dates & moon · convert images
```

---

## 2. Folder layout

```
content/
  _templates/            copy-and-fill templates for every type below
  _schema/               JSON Schema per output type (the contract Cursor codes against)
  master/                tables
    masams.csv  tithis.csv  nakshatras.csv  rashis.csv  samvatsaras.csv  states.csv  cities.csv
  deities/               ganesha.md, lakshmi.md …
  pujas/<deity>/         vinayaka-chavithi.md, sankashti.md …   (pre / during / post)
  mantras/               ganesha.md, lakshmi.md …                (all mantras of a deity)
  texts/                 ganesha.ashtottara.yaml …               (nāmalu for Sankalpam)
  naivedyam/             modaka.md, pongali.md …
  samagri/               items.csv
  calendar/
    observances/         vinayaka-chavithi.md, varalakshmi-vratam.md …
    overrides.csv        your fixes when a computed date disagrees with your reference panchāṅgam
  rashi/                 profiles.md, gochara.md
  stories/<deity>/       elephant-head.md …
  images/<kind>/         <image-id>.png    + images/manifest.csv
  audio/                 <text-id>.mp3
```

---

## 3. IDs

Lowercase ASCII, dots between levels, hyphens inside words. **An ID never changes once published.** To remove
something, set `status: retired`; never delete or rename it (practice history and links depend on it).

| Type | Pattern | Example |
|---|---|---|
| Deity | `<deity>` | `ganesha` |
| Puja | `<deity>.puja.<slug>` | `ganesha.puja.vinayaka-chavithi` |
| Puja step | `<puja-id>.<stage>.<slug>` | `ganesha.puja.vinayaka-chavithi.during.dhupam` |
| Mantra | `<deity>.mantra.<slug>` | `ganesha.mantra.mula` |
| Nāmalu text | `<deity>.<type>` | `ganesha.ashtottara` |
| Name in a text | `<text-id>.<nnn>` | `ganesha.ashtottara.047` |
| Observance | `obs.<slug>` | `obs.vinayaka-chavithi` |
| Naivedyam | `nv.<slug>` | `nv.modaka` |
| Samagri item | `sm.<slug>` | `sm.durva` |
| Story | `<deity>.story.<slug>` | `ganesha.story.elephant-head` |
| Rāśi | `rashi.<slug>` | `rashi.mesha` |
| Moon-transit note | `gochara.h<nn>` | `gochara.h05` |
| Image | `img.<kind>.<slug>` | `img.deity.ganesha.hero` |

Steps use a slug, not a number, so inserting a step later never breaks anything. Order is the order in the file.

---

## 4. Status and review

Every file has `status: draft | reviewed | published | retired`.

- Anything containing a mantra, a divine name, katha or a story needs `reviewed_by:` before it can be `reviewed`.
- Release builds include only `reviewed` and `published`. Drafts appear in dev builds only.
- The build writes `content_status.csv`: what is missing per puja, story, observance and image.

---

## 5. Language rules

| Kind of text | Write in | App shows |
|---|---|---|
| Mantras, divine names, sacred titles | **Devanagari only** (`sa:`) | Pipeline renders IAST, Telugu script, Devanagari |
| Prose (meaning, katha, setup notes, festival notes, rāśi notes) | `te`, `hi`, `en` — all required | App language |
| Stories | `te`, `hi`, `en` — all required | App language, switchable inside the story |
| Short labels (step titles, item names) | `te`, `hi`, `en` | App language |

Inside `.md` bodies, every prose block is split by language headings:

```
#### te
తెలుగు వచనం…
#### hi
हिंदी पाठ…
#### en
English text…
```

The build fails if a `reviewed` item is missing `te`, `hi` or `en`.

---

## 6. Templates by type

### 6.1 Deity — `content/deities/ganesha.md`

```yaml
---
id: ganesha
status: draft
name_sa: गणेशः                    # pipeline → IAST + Telugu script
name_common: { te: వినాయకుడు, hi: गणेश जी, en: Ganesha }
also_known_as_sa: [विनायकः, विघ्नेश्वरः, गणपतिः]
order: 1                          # position in god grids
images: { hero: img.deity.ganesha.hero, card: img.deity.ganesha.card }
art_brief: >                      # for the illustrator, not shown in app
  Nature, power, stature, wisdom and intellect to convey…
---
## intro
#### te
(60–80 words: who he is, why people pray to him)
#### hi
…
```

### 6.2 Puja vidhi — `content/pujas/ganesha/vinayaka-chavithi.md`

One file per puja, three stages: **pre** (get ready), **during** (perform), **post** (after).

```yaml
---
id: ganesha.puja.vinayaka-chavithi
deity: ganesha
kind: festival                    # festival | vratam | nitya
observance: obs.vinayaka-chavithi # omit for nitya pujas
title: { te: …, hi: …, en: Vinayaka Chavithi Puja }
minutes: 60
regions: [all]                    # or state codes from master/states.csv
status: draft
reviewed_by:
---
```

Body: three `##` stages. Each step is a `###` heading `<slug> | <title in IAST>`, then `key: value` lines,
then prose by language.

```markdown
## pre

### mandapam | Maṇḍapa sthāpana
image: img.puja.ganesha.mandapam
minutes: 20
#### te
…how to set up the mandapam, simplest acceptable version first…
#### hi
…

### samagri | Sāmagrī
| item | qty | required | alternatives |
|---|---|---|---|
| sm.durva | 21 blades | yes | sm.akshata |
| sm.patri-21 | 21 kinds | no | any leaves available |

## during

### sankalpam | Saṅkalpam
mantra: ganesha.mantra.sankalpam
fills: [name, gotra, nakshatra, rashi]   # filled from the phone if the user saved them
#### te
…
#### hi
…

### ekavimsati-patri | Ekaviṃśati patra pūjā
mantra: ganesha.mantra.patri-pooja
repeat: 21
image: img.puja.ganesha.patri

### katha | Kathā
#### te
…
#### hi
…

## post

### naivedyam | Naivedyam
naivedyam: [nv.modaka, nv.undrallu]

### harati | Mangala harati
mantra: ganesha.mantra.harati

### udvasana | Udvāsana
#### te
…what to do with the idol, flowers and leaves; eco-friendly option first…
#### hi
…
```

Allowed step keys: `image`, `mantra` (one or a list), `repeat`, `minutes`, `fills`, `naivedyam`, `text`
(a nāmalu to chant, e.g. `ganesha.ashtottara`), `optional: yes`.

### 6.3 Mantras — `content/mantras/ganesha.md`

```markdown
---
deity: ganesha
status: draft
reviewed_by:
---
## ganesha.mantra.mula
sa: ॐ गं गणपतये नमः
use: [japa, puja]
count_default: 108
audio: { file: ganesha.mantra.mula.mp3, length_ms: 3200, words_ms: [0, 520, 980, 2010] }
#### te
(meaning)
#### hi
(meaning)

## ganesha.mantra.sankalpam
sa: …
use: [puja]
```

### 6.4 Nāmalu texts (Sankalpam) — `content/texts/ganesha.ashtottara.yaml`

```yaml
id: ganesha.ashtottara
deity: ganesha
type: ashtottara          # ashtottara | sahasranama | stotram | mantra
unit: reading             # reading (one full text = 1) | repetition (mantra japa)
status: reviewed
reviewed_by: …
audio: { file: ganesha.ashtottara.mp3, intro_ms: 8000, slot_ms: 3500 }
names:
  - { n: 1, sa: "ॐ विनायकाय नमः" }
  - …
```

The existing verified 108-name dataset converts straight into this.

### 6.5 Naivedyam — `content/naivedyam/modaka.md`

Meaning, not recipe. One offering, regional forms kept as they are.

```yaml
---
id: nv.modaka
offered_to: [ganesha]
occasions: [obs.vinayaka-chavithi, obs.sankashti]
regional_forms:
  - { name: { te: ఉండ్రాళ్ళు, hi: उंडराल्लु }, region: [AP, TS], note: { te: …, hi: … } }
  - { name: { te: మోదకం, hi: मोदक }, region: [MH], note: { te: …, hi: … } }
image: img.naivedyam.modaka
status: draft
---
## why
#### te
(why this food, for this deity, at this time)
#### hi
…
```

### 6.6 Samagri — `content/samagri/items.csv`

```
id,name_te,name_hi,name_en,image,alternatives,note_te,note_hi
sm.durva,గరిక,दूर्वा,Durva grass,img.samagri.durva,sm.akshata,…,…
```

A puja's table can override `alternatives` for that puja.

### 6.7 Calendar

**Master tables** (`content/master/`): māsams, tithis, nakṣatras, rāśis, the 60 samvatsaras, states (with
`month_system`: amānta | pūrṇimānta | solar, and the saṅkalpam `desha_clause_sa`), cities (name per language,
lat, lon, state). Names of māsams, tithis, nakṣatras, rāśis and samvatsaras are written once in Devanagari; the
build renders IAST and Telugu script. Solar-month states (TN, KL, WB) see lunar labels in MVP; solar month names
come later.

**Observances** — `content/calendar/observances/vinayaka-chavithi.md`

```yaml
---
id: obs.vinayaka-chavithi
kind: festival            # festival | vratam | period | weekly | monthly
rule:
  masam: bhadrapada       # always amānta
  paksha: shukla
  tithi: 4
  prevails_at: madhyahna  # sunrise | madhyahna | pradosha | nishita | chandrodaya
regions: [all]
deity: ganesha
links:
  pujas: [ganesha.puja.vinayaka-chavithi]
  texts: [ganesha.ashtottara]
  stories: [ganesha.story.elephant-head]
  naivedyam: [nv.modaka]
image: img.obs.vinayaka-chavithi
status: draft
---
## special          (what makes the day special)
## families-do      (what people traditionally do)
## history
## regional-notes
(each with #### te / #### hi)
```

**Dates are computed, not typed.** `tools/panchanga.py` (PyEphem, Lahiri ayanāṁśa, amānta, IST) runs for every
city in `cities.csv` and every samvatsara listed in `tools/build_config.yaml`. One record per day per city, values
at sunrise:

```
date · sunrise · sunset · tithi (+ end) · nakṣatra (+ end) · māsam (+ adhika) · moon rāśi · moon % · observances[]
```

How a rule becomes a date: the build finds the tithi inside the right māsam (adhika months are skipped for yearly
rules, kept for monthly ones), then picks the civil day on which that tithi covers the `prevails_at` window —
madhyāhna is the 3rd fifth of daytime, pradoṣa the 96 minutes after sunset, niśītha the 48 minutes around
midnight, chandrodaya the moonrise. Checked: Ugadi 19 Mar 2026 and 7 Apr 2027, Adhika Jyeṣṭha 17 May–15 Jun 2026,
Vināyaka Chavithi 14 Sep 2026.

**Reference: TTD Āsthāna Siddhānti panchāṅgam (Relangi Tangirala).** Several publishers print "gantala"
panchāṅgams, so always name this one. The calendar year is the **samvatsara, Ugadi to Ugadi**.

**Every Ugadi (start in Phālguna, before the new panchāṅgam year):**
1. Run the build for the coming samvatsara.
2. The build writes `calendar/check-<year>-<samvatsara>.csv`: every observance, per state, with its computed date.
3. Tick each row against the TTD panchāṅgam. Where it differs, add a row to `overrides.csv`
   (`samvatsara, observance, region, date, reason`). You fix dates with data, never with code.
4. Publish in an app update before Ugadi. The app always carries the current and the next samvatsara, so a late
   update never leaves users without dates. Pujas, stories and the rāśi note follow the dates automatically.

### 6.8 Rāśi — daily note

Computed from the moon, written once by you. No daily writing.

- Each day the build records the moon's rāśi (from 6.7).
- The app works out the house: `house = (moon_rashi − my_rashi) mod 12 + 1`.
- It shows the note for that house. Twelve notes cover every rāśi on every day. "Check another rāśi" just
  changes `my_rashi`.

`content/rashi/profiles.md` — one short nature note per rāśi (te / hi).

`content/rashi/gochara.md` — twelve sections, two or three variants each so the text doesn't repeat daily:

```markdown
## gochara.h05
tone: open                # open | steady | gentle  (there is no "bad")
suggest_text: ganesha.ashtottara   # optional: a nāmalu to chant, never a paid remedy
#### te
(variant 1)
---
(variant 2)
#### hi
…
```

Writing rules, checked at review: no fear words; no predictions of illness, loss, death or money; no remedies to
buy; careful days are framed as "go gently, new experiences with care". The app shows the note for **today only**.

### 6.9 Stories — `content/stories/ganesha/elephant-head.md`

A story is a list of **beats**. Each beat is one screen: an image, a short line, an optional longer paragraph,
and an optional touch moment.

```yaml
---
id: ganesha.story.elephant-head
deity: ganesha
title: { te: …, hi: …, en: … }
ages: 4-12
minutes: 4
feeling: [safe, brave]    # what the child should take away
source: Śiva Purāṇa (retold gently)
observances: [obs.vinayaka-chavithi]
cover: img.story.ganesha.elephant-head.cover
status: draft
reviewed_by:
---
## beat 1
image: img.story.ganesha.elephant-head.b1
touch: { target: diya, effect: glow }      # optional
### short
#### te
…
#### hi
…
#### en
…
### more                                   # optional, shown for "Older children"
#### te
…
```

- 4–8 beats. `short` ≤ 20 words; `more` ≤ 60 words, per language.
- `touch.effect` is one of: `glow`, `wiggle`, `hide`, `appear`, `bounce`. The app animates; you only name it.
- No gore or fear; conflicts end in wisdom or kindness; the last beat gives the child one line to hold on to.

### 6.10 Images

You drop PNGs (or SVGs for flat art) into `content/images/<kind>/<image-id>.png` and list them in
`content/images/manifest.csv`:

```
id,kind,file,used_by,priority,status,alt_te,alt_hi,alt_en,brief
img.deity.ganesha.hero,deity,deity/img.deity.ganesha.hero.png,ganesha,P0,missing,…,…,…,Seated on a red lotus…
```

`status`: missing | supplied | approved. The build converts to WebP, checks sizes, and lists what's missing.
Missing images fall back to the deity symbol, then to the module icon (see `docs/design-v6.md`).

| Kind | Size (px) | Notes |
|---|---|---|
| deity hero | 1200 × 1500 | form, matches app style |
| deity card | 600 × 600 | |
| puja step | 1200 × 900 | |
| observance | 1200 × 800 | |
| story beat | 1600 × 1200 | 4:3, same style across a story; touch target as a separate transparent layer |
| deity symbol | SVG, 96 × 96 viewBox | flat, app ritual colours |
| naivedyam | 800 × 800 | |
| samagri icon | 256 × 256 | transparent background |

A folder is enough for now; an upload screen belongs to the admin dashboard later.

### 6.11 Audio

`content/audio/<text-id>.mp3`, timing in the text's `.yaml` (6.4). Human recording for release.

---

## 7. What the app reads (output contract)

```
assets/content/
  manifest.json              content version, build time, mode, samvatsara ranges, counts, file hashes
  master.json                māsams, tithis, nakṣatras, rāśis, samvatsaras, states, cities
  deities.json
  pujas.json                 index (no steps)       pujas/<puja-id>.json     full puja
  texts.json                 index (no names)       texts/<text-id>.json     full text
  stories.json               index (no beats)       stories/<story-id>.json  full story
  mantras.json
  naivedyam.json
  samagri.json
  observances.json           rules and prose; dates live in calendar files
  calendar/index.json        carried samvatsaras
  calendar/<city>/<year>-<samvatsara>.json      e.g. calendar/hyderabad/2026-parabhava.json
  rashi.json                 profiles + gochara
  images.json                image id → webp path (null when missing)
  images/<image-id>.webp
  audio/<text-id>.mp3
```

Every file has a JSON Schema in `content/_schema/`; the build validates its own output against them.
Kotlin mirrors them in `app/src/main/java/app/sannidhi/content/ContentModels.kt`.

Field shapes used everywhere:

- Prose and labels: `{ "te": "…", "hi": "…", "en": "…" }`
- Sacred text: `{ "sa": "…", "iast": "…", "te": "…" }` (te = Telugu script)
- Links are IDs only; the app resolves them.

Cursor builds one `ContentRepository` against `content/_schema/*.json`. Any screen needing data not in the schema
means the schema is updated first, then the code.

### Which module reads what

| Module | Reads |
|---|---|
| Home | calendar (today, next observance), pujas, my sankalpams (phone) |
| Puja | deities, pujas, mantras, samagri, naivedyam, images |
| Calendar | calendar, observances, rāśi |
| Sankalpam | texts, mantras, audio; groups from Firebase |
| Stories | stories, deities, images |

---

## 8. Your workflow

One-time: `pip install -r tools/requirements.txt` (Python 3.10+).

1. Copy a template from `content/_templates/`, fill it, keep `status: draft`.
2. `python tools/build_content.py --check` — fix every ERROR; warnings are reminders.
3. Get sacred text reviewed, then set `status: reviewed` and `reviewed_by`.
4. `python tools/build_content.py` (dev: drafts included) or `--release` (reviewed and published only).
   `--no-calendar` skips the calendar (about 40 s) when only prose changed.
5. Commit `content/` and `app/src/main/assets/content/` together. `content/content_status.csv` lists what is
   still draft or missing.

Nāmalu names are never typed: `python tools/import_ashtottara.py <verified.json> content/texts/<id>.yaml`.

---

## 9. Moving the existing Excel files

A one-time script converts them; after that, `content/` is the only source.

| From | To |
|---|---|
| `naamalu-calendar-core.xlsx` | `master/*.csv` + `calendar/observances/*.md` |
| `naamalu-deities-parayanam-library.xlsx` | `deities/*.md` + `texts/*.yaml` |
| `naamalu-master-data.xlsx` | `master/states.csv`, `cities.csv`, `rashis.csv`, `nakshatras.csv` |
| Gaṇeśa aṣṭottaram JSON | `texts/ganesha.ashtottara.yaml` |

---

## 10. Open items

- Whether nitya (daily) pujas are in launch scope.
