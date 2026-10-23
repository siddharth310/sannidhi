# C2 — Portal editors, drafts, uploads and review

Build editing, uploads and review to `docs/portal/portal-spec.md`. C1 must pass first. Report back at the end
with every acceptance check.

READ FIRST:
- `docs/portal/portal-spec.md` §4–8 (S5–S8), §11–12, §15
- `docs/content-architecture.md` §3 (IDs), §5 (language rules), §6 (every template)
- `content/_schema/source/*.json`
- `docs/design-v6.md` components used by the preview frames (god card, god hero, puja step card, story beat,
  naivedyam card, samagri icon)

STEP 1 — Drafts
- `drafts/{itemId}` as spec §11. Edit on an item creates the draft from `source/v{n}/{itemId}.json`, storing
  `base_version` and `base_hash`.
- One open draft per item. Another member sees "Being edited by <name> since <date>" and a read-only view.
- Autosave (debounced ~2 s). Discard: author or owner.
- New item: pick a type, enter a title; propose the ID from the §3 pattern; check pattern and uniqueness against
  `items`. ID editable until first publish, then locked.

STEP 2 — S5 Editor
- One form per type, generated from a per-type form config plus the source schema; validate with Ajv against
  `content/_schema/source/<type>.json`.
- Prose: te / hi / en tabs, a dot on an empty tab.
- Sacred text (`sa`): Devanagari only (block Latin, digits other than Devanagari digits, and other scripts) with a
  live IAST and Telugu preview via `@indic-transliteration/sanscript`, labelled "Preview — the build makes the
  final version".
- Puja editor: Pre · During · Post columns or sections; step cards with drag to reorder; step keys from
  content-architecture §6.2 only; ID pickers (mantra, text, naivedyam, samagri, image) search `items`.
- Story editor: beats with image slot, short line, more paragraph, per language.
- Nāmalu text editor: names table; paste a column or import CSV (`n,sa`); count must match the type
  (aṣṭottaram 108, sahasranāmam 1000) or show a check.
- Words check: highlight never-list words in prose (spec §4). Warning only.
- Checks panel: live list from schema validation, language gaps, words check, image and audio rules.
- Preview panel: the item inside the app frames, Dina and Ratri.

STEP 3 — S6 Images and S7 Audio
- Images: every slot from the manifest; filters missing / supplied / approved, kind, god, P0/P1.
- Upload to `uploads/{itemId}/`. Check in the browser before upload: type, size, minimum pixels and ratio from
  spec §7 (1% tolerance), alpha channel for samagri icons. Reject with the exact reason, e.g.
  "Needs at least 600 × 600; this is 500 × 500."
- Alt text te and hi on every image draft.
- Audio: MP3 ≤ 30 MB; player with names highlighted using the timing fields from content-architecture §6.4;
  owner adjusts values and hears the result.

STEP 4 — Submit, S8 Review
- Submit (helper or owner) → `submitted`. Blocked while the Checks panel has errors (warnings are fine).
- Review queue (owner): oldest first. Before / after view: changed fields highlighted, old and new image side by
  side, both recordings playable.
- Approve: for mantra, deity names, texts, katha steps and stories, **Reviewed by** (free text, remembers last
  value) is required. Sets `approved`, `approved_by`, `approved_at`.
- Send back: note required → `sent_back`; the author sees it in Home "Needs you".
- Home "Needs you" now live: owner → submitted drafts; helper → their sent-back drafts.

STEP 5 — Retire (owner)
- Retire on any published item: confirmation "It will disappear from the app. Its ID stays forever." Creates a
  draft with status `retired` that goes through Publish like any change.

DO NOT:
- Write content files from TypeScript; drafts stay JSON in Firestore until C3 applies them.
- Let a helper set `approved`, `reviewed_by` or `approved_by` (rules must reject it even if the UI is bypassed).
- Offer AI text generation for any sacred field.

ACCEPTANCE:
1. Helper edits `ganesha.mantra.mula` meaning (te), submits; owner sees it in Review with only that field
   highlighted; approves with Reviewed by "Sri Rama Sastry".
2. Typing "om gam" in the `sa` field shows "Mantra text must be in Devanagari." and Submit is blocked.
3. Approve on a story with Reviewed by empty is blocked.
4. Uploading 500 × 500 into `img.deity.ganesha.card` is rejected with the exact message; 600 × 600 is accepted and
   the card preview shows it in Dina and Ratri.
5. A samagri icon PNG without transparency is rejected: "Needs a transparent background."
6. While a helper edits an item, the owner sees it read-only with "Being edited by …".
7. Reordering two During steps in the Vināyaka Chavithi puja keeps every step ID unchanged.
8. Send back without a note is blocked; with a note, the helper sees it on Home.
9. Rules test: a helper's direct Firestore write of `state: "approved"` is rejected.
