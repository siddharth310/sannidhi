# Content Portal — spec v1 (18 Oct 2026)

A web portal where the team adds and changes app content (text, names, mantras, images, audio) and publishes it
to every phone **without a Play Store or App Store update**.

Builds on `docs/content-architecture.md` (v1.2). That file still defines every content type, ID and template;
this file defines how the portal edits them and how published content reaches the app.

---

## 1. The one rule (unchanged, extended)

Content is data in `content/`, checked by `tools/build_content.py`. **Nothing reaches a phone without passing that
check.** The portal is a friendly way to change `content/`; it never bypasses the build.

```
 Helper / Owner                         GitHub (source of truth)                 Phones
 ───────────────                        ───────────────────────                 ──────
 Portal ──draft──▶ Firestore            content/*.md · *.yaml · *.csv
        ──upload─▶ Storage uploads/          ▲
 Owner  ──Approve                            │ 1 apply approved drafts
 Owner  ──Publish ─▶ function ─▶ GitHub Action ─ 2 build --check (stop on any error)
                                             │ 3 build pack v{n}
                                             │ 4 commit to main
                                             ▼
                                      Storage packs/v{n}/  ──▶ channels/preview.json ──▶ test phone
                                                           ──▶ channels/stable.json  ──▶ every phone
                                                               (Owner: "Make live")      (silent, offline after)
```

## 2. Decisions (18 Oct 2026)

| Decision | Choice |
|---|---|
| How content reaches the app | Versioned content packs from Firebase. First pack bundled in the app; later packs download silently. |
| Who edits | Owner plus a few helpers. Helpers draft and submit; **only the owner approves and publishes.** |
| Source of truth | `content/` in the GitHub repo. Firestore holds only work in progress (drafts, review, publish runs). |
| Who parses and writes content files | Python only (`tools/mdparse.py`). The portal never reads or writes `.md` itself. |
| Test before live | Every publish goes to **preview** first; the owner checks on a test phone, then **Make live**. |

## 3. Roles

| Can | Helper | Owner |
|---|---|---|
| Sign in with Google (invited emails only) | ✓ | ✓ |
| Browse everything, see what's missing | ✓ | ✓ |
| Create and edit drafts, upload images and audio | ✓ | ✓ |
| Submit a draft for review | ✓ | ✓ |
| Approve, send back with a note | | ✓ |
| Fill **Reviewed by** for sacred text (mantra, divine name, katha, story) | | ✓ |
| Publish to preview, Make live, Undo the latest publish | | ✓ |
| Retire an item, change an ID before its first publish | | ✓ |
| Add or remove team members | | ✓ |

The first owner is set by hand in the Firebase console. There is no sign-up screen.

## 4. What the portal edits

Each item is one entry from content-architecture §3 (one deity, one puja, one mantra, one image …). The draft
holds the item in **source JSON** (the parsed form of its file, before transliteration), defined by
`content/_schema/source/<type>.json`.

| Item type | File it changes | Editor |
|---|---|---|
| Deity | `deities/<id>.md` | names, also-known-as, order, image slots, intro (te/hi/en), art brief |
| Puja | `pujas/<deity>/<slug>.md` | header fields; **Pre · During · Post** with step cards (drag to reorder); step keys from §6.2 picked by search |
| Mantra | entry in `mantras/<deity>.md` | `sa` (Devanagari only), use, count, meaning (te/hi/en) |
| Nāmalu text | `texts/<id>.yaml` | names table (paste or CSV import), unit, audio slot and timing |
| Naivedyam | `naivedyam/<slug>.md` | regional forms list, why (te/hi/en), image |
| Samagri | row in `samagri/items.csv` | names, alternatives, image, notes |
| Observance | `calendar/observances/<slug>.md` | rule (māsam, pakṣa, tithi, prevails at), links, sections |
| Date fix | row in `calendar/overrides.csv` | year, observance, region, date, reason |
| Rāśi notes | `rashi/profiles.md`, `rashi/gochara.md` | 12 profiles; 12 houses × variants, tone |
| Story | `stories/<deity>/<slug>.md` | beats: image, short line, more paragraph (te/hi/en) |
| Image | `images/<kind>/<id>.png` + row in `images/manifest.csv` | upload, alt text, status |
| Audio | `audio/<text-id>.mp3` | upload, timing check |

Not in the portal (developer only, v1): master tables (`master/*.csv`), build years for the calendar, schemas.

### Language and text rules in the editor

- Sacred text field accepts **Devanagari only**. A live IAST and Telugu preview sits under it, labelled
  "Preview — the build makes the final version".
- Prose has te / hi / en tabs. A tab shows a dot when empty. Submit is blocked only where the build would fail.
- **Words check** on prose: highlights words from the never-lists (design §6 "Never", the rāśi writing rules and
  the good-times list: rāhu kālam, yamagaṇḍam, durmuhūrtam, varjyam, avoid, bad, inauspicious). The portal warns;
  the build check decides.
- New item: the portal proposes an ID from the §3 pattern and the title. Editable until the first publish, then
  locked forever. To remove something, the owner **retires** it; nothing is ever deleted.

## 5. Draft lifecycle

```
            ┌──────── Send back (note required) ───────┐
            ▼                                           │
 Edit ─▶ editing ─▶ Submit ─▶ submitted ─▶ Approve ─▶ approved ─▶ Publish ─▶ published (draft closed)
  │                                         (owner)      │  ▲
  └─ Discard (author or owner)                Unapprove ─┘  └─ publish failed: stays approved, report shown
```

- **One open draft per item.** The draft's document ID is the item ID. A second person sees
  "Being edited by Lakshmi since Tue 16 Oct" and a read-only view.
- A draft remembers the version it started from (`base_version`, `base_hash` of the source file). If the file
  changed in GitHub since then, publish stops for that item: "Changed since this draft began. Reopen to see the
  new version."
- The owner may approve their own draft.
- On Approve, sacred items need **Reviewed by** (a person's name, e.g. the purohit). It is written into the file's
  `reviewed_by`, and the file's `status` becomes `reviewed`; after a successful publish it becomes `published`.
- Autosave every few seconds while editing. Nothing is lost on a closed tab.

## 6. Screens

| # | Screen | What it shows | Actions |
|---|---|---|---|
| S1 | Sign in | "Sign in with Google". Not invited → "This account isn't on the team. Ask the owner to add you." | Sign in |
| S2 | Home | **Needs you** (owner: waiting for review; helper: sent back to you) · **What's missing** (from the build's `content_status.csv`: P0 images first, missing languages, unreviewed sacred text) · **Live now** (stable v14 · 16 Oct; preview v15) | Open item; Publish (owner) |
| S3 | Library | By god / By type toggle, search (te, hi, en, IAST). Row: title, ID, status chip, what's missing, "Being edited by …" | New item; open |
| S4 | God view | Everything for one god: deity, pujas, mantras, texts, observances, naivedyam, stories, and every image slot | Open; upload into a slot |
| S5 | Editor | Header: title, ID, file status, draft state. Body: the type's form. Right panel: **Preview** in app frames and **Checks** (live list of problems) | Save (auto), Submit / Approve, Discard |
| S6 | Images | Every slot from `images/manifest.csv`. Filters: missing / supplied / approved, kind, god, P0/P1. Slot card: brief, size needed | Upload, replace |
| S7 | Audio | Per text: player, timing values, names highlighted in time with the recording | Upload, adjust timing |
| S8 | Review (owner) | Queue, oldest first. Item opens **before / after**: changed fields highlighted, old and new image side by side, both recordings playable | Approve, Send back (note) |
| S9 | Publish (owner) | **Ready to publish** (approved drafts) · **Publish to preview** · live progress: Applying → Checking → Building → Uploading → On preview · **Make live** · History: version, date, by, items, channel | Unapprove, Publish, Make live, Undo latest |
| S10 | Team (owner) | Members, role, last active | Add by Google email, change role, remove (their open drafts move to the owner) |

Layout: desktop first, works on a tablet. S6 upload and S8 review also work on a phone. Portal text is English.
Look: app tokens (ivory surface, maroon and gold hairlines, Inter for UI, Noto Serif Telugu / Devanagari and
Cormorant for sacred text previews). Calm, no dashboards of numbers about app users.

### Previews (S5, S6)

Images and text are shown inside frames that copy the app's components: god-grid card, god-page hero (45%
height crop), puja step card, story beat, naivedyam card, samagri icon. Each in **Dina** (light) and **Ratri**
(dark). This is how the owner checks an image fits the design before approving.

## 7. Upload rules

### Images

| Kind | At least (px) | Ratio | Extra |
|---|---|---|---|
| deity hero | 1200 × 1500 | 4:5 | |
| deity card | 600 × 600 | 1:1 | |
| puja step | 1200 × 900 | 4:3 | |
| observance | 1200 × 800 | 3:2 | |
| story beat | 1600 × 1200 | 4:3 | same style across a story |
| naivedyam | 800 × 800 | 1:1 | |
| samagri icon | 256 × 256 | 1:1 | transparent background required |

- PNG or JPG, up to 15 MB. Larger images are fine; the build resizes and converts to WebP.
- Ratio may differ by at most 1%. Wrong size or ratio → rejected with the reason, e.g. "Needs at least 600 × 600;
  this is 500 × 500."
- Alt text in te and hi is required for approval.
- Image status follows the manifest: `missing` → `supplied` (uploaded) → `approved` (owner approved).

### Audio

- MP3, up to 30 MB, human recording for release.
- Timing fields as defined in content-architecture §6.4. S7 plays the recording with the names highlighted using
  those values so the owner can adjust until they match.

## 8. Publish pipeline

The owner presses **Publish to preview**. The function `startPublish` creates `publishes/{n}` and starts the
GitHub Action `publish-content.yml`. One publish runs at a time (Action concurrency group `publish`).

| Step | Action does | On failure |
|---|---|---|
| 1 Apply | `tools/apply_drafts.py --publish {n}` reads approved drafts and uploads, checks `base_hash`, writes `content/` files in canonical form, sets `status` and `reviewed_by` | That item is reported; nothing is committed |
| 2 Check | `build_content.py --check` | Report saved to `publishes/{n}`; nothing is committed; phones unchanged |
| 3 Build | `build_content.py` → app assets, pack `build/pack/`, source JSON `build/source/` | Same as above |
| 4 Commit | Commit `content/` and `app/src/main/assets/content/` to `main`: "Content v{n}: <item IDs>" | Same as above |
| 5 Upload | `packs/v{n}/`, `source/v{n}/`, Firestore `items` index | Retry; channel not moved |
| 6 Preview | Write `channels/preview.json` → v{n}; close the drafts as published | — |

**Make live** (owner, S9) points `channels/stable.json` at the same pack. No rebuild, so what was tested is what
ships.

**Undo latest** reverts the latest content commit and runs steps 2–6 as a new version (n+1). Versions only go up,
so phones always accept the change. Older publishes are undone by a developer in git.

Developers can still edit `content/` files directly and commit. The next publish builds from `main`, so their
changes go out too.

## 9. Content pack contract

```
packs/v{n}/manifest.json
packs/v{n}/<same tree as app/src/main/assets/content/>   (content-architecture §7)
channels/stable.json     { "pack_version": 14, "schema_version": 3, "min_app_version": 10200,
                           "manifest": "packs/v14/manifest.json" }
channels/preview.json    same shape
```

`manifest.json`:

```json
{
  "pack_version": 14,
  "schema_version": 3,
  "min_app_version": 10200,
  "built_at": "2026-10-18T10:20:00Z",
  "commit": "a1b2c3d",
  "files": [ { "path": "pujas/ganesha.puja.vinayaka-chavithi.json", "sha256": "…", "bytes": 18234 } ]
}
```

- `pack_version` only goes up. The app bundles the pack built at release time.
- `schema_version` changes only when `content/_schema/*.json` changes shape. Adding content never changes it.
- Retired items stay in the pack with `status: retired`, so practice history can still show their names.
- Pack files are public and cached as immutable; channel files are served with `Cache-Control: no-cache`.

## 10. How the app gets updates

| Rule | Detail |
|---|---|
| When | On app start, at most once every 12 hours, only with a network |
| What it fetches | `channels/stable.json` (release builds) or `channels/preview.json` (internal test builds) |
| Accept a pack | Only if `pack_version` is newer, `schema_version` is supported and `min_app_version` ≤ this app. Otherwise skip silently |
| Download | Background job. Only files whose hash differs from the active pack; the rest are copied locally |
| Large files | Files over 2 MB (audio) wait for an unmetered network. Until then the old file is used; a text with no recording on the phone yet shows Read and Count only, as today |
| Verify | Every file's SHA-256 must match the manifest. Any mismatch → throw the download away, try at the next check |
| Switch | The new pack becomes active on the **next app start**, never mid-session. The previous pack is kept until the next one is active |
| Fallback | No downloaded pack, or a broken one → the bundled pack |
| User sees | Nothing. No "update available", no progress, no error |
| Privacy | Plain HTTPS GET. No user ID, no analytics, no Firebase SDK needed for content |

Practice entries, sankalpams and puja ticks refer to IDs, and IDs never change, so a new pack never breaks a
user's history.

## 11. Data

### Firestore

| Collection | Fields | Written by |
|---|---|---|
| `users/{uid}` | email, name, role (`owner` \| `helper`), active, added_at | owner |
| `items/{itemId}` | type, title, file path, file status, missing[], source path, version | Action |
| `drafts/{itemId}` | type, base_version, base_hash, data (source JSON), uploads[] {slot, path, width, height, bytes}, state, author, notes[] {by, at, text}, reviewed_by, approved_by, approved_at, updated_at | portal |
| `publishes/{n}` | version, state (`queued` \| `applying` \| `checking` \| `building` \| `uploading` \| `on_preview` \| `live` \| `failed`), items[], commit, report {errors[], warnings[]}, by, started_at, finished_at | function, Action |
| `settings/channels` | stable, preview | function, Action |

### Storage

| Path | Read | Write |
|---|---|---|
| `uploads/{itemId}/{file}` | team | team; PNG, JPEG or MP3 only; 15 MB images, 30 MB audio |
| `source/v{n}/…` | team | Action only |
| `packs/v{n}/…`, `channels/*.json` | public | Action and function only |

Bucket location is chosen once when the project is created and can't change; `asia-south1` (Mumbai) is closest to
most users.

## 12. Security

- Firestore and Storage rules read the caller's role from `users/{uid}`. No custom claims.
- Helpers: may create and edit drafts in `editing` or `sent_back` that they authored; may set `submitted`. Any
  write that sets `approved`, `reviewed_by` or `approved_by` from a helper is rejected by rules.
- Owner: everything in drafts and users. No one can write `items`, `publishes`, `settings`, `source/`, `packs/` or
  `channels/` from the browser.
- Functions (`startPublish`, `makeLive`, `undoLatest`) check the caller is an active owner.
- The GitHub token lives in Secret Manager (function); the Firebase service account lives in GitHub Actions
  secrets. Neither is ever in the portal code.
- Rules have unit tests (Firebase emulator) for every row of §3.

## 13. Repo additions

```
portal/                          Vite + React + TypeScript; Firebase JS SDK
functions/                       Cloud Functions (TypeScript): startPublish, makeLive, undoLatest
tools/apply_drafts.py            approved drafts → content/ files (uses mdparse.py)
tools/requirements.txt
content/_schema/source/*.json    source JSON per item type (portal forms validate against it)
.github/workflows/publish-content.yml
firebase.json  .firebaserc  firestore.rules  storage.rules  firestore.indexes.json
```

Folder names and IDs never use the app name.

## 14. Setup and cost

- One Firebase project for portal, content packs and (Phase 5) groups.
- **Blaze (pay-as-you-go) plan is required**: Cloud Storage needs a billing account since Feb 2026, and the
  function must call GitHub. Set a budget alert on day one. At this size the cost is expected to be small.
- GitHub: the repo, a fine-grained token for `startPublish` (Actions: write on this repo only).
- Hosting: the portal on Firebase Hosting, on its own site (e.g. `content-admin`).

## 15. Never

- The portal never sees app users, their practice, sankalpams or rāśi. It has no analytics about users.
- Nothing reaches stable without being on preview first.
- No content is ever deleted or renamed after it is published; it is retired.
- The portal never transliterates for real or computes dates; the build does.
- No AI-generated sacred text is accepted into a mantra, name or text field.

## 16. Acceptance checks (end to end)

1. A helper uploads a 600 × 600 PNG into `img.deity.ganesha.card` and submits. The owner approves and publishes to
   preview. The test phone shows the new card after its next start; a store-build phone still shows the old one
   until **Make live**.
2. A 500 × 500 upload into the same slot is rejected with "Needs at least 600 × 600; this is 500 × 500."
3. A helper sees no Approve, Publish or Team. A helper's direct write setting a draft to `approved` is rejected
   (rules test).
4. A mantra `sa` field containing Latin letters blocks Submit: "Mantra text must be in Devanagari."
5. Approving a mantra with **Reviewed by** empty is blocked.
6. A publish where the build check fails (e.g. a story missing hi) ends `failed`, names the item and the field, and
   leaves both channels unchanged.
7. While Lakshmi edits `ganesha.mantra.mula`, another member sees it read-only with "Being edited by Lakshmi".
8. A draft whose file changed in GitHub after it began stops at Apply with "Changed since this draft began".
9. `apply_drafts.py` round trip: for every existing file in `content/`, parse → write → parse gives identical data.
10. A phone in airplane mode keeps working on its current pack. A download with one wrong hash never becomes active.
11. **Undo latest** produces version n+1 with the previous content; after **Make live**, phones show it.
12. Retiring `ganesha.story.fruit-of-wisdom` removes it from Stories; nothing else changes.
13. A new 25 MB recording is not downloaded on mobile data, is downloaded on Wi-Fi, and Listen appears after the
    next start.

## 17. Open questions

| # | Question | Default if not answered |
|---|---|---|
| 1 | Should the purohit get a **Reviewer** login to mark sacred text reviewed, or does the owner type their name? | Owner types the name |
| 2 | Is the test phone an internal-testing build (Play internal track / TestFlight) reading the preview channel? | Yes |
| 3 | Which GitHub repo (owner/repo) holds the app? Needed for the Action. | — |
| 4 | Master tables and calendar years: stay developer-only in v1? | Yes |
| 5 | Group reports (Report on public cards): add a Reports inbox to the portal in Phase 5? | Yes, Phase 5 |

## 18. Changes to other docs

- **content-architecture.md §8 Your workflow** → "Add or change content in the portal: draft, submit, approve,
  publish to preview, make live. Developers may still edit `content/` and commit; the next publish includes it."
- **content-architecture.md §6.10** → drop "A folder is enough for now; an upload screen belongs to the admin
  dashboard later." The portal is that screen.
- **AGENTS.md** → add: "The app reads content only through `ContentRepository`, which reads the active pack from
  `PackStore`. The app never writes content. Content updates are silent: no message, badge or prompt."
- **app-flow.md §0** → add to principle 6: "Content updates arrive silently in the background."
