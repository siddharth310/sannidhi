# C3 — Publish pipeline: apply, check, build, pack, preview, make live

Build publishing to `docs/portal/portal-spec.md` §8–9. C2 must pass first. Report back with every acceptance check.

READ FIRST:
- `docs/portal/portal-spec.md` §5, §8, §9, §11, §12, §16
- `docs/content-architecture.md` §4 and §7
- `tools/build_content.py`, `tools/mdparse.py`

BEFORE YOU START — ask me for: the GitHub repo (owner/repo), and confirm I have created the fine-grained token
(Actions: write, this repo only) and the Firebase service account key. Stop until I answer.

STEP 1 — `tools/apply_drafts.py`
- `--publish <n>`: read `drafts` with state `approved` (service account); for each, compare `base_hash` with the
  current file; on mismatch, report "Changed since this draft began" for that item and skip it.
- Write the item into its file in canonical form using `mdparse.py` (one writer, one parser). Copy uploads to
  `content/images/<kind>/<id>.png` or `content/audio/<text-id>.mp3`; update `images/manifest.csv` status.
- Set `status: reviewed` and `reviewed_by` from the draft; retire drafts set `status: retired`.
- `--roundtrip-test`: for every file in `content/`, parse → write → parse must give identical data. Run it once
  and commit any formatting changes as a separate "Canonical format" commit before the first publish.

STEP 2 — Pack output in `build_content.py`
- `--pack <dir> --pack-version <n>`: the same tree as `app/src/main/assets/content/` plus `manifest.json`
  (spec §9) with SHA-256 and bytes for every file. Also write the same manifest into the app assets.
- Retired items stay in the output with `status: retired`.
- `schema_version` read from `content/_schema/VERSION`; `min_app_version` from `content/_schema/MIN_APP`.

STEP 3 — `.github/workflows/publish-content.yml`
- `workflow_dispatch` inputs: `publish_id`, `mode` (`apply` | `revert`). Concurrency group `publish`.
- Steps per spec §8: Apply → `--check` → build (assets, pack, source) → commit to `main` ("Content v{n}: <ids>")
  → upload `packs/v{n}/` (Cache-Control `public, max-age=31536000, immutable`) and `source/v{n}/` → update
  `items` → write `channels/preview.json` (Cache-Control `no-cache`) → close drafts → state `on_preview`.
- After each step, update `publishes/{n}.state`. On any failure: state `failed`, `report.errors[]` with item ID and
  field where known, no commit, no channel change.
- `mode: revert`: `git revert` the latest content commit, then the same steps from `--check` on.

STEP 4 — Functions (`functions/`, TypeScript)
- `startPublish()`: caller must be an active owner; at least one approved draft; next version = last + 1; create
  `publishes/{n}` (`queued`); dispatch the workflow. GitHub token from Secret Manager.
- `makeLive(version)`: owner; version must be `on_preview`; copy `channels/preview.json` to
  `channels/stable.json`; mark `live`.
- `undoLatest()`: owner; dispatch with `mode: revert` as a new version.

STEP 5 — S9 Publish (owner)
- Ready to publish: approved drafts, with Unapprove.
- Publish to preview → live progress from `publishes/{n}.state`: Applying → Checking → Building → Uploading →
  On preview. Failure shows the report; each line links to its item.
- On preview: "v{n} is on preview. Check it on your test phone, then Make live." **Make live** button.
- History: version, date, by, items, channel; Undo on the latest only.

DO NOT:
- Let any path skip `--check`.
- Reuse or decrease a version number.
- Move `channels/stable.json` anywhere except in `makeLive`.

ACCEPTANCE:
1. Round-trip test passes on all of `content/`.
2. Approve one mantra change and one image; Publish → `on_preview`; `channels/preview.json` points at v{n};
   `channels/stable.json` unchanged; one commit on `main` touching only those files and the built assets.
3. Approve a story missing hi → publish ends `failed`, report names the story and "hi", no commit, channels
   unchanged, the draft stays approved.
4. Edit a file directly on `main` after a draft began on it → Apply reports "Changed since this draft began".
5. Make live → `channels/stable.json` equals `channels/preview.json`; no rebuild ran.
6. Undo latest → version n+1 on preview with the previous content.
7. Two Publish clicks in quick succession run one after the other, never together.
8. Every file in `packs/v{n}/` matches its SHA-256 in `manifest.json`.
