# M0 — Bring the v1 files into the repo without changing the app

## You (before Cursor)

1. Put `sannidhi-v1-complete.zip` in the repo root.
2. Make sure the app builds and runs as it does today, and commit anything open.
3. Open Cursor in the repo, paste everything below the line, and send it.

---

You are bringing a pack of documents, content and tools into the Sannidhi repo. **This task must not change the
app.** After you finish, the app must look and behave exactly as it does now, screen for screen. Nothing changes:
not the colours, fonts, layout, text, navigation, behaviour or data. The designs in the pack are for later modules.
Do not apply them now.

### Step 1 — Safety net
- Run `git status`. If anything is uncommitted, stop and tell me.
- Create the tag `pre-v1-intake` on the current commit and the branch `v1/m0-intake`.
- Record the baseline:
  - **Build:** run the debug build and confirm it passes.
  - **Content check:** save the full output of `python tools/build_content.py --check` to
    `_incoming/baseline-content-check.txt`.
  - **Screenshots:** if an emulator or phone is connected, use `adb` to screenshot Home, Puja, Calendar, Sankalpam
    and Stories (English, Hyderabad). Save them to `_incoming/baseline/`. If no device is connected, say so and go on.

### Step 2 — Unpack into a holding area, outside the app
- Run `unzip sannidhi-v1-complete.zip -d _incoming/`. Add `_incoming/` and `*.zip` at the root to `.gitignore`.
- Nothing in `_incoming/` is built, imported or referenced by the app.
- `_incoming/sannidhi-v1-complete/previous/` is reference only. Never copy from it. Never run P2e (replaced by
  Sankalpam v7) or C1–C4 (the portal is on hold).

### Step 3 — Compare before copying
For every file in `_incoming/sannidhi-v1-complete/current-v1/`, find the same path in the repo and mark it:
- **NEW** — the file is not in the repo;
- **SAME** — the file is identical;
- **DIFFERENT** — the file exists but its content differs.

Write the table to `docs/v1/INTAKE-REPORT.md`, with a one-line summary of each difference for DIFFERENT files.

### Step 4 — Copy only what cannot change the app

| Path in the pack | NEW | DIFFERENT |
|---|---|---|
| `START-HERE.md`, `docs/**` | Copy | Move the old file to `docs/_archive/2026-10-24/<same path>`, then copy the new one |
| `tests/**` | Copy | Same as docs |
| `content/source/**` (the 108 names and the review sheet) | Copy | Stop and ask me |
| `narration/**` | Copy | Stop and ask me |
| `tools/make_sacred_draft_audio.py`, `tools/make_narration.py`, `tools/make_placeholder_music.py`, `tools/sannidhi-audio.ipynb` | Copy | Stop and ask me |
| `content/stories/**`, `content/music/**`, `content/sfx/**` | **Do not copy.** Leave them in `_incoming/` | Do not copy |

The last row stays in `_incoming/` because M2 (Stories) brings it in at its first step. Copying it now could change
how the stories look today.

Never touch any of these in this task:
- `app/`, `res/`, `assets/content/`, any Gradle file or the manifest;
- `tools/build_content.py`, `tools/import_ashtottara.py`, `tools/panchanga.py`, `tools/mdparse.py`;
- any file in `content/` other than `content/source/`.

Old Cursor prompts (`docs/cursor/P2c*`, `docs/cursor/P2e*`): move them to `docs/_archive/2026-10-24/`. Do not
delete them.

### Step 5 — Prove nothing changed
- **Changed files:** `git diff --stat pre-v1-intake` lists only `.gitignore`, `START-HERE.md`, `docs/`, `tests/`,
  `content/source/`, `narration/` and the four tools files. If anything else appears, undo that change.
- **Content check:** run `python tools/build_content.py --check` and compare it with the baseline. It must match.
  If `content/source/` produces a new warning, report it and change nothing; I will decide.
- **Build:** run the debug build. It must pass.
- **Screens:** install the build and screenshot the same five screens. Compare each one with its baseline. They must
  look the same. Report each screen as same or different.
- **Sankalpam:** if it shows anything at all, it shows exactly what it showed before. The names are not imported
  yet; that happens in M1.

### Step 6 — Commit and report, then stop
- Commit with the message "v1 intake: docs, tests, names source, narration, audio tools (no app changes)".
- Report:
  - the INTAKE-REPORT summary (counts of NEW, SAME and DIFFERENT);
  - what you copied, archived and held back, and why;
  - the Step 5 results, one line each;
  - anything you need from me.
- Do not start M1. Wait for "M0 signed off".

### Do not
- Restyle, rename, re-layout, re-word or "tidy" anything in the app, even if the designs in `docs/design/` differ.
- Run the importer, the build in release mode, the Colab notebook or any module prompt.
- Delete any file. Archive it instead.
