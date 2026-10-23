# Content portal — start here

1. Read `portal-spec.md` (the whole design: roles, screens, publish, packs, app updates).
2. Set up first (you, not Cursor):
   - Firebase project on the **Blaze** plan with a budget alert; Storage bucket in `asia-south1`.
   - Your Google email as the first owner.
   - GitHub fine-grained token (Actions: write, this repo only) and a Firebase service account key — needed at C3.
3. Run the Cursor prompts in order, one at a time, committing after each passes:

| Prompt | Builds | Depends on |
|---|---|---|
| `docs/cursor/C1-portal-foundation.md` | Source JSON from the build, Firebase rules, sign-in, roles, library, team | — |
| `docs/cursor/C2-portal-editors-uploads.md` | Drafts, editors, image and audio upload, review | C1 |
| `docs/cursor/C3-portal-publish-pipeline.md` | Apply, check, build, pack, preview, make live, undo | C2 |
| `docs/cursor/C4-app-content-updates.md` | App takes new packs silently | can run any time; **must ship in the first store release** |

The portal track runs alongside the app phases. C4 belongs before Phase 6 (Release).
