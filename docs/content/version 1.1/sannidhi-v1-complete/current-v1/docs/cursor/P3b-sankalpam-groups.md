# P3b — Sankalpam v7: groups

Add groups on top of P3a. P3a must pass first. Report back with every acceptance check as pass/fail.

READ FIRST:
- `docs/design/v7/png/*.png` — reference screens; match them
- `docs/design/sankalpam-design.md` (v7) — S1 Groups tab, S3 group variant, S5 group About, S8–S13, §7 Firebase
- `docs/stories/sankalpam-user-stories.md` (v7) — US-07.1 to US-11.2
- `AGENTS.md`

BEFORE YOU START — ask me for the Firebase project and the link domain, and stop until I answer.

STEP 1 — Sign-in (S10)
- Firebase Auth with Google; on iPhone builds also Sign in with Apple. Asked once, only before Start a group or
  Join. No phone, OTP or password anywhere.

STEP 2 — Firestore model and rules (design §7)
- Collections exactly as design §7. Counts are written as one batch: `members/{uid}.count`, `groups/{gid}.total`
  and `days/{date}/readers/{uid}` with `FieldValue.increment`.
- Rules: anyone may read `open` groups' summary fields; only members read `members` and `days`; a member may only
  increment their own count by 1–10,000 per write and only while the group is `open`, they are `member`, and
  today ≥ startDate; only the organiser may change goal/days or set a member `removed`; `memberCount` ≤ 200;
  purpose ≤ 140 characters with no links or digits. Rules tests in the emulator for each.
- A scheduled function (or the first client open after endDate) sets `status: ended`.

STEP 3 — Start a group (S11) and code screen (S12)
- Reuse S2 steps 1–2, then the group form. 6-character code without look-alike letters (no 0/O, 1/I);
  `codes/{code}` → gid. Share message with name, code and link `https://<domain>/g/{code}` (App Links on Android,
  Universal Links on iPhone).
- Creates a local `Sankalpam` (kind `group`, groupId) for the organiser.

STEP 4 — Join (S1 Join card, S9) and Find open groups (S8)
- Code field and links open the preview; a member opening their link goes straight to the player.
- Join sheet: name (from Profile), optional reason, Keep private / Show to the group. Creates the local
  `Sankalpam` (kind `group`).
- Find open groups: globe band, filters Near me (same city, then same state), Starting soon (start > today), All.
  No member names anywhere on this screen.

STEP 5 — Group player and About
- Group card opens the player directly. Count line "You {n} · Group {total} of {goal}".
- Each saved `PracticeEntry` for a group is sent in the batch above; offline entries stay with
  `sentToGroup = false` and the line "Will add to the group when online"; send when back online. If the member was
  removed meanwhile, do not send.
- Notes: listen to today's `readers`; show "Name · +n" for changes by others, one at a time, queued.
- About (group): as design S5, readers ordered by `lastAt` descending, reasons only where shown; Leave (members);
  organiser: Change goal or days, Remove a member. Report on open groups (S13).
- A group never shares entries with a japam of the same text.

STEP 6 — End
- After endDate the group card moves to Completed for every member with group total and own count. Leaving or
  being removed moves it to Completed for that member only.

DO NOT:
- Add chat, photos, messages, phone numbers, rankings, medals or "top".
- Show a location finer than a city.
- Count group chanting into My japam, or My japam into a group.

ACCEPTANCE (from US-07.1 to US-11.2, two phones A and B):
1. A (signed out) taps Start a group → sign-in once (Google; Apple too on iPhone) → creates Kārtika Aṣṭottaram,
   goal 1,008, 41 days, Open, Hyderabad → code screen with Share; A is the first member.
2. Purpose with a link → "Purpose can't have links or numbers"; 141st character not accepted.
3. B enters the code → preview → joins as Lakshmi with a private reason → player opens; A's About lists Lakshmi
   without the reason.
4. B opens the shared link → preview; once joined, the link opens the player.
5. B finishes one reading → B sees "You 1 · Group 1 of 1,008"; A sees the note "Lakshmi · +1".
6. B also has a japam for the aṣṭottaram; a reading there leaves the group total unchanged, and vice versa.
7. B offline finishes 54 → "Will add to the group when online"; back online → group +54 once.
8. Wrong code → "This code doesn't match a group". 200-member group → Join disabled, "This group is full".
9. Find open groups lists the open group under Hyderabad with no member names; a code-only group never appears.
10. Goal reached on day 10 of 21 → group still open and counting; after day 21 → Completed for A and B.
11. B leaves → Completed for B, B's counts stay in the total. A removes a member → their counts stay.
12. Report shows exactly four reasons; offline → "We'll send this when you're online".
13. No screen in groups offers chat, photos, messages, phone numbers or ranks.
