# Sankalpam — design spec (v7, rewrite)

Replaces v6.1 and v6.2 entirely. Stories: `docs/stories/sankalpam-user-stories.md` (v7).
Reference screens: `docs/design/v7/sankalpam-v7-screens.html` (open in a browser, works offline) and one PNG per
screen in `docs/design/v7/png/` named by screen id (`s1a` My japam, `s1b` Groups, `s1c` Completed, `s2a`–`s2c`
Start a japam, `s3a` player text, `s3b` player mantra in a group, `s3c` text without recording, `s4` outside the
app, `s5a` About japam, `s5b` About group, `s7` completed view, `s8` find, `s9` join, `s10` sign in, `s11` start a
group, `s12` code). Where the PNG and this text differ, this text wins.
Examples use Hyderabad, Fri 11 Sep 2026, app language English.

**One rule above all:** Sankalpam looks and behaves like Puja and Calendar. It uses only the shared components of
`docs/design-v6.md`: same TopBar, cards, type, sheets, chips and buttons, and no component that exists only here
except the player controls and the ring (§4). If a pattern exists in Puja, use it.

**Module colour: tulasi green**, as every module carries its own accent from its Home card. Accent `#3E6A33`,
dark `#2F4F27`, tint `#E3EEDB`, border `#B7CDA8`. Use it for primary buttons, selected chips and segments, the
active tab, Play, text buttons, the lit row and active cards. The shared TopBar keeps the app's maroon Back and
Home. Gold stays hairline-only (progress, rings, the lit row's left line). Destructive actions (Leave, Mark
complete, Remove) stay maroon `#7A1F2B` text on an outlined button, never a red fill.

---

## 1. The idea in one line

A **sankalpam** is something you have resolved to chant: alone (**My japam**) or with others (**Groups**). Both
open into the same **player**, where the text moves in step with its recording and every finished recitation is
counted for you.

| Principle | On screen |
|---|---|
| Chant first | A card opens the player directly. No page in between. |
| Counted for you | Finished recitations count themselves; a cut-off one never does. |
| Goal guides, never blocks | Counting goes past the goal. No limits, no warnings. |
| Free will | No reminders, streaks, gaps, "0 today" or red anywhere. |
| Gathering, not competition | Readers in the order they last added; no ranks. |
| Private by default | Reasons stay on the phone, or private in a group unless shown. City-level only. |
| Elder-first | 56 dp targets, words not icons alone, every gesture also has a button. |

---

## 2. Map and routes

```
Home ─ Sankalpam card / My sādhana bar
 └─ Sankalpam                      tabs (StageBar style): My japam · Groups · Completed
     ├─ My japam
     │    ├─ card ─────────────────────────────────▶ PLAYER
     │    └─ + Start a japam → god grid → text list → Goal & days form → Start ──▶ PLAYER
     ├─ Groups
     │    ├─ card ─────────────────────────────────▶ PLAYER (group)
     │    ├─ Join with code (field on the tab) → Preview → Join sheet ──▶ PLAYER
     │    ├─ Find open groups → Preview → Join sheet ──────────────────▶ PLAYER
     │    └─ Start a group → god grid → text list → Group form → Code screen ──▶ PLAYER
     └─ Completed ─ card → Completed view (read-only) ─ Start again (japam only)

PLAYER ─ About (sheet) · I chanted outside the app (sheet)
Shared link ──▶ Preview (or PLAYER when already a member)
Sign in (sheet) appears once, before the first Start a group or Join.
```

Routes: `sankalpam?tab=japam|groups|completed` · `sankalpam/new` · `sankalpam/play/{sankalpamId}` ·
`sankalpam/done/{sankalpamId}` · `group/new` · `group/find` · `group/{groupId}/preview` · `group/{groupId}/share`.
Deep link: `https://<domain>/g/{code}` → preview. Sheets are not routes.

---

## 3. Screens

### S1 · Sankalpam home
TopBar "Sankalpam" (Back · title · Home). Under it the three tabs, styled exactly like the Puja page StageBar:
**My japam · Groups · Completed**. Last tab used is remembered.

**My japam**
- Cards (same card style as Puja's puja list, full width, gap 12), most recently chanted first:
  - Row 1: text name (Cormorant 22) · god name (Inter 14, muted).
  - Row 2: total (Inter SemiBold 28) + "so far · of 108" (Inter 15).
  - Row 3: "Day 9 of 41" (Inter 15) · gold hairline progress (hidden if the total is 0).
  - Future start: Row 2 becomes "Starts Wed 7 Apr" and no hairline.
- Button below the cards (outlined, full width, 56): **+ Start a japam**.
- Empty: one primary button **Start a japam** and the line "Chant alone at your own pace." No zeros.

**Groups**
- Join card at the top (surface card): "Have a code?" · code field (56, 6 characters, auto-caps) · **Join**.
- Group cards, most recently chanted first:
  - Row 1: group name (Cormorant 22) · text name (Inter 14, muted).
  - Row 2: group total (Inter SemiBold 28) + "of 1,008" · right-aligned "You 24" (Inter SemiBold 17).
  - Row 3: "38 people · day 9 of 21" · gold hairline progress.
- Two outlined buttons below (56, side by side): **Find open groups** · **Start a group**.
- Empty: the Join card, then the line "Chant with family and friends anywhere." and the two buttons.
- Offline: cards show their last known totals with "Updated when online" (Inter 13, muted).

**Completed**
- Cards in the muted surface style, newest end date first:
  - Japam: text name · "1,062 offered · 41 days · ended Tue 13 Oct".
  - Group: group name · "Together 1,240 offered · your 96 · ended Tue 13 Oct".
- Empty: "Completed sankalpams will be kept here."

### S2 · Start a japam
Three steps on one route, each with the same TopBar ("Start a japam") and a step line "1 of 3":
1. **Choose a god** — the Puja home god grid and search, unchanged (no festival card).
2. **Choose what to chant** — the god's texts and mantras as list rows like the god page puja list: name
   (SacredText), kind (Aṣṭottaram · Sahasranāmam · Stotram · Mantra), a small "recording" mark when audio exists.
3. **Goal and days** form, gap 16:
   - Header card: chosen text and god, "Change ›".
   - **Goal** — field 56 + chips. Text: 11 · 21 · 108 · 1,008. Mantra: 108 · 1,008 · 10,008 · 1,00,000.
     Prefilled: 108 for a text, 10,008 for a mantra.
   - **Days** — chips 11 · 21 · 41 · One māsam + "Other" (number field). Prefilled 41.
   - **Starts** — chips Today · Tomorrow · Pick a date; an observance in the next 30 days adds its chip
     ("Vināyaka Chavithi · Mon 14").
   - **Reason (optional)** — field 56, helper "Only you see this, on this phone."
   - Primary **Start** pinned bottom → creates the japam and opens the player.
   - Errors inline, Inter 14, maroon text, no red: "Choose what you will chant" · "Enter a goal above zero".

### S3 · Player (japam and group)
Full-screen like Puja guided mode; screen stays on while it is in front.

Top to bottom:
1. TopBar: Back · text name · Home.
2. **Context line** (Inter 15, muted), with **About** text button on the right:
   japam "My japam · day 9 of 41" · group "Kārtika Aṣṭottaram · day 9 of 21".
3. **Note** (groups only): a pill "Lakshmi · +3" that slides in under the context line for 2.4 s; one at a time,
   queued.
4. **Text area** (fills the space):
   - *Text* (aṣṭottaram, sahasranāmam, stotram): numbered rows, one name per row, SacredText 22 sp in the
     chosen script. The lit row: tulasi tint fill, gold hairline on the left, name in dark tulasi. The list follows
     the lit row, keeping it at about 40% height; if the devotee scrolls by hand, following pauses for 5 s.
   - *Mantra*: the mantra centred, SacredText 30 sp, current word lit (dark tulasi text, 3 dp gold underline); without
     word timings the line glows once per repetition. Below it the **Ring** (§4).
5. **Count line** (Inter SemiBold 20): japam "Today 2 · Total 54 of 108" · group "You 25 · Group 413 of 1,008".
   When the goal is first reached a line replaces it for the session: "Goal reached · 1,008 · keep chanting".
6. **Controls** pinned bottom (surface, top hairline, padding 16):
   - Row 1: **Script** segmented — [my language script] · संस्कृतम् · IAST (same control as the app's text view).
   - Row 2: **Speed** segmented 1× · 1.25× · 1.5× on the left · **Play / Pause** 72 round tulasi on the right,
     label under it.
   - Row 3: text button **I chanted outside the app**.

Variants:
- **Text without a recording:** no Speed or Play; the list ends with a full-width primary **Done** (adds one
  reading, then shows "1 reading added" for 2 s and returns to the top of the list).
- **Mantra without a recording:** no Speed or Play; the Ring is the counter (touch adds one).
- **Future start:** text visible, controls disabled, line "Starts Wed 7 Apr".
- **Completed:** never opens the player; opens S7.

Behaviour:
- Text: the recording playing to its end adds one reading; playback stops at the end. Play starts again from the
  top.
- Mantra: the recording loops without a gap; each repetition that ends adds one; it plays until paused.
- Pause keeps the place; Play resumes the same recitation. Leaving keeps finished ones; a cut-off one adds nothing.
- Speed and script change at any moment without stopping playback; the lit row or word stays correct.
- Screen locked or app in the background: playback and counting continue. Lock screen and notification show the
  text name, the count line and Play/Pause only.
- Phone call, other app's audio, unplugged headphones: pause. Never resumes by itself.

### S4 · I chanted outside the app (sheet)
Title "Chanted outside the app" · Stepper (− value +, value Inter SemiBold 28; quick chips 1 · 11 · 108 · 1,008;
default 1 for a text, 108 for a mantra) · "Which day?" day chips: Today · Yesterday · Wed 9 · … back to 7 days, never
before the start date · note "Marked 'added by hand'" · primary **Add 108** (label repeats the number). Disabled at 0.

### S5 · About (sheet)
Same sheet style as the symbol sheet on the god page. Content depends on the sankalpam:

**Japam:** reason with lock icon ("For my mother's health · only you see this") · goal and days
("Goal 108 · 41 days · started Thu 3 Sep · ends Tue 13 Oct") · **This month**: rows of days with chanting, newest
first, count right, "added by hand" tag where it applies (only days with chanting appear) · buttons:
**Change goal or days** · **Mark complete** (outlined maroon).

**Group:** purpose · "Run by Lakshmi · Hyderabad" · "Goal 1,008 · day 9 of 21 · 38 people" · **Today's readers**:
rows 64 (initial circle, name, shown reason in italics if any, "+3" right), in the order they last added; empty
"Today's readers will appear here" · invite card: code (Inter SemiBold 28, spaced) + **Share** · **Leave group**
(outlined maroon; organiser does not see it) · **Report** (text button, open groups only).
Organiser also sees **Change goal or days** and **Remove a member** (member list, Remove per row, confirm
"Remove Rama? Their past readings stay in the total.").

### S6 · Change goal or days (sheet)
Goal field + chips, Days chips + Other, line "54 offered so far stays as it is." · primary **Save**. No limits:
any goal above zero, any number of days. If the new last day is before today, it moves to Completed tomorrow.

### S7 · Completed view
TopBar with the text or group name. Card: total offered, days, start and end dates, reason (japam, lock icon) or
group total with "your 96" (group). Japam only: primary **Start again** → S2 step 3 prefilled (same text, goal
and days, start Today). No counting controls.

### S8 · Find open groups
TopBar "Open groups". Globe band 200 tall (ivory sphere, gold hairline meridians, tulasi dots on cities with open
groups; tapping a dot filters to that city) · filter chips **Near me · Starting soon · All** · group cards: name
(Cormorant 22) · text · purpose (2 lines) · "Run by Lakshmi · Hyderabad" · "38 people · day 9 of 21 · 412 offered" ·
Report link bottom-right. Empty: "No groups here yet" + outlined **Start a group**. Offline: "Finding groups needs
internet. Your own japam works offline."

### S9 · Preview and Join
Preview: group name (Cormorant 28) · text · purpose · "Run by Lakshmi · Hyderabad" · total + "of 1,008" ·
"38 people · day 9 of 21" · privacy card: "Members see your name and your count. Your reason only if you choose.
No chat, no phone numbers." · primary **Join** pinned bottom. Full: disabled "This group is full". Ended: "This
group has ended".
Join sheet: "Your name in this group" (prefilled from Profile) · "Your reason (optional)" · segmented **Keep
private · Show to the group** (Keep private default) · primary **Join** → player.

### S10 · Sign in (sheet, once)
"Groups need a sign-in" · "So your count reaches the group and stays yours. You do this once." · lines: "No phone
number or OTP is needed." · "Members see only the name you choose." · "Everything else works without it." ·
**Continue with Google** · on iPhone also **Continue with Apple** · text button "Not now".

### S11 · Start a group
Steps 1–2 as S2 (god grid, text list), then the form: Group name · Purpose (optional, counter "54 of 140", helper
"No links or numbers.") · Goal (chips as S2) · Days (chips as S2) · Starts (chips as S2) · Who can join:
segmented **Open · Code only** · City (prefilled from Profile, searchable list; shown for Open) · primary
**Create group**. Errors: "Purpose can't have links or numbers" · "No internet. Try again when connected".

### S12 · Group code screen
Centred mala mark · "Your group is ready" (Cormorant 32) · "<name> · 41 days from today" · code card (code Inter
SemiBold 34, spaced) · "Anyone with this code can join." · primary **Share** (message: "Join our sankalpam
'<name>' on Sannidhi. Code: <code> · <link>") · outlined **Open the group** → player.

### S13 · Report (sheet)
"Report this group" · "The organiser is not told who reported." · four radio rows: Not about chanting · Hurtful
words · Asks for money · Something else · optional note · primary **Send report**. After: "Thank you. It is
hidden from your list from now on." Offline: "We'll send this when you're online".

---

## 4. Components (only these are new)

| Component | Spec |
|---|---|
| **SankalpamCard** | The Puja list card with three rows as in S1; active cards use the tulasi tint and border, total in dark tulasi. Progress hairline 2 dp gold. Completed cards use the muted surface. |
| **LitRow** | A text row in the player: 56 min height, number (Inter 14, muted) + SacredText 22. Lit: tulasi tint fill, 3 dp gold left hairline, dark tulasi text, 120 ms cross-fade. |
| **Ring** | 220 round surface with soft shadow; ring stroke 6, gold on a light track, fills per 108. Centre: count 48 sp, "of 108 · round N" 15 sp. Touchable only when not playing (scale 0.97 on press, light haptic; medium haptic at each 108). |
| **PlayerControls** | As S3 §6. Play/Pause 72 round tulasi with label; Speed and Script use the app's segmented control with tulasi selection. |
| **Note** | Pill 40, surface 95% + shadow, "Lakshmi · +3", 2.4 s, one at a time. |

Everything else (TopBar, cards, chips, fields, sheets, buttons, segmented, StageBar tabs, god grid, search,
SacredText) comes from design-v6 unchanged.

---

## 5. Copy deck (en)

| Key | Text |
|---|---|
| tab.japam / tab.groups / tab.completed | My japam · Groups · Completed |
| japam.empty | Chant alone at your own pace. |
| groups.empty | Chant with family and friends anywhere. |
| completed.empty | Completed sankalpams will be kept here. |
| card.of | so far · of {goal} |
| card.dayOf | Day {n} of {m} |
| card.starts | Starts {date} |
| join.prompt | Have a code? |
| join.noMatch | This code doesn't match a group |
| player.today | Today {n} · Total {total} of {goal} |
| player.group | You {mine} · Group {total} of {goal} |
| player.goal | Goal reached · {goal} · keep chanting |
| player.outside | I chanted outside the app |
| player.starts | Starts {date} ({date} reads "tomorrow" for the next day) |
| player.pending | Will add to the group when online |
| done.added | 1 reading added |
| hand.note | Marked 'added by hand' |
| about.reason | Only you see this |
| about.readersEmpty | Today's readers will appear here |
| edit.keeps | {total} offered so far stays as it is. |
| form.chooseText | Choose what you will chant |
| form.goalZero | Enter a goal above zero |
| form.reasonHelp | Only you see this, on this phone. |
| group.full / group.ended | This group is full · This group has ended |
| purpose.invalid | Purpose can't have links or numbers |
| net.none | No internet. Try again when connected |
| find.offline | Finding groups needs internet. Your own japam works offline. |
| find.empty | No groups here yet |
| signin.safe | No phone number or OTP is needed. |
| preview.privacy | Members see your name and your count. Your reason only if you choose. No chat, no phone numbers. |
| report.anon | The organiser is not told who reported. |
| updated | Updated when online |

Never: missed, streak, behind, incomplete, you haven't, 0 today, overdue, reminder, failed, only, short of, rank, top.

---

## 6. Motion, sound, accessibility

- Lit row and word move with the audio clock (never ahead); 120 ms cross-fade. Reduced motion: instant.
- Ring step 120 ms per count. Goal reached: the count line cross-fades once; no sound, no confetti.
- Notes: slide up 16 dp + fade, 2.4 s, queued.
- TalkBack: Play/Pause, speed and script labelled; it announces each reading added and each completed round of
  108, never each repetition. Lit row is not announced while playing.
- 200% font: rows grow; sacred text never truncates; controls stay pinned and wrap to two rows of buttons.

---

## 7. Data

**On the phone (Room)**
- `Sankalpam` — id, kind (`japam | group`), textId, unit (`reading | repetition`), goal, days, startDate,
  reason?, status (`active | completed`), completedAt?, groupId?
- `PracticeEntry` — id, sankalpamId, count, unit, source (`play | done | ring | hand`), localDate, createdAt,
  sentToGroup (bool, groups only).
- Totals = sum of entries per sankalpam. A japam and a group never share entries.
- Status moves to `completed` on the first app open after the last day (or by Mark complete / Leave).

**Groups (Firebase, Phase 5)**
- `groups/{gid}` — name, purpose?, textId, unit, goal, days, startDate, endDate, visibility (`open | code`), code,
  city {id, name, state}, organiser {uid, name}, memberCount, total, status (`open | ended`), createdAt.
- `groups/{gid}/members/{uid}` — name, reason?, reasonShown, count, joinedAt, state (`member | left | removed`).
- `groups/{gid}/days/{yyyy-mm-dd}/readers/{uid}` — name, count, lastAt.
- `codes/{code}` → gid. `reports/{id}` — gid, reason, note?, at (no reporter identity stored with it).

**Content**
- Texts: `audio: { file, intro_ms, slot_ms }` (equal slots) or `audio: { file, names_ms: [...] }` (start of each
  name) — the lit row is the last name whose start ≤ position.
- Mantras: `audio: { file, length_ms, words_ms: [...] }`, one clean repetition that the app loops; `words_ms` optional.
- Human recordings only, reviewed like the text (`reviewed_by`).
