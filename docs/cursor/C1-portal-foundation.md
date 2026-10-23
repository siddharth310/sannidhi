# C1 — Portal foundation: Firebase, sign-in, roles, library (read-only)

Build the foundation of the content portal to `docs/portal/portal-spec.md`. Report back at the end with what you
built, what you could not, and the result of every acceptance check.

READ FIRST:
- `docs/portal/portal-spec.md` §1–3, §6 (S1–S4, S10), §11, §12, §13, §15
- `docs/content-architecture.md` §2–4 and §7
- `AGENTS.md`

BEFORE YOU START — ask me for anything you need and stop until I answer:
- Firebase project ID, and confirmation that it is on the Blaze plan with a budget alert
- Storage bucket location (default `asia-south1`)
- My Google email for the first owner

STEP 0 — Python side: source JSON
- Add `content/_schema/source/<type>.json` for every item type in spec §4 (deity, puja, mantra, text, naivedyam,
  samagri, observance, override, rashi-profile, gochara, story, image, audio). Shape = the parsed source file
  before transliteration: frontmatter fields + prose as `{te, hi, en}` + sacred text as `{sa}`.
- Add `--emit-source <dir>` to `tools/build_content.py`: writes `<dir>/<itemId>.json` per item, validated against
  the source schemas, plus `<dir>/_index.json` (id, type, title, file path, status, missing[]).
- Use `tools/mdparse.py` for parsing. Do not write a second parser.

STEP 1 — Firebase scaffolding
- `firebase.json`, `.firebaserc`, `firestore.rules`, `storage.rules`, `firestore.indexes.json` at repo root.
- Rules exactly as spec §11–12 (role read from `users/{uid}`; nothing writable from the browser except
  `drafts`, `users` by owner, and `uploads/` within type and size limits).
- Rules unit tests with the Firebase emulator, one test per row of spec §3, under `portal/tests/rules/`.

STEP 2 — Portal app shell (`portal/`)
- Vite + React + TypeScript, Firebase JS SDK (modular), React Router.
- Tokens from `docs/design-v6.md`: ivory surface, maroon and gold hairlines, Inter for UI; Noto Serif Telugu,
  Noto Serif Devanagari and Cormorant Garamond for sacred text previews. Calm, no charts.
- Layout: left rail (Home, Library, Images, Audio, Review*, Publish*, Team*; * owner only), content area. Desktop
  first; works at 768 px.
- Deploy target: Firebase Hosting site `content-admin`.

STEP 3 — S1 Sign in
- Google sign-in only. If `users/{uid}` is missing or inactive: "This account isn't on the team. Ask the owner to
  add you." and sign out. No sign-up anywhere.
- Owner bootstrap is manual (document the console steps in `docs/portal/setup.md`).

STEP 4 — Seed the index
- A one-off script `tools/seed_portal_index.py`: runs the build with `--emit-source`, uploads to
  `source/v{current}/` and writes Firestore `items/{itemId}` from `_index.json`, using a service account file
  passed as an argument. Document how to run it in `docs/portal/setup.md`.

STEP 5 — S3 Library and S4 God view (read-only in C1)
- Library: By god / By type toggle; search across te, hi, en and IAST titles; row shows title, ID, status chip,
  what's missing.
- God view: every item linked to a god, grouped (deity, pujas, mantras, texts, observances, naivedyam, stories),
  plus its image slots with status.
- Opening an item shows its source JSON rendered as a read-only form (editing comes in C2).

STEP 6 — S2 Home and S10 Team
- Home: What's missing (from `items[].missing`, P0 images first) and Live now (`settings/channels`; show "Not
  published yet" when empty). "Needs you" is a placeholder until C2.
- Team (owner): list, add by Google email with role, change role, deactivate. A member is matched to `users/{uid}`
  on first sign-in by email.

DO NOT:
- Parse or write `.md`, `.yaml` or `.csv` in TypeScript.
- Show or store anything about app users.
- Use the app name in any folder, ID, package or site name.
- Add analytics.

ACCEPTANCE:
1. `python tools/build_content.py --emit-source build/source` writes one JSON per item and every file validates.
2. Rules tests pass: a helper cannot write `items`, `publishes`, `settings`, or another person's draft; a signed-out
   user can read nothing except `packs/` and `channels/`.
3. An email not in Team cannot get past S1.
4. Library lists every Gaṇeśa item from the current `content/`, and searching "modak" finds `nv.modaka`.
5. God view for Gaṇeśa shows `img.deity.ganesha.card` and every other Gaṇeśa slot with its manifest status.
6. A helper does not see Review, Publish or Team in the rail.
