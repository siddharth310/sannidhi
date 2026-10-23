# Sankalpam — design spec (v6.2)

Extends `docs/design-v6.md`. Same tokens, type, shapes, TopBar and rules; nothing here overrides them.
Stories: `sankalpam-user-stories.md`. Reference screens: `docs/design/v6/png/` (files `30`–`37`, `50`–`58`).
All examples use Hyderabad, Fri 11 Sep 2026, app language English.

**What changed in v6.2 (18 Oct 2026):** Chant along is back for mantras (31c): the recording plays, the mantra is
on screen with the current word lit, and every recitation adds one to the ring. Japa and sankalpam are one count
(§1b). Listen (32b) is for texts only. Mantras now carry a recording field in content (§10).

---

## 1. What this module must feel like

| Principle | What it means on screen |
|---|---|
| One press, one count | The counter adds exactly one. No long-press tricks, no hidden totals. |
| One count everywhere | Japa, sankalpam and group totals come from the same practice entries. There is no separate japa count. |
| Free will | Never show what wasn't done: no zero days, gaps, streaks, "today 0", reminders or red. |
| Gathering, not competition | Readers are listed by when they last added, never by size. No ranks, medals or "top". |
| Private by default | Intents stay on the phone unless the member chooses to show one to the group. Locations are city-level. |
| Elder-first | One primary action per screen, 56 dp buttons, labels in words not icons alone, every gesture also has a button. |

Module colour: **tulasi** — accent `#3E6A33`, dark `#2F4F27`, tint `#E3EEDB`, border `#B7CDA8`. Gold stays hairline-only
(progress, rings, dividers). Destructive actions (Leave, Close group, Remove) use maroon `#7A1F2B` text on an outlined
button — never a red fill.

## 1b. One count, every mode

Japam is practising a mantra; a sankalpam is a vow over that practice; a group is a shared sankalpam. All three
read the same ledger.

| Practised | Modes, in order | One entry adds |
|---|---|---|
| Mantra (`use: japa`) | **Chant along** (when it has a recording) · Count | repetitions |
| Text (aṣṭottaram, sahasranāmam, stotram) | Read · Listen (when it has a recording) · Count | readings |

- Every mode saves `PracticeEntry` rows. My practice = all entries. A sankalpam total = entries with its
  `sankalpamId` (including Add by hand). A group total = members' entries for that group.
- Counts for (one sankalpam or Just my practice) applies to every mode the same way.
- The Nāmalu chip lists a god's texts **and** japa mantras together, so japa starts from the same place as
  pārāyaṇam. New sankalpam's "What will you chant?" offers both.
- A sankalpam for a mantra opens Chant along from Practise when the mantra has a recording, otherwise Count.

---

## 2. Navigation map

```
Home ─ Sankalpam card / My sādhana bar
 └─ Sankalpam (TopBar title "Sankalpam")
     ├─ chips:  My sankalpams | Nāmalu | Discover
     │
     ├─ My sankalpams ─ segmented: Active | Completed
     │    ├─ card → Sankalpam page (34) ─ options ⋮ → sheet: Add by hand (35) · Change total or days (36)
     │    │                                               · Mark complete · Leave
     │    │    └─ Practise → Practise (31 / 31b / 31c / 32 / 32b) with Counts for preset
     │    ├─ group card → Group page (54)
     │    └─ + New sankalpam (33) ─ Just me → Begin → Sankalpam page
     │                             └─ Start a group → Sign in (52, once) → Start a group (55) → Invite (56) → Group page
     │
     ├─ Nāmalu ─ god grid → texts and mantras → Practise
     │                                           texts:   Read · Listen · Count
     │                                           mantras: Chant along · Count
     │
     └─ Discover (50) ─ group card → Group preview (51) ─ Join → Sign in (52, once) → Join sheet (53) → Group page (54)
                                                        └─ Report (58)
Puja step with a mantra and a repeat count → Chant along (31c) with Stop after preset → back to the same step.
Completion moment (37) appears over whatever screen saved the count that completed a sankalpam.
```

Routes: `sankalpam`, `sankalpam/mine?tab=active|completed`, `sankalpam/{id}`, `sankalpam/new`,
`sankalpam/text/{textId}?for={sankalpamId}&mode=read|listen|count&return=puja`,
`sankalpam/mantra/{mantraId}?for={sankalpamId}&mode=chant|count&stop={n}&return=puja`, `sankalpam/discover`,
`group/{groupId}`, `group/new`, `group/{groupId}/invite`. Sheets are not routes.

---

## 3. Screen inventory

| # | Screen | Route / surface | Story | Status |
|---|---|---|---|---|
| 30 | My sankalpams · Active | `sankalpam/mine?tab=active` | US-04.1 | updated: segmented tabs, Discover enabled, group card |
| 30b | My sankalpams · Completed | `sankalpam/mine?tab=completed` | US-05.1 | new |
| 31 | Practise · Count (mantra) | `sankalpam/mantra/{id}?mode=count` | US-01.1 | updated: second mode when a recording exists |
| 31b | Practise · Count (text) | `sankalpam/text/{id}?mode=count` | US-01.1 | new |
| 31c | Practise · Chant along (mantra) | `sankalpam/mantra/{id}?mode=chant` | US-01.3 | **restored** (from v5) |
| 32 | Practise · Read | `…text/{id}?mode=read` | US-01.2 | unchanged |
| 32b | Practise · Listen (texts only) | `…text/{id}?mode=listen` | US-01.2 | updated: texts only |
| 33 | New sankalpam | `sankalpam/new` | US-03.1 | updated: start date, group enabled, mantras listed |
| 34 | Sankalpam page | `sankalpam/{id}` | US-04.2 | new |
| 35 | Add by hand | sheet over 34 | US-04.3 | new |
| 36 | Change total or days | sheet over 34 | US-04.4 | new |
| 37 | Sankalpam complete | full-screen moment | US-05.1 | new |
| 50 | Discover | `sankalpam/discover` | US-08.1 | new |
| 51 | Group preview | `group/{id}` (not a member) | US-08.1 | new |
| 52 | Sign in for groups | sheet | US-06.1 | new |
| 53 | Join | sheet over 51 | US-08.2 | new |
| 54 | Group page | `group/{id}` (member) | US-09.2 | new |
| 55 | Start a group | `group/new` | US-07.1 | new |
| 56 | Group ready · invite | `group/{id}/invite` | US-07.1 | new |
| 57 | Organiser options | sheet over 54 | US-10.1 | new |
| 58 | Report | sheet | US-10.2 | new |

---

## 4. Shared components

All sizes in dp; text in sp. Side margin 16. Card radius 24, sheet top radius 28, buttons 16, chips pill.

| Component | Spec |
|---|---|
| **SectionChips** | Three pills, 44 tall (56 hit), gap 8: My sankalpams · Nāmalu · Discover. Selected = tulasi fill, ivory text. |
| **Segmented** | 48 tall, surface `#FFFDF8`, border `#E6D6BF`, radius 14, inner pills radius 11. Used for Active/Completed, Read/Listen/Count (texts) and Chant along/Count (mantras). Selected = tulasi fill. |
| **SankalpamCard** | Tint `#E3EEDB`, radius 24, padding 18, min height 112. Row 1: title (Cormorant 22) + ⋮ (44). Row 2: owner line (Inter 14, `#4A2E32`): "Just me · day 9 of 41" or "Group · 38 people · day 9 of 21". Row 3: total (Inter SemiBold 28, `#2F4F27`) + "so far · of 1,00,000" (Inter 15). Row 4: progress hairline 2, track `rgba(62,106,51,.18)`, fill gold. Hairline hidden when no target. |
| Card variant · Starts | Row 3 replaced by "Starts Wed 7 Apr" (Inter SemiBold 20). No hairline. |
| Card variant · Completed | Surface `#FFFDF8`, border `#E6D6BF`. Total line "1,00,000 offered". Owner line ends with the end date. Extend chip (outlined tulasi, 44) only when it ended by days. |
| **CountsForChip** | Pill 44, tint fill, border `#B7CDA8`, "Counts for: <name> ▾". Opens a sheet with radio rows: "Just my practice" + each active sankalpam for this text or mantra. One choice only. |
| **TotalBlock** | Big number Inter SemiBold 44 / 48, unit line Inter 16. Under it, day line Inter 15. Gold hairline 2 below. |
| **Counter · mantra** | 236 round surface, shadow `0 10 30 rgba(74,28,34,.12)`. Ring r 104, stroke 6, gold on `#EFE3D1`, fills per 108. Centre: count 56 sp, "of 108 · round N" 15 sp. Haptic tick per count; stronger tick at 108. |
| **Counter · text** | Same surface, no ring segments: a single thin gold circle. Centre: count 56 sp, "readings" 15 sp. Caption below: "Count each reading you finish". |
| **MantraStage** | Card, surface `#FFFDF8`, border `#E6D6BF`, radius 24, padding 24, min height 168. Mantra centred in the chosen text view: Noto Serif Telugu or Devanagari 30 sp / line 1.5, IAST in Cormorant Italic 28. Wraps, never truncates. Current word: maroon `#7A1F2B` text with a 3 dp gold underline, moving with the word timings. Without word timings the whole line rises from 70% to 100% opacity once per recitation. Under it: text-view pill (తెలుగు · संस्कृतम् · IAST) and a "Meaning" text button that shows one line (Inter 15) under the mantra. |
| **Ring · compact** | Counter · mantra at 180, not pressable. Centre: count 44 sp, "of 108 · round N" 14 sp. |
| **StopAfterChips** | Overline "STOP AFTER" (Inter 12, tracking .08em) + one row of 44 pills: 11 · 21 · 108 · 1,008 · Keep going. Default = the mantra's `count_default`. From a puja step: the step's `repeat`, shown as its own selected pill. |
| **ChantPlayerBar** | Pinned bottom, surface `#FFFDF8`, top hairline. Play/pause 72 round tulasi (label under: "Play" / "Pause") · right of it "Finish and save" (56, outlined tulasi). Above the bar, caption Inter 15 `#6B5559`: "Each recitation that plays through adds one". |
| **ReaderRow** | 64 tall: initial circle 40 (tint, Inter SemiBold 16) · name (Inter 17) · optional intent line (Inter 14, italic) · count right ("+3", Inter SemiBold 17). |
| **Bubble** | Pill 40 tall, ivory 95% with shadow, "Lakshmi · +3", slides up 16 dp and fades out over 2.4 s. Never more than one at a time; queued. |
| **BottomSheet** | Surface `#FFFDF8`, radius 28 top, handle 40 × 5, padding 20, scrim `rgba(43,26,28,.32)`. Primary button pinned at the bottom. |
| **DateChips** | Horizontal row of 44 pills: "Today", "Yesterday", then "Wed 9", "Tue 8"… back to 7 days. Future and older dates never appear. |
| **Stepper** | − / value / + in one 64-tall row; value field 120 wide, Inter SemiBold 28, numeric keyboard on tap. Quick chips under it: 1 · 11 · 108 · 1,008. |
| **ConfirmDialog** | Title (Cormorant 22), one line of body, two 56 buttons: secondary left, primary right. Destructive primary = maroon outlined. |

Numbers use the Indian grouping in en and te/hi digits per locale settings (1,00,000). Counts never abbreviate (no "1L", "100K").

---

## 5. Screens

### 30 · My sankalpams · Active
TopBar "Sankalpam" → SectionChips (My sankalpams selected) → Segmented Active | Completed (16 below chips) →
cards, gap 12 → footnote (Inter 15, `#6B5559`): "Practising without a sankalpam counts too." →
floating button bottom-right: "+ New sankalpam", 56 tall, tulasi fill, 16 from right and bottom.
- Order: personal and group mixed, most recently practised first; Starts cards last.
- Empty: illustration-free, one centred button "Begin a sankalpam" (primary) and the footnote. No zero totals.
- Offline: group cards show their last known total and "Updated when online" (Inter 13) — no error colour.
- Home's My sādhana bar lists only running sankalpams (personal and group). "Starts …" cards stay off it until their start date.

### 30b · My sankalpams · Completed
Same frame, Completed selected. Cards in Completed variant, newest end date first. Group items show
"Group · 64 people · your 54" and the group total "2,016 readings offered".
- Copy for ended-by-days: "41 days · 60,000 offered" + Extend chip. Never "only", "short of", "failed".
- Every personal Completed card carries two small buttons: **Extend** (only when it ended by days) and **Start again** (opens 33 prefilled with the same text, total and days; start = Today). Group items have no buttons.
- Empty: "Completed sankalpams will be kept here."

### 31 · Count (mantra)
Unchanged from v6. When the mantra has a recording, Segmented shows Chant along | Count and Count is the second
segment; without one, no Segmented is shown. Counts-for chip reads the chosen sankalpam's name.

### 31b · Count (text)
Same layout as 31 with Counter · text. Title in TopBar is the text name. Under the counter: "Count each reading you finish".
Buttons: −1 (96 wide) · "Finish and save" (primary). If Counts for is a sankalpam with a target, one line above the buttons:
"12 + 3 = 15 so far · of 108" (Inter 15) so the effect is visible before saving.

### 31c · Chant along (mantra) — restored
TopBar (title = mantra name, e.g. "Gaṇeśa mūla mantra"). Body, top to bottom, gap 16:
1. Segmented: **Chant along** | Count (Chant along selected; it is the default when a recording exists).
2. CountsForChip.
3. MantraStage.
4. Ring · compact, centred.
5. StopAfterChips.
6. ChantPlayerBar pinned bottom, caption above it.

Behaviour:
- **Play** starts the recording and loops it without a gap. Each recitation that plays to its end adds one: the
  ring steps, light haptic; medium haptic at every 108.
- **Pause** stops mid-recitation. The count so far is kept. Play resumes the same recitation from where it stopped;
  it counts when it ends.
- **Stop after** reached: the recording fades out over 600 ms and stops. The ring shows the count and a line
  replaces the caption: "108 offered" (Inter SemiBold 20). Finish and save becomes the primary button. Play starts
  another set.
- **Keep going**: loops until paused; rounds continue (round 3 after 216).
- **Finish and save** saves the count and returns (to the puja step when `return=puja`). Back saves too; a
  cut-off recitation is never counted. Nothing finished → nothing saved.
- **Screen locked or app in background**: playback and counting continue. Lock-screen and notification controls
  show the mantra name, "54 of 108" and Play/Pause only (no skip buttons).
- **Interruptions**: a phone call, another app's audio or unplugged headphones pause playback; it never resumes by
  itself.
- **Text view** can change while playing; the highlight moves to the same word in the new script without a break.
- Screen stays on while this screen is in front.
- No recording → this screen is never offered; 31 is shown alone.

### 32 · Read — unchanged from v6.

### 32b · Listen (texts only)
Segmented with Listen selected → CountsForChip → names list with the current row highlighted (tint fill, gold left
number ring filled) → player bar pinned bottom: progress hairline (gold, full width), time left (Inter 14),
play/pause 72 round tulasi, −10 s and +10 s 56 buttons. Line above player: "Counts automatically when it finishes".
- For texts only (aṣṭottaram, sahasranāmam, stotram). Mantras use 31c.
- Listen segment is hidden when the text has no recording.
- Screen stays on while playing; playback continues with the screen locked, as in 31c.

### 33 · New sankalpam (updated)
Fields in order, gap 16:
1. What will you chant? — card button (64) showing the chosen text or mantra; "Change ›". The picker lists each
   god's texts and japa mantras together.
2. With whom? — two 72 tiles: **Just me** (default) · **Start a group** (enabled; leads to Sign in once, then 55).
3. Total to reach (optional) — field 56 + chips 1,008 · 10,008 · 1,00,000.
4. For how many days? (optional) — chips 11 · 21 · 41 · One māsam · No end.
5. Starts — DateChips "Today · Tomorrow · Pick a date"; when a calendar observance falls in the next 30 days add its chip, e.g. "Vināyaka Chavithi · Mon 14".
6. Your intent (optional, only you see it) — field 56.
Primary "Begin" pinned bottom. Disabled until a text or mantra is chosen.
Errors (inline, Inter 14, maroon text, no red): "Enter a number above zero, or leave it empty".

### 34 · Sankalpam page
TopBar (title = text or mantra name). Body:
1. Header card (tint, radius 24, padding 20): owner line "Just me · started Thu 3 Sep" · intent line with lock icon
   "For a peaceful home · only you see this" (only when an intent exists).
2. TotalBlock: "12,960" / "so far · of 1,00,000" / "Day 9 of 41 · ends Tue 13 Oct".
3. Primary "Practise" (56) → Practise with Counts for preset; for a mantra with a recording it opens Chant along.
   Secondary text button "Add by hand".
4. **This month** (Cormorant 22): rows 56 tall, newest first: date left ("Fri 11 Sep"), count right ("1,296"), a small
   "added by hand" tag (pill 22, `#EFE6D8`) where it applies. Only days with practice appear.
   No month calendar grid, no bars, no empty dates.
5. Options button (⋮, 48×48) sits top-right inside the header card, not in the TopBar — TopBar stays Back · title · Home on every screen. It opens the options sheet: Add by hand · Change total or days · Mark complete · Leave.
   For a group the same page is replaced by 54.

### 35 · Add by hand (sheet)
Title "Add by hand" · Stepper (default 108 for mantras, 1 for texts) · "Which day?" DateChips (Today selected) ·
note "Marked 'added by hand' on this sankalpam" · primary "Add 108" (label repeats the number).
Save disabled at 0. Dates before the start date do not appear.

### 36 · Change total or days (sheet)
Title "Change total or days" · "Total to reach" field (current value) · "Days" chips with current selected and
"Extend by 21" helper chip · line "12,960 offered so far stays as it is." · primary "Save".
Error when total < so far: "Already 12,960 offered. Choose a higher total or mark it complete".

### 37 · Sankalpam complete
Full screen, dusk sky (`#F0C9B4 → #F8E2C6`), layered hills at the bottom 30%, a single diya illustration
(gold flame, maroon base) centred at 40% height, soft glow.
Text centred: "Sankalpam complete" (Cormorant 34) · "1,00,000 offered" (Inter SemiBold 28) ·
"Gaṇeśa mūla mantra · 41 days" (Inter 16). Buttons stacked: primary "Keep chanting" · secondary "See in Completed".
Gentle haptic once; no sound, no confetti. For a group: "Together you offered 1,008 readings".
If it appears during Chant along, playback fades out first.

### 50 · Discover
SectionChips (Discover selected) → globe band 200 tall: a stylised globe (ivory sphere, gold hairline meridians) with
tulasi dots on cities that have open groups; a city label pill appears on the selected dot →
filter chips: Running · Starting soon · Near me → group cards (surface, radius 24):
name (Cormorant 22) · text (Inter 15) · purpose (Inter 15, 2 lines max) · "Run by Lakshmi · Hyderabad" ·
"38 people · day 9 of 21 · 412 readings". Report link (Inter 14, `#6B5559`) bottom-right of each card.
- Empty filter: "No groups here yet" + outlined "Start a group".
- Offline: "Discover needs internet. Your own practice works offline." Nothing else changes.
- Never member names, never a map pin finer than a city.

### 51 · Group preview (not a member)
TopBar "Group" → header: name, text, purpose, "Run by Lakshmi · Hyderabad" → TotalBlock with group total and day →
"38 people chanting" (initials row of up to 4 circles + "+34") → privacy card (shield icon): "Members see your name in this group and your readings. Your intent only if you choose. No chat, no phone numbers." → primary "Join this group" (56, pinned bottom) → "Report this group" text button.
Full group: Join replaced by disabled "This group is full".

### 52 · Sign in for groups (sheet)
Title "Groups need a sign-in" · body "So your readings reach the group and stay yours. You do this once." ·
three icon lines: "No phone number or OTP is needed." · "Members see only the name you choose for the group." ·
"Puja, calendar, stories and your own sankalpams never need it." · primary "Continue with Google" (peacock fill) · text button "Not now".
Shown once; never for anything outside groups.

### 53 · Join (sheet)
Group summary row (name + "38 people · day 9 of 21") · "Your name in this group" field (prefilled from Profile) ·
"Your intent (optional)" field · Segmented: Keep private | Show to the group (Keep private default) ·
primary "Join". Errors: "This group is full" · "This group has closed".
Invite-only path: first a field "Invite code" + "Continue", then the same sheet.

### 54 · Group page (member)
TopBar "Group" (Back · title · Home, as everywhere). Body:
1. Header: group name (Cormorant 28) · purpose · "Run by Lakshmi · Hyderabad"; ⋮ (48×48) top-right of the header opens 57 for the organiser, and a two-row sheet (Leave group · Report) for members.
2. Ring card: 132 ring (gold on `#EFE3D1`) with group total centre "412" / "of 1,008 readings"; right of it
   "Day 9 of 21", "38 people", "Your 24".
3. Primary "Practise with the group" → Practise with Counts for = this group (Chant along for a mantra group with a recording).
4. **Today's readers** (Cormorant 22): ReaderRows by most recent first. Intents only where shown.
   Empty: "Today's readers will appear here".
5. Invite card, below Today's readers (reached by scrolling; organiser and members alike): code in large letters + "Share" (Android share sheet).
Bubble ("Padma · +2", 36 tall) appears in the Today's readers heading row, right-aligned, for 3 s when someone adds. Pending offline count: line under the ring
"Your 54 will add to the group when online".

### 55 · Start a group
Same form style as 33. Fields: Text or mantra · Group name · Purpose (counter "54 of 140", helper "No links or numbers.") ·
"Starts · for how long?" — one horizontally scrolling chip row: Today · Pick a date | 11 · 21 · 41 · One māsam · No end ·
Group target (optional) · Who can join: Open (on Discover) | Invite only ·
City (from master list, search) · primary "Create group" pinned bottom above a hairline.
Note under City: "Only your city is shown, never your address."

### 56 · Group ready · invite
Centred mala mark (64) · "Your group is ready" (Cormorant 32) · "<name> · 21 days from today. Share it with the people you chant with." ·
invite card (tint, tulasi border, radius 24): "INVITE CODE" · code (Inter SemiBold 34, letter-spacing 0.12em, e.g. VNK·4821) ·
"Anyone with this code can join." · "Copy code" pill → for Open groups a note: "Open group: people in and around <city> can also find it on Discover." →
pinned bottom: primary "Share invite" · secondary outlined "Open group".
Share text: "Join our pārāyaṇam '<name>' on Sannidhi. Invite code: <code>".

### 57 · Organiser options (sheet)
Overline "YOU RUN THIS GROUP" · rows 64 with icon + title + one-line hint: Change total or days ("1,008 readings · 21 days") ·
Share invite ("Code VNK·4821") · Remove a member ("Their readings stay in the total") · Close group (maroon, "Moves to Completed for everyone").
Remove opens the member list with a Remove action per row and a ConfirmDialog "Remove Rama? Their past readings stay in the total."
Close replaces the sheet with a confirm sheet: "Close this group?" · "412 readings were offered together. The group moves to Completed for all 38 members, and no one can add to it after this." · maroon "Close group" · outlined "Keep it open".

### 58 · Report (sheet)
Title "Report this group" · line "The organiser is not told who reported." · four radio rows 56: Not about chanting · Hurtful words · Asks for money · Something else ·
"Anything to add? (optional)" note field · primary "Send report" (disabled until a reason is chosen). After, the sheet becomes:
"Thank you" · "We will look at this group. It stays hidden from your Discover from now on." · "Done" → Discover. Offline: "We'll send this when you're online".

---

## 6. Copy deck (en)

| Key | Text |
|---|---|
| counter.caption.text | Count each reading you finish |
| listen.caption | Counts automatically when it finishes |
| mode.chant | Chant along |
| chant.caption | Each recitation that plays through adds one |
| chant.stopAfter | Stop after |
| chant.keepGoing | Keep going |
| chant.offered | {n} offered |
| chant.finish | Finish and save |
| chant.lockscreen | {n} of {target} |
| chant.meaning | Meaning |
| countsFor.none | Just my practice |
| card.starts | Starts {date} |
| card.dayOf | day {n} of {m} |
| total.soFar | so far · of {target} |
| completed.total | {total} offered |
| complete.title | Sankalpam complete |
| complete.group | Together you offered {total} {unit} |
| hand.note | Marked "added by hand" on this sankalpam |
| edit.tooLow | Already {total} offered. Choose a higher total or mark it complete |
| group.full | This group is full |
| group.closed | This group has closed |
| group.pending | Your {n} will add to the group when online |
| group.readersEmpty | Today's readers will appear here |
| discover.offline | Discover needs internet. Your own practice works offline. |
| discover.empty | No groups here yet |
| signin.safe | No phone number or OTP is needed. |
| purpose.invalid | Purpose can't have links or numbers |
| report.anon | The organiser is not told who reported. |
| report.done | We will look at this group. It stays hidden from your Discover from now on. |
| signin.body | So your readings reach the group and stay yours. You do this once. |
| preview.privacy | Members see your name in this group and your readings. Your intent only if you choose. No chat, no phone numbers. |
| invite.ready | Your group is ready |
| invite.anyone | Anyone with this code can join. |
| group.closeConfirm | {total} readings were offered together. The group moves to Completed for all {n} members, and no one can add to it after this. |
| join.private | Only you will see this, on your phone. |
| join.shown | Members will see this under your name. |
| city.note | Only your city is shown, never your address. |

Never: missed, streak, behind, incomplete, you haven't, 0 today, overdue, reminder, failed, only, short of, rank, top.

---

## 7. Motion and feedback

- Counter: scale 0.97 on press, 80 ms; light haptic per count, medium haptic at each 108.
- Ring fill animates 120 ms per count (Chant along: per recitation).
- Chant along word highlight: moves at each word timing with a 120 ms cross-fade; never runs ahead of the audio.
- Chant along stop: audio fade-out 600 ms; the "{n} offered" line fades in 300 ms.
- Completion moment: fade in 400 ms, diya glow pulses twice, nothing loops.
- Bubble: up 16 dp + fade, 2.4 s total, queued.
- Sheets: 250 ms slide; scrim 200 ms.
- Reduced motion setting: all of the above become instant changes (the word highlight still moves, without fades).

## 8. Accessibility

- Counter has `contentDescription` "Count one. {n} so far" and announces each round.
- Chant along: Play/Pause labelled; TalkBack announces only each completed round ("Round 2, 108"), never each
  recitation, so it does not talk over the mantra. MantraStage reads the mantra once when focused.
- ReaderRows read as "{name}, {count} today".
- Every icon-only control (⋮, −10 s, +10 s) has a label.
- 200% font: cards grow in height, totals wrap under labels; sacred text never truncates; MantraStage grows and the
  ring moves below the fold if needed (the player bar stays pinned).

## 9. Data shown

| Screen element | Source |
|---|---|
| Totals, day N of M, This month | Room: `Sankalpam`, `PracticeEntry` (sum by `sankalpamId`, group by `localDate`) |
| My practice | Room: all `PracticeEntry` rows (every mode) |
| Text names, mantra text, audio | `ContentRepository.text(id)`, `mantra(id)`; mantra recording from `mantra(id).audio` |
| Observance date chips in 33 | `ContentRepository.day()` over the next 30 days |
| Groups, members, readers, totals | Firebase (Phase 5): `Group`, `Membership`, contributions |
| City list in 55 | `master.cities` |

`PracticeEntry.mode` gains `chant`: `read | listen | count | chant | manual`. One Chant along session saves one
entry with `count` = recitations finished and `unit = repetition`.

## 10. Content needed (content-architecture change)

Mantras get an optional recording, one clean repetition that the app loops:

```markdown
## ganesha.mantra.mula
sa: ॐ गं गणपतये नमः
use: [japa, puja]
count_default: 108
audio: { file: ganesha.mantra.mula.mp3, length_ms: 3200, words_ms: [0, 520, 980, 2010] }
```

- `file` in `content/audio/`; one repetition, natural pace, no silence at the start, about 150 ms at the end so the
  loop breathes.
- `words_ms`: start of each word, one per word of `sa` split on spaces. Optional; without it the whole line pulses.
- The build checks: file exists, `words_ms` count = word count, values rising and below `length_ms`.
- Human recording only, reviewed like the mantra text (`reviewed_by`).
