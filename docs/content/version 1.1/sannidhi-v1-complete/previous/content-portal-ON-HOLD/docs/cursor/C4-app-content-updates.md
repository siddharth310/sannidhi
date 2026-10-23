# C4 — App: silent content updates from packs

Make the Android app take new content packs without a store update, to `docs/portal/portal-spec.md` §9–10.
This must ship in the **first** store release; without it, those phones can never receive content updates.
Report back with every acceptance check.

READ FIRST:
- `docs/portal/portal-spec.md` §9, §10, §15
- `AGENTS.md`
- `ContentRepository.kt` (in the project's base package since P1)

STEP 1 — `PackStore`
- Knows two sources: the bundled pack (`assets/content/`) and downloaded packs in
  `filesDir/content/packs/v{n}/`, with `filesDir/content/active` holding the active version number.
- `root()` returns the active pack if it is complete and verified, otherwise the bundled pack.
- On cold start only: if a `ready` pack newer than active exists, make it active; keep the previous one; delete
  anything older.

STEP 2 — `ContentRepository` reads through `PackStore.root()`
- No other class reads content files. Image and audio paths resolve through `PackStore` too.
- Retired items: never listed, still resolvable by ID (practice history shows their names).

STEP 3 — `ContentUpdateWorker` (WorkManager)
- Enqueued on app start, unique work, at most once per 12 hours, constraint: network connected.
- Channel URL from `BuildConfig.CONTENT_CHANNEL_URL`: release → `channels/stable.json`, internal test builds →
  `channels/preview.json`.
- Accept only if `pack_version` > active, `schema_version` is in `BuildConfig.CONTENT_SCHEMAS`, and
  `min_app_version` ≤ `BuildConfig.VERSION_CODE`. Otherwise finish quietly.
- Download the manifest, then only files whose SHA-256 differs from the active pack into
  `packs/v{n}.staging/`; copy unchanged files locally.
- Files over 2 MB: download only on an unmetered network (separate work with that constraint). Until present, the
  previous file is used; a text with no recording on the phone shows Read and Count only.
- Verify every SHA-256. Any mismatch or error: delete the staging folder and stop; next check retries.
- When complete, rename to `packs/v{n}/` and write `ready`.

STEP 4 — Privacy and silence
- Plain HTTPS GET with OkHttp or HttpURLConnection. No Firebase SDK for content, no user ID, no analytics, no
  extra headers.
- No UI at all: no message, badge, dialog, progress or error.

DO NOT:
- Switch packs while the app is running.
- Change any content model or ID.
- Show the user anything about updates.

ACCEPTANCE:
1. Fresh install, airplane mode: the app runs fully on the bundled pack.
2. Publish a changed mantra meaning to preview; an internal build picks it up on the second cold start after a
   network is available; a release build does not until Make live.
3. Corrupt one byte of a downloaded file (test hook): the pack never becomes active; the app keeps working.
4. A pack with `schema_version` the app doesn't support is ignored with no visible effect.
5. A new 25 MB recording: not downloaded on mobile data; downloaded on Wi-Fi; Listen appears on the next start.
6. Retire a story: it disappears from Stories after the next start; a puja or practice entry that links it still
   resolves its name.
7. Unit tests for `PackStore` selection, version rules and hash verification.
