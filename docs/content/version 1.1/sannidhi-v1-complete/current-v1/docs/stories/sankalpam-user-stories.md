# Sankalpam — user stories (v7, rewrite)

Sannidhi · Sankalpam module · v7 · 18 Oct 2026 · replaces v6.1 and v6.2 entirely

Part 1 is the flow the stories are written against (one-page flow and decisions of 18 Oct 2026). Part 2 is the
index: stories, coverage, traceability and open questions. Part 3 holds the 14 stories in the house template.

Design: `docs/design/sankalpam-design.md` (v7). Build: `docs/cursor/P3a-sankalpam-solo.md`, `P3b-sankalpam-groups.md`.

---

# Part 1 · Flow rules

**Project Goal**

- Devotees can chant their japa and pārāyaṇam with the text and its recording in one place, alone or with a group.
- Devotees can keep a personal reason, a goal and a number of days for what they chant, and see it counted without effort.

**Requirement 01 – Sankalpam home**

- Sankalpam opens on three tabs: My japam, Groups and Completed.
- My japam lists the devotee's own sankalpams, each card showing the text, the total so far, the goal and day N of M.
- Groups lists the groups the devotee has joined, each card showing the group name, the text, the group total, the goal, day N of M and the devotee's own count.
- Opening a card in My japam or Groups goes straight to the player for that sankalpam.
- Completed lists the devotee's own sankalpams and groups whose days have ended.
- The Home sādhana bar lists active sankalpams and groups and opens Sankalpam.

**Requirement 02 – Start a japam**

- A devotee starts a japam by choosing a god, then a text or mantra, then a goal, a number of days and a start date.
- The god and text are chosen from the same god grid and search used in Puja.
- The goal and the number of days are filled in by default and can be changed with chips or typed.
- A reason is optional, stays on the phone and is shown only to the devotee.
- A japam that starts on a future date is listed with its start date and accepts no counts before then.

**Requirement 03 – The player**

- The player shows the text in the chosen script with the current name or line lit in step with the recording.
- The player has play and pause and three speeds: 1x, 1.25x and 1.5x.
- At every speed the lit name or line stays in step with the recording.
- The devotee can switch the script between their language script, Sanskrit in Devanagari and IAST while the recording plays, and it stays on the same name without a break.
- The recording keeps playing with the screen locked, with play and pause on the lock screen.
- A phone call or unplugged headphones pause the recording, and it never resumes by itself.

**Requirement 04 – Counting in the player**

- For a text, the recording playing to its end adds one reading.
- For a mantra, the recording loops and each repetition that plays to its end adds one, on a ring of 108.
- A recitation or repetition cut off part-way adds nothing; finished ones are kept when the devotee leaves.
- A text without a recording is read along and Done at the end adds one reading.
- A mantra without a recording, or a paused mantra, is counted by touching the ring, one count each time.

**Requirement 05 – Count chanting done outside the app**

- The player has an action to add chanting done outside the app as a number for today or one of the last 7 days.
- A count added outside the app is marked as added by hand.

**Requirement 06 – Goal and days**

- Counting never stops at the goal; the total can go past it.
- When the total first reaches the goal, a gentle line says the goal is reached and the devotee can keep chanting.
- On the day after the last day, the sankalpam moves to Completed with its total.
- A devotee can change the goal or the days of their own japam at any time, with no limits checked.
- A devotee can mark their own japam complete at any time.
- A completed japam can be started again with the same text, goal and days.

**Requirement 07 – Sign in for groups only**

- Starting or joining a group needs a sign-in with Google, or Apple on iPhone; everything else works without an account.
- There is no OTP, password or phone number anywhere.

**Requirement 08 – Start a group**

- An organiser starts a group by choosing a text or mantra, a group name, an optional purpose, a goal, a number of days and a start date.
- The organiser chooses Open, listed in Find open groups, or Code only, joined with a code or a shared link.
- An open group shows the organiser's display name and city, never an exact location.
- After creating, the organiser sees the group's code with Share and is the first member.
- A group holds up to 200 members.

**Requirement 09 – Join a group**

- A devotee joins a group by entering its code on the Groups tab or by opening a shared link.
- Find open groups lists open groups by city, with a globe at the top and filters Near me, Starting soon and All.
- Before joining, the devotee sees the group name, text, purpose, organiser and city, members, total and day N of M.
- A devotee joins with a display name and an optional reason, kept private unless the devotee chooses to show it to the group.
- A full group cannot be joined.

**Requirement 10 – Chant in a group**

- Opening a group goes straight to the player for the group's text.
- Chanting in a group adds to the devotee's own count in that group and to the group total at the same time.
- Chanting a text in My japam counts only for that japam, and chanting in a group counts only for that group.
- The player shows the devotee's count and the group total with the goal.
- When another member adds, a small note with their name and count appears briefly.
- About this group shows the purpose, day N of M, members, today's readers with their counts in the order they last added, the code with Share, and Leave.
- Counts made offline are kept on the phone and added to the group when the phone is back online.

**Requirement 11 – Group end and safety**

- A group stays open until its days end, then moves to Completed for every member.
- A member can leave at any time and their past counts stay in the group total.
- The organiser can remove a member, and the removed member's past counts stay in the total.
- Every open group has a Report action.
- There is no chat, photos, private messages, phone numbers or rankings in a group.
- A group purpose of up to 140 characters can't hold links or numbers.

**Non-Functional Requirements**

- My japam, the player and counting work offline and stay on the phone.
- Text is at least 16 sp, touch targets at least 56 dp, and screens work at 200% font scale.
- No reminders, notifications, streaks or words that suggest the devotee missed anything.
- Sankalpam uses the same components, colours and layout as Puja and Calendar.

**Assumptions**

- Moving practice data to a new phone is out of scope for now.
- Handing a group to a new organiser is out of scope for now.

---

# Part 2 · Index

**Source:** Sankalpam flow v7 (one-page flow and decisions of 18 Oct 2026) **Prepared:** 18 Oct 2026 **Stories:** 14 · **Validator:** 14/14 pass, 63/63 flow rules traced

| Story | Title | Complexity | ACs |
|---|---|---|---|
| US-01.1 | Devotee sees every japam and group in one calm place and opens it straight away | medium | 9 |
| US-02.1 | Devotee starts a japam with a goal, days and an optional reason | medium | 9 |
| US-03.1 | Devotee follows the text in step with the recording at their own speed and script | high | 12 |
| US-04.1 | Devotee is counted automatically for every reading or repetition finished in the player | high | 11 |
| US-05.1 | Devotee adds chanting done outside the app to the right day | medium | 8 |
| US-06.1 | Devotee keeps chanting past the goal and sees the japam complete when its days end | high | 11 |
| US-07.1 | Devotee signs in only when starting or joining a group | low | 6 |
| US-08.1 | Group organiser starts a group that others can join with a code or find | high | 11 |
| US-09.1 | Devotee joins a group with a code or a link and an optional private reason | medium | 9 |
| US-09.2 | Devotee finds open groups by city | medium | 8 |
| US-10.1 | Group member's chanting counts for them and the group together | medium | 9 |
| US-10.2 | Group member sees today's readers and the gathering, never ranked | medium | 8 |
| US-11.1 | Group member sees the group complete when its days end, and can leave or report safely | medium | 8 |
| US-11.2 | Group organiser removes a member, and their past counts stay | low | 6 |

## Coverage map
| Story | Positive | Negative | Boundary | Edge | Exception |
|---|---|---|---|---|---|
| US-01.1 | 1,7,8 | 2,3 | 4,5 | 6 | 9 |
| US-02.1 | 1,2 | 3,4 | 5,6 | 7,8 | 9 |
| US-03.1 | 1,2,10,11 | 5,8,12 | 3,9 | 4,6 | 7 |
| US-04.1 | 1,2,3 | 4,5,6,7 | 8,9 | 10 | 11 |
| US-05.1 | 1 | 2,3 | 4,5 | 6,7 | 8 |
| US-06.1 | 1,3,6,8 | 7,9,10 | 2,4 | 5 | 11 |
| US-07.1 | 1,6 | 2 | 3 | 4 | 5 |
| US-08.1 | 1,7,9,10 | 2,3,4 | 5,6 | 8 | 11 |
| US-09.1 | 1,2,3 | 4,5 | 6,7 | 8 | 9 |
| US-09.2 | 1 | 2,3,4 | 5,6 | 7 | 8 |
| US-10.1 | 1,2 | 3,4 | 5,6 | 7,8 | 9 |
| US-10.2 | 1,2 | 3,4 | 5,6 | 7 | 8 |
| US-11.1 | 1,2 | 3,4 | 5,6 | 7 | 8 |
| US-11.2 | 1 | 2,5 | 3 | 4 | 6 |

## Flow traceability
| ID | Flow text | Covered by |
|---|---|---|
| GOAL.01 | Chant with the text and its recording, alone or with a group | US-03.1 AC1; US-10.1 AC1 |
| GOAL.02 | Reason, goal and days, counted without effort | US-02.1 AC1; US-04.1 AC1 |
| R01.01 | Three tabs: My japam, Groups, Completed | US-01.1 AC1 |
| R01.02 | My japam cards: text, total, goal, day N of M | US-01.1 AC1; US-01.1 AC4 |
| R01.03 | Groups cards: name, text, group total, goal, day, own count | US-01.1 AC8 |
| R01.04 | A card opens the player directly | US-01.1 AC6; US-02.1 AC1 |
| R01.05 | Completed lists ended japams and groups | US-01.1 AC7; US-01.1 AC9 |
| R01.06 | Home sādhana bar lists active ones | US-01.1 AC5 |
| R02.01 | God, then text or mantra, goal, days, start date | US-02.1 AC1; US-02.1 AC3 |
| R02.02 | Same god grid and search as Puja | US-02.1 AC2 |
| R02.03 | Goal and days prefilled, changeable | US-02.1 AC1; US-02.1 AC4; US-02.1 AC5 |
| R02.04 | Reason optional, private, on the phone | US-02.1 AC7 |
| R02.05 | Future start: no counts before then | US-02.1 AC6 |
| R03.01 | Text in chosen script, lit in step | US-03.1 AC1; US-03.1 AC9; US-03.1 AC11 |
| R03.02 | Play, pause, 1x, 1.25x, 1.5x | US-03.1 AC3; US-03.1 AC12 |
| R03.03 | In step at every speed | US-03.1 AC2 |
| R03.04 | Switch script mid-way, same name, no break | US-03.1 AC4; US-03.1 AC10 |
| R03.05 | Plays with the screen locked | US-03.1 AC6 |
| R03.06 | Call or headphones pause; no auto-resume | US-03.1 AC7; US-03.1 AC8 |
| R04.01 | Text to the end adds one reading | US-04.1 AC1 |
| R04.02 | Mantra loops; each repetition adds one; ring of 108 | US-04.1 AC2; US-04.1 AC8 |
| R04.03 | Cut-off adds nothing; finished ones kept | US-04.1 AC4; US-04.1 AC5; US-04.1 AC6 |
| R04.04 | No recording: Done adds one reading | US-04.1 AC3; US-03.1 AC5 |
| R04.05 | Ring touch counts when no recording or paused | US-04.1 AC9; US-04.1 AC7 |
| R05.01 | Add outside chanting for today or last 7 days | US-05.1 AC1; US-05.1 AC2; US-05.1 AC4 |
| R05.02 | Marked added by hand | US-05.1 AC1; US-05.1 AC4 |
| R06.01 | Counting never stops at the goal | US-06.1 AC2; US-05.1 AC7 |
| R06.02 | Goal reached line, keep chanting | US-06.1 AC1 |
| R06.03 | Day after the last day: Completed | US-06.1 AC3; US-06.1 AC11 |
| R06.04 | Change own goal or days, no limits | US-06.1 AC5 |
| R06.05 | Mark own japam complete | US-06.1 AC6; US-06.1 AC7 |
| R06.06 | Start again with same text, goal, days | US-06.1 AC8 |
| R07.01 | Sign-in only for groups; Google, Apple on iPhone | US-07.1 AC1; US-07.1 AC2; US-07.1 AC4 |
| R07.02 | No OTP, password or phone number | US-07.1 AC6 |
| R08.01 | Start a group: text, name, purpose, goal, days, start | US-08.1 AC1; US-08.1 AC2; US-08.1 AC4 |
| R08.02 | Open or Code only | US-08.1 AC7; US-08.1 AC10 |
| R08.03 | Organiser name and city only | US-08.1 AC9 |
| R08.04 | Code with Share; organiser first member | US-08.1 AC1; US-08.1 AC10 |
| R08.05 | Up to 200 members | US-09.1 AC5; US-09.1 AC6 |
| R09.01 | Join by code or shared link | US-09.1 AC1; US-09.1 AC2; US-09.1 AC4 |
| R09.02 | Find open groups: globe, Near me, Starting soon, All | US-09.2 AC1; US-09.2 AC5; US-09.2 AC6 |
| R09.03 | Preview before joining | US-09.1 AC1 |
| R09.04 | Display name, optional reason, private or shown | US-09.1 AC3; US-09.1 AC8 |
| R09.05 | A full group cannot be joined | US-09.1 AC5 |
| R10.01 | Opening a group goes straight to the player | US-01.1 AC6; US-09.1 AC3 |
| R10.02 | Counts for own count and group total at once | US-10.1 AC1; US-10.1 AC2 |
| R10.03 | My japam and group counts kept apart | US-10.1 AC3; US-10.1 AC4 |
| R10.04 | Player shows own count, group total and goal | US-10.1 AC1 |
| R10.05 | Brief note when another member adds | US-10.2 AC2; US-10.2 AC6 |
| R10.06 | About this group contents, readers by last added | US-10.2 AC1; US-10.2 AC3 |
| R10.07 | Offline counts sent later | US-10.1 AC8 |
| R11.01 | Open until days end, then Completed | US-11.1 AC1; US-11.1 AC5 |
| R11.02 | Leave any time; past counts stay | US-11.1 AC2; US-11.1 AC7 |
| R11.03 | Organiser removes a member; counts stay | US-11.2 AC1; US-11.2 AC4 |
| R11.04 | Report on every open group | US-11.1 AC6 |
| R11.05 | No chat, photos, messages, numbers, rankings | US-11.1 AC4 |
| R11.06 | Purpose up to 140, no links or numbers | US-08.1 AC3; US-08.1 AC5; US-08.1 AC6 |
| NFR.01 | Offline on the phone | US-02.1 AC9; US-04.1 AC11 |
| NFR.02 | 16 sp, 56 dp, 200% font scale | Context only — proved for every screen by the 200% screenshot tests in each story's Done when |
| NFR.03 | No reminders, streaks or missed words | US-01.1 AC2; US-01.1 AC3 |
| NFR.04 | Same components, colours and layout as Puja and Calendar | US-02.1 AC2 |
| ASM.01 | Moving data to a new phone is out of scope | Out of scope — moving practice data to a new phone is deferred by decision |
| ASM.02 | Handing a group to a new organiser is out of scope | Out of scope — handing a group to a new organiser is deferred by decision |

## Open questions
| # | Story | Question | Default if not answered |
|---|---|---|---|
| 1 | US-02.1 | Prefilled goal and days | Text 108 readings, mantra 10,008 repetitions, 41 days |
| 2 | US-10.2, US-11.1 | Who reviews reports, and what happens to a reported group? | Hidden from the reporter's list; owner reviews |
| 3 | US-08.1, US-09.2 | City list is Indian cities only. Add world cities for organisers abroad? | Add the top cities abroad where families live |
| 4 | US-08.1 | Code length and does it expire? | 6 characters, valid until the group ends |
| 5 | US-03.1 | Who records the texts and mantras? | You supply them |

## Split notes
- Solo: home (US-01.1), start (US-02.1), player (US-03.1), counting (US-04.1), outside the app (US-05.1), goal and days (US-06.1).
- Groups by actor and moment: sign-in (US-07.1), organiser starts (US-08.1), devotee joins or finds (US-09.1, US-09.2), members chant and see the gathering (US-10.1, US-10.2), end and safety (US-11.1, US-11.2).

---

# Part 3 · Stories


## US-01.1 · Devotee sees every japam and group in one calm place and opens it straight away

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-01.1 | medium | Sannidhi | Sankalpam | Requirement 01 – Sankalpam home |

#### Description
As a devotee, I want to see my own japams and my groups on one screen and open any of them directly, so that I can start chanting in one step from wherever I left off.

Today an elder keeps vows in memory and group counts in chat messages, with no single place to start from.

Terms: sankalpam — something the devotee has resolved to chant, alone or with a group; japam — a sankalpam kept alone on the phone; My japam, Groups, Completed — the three tabs of Sankalpam; player — the screen where the text and recording are chanted.

Dependencies: Start a japam — delivered by US-02.1; player — delivered by US-03.1; groups — delivered by US-08.1 and US-09.1; Home sādhana bar — exists.

Sizing: one sprint; covers the three tabs, the cards and the Home sādhana bar. The player and the forms are excluded.

Done when: every acceptance criterion passes on a real phone in te, hi and en at 200% font scale, the screens match Puja and Calendar in look, and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 01 – Sankalpam home

Source: Sankalpam flow v7, Requirement 01.

The flow requires that "Sankalpam opens on three tabs: My japam, Groups and Completed." and "Opening a card in My japam or Groups goes straight to the player for that sankalpam."

Outcome: today vows and group counts are scattered; after this story they sit on one screen, one step from chanting.

Success measure: steps from Home to chanting an existing sankalpam; baseline: not measured today; target: two.

Priority: Must — every other Sankalpam story starts from this screen.

#### Acceptance Criteria
- Given a devotee has one japam and one group, When the devotee opens Sankalpam, Then the tabs My japam, Groups and Completed are shown with My japam open, listing the japam card with its text, total so far, goal and day N of M.
- Given a devotee has no japam yet, When the devotee opens My japam, Then it shows only Start a japam and no empty counters or zero totals.
- Given a devotee has not chanted for five days, When the devotee opens Sankalpam, Then no card shows missed days, streaks or gaps.
- Given a devotee's japam is on day 41 of 41, When the devotee opens My japam, Then the card shows day 41 of 41 and stays in My japam until that day ends.
- Given a devotee has exactly one active sankalpam, When the devotee opens Home, Then the sādhana bar shows its title alone and opens Sankalpam.
- Given a devotee has a group card in Groups, When the devotee opens the card, Then the player for the group's text opens with no page in between.
- Given a devotee's japam ended yesterday, When the devotee opens Sankalpam, Then it is listed under Completed with its total and is not in My japam.
- Given a devotee is in three groups, When the devotee opens Groups, Then each card shows the group name, the text, the group total, the goal, day N of M and the devotee's own count.
- Given a devotee was removed from a group by its organiser, When the devotee opens Groups, Then that group is not listed there and is listed under Completed.

#### Out of Scope
- Starting a japam (US-02.1)
- The player (US-03.1)
- Starting or joining a group (US-08.1, US-09.1)


## US-02.1 · Devotee starts a japam with a goal, days and an optional reason

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-02.1 | medium | Sannidhi | Sankalpam | Requirement 02 – Start a japam |

#### Description
As a devotee, I want to start a japam by picking a god and a text or mantra and setting a goal and days, so that my resolve is kept on the phone and counted from the first recitation.

Today a vow such as one lakh repetitions in 41 days is kept in memory or on paper.

Terms: goal — the number of readings or repetitions the devotee resolves to offer; days — how long the japam runs from its start date; reason — the devotee's private purpose for the japam.

Dependencies: god grid and search — exists in Puja; texts, mantras and recordings — exists in the content build; player — delivered by US-03.1.

Sizing: one sprint; covers the Start a japam form. Starting a group is excluded.

Done when: every acceptance criterion passes offline on a real phone and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 02 – Start a japam

Source: Sankalpam flow v7, Requirement 02.

The flow requires that "A devotee starts a japam by choosing a god, then a text or mantra, then a goal, a number of days and a start date." and "A reason is optional, stays on the phone and is shown only to the devotee."

Outcome: today vows live on paper; after this story a vow is created in one short form and counted on the phone.

Success measure: fields a devotee must fill to start; baseline: not applicable; target: only the god and the text, everything else prefilled.

Priority: Must — the solo japam is the first thing elders asked for.

#### Acceptance Criteria
- Given a devotee has chosen Gaṇeśa and the mūla mantra, When the devotee keeps the prefilled goal and days and starts, Then My japam lists it with 0 so far, the goal and day 1 of 41, and the player opens.
- Given a devotee opens Start a japam, When the devotee looks for a god, Then the same god grid and search as Puja are shown.
- Given a devotee has not chosen a text, When the devotee tries to start, Then Start stays disabled and the form shows "Choose what you will chant".
- Given a devotee clears the goal field, When the devotee tries to start, Then Start stays disabled and the field shows "Enter a goal above zero".
- Given a devotee types a goal of 1, When the devotee starts, Then the japam is listed with a goal of 1.
- Given a devotee picks Ugadi, Wed 7 Apr 2027, as the start date, When the devotee starts, Then the card shows "Starts Wed 7 Apr" and the player adds no counts before that day.
- Given a devotee writes the reason "for my mother's health", When the devotee starts, Then the reason is shown only in About on this phone.
- Given a devotee already has a japam for the same mantra, When the devotee starts another one, Then both are listed separately in My japam.
- Given a devotee's phone has no internet, When the devotee starts a japam, Then it is listed in My japam and no connection message is shown.

#### Out of Scope
- Starting a group (US-08.1)
- Changing the goal or days later (US-06.1)


## US-03.1 · Devotee follows the text in step with the recording at their own speed and script

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-03.1 | high | Sannidhi | Sankalpam | Requirement 03 – The player |

#### Description
As a devotee, I want to see the text lit name by name in step with its recording, at the speed and in the script I choose, so that I can chant along without losing my place.

Today a devotee plays a recording on one device and follows a book or a screenshot on another, and loses the place.

Terms: player — the screen with the text, the recording and the controls; script — the devotee's language script, Sanskrit in Devanagari, or IAST; speed — 1x, 1.25x or 1.5x.

Dependencies: recordings with name timings for texts and word timings for mantras — delivered by the content build; Sankalpam home — delivered by US-01.1.

Sizing: one sprint; covers the text, the highlight, play and pause, speed, script switch and locked-screen playback. Counting is excluded.

Done when: every acceptance criterion passes on a real phone with the screen locked and unlocked, in te, hi and en at 200% font scale, and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 03 – The player

Source: Sankalpam flow v7, Requirement 03.

The flow requires that "The player shows the text in the chosen script with the current name or line lit in step with the recording." and "At every speed the lit name or line stays in step with the recording."

Outcome: today text and audio are on separate devices; after this story they move together on one screen.

Success measure: names lit out of step with the recording; baseline: not tracked today; target: zero at every speed.

Priority: Must — guided chanting with the text in step is the heart of the module and was in the earlier app.

#### Acceptance Criteria
- Given a devotee opens the Gaṇeśa aṣṭottaram, which has a recording, When the devotee starts playback, Then the names are shown in the chosen script and the current name is lit in step with the recording.
- Given a devotee is playing at 1x, When the devotee changes the speed to 1.5x, Then the recording plays faster and the lit name stays on the name being recited.
- Given a devotee is playing, When the devotee opens the speed choice, Then only 1x, 1.25x and 1.5x are offered.
- Given a devotee is playing in Telugu script at the 40th name, When the devotee switches the script to IAST, Then the 40th name is lit in IAST and the recording carries on without a break.
- Given a devotee opens a text that has no recording, When the devotee views the player, Then the names are shown with Done at the end and no play, pause or speed controls.
- Given a devotee locks the phone while the recording plays, When the devotee unlocks it later, Then playback has continued and the lock screen showed the text name with play and pause.
- Given a devotee is playing and a phone call arrives, When the devotee returns after the call, Then the recording is paused and stays paused until the devotee plays it again.
- Given a devotee is playing with headphones, When the devotee unplugs them, Then the recording is paused.
- Given a devotee starts the aṣṭottaram recording, When the devotee listens through the opening invocation, Then no name is lit until the first name begins.
- Given a devotee chose IAST in the player, When the devotee opens the player again later, Then it opens in IAST.
- Given a devotee plays a mantra with a recording, When the devotee watches the player, Then the mantra is shown large with the current word lit and the ring of 108 below it.
- Given a devotee has the player open for a text with a recording, When the devotee looks for skip or seek controls, Then none are shown beyond play, pause and speed.

#### Out of Scope
- Counting readings and repetitions (US-04.1)
- Adding chanting done outside the app (US-05.1)


## US-04.1 · Devotee is counted automatically for every reading or repetition finished in the player

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-04.1 | high | Sannidhi | Sankalpam | Requirement 04 – Counting in the player |

#### Description
As a devotee, I want to have each finished reading or repetition in the player counted for me, so that I never stop chanting to keep count.

Today a devotee counts on fingers or a mala while listening and often loses the count.

Terms: reading — one full recitation of a text such as the aṣṭottaram; repetition — one recitation of a mantra; ring — the circle that fills once every 108 repetitions; round — one full ring.

Dependencies: player — delivered by US-03.1; practice storage on the phone — delivered by this story; goal and days — delivered by US-06.1.

Sizing: one sprint; covers automatic counting for texts and mantras, Done for texts without a recording, and the ring for mantras. Counts from outside the app are excluded.

Done when: every acceptance criterion passes on a real phone with the screen locked and unlocked and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 04 – Counting in the player

Source: Sankalpam flow v7, Requirement 04.

The flow requires that "For a text, the recording playing to its end adds one reading." and "A recitation or repetition cut off part-way adds nothing; finished ones are kept when the devotee leaves."

Outcome: today counting interrupts chanting; after this story the player counts every finished recitation by itself.

Success measure: recitations counted that did not finish, plus finished ones not counted; baseline: not tracked today; target: zero.

Priority: Must — every total, goal and group count is built from it.

#### Acceptance Criteria
- Given a devotee plays the Gaṇeśa aṣṭottaram recording, When the devotee lets it play to its end, Then one reading is added and the count line shows it.
- Given a devotee plays the mūla mantra recording, When the devotee lets 3 repetitions finish, Then the ring shows 3 of 108.
- Given a devotee reads a text without a recording to the last name, When the devotee confirms Done, Then one reading is added.
- Given a devotee stops the aṣṭottaram before the last name, When the devotee leaves the player, Then nothing is added.
- Given a devotee has finished 40 repetitions and paused part-way through the next, When the devotee leaves the player, Then 40 repetitions are saved.
- Given a devotee started the mantra and no repetition has finished, When the devotee leaves the player, Then nothing is saved.
- Given a devotee's mantra recording is playing, When the devotee touches the ring, Then nothing extra is added.
- Given a devotee has 107 repetitions, When the devotee lets the next repetition finish, Then the ring is full, the round shows 2 and the total shows 108.
- Given a devotee has paused the mantra recording, When the devotee touches the ring once, Then one repetition is added.
- Given a devotee locks the phone during the mantra, When the devotee unlocks it five minutes later, Then every repetition finished while locked is counted.
- Given a devotee has 30 repetitions and the phone closes the app in the background, When the devotee opens the app again, Then the 30 repetitions are kept.

#### Out of Scope
- The player controls (US-03.1)
- Adding chanting done outside the app (US-05.1)


## US-05.1 · Devotee adds chanting done outside the app to the right day

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-05.1 | medium | Sannidhi | Sankalpam | Requirement 05 – Count chanting done outside the app |

#### Description
As a devotee, I want to add a number for chanting I did from a book or a mala, so that practice done without the phone still counts towards my goal.

Today chanting done at a temple or from a book is lost from the total.

Terms: added by hand — a count entered as a number with a day instead of counted in the player; it is marked so in About.

Dependencies: player — delivered by US-03.1; group totals — delivered by US-10.1.

Sizing: one sprint; covers the I chanted outside the app sheet for japams and groups. Changing the goal is excluded.

Done when: every acceptance criterion passes and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 05 – Count chanting done outside the app

Source: Sankalpam flow v7, Requirement 05.

The flow requires that "The player has an action to add chanting done outside the app as a number for today or one of the last 7 days."

Outcome: today offline chanting is lost; after this story it is added to the right day and marked.

Success measure: counts added by hand outside the allowed days; baseline: not applicable; target: zero.

Priority: Should — many elders chant with a mala and enter the count later.

#### Acceptance Criteria
- Given a devotee chanted the aṣṭottaram 3 times yesterday from a book, When the devotee adds 3 for yesterday, Then the total rises by 3 and About shows 3 for yesterday marked added by hand.
- Given a devotee opens I chanted outside the app, When the devotee looks at the days offered, Then only today and the last 7 days are offered.
- Given a devotee enters 0, When the devotee tries to add, Then Add stays disabled.
- Given a devotee picks the day exactly 7 days back, When the devotee adds 108, Then 108 is listed under that day marked added by hand.
- Given a devotee's japam started 2 days ago, When the devotee opens the day choices, Then only the start day and later are offered.
- Given a devotee is in the player of a group, When the devotee adds 2, Then the devotee's group count and the group total both rise by 2.
- Given a devotee's japam is already past its goal, When the devotee adds 54, Then the total rises by 54 with no limit.
- Given a devotee's japam moved to Completed while the sheet was open, When the devotee adds the count, Then it is not added and the sheet says the japam has completed.

#### Out of Scope
- Counting in the player (US-04.1)
- Changing the goal or days (US-06.1)


## US-06.1 · Devotee keeps chanting past the goal and sees the japam complete when its days end

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-06.1 | high | Sannidhi | Sankalpam | Requirement 06 – Goal and days |

#### Description
As a devotee, I want to have my goal and days guide my japam without stopping me, so that reaching the goal is honoured and I can still offer more.

Today a vow ends in memory with no record of what was offered.

Terms: goal reached — the moment the total first equals or passes the goal; Completed — the tab that keeps sankalpams whose days have ended; Start again — a new japam with the same text, goal and days.

Dependencies: counting — delivered by US-04.1; Sankalpam home — delivered by US-01.1; group end — delivered by US-11.1.

Sizing: one sprint; covers goal reached, moving to Completed, changing goal or days, Mark complete and Start again for japams. Group changes are excluded.

Done when: every acceptance criterion passes and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 06 – Goal and days

Source: Sankalpam flow v7, Requirement 06.

The flow requires that "Counting never stops at the goal; the total can go past it." and "On the day after the last day, the sankalpam moves to Completed with its total."

Outcome: today vows vanish when they end; after this story each one is kept with its total, and chanting is never cut short by a number.

Success measure: counts refused because a goal was reached; baseline: not applicable; target: zero.

Priority: Must — the goal must encourage, never block.

#### Acceptance Criteria
- Given a devotee's japam has 1,000 of 1,008, When the devotee finishes 8 more repetitions, Then the line "Goal reached · 1,008" appears once and counting carries on.
- Given a devotee's japam has reached its goal of 1,008, When the devotee finishes 54 more, Then the total shows 1,062 of 1,008 with no stop.
- Given a devotee's japam had its last day yesterday, When the devotee opens Sankalpam, Then it is under Completed with its total and not in My japam.
- Given a devotee's japam is on its last day, When the devotee chants, Then the counts are added and it stays in My japam until the day ends.
- Given a devotee's japam has 700 done with a goal of 1,008, When the devotee changes the goal to 500 and saves, Then the goal shows 500 and the total 700 with no warning.
- Given a devotee marks a japam complete, When the devotee confirms, Then it moves to Completed with its total.
- Given a devotee chooses Mark complete, When the devotee cancels, Then the japam stays in My japam.
- Given a devotee opens a completed japam, When the devotee chooses Start again, Then Start a japam opens with the same text, goal and days and today as the start.
- Given a devotee opens a completed japam, When the devotee looks at it, Then it shows the total, the days and the end date and no counting controls.
- Given a devotee is a member but not the organiser of a group, When the devotee opens About for that group, Then no option to change its goal or days is shown.
- Given a devotee's phone was off through the last day, When the devotee opens the app days later, Then the japam is under Completed with its last day as the end date and no message about the time away.

#### Out of Scope
- Group end and Completed for groups (US-11.1)
- Counting (US-04.1)


## US-07.1 · Devotee signs in only when starting or joining a group

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-07.1 | low | Sannidhi | Sankalpam | Requirement 07 – Sign in for groups only |

#### Description
As a devotee, I want to be asked to sign in only when I start or join a group, so that the rest of the app needs no account and no one can ask me for a phone number or OTP.

Today scams reach elders through OTPs and phone numbers, so any sign-in is a risk.

Terms: sign-in step — one sheet with Continue with Google, and Continue with Apple on iPhone.

Dependencies: Google and Apple sign-in — exists on each platform; start and join — delivered by US-08.1 and US-09.1.

Sizing: one sprint; covers the sign-in step and where it appears. Account deletion is excluded.

Done when: every acceptance criterion passes on Android and iPhone and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 07 – Sign in for groups only

Source: Sankalpam flow v7, Requirement 07.

The flow requires that "There is no OTP, password or phone number anywhere."

Outcome: today any account is a scam surface; after this story an account exists only for group members and holds no phone number.

Success measure: screens outside groups that ask for sign-in; baseline: not applicable; target: zero.

Priority: Must — groups need it, and elders must stay safe.

#### Acceptance Criteria
- Given a devotee has never signed in, When the devotee chooses Join on a group, Then a sign-in step shows Continue with Google and the line "No phone number or OTP is needed".
- Given a devotee is not signed in, When the devotee starts a japam and plays it, Then no sign-in is asked for.
- Given a devotee signed in once, When the devotee joins a second group, Then no sign-in step appears.
- Given a devotee uses an iPhone, When the devotee reaches the sign-in step, Then Continue with Apple is offered with Continue with Google.
- Given a devotee cancels the sign-in, When the devotee returns to the group, Then nothing is joined and nothing else changes.
- Given a devotee completes the sign-in step, When the devotee looks at what was asked, Then no password, OTP or phone number was asked for.

#### Out of Scope
- Account and data deletion (Profile)
- Starting or joining a group (US-08.1, US-09.1)


## US-08.1 · Group organiser starts a group that others can join with a code or find

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-08.1 | high | Sannidhi | Sankalpam | Requirement 08 – Start a group |

#### Description
As a group organiser, I want to start a group for a text with a goal, days and an optional purpose and share its code, so that relatives and friends anywhere can chant towards one shared total.

Today organisers run these groups in chat apps and add up counts by hand.

Terms: group — devotees chanting the same text towards one total; Open — listed in Find open groups; Code only — joined with the code or a shared link; code — a short code the organiser shares.

Dependencies: sign-in — delivered by US-07.1; god grid and search — exists in Puja; cities — exists in the master list; player — delivered by US-03.1.

Sizing: one sprint; covers the Start a group form and the code screen. Joining is excluded.

Done when: every acceptance criterion passes with two phones and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 08 – Start a group

Source: Sankalpam flow v7, Requirement 08.

The flow requires that "The organiser chooses Open, listed in Find open groups, or Code only, joined with a code or a shared link." and "An open group shows the organiser's display name and city, never an exact location."

Outcome: today group totals are kept by hand in chats; after this story a group keeps its own total.

Success measure: groups that show a location finer than a city; baseline: not applicable; target: zero.

Priority: Must — the elder japa groups are a primary user need.

#### Acceptance Criteria
- Given a signed-in group organiser chose the Gaṇeśa aṣṭottaram, When the organiser creates Kārtika Aṣṭottaram with a goal of 1,008, 41 days from today, Open and Hyderabad, Then the code screen shows the code with Share and the organiser is the first member.
- Given a group organiser has not written a group name, When the organiser tries to create the group, Then Create stays disabled.
- Given a group organiser writes a purpose that contains a link, When the organiser tries to create the group, Then it is not created and the field shows "Purpose can't have links or numbers".
- Given a group organiser clears the goal field, When the organiser tries to create the group, Then Create stays disabled and the field shows "Enter a goal above zero".
- Given a group organiser writes a purpose of exactly 140 characters, When the organiser creates the group, Then the full purpose is shown in About.
- Given a group organiser has written 140 characters of purpose, When the organiser types one more, Then the field keeps 140 characters and shows 140 of 140.
- Given a group organiser chooses Code only, When the organiser creates the group, Then it is not listed in Find open groups.
- Given a group organiser picks a start date of Wed 7 Apr 2027, When the organiser creates the group, Then its card shows "Starts Wed 7 Apr" and no counts are accepted before then.
- Given a group organiser set the city to Hyderabad, When the organiser creates an open group, Then it shows Hyderabad and never a street, area or map pin.
- Given a group organiser is on the code screen, When the organiser chooses Share, Then the message holds the group name, the code and a link that opens the group in the app.
- Given the organiser's phone loses internet while creating, When the organiser creates the group, Then it is not created, every value stays in the form and the form shows "No internet. Try again when connected".

#### Out of Scope
- Joining a group (US-09.1)
- Removing a member (US-11.2)


## US-09.1 · Devotee joins a group with a code or a link and an optional private reason

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-09.1 | medium | Sannidhi | Sankalpam | Requirement 09 – Join a group |

#### Description
As a devotee, I want to join a group with its code or a shared link and a reason I may keep private, so that I chant with others while sharing only what I choose.

Today joining a chanting group means sharing a phone number in a chat.

Terms: preview — the group's details shown before joining; display name — the name members see; reason — the devotee's purpose, private unless shown.

Dependencies: sign-in — delivered by US-07.1; groups — delivered by US-08.1; Find open groups — delivered by US-09.2.

Sizing: one sprint; covers the code field, links, the preview and the Join sheet. Finding open groups is excluded.

Done when: every acceptance criterion passes with two phones and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 09 – Join a group

Source: Sankalpam flow v7, Requirement 09.

The flow requires that "A devotee joins a group by entering its code on the Groups tab or by opening a shared link." and "A devotee joins with a display name and an optional reason, kept private unless the devotee chooses to show it to the group."

Outcome: today joining exposes a phone number; after this story a member shares only a display name and, if chosen, a reason.

Success measure: joins that expose a phone number; baseline: every join in a chat today; target: zero.

Priority: Must — joining is how the elder groups grow.

#### Acceptance Criteria
- Given a signed-in devotee has a group's code, When the devotee enters it on the Groups tab, Then the preview shows the group name, text, purpose, organiser and city, members, total and day N of M with Join.
- Given a devotee has the app and a shared group link, When the devotee opens the link, Then the app opens on that group's preview.
- Given a devotee is on a group preview, When the devotee joins as Lakshmi with a reason and Keep private, Then the group is listed in Groups, its player opens and members see Lakshmi but not the reason.
- Given a devotee enters a code that matches no group, When the devotee submits it, Then nothing opens and the field shows "This code doesn't match a group".
- Given a devotee finds a group with 200 members, When the devotee views its preview, Then Join is disabled and it shows "This group is full".
- Given a devotee finds a group with 199 members, When the devotee joins, Then the devotee is the 200th member.
- Given a devotee leaves the reason empty, When the devotee joins, Then no reason is stored or shown.
- Given a devotee chose Show to the group, When the devotee joins, Then members see the reason under the devotee's name in About.
- Given a devotee has a preview open while the group's days end, When the devotee joins, Then nothing is joined and the preview shows "This group has ended".

#### Out of Scope
- Finding open groups (US-09.2)
- Chanting in the group (US-10.1)


## US-09.2 · Devotee finds open groups by city

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-09.2 | medium | Sannidhi | Sankalpam | Requirement 09 – Join a group |

#### Description
As a devotee, I want to see open groups around the world by city, so that I can find one to join for the text and dates that suit me.

Today a person hears of a group only by word of mouth.

Terms: Find open groups — the list reached from the Groups tab; globe — the picture of cities with open groups; Near me, Starting soon, All — the filters.

Dependencies: open groups — delivered by US-08.1; preview and join — delivered by US-09.1; internet — needed for this screen only.

Sizing: one sprint; covers the globe, the filters and the group cards. Joining is excluded.

Done when: every acceptance criterion passes and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 09 – Join a group

Source: Sankalpam flow v7, Requirement 09.

The flow requires that "Find open groups lists open groups by city, with a globe at the top and filters Near me, Starting soon and All."

Outcome: today groups are found by chance; after this story every open group is visible by city.

Success measure: member names shown on Find open groups; baseline: not applicable; target: zero.

Priority: Should — it connects families abroad with groups at home.

#### Acceptance Criteria
- Given a devotee is signed out and open groups exist in Hyderabad and Chennai, When the devotee opens Find open groups, Then the globe marks both cities and each card shows the name, text, purpose, organiser and city, members, day N of M and total.
- Given a code-only group exists that the devotee is not in, When the devotee opens Find open groups, Then it is not listed.
- Given a devotee views an open group's card, When the devotee looks for member names, Then none are shown, only the number of members.
- Given a devotee had seen a group whose days have since ended, When the devotee opens Find open groups, Then it is not listed.
- Given a devotee sees one group starting tomorrow and one that started today, When the devotee chooses Starting soon, Then only the group starting tomorrow is listed.
- Given the devotee's city is Hyderabad, When the devotee chooses Near me, Then groups in Hyderabad are listed first, then others in Telangana.
- Given a devotee chose a filter that no open group matches, When the devotee views the list, Then it shows "No groups here yet" with Start a group.
- Given the devotee's phone has no internet, When the devotee opens Find open groups, Then it shows "Finding groups needs internet. Your own japam works offline."

#### Out of Scope
- Joining a group (US-09.1)
- Starting a group (US-08.1)


## US-10.1 · Group member's chanting counts for them and the group together

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-10.1 | medium | Sannidhi | Sankalpam | Requirement 10 – Chant in a group |

#### Description
As a group member, I want to have my chanting in a group count for my own resolve and for the group's total at once, so that my personal reason and our shared goal are both fulfilled by the same chanting.

Today group totals are added up by hand from chat messages and often go wrong.

Terms: own count — the member's count in that group; group total — the sum of all members' counts; pending — a count saved on the phone that has not reached the group yet.

Dependencies: player and counting — delivered by US-03.1 and US-04.1; joining — delivered by US-09.1; internet — needed only to send counts.

Sizing: one sprint; covers counting in a group, keeping group and My japam counts apart, and offline sending. About this group is excluded.

Done when: every acceptance criterion passes with two phones, one offline, and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 10 – Chant in a group

Source: Sankalpam flow v7, Requirement 10.

The flow requires that "Chanting in a group adds to the devotee's own count in that group and to the group total at the same time." and "Chanting a text in My japam counts only for that japam, and chanting in a group counts only for that group."

Outcome: today totals are tallied by hand; after this story the member's count and the group total add themselves.

Success measure: counts lost between a member's phone and the group total; baseline: not tracked today; target: zero.

Priority: Must — the shared total is why the group exists.

#### Acceptance Criteria
- Given a group member opens Kārtika Aṣṭottaram from Groups with 24 of their own and 412 in the group, When the member lets the recording play to its end, Then the player shows "You 25 · Group 413 of 1,008".
- Given a group member is in a mantra group, When the member lets 108 repetitions finish, Then the member's count and the group total both rise by 108 repetitions.
- Given a group member also has a japam for the same aṣṭottaram, When the member finishes a reading in that japam, Then the group total is unchanged.
- Given a group member finishes a reading in the group, When the member opens My japam, Then no japam total has changed.
- Given a group member's group starts tomorrow, When the member opens it, Then the player shows "Starts tomorrow" and adds no counts.
- Given a group member's group is on its last day, When the member chants, Then the counts are added to the member's count and the group total.
- Given a group member's group has passed its goal, When the member chants, Then the group total keeps rising past the goal.
- Given a group member is offline, When the member finishes 54 repetitions, Then the member's count shows 54 at once and the player shows "Will add to the group when online" until it is sent.
- Given a group member was removed while offline with pending counts, When the member's phone comes back online, Then the pending counts are not added to the group total.

#### Out of Scope
- About this group and today's readers (US-10.2)
- Leaving and reporting (US-11.1)


## US-10.2 · Group member sees today's readers and the gathering, never ranked

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-10.2 | medium | Sannidhi | Sankalpam | Requirement 10 – Chant in a group |

#### Description
As a group member, I want to see who chanted today and a small note when someone adds, so that I feel part of the gathering without anyone being compared.

Today members only see counts scattered through chat messages.

Terms: About this group — the sheet with the group's details; today's readers — members who added today, with their counts; note — a small brief line on the player when another member adds.

Dependencies: group counts — delivered by US-10.1; reasons shown or private — delivered by US-09.1.

Sizing: one sprint; covers About this group and the notes on the player. Organiser tools are excluded.

Done when: every acceptance criterion passes with two phones and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 10 – Chant in a group

Source: Sankalpam flow v7, Requirement 10.

The flow requires that "When another member adds, a small note with their name and count appears briefly."

Outcome: today members piece the picture together from chat; after this story the player and one sheet show the gathering.

Success measure: positions, medals or top labels shown; baseline: not applicable; target: zero.

Priority: Must — this is the virtual gathering.

#### Acceptance Criteria
- Given a group has 38 members, When a group member opens About this group, Then it shows the purpose, day N of M, 38 members, today's readers with each count in the order they last added, the code with Share, and Leave.
- Given a group member has the player open, When another group member adds 3, Then the note "Lakshmi · +3" appears briefly and the group total rises by 3.
- Given several members chanted today, When a group member opens About this group, Then no positions, medals or top labels are shown.
- Given some members have not chanted today, When a group member opens About this group, Then they are not among today's readers and no "not yet" status is shown.
- Given no group member has chanted today, When a group member opens About this group, Then today's readers shows "Today's readers will appear here".
- Given three members add within one second, When a group member watches the player, Then the notes appear one after another, never more than one at a time.
- Given one member chose Show to the group for their reason, When a group member opens About this group, Then only that member's reason is shown, under their name.
- Given a group member's phone is offline, When the member opens About this group, Then the last known total and readers are shown with "Updated when online".

#### Out of Scope
- Counting in the group (US-10.1)
- Leaving, reporting and removing (US-11.1, US-11.2)


## US-11.1 · Group member sees the group complete when its days end, and can leave or report safely

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-11.1 | medium | Sannidhi | Sankalpam | Requirement 11 – Group end and safety |

#### Description
As a group member, I want to have the group stay open until its days end and be able to leave or report it, so that the gathering runs its full course and stays safe.

Today leaving a chat group is visible to everyone and there is no way to flag misuse.

Terms: Leave — the member's way out of a group; Report — the action on every open group; Completed — where ended groups are kept.

Dependencies: groups — delivered by US-08.1; About this group — delivered by US-10.2; who reviews reports — owned by business.

Sizing: one sprint; covers the group's end, Leave, Report and the absence of chat. Removing a member is excluded.

Done when: every acceptance criterion passes and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 11 – Group end and safety

Source: Sankalpam flow v7, Requirement 11.

The flow requires that "A group stays open until its days end, then moves to Completed for every member." and "There is no chat, photos, private messages, phone numbers or rankings in a group."

Outcome: today groups end in silence and misuse cannot be reported; after this story each group completes on its last day and every open group can be reported.

Success measure: ways to send a message inside a group; baseline: every chat group today; target: zero.

Priority: Must — safety for elders is a launch condition for groups.

#### Acceptance Criteria
- Given a group member's group had its last day yesterday, When the member opens Sankalpam, Then the group is under Completed with the group total and the member's own count.
- Given a group member chooses Leave, When the member confirms, Then the group moves to Completed for the member and their past counts stay in the group total.
- Given a group member chooses Leave, When the member cancels, Then the member is still in the group.
- Given a group member is on the player or About this group, When the member looks for a way to message others, Then no chat, photo, private message, phone number or ranking is shown.
- Given a group member's group reached its goal on day 10 of 21, When the member opens Groups, Then the group is still listed and still counts.
- Given a group member opens Report on an open group, When the member views the reasons, Then four are listed: Not about chanting, Hurtful words, Asks for money, Something else.
- Given a group member left a group, When the member joins again with its code, Then the member's earlier counts are still in the group total.
- Given a group member's phone is offline, When the member sends a report, Then the sheet shows "We'll send this when you're online" and the report is sent later.

#### Out of Scope
- Removing a member (US-11.2)
- Who reviews reports, owned by business


## US-11.2 · Group organiser removes a member, and their past counts stay

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-11.2 | low | Sannidhi | Sankalpam | Requirement 11 – Group end and safety |

#### Description
As a group organiser, I want to remove a member from my group, so that I can keep the gathering safe without losing what was already offered.

Today an organiser cannot remove someone from a chanting chat without leaving it.

Terms: remove — take a member out of the group; the group moves to Completed for that member.

Dependencies: About this group — delivered by US-10.2; group counts — delivered by US-10.1.

Sizing: one sprint; covers Remove a member. Handing the group to another organiser is excluded.

Done when: every acceptance criterion passes with two phones and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 11 – Group end and safety

Source: Sankalpam flow v7, Requirement 11.

The flow requires that "The organiser can remove a member, and the removed member's past counts stay in the total."

Outcome: today organisers cannot moderate; after this story they can remove a member and keep the total intact.

Success measure: past counts lost when a member is removed; baseline: not applicable; target: zero.

Priority: Must — groups open to anyone need someone who can remove a member.

#### Acceptance Criteria
- Given a group organiser opens About this group, When the organiser removes Rama and confirms, Then Rama is not listed among the members and Rama's past counts stay in the group total.
- Given a group organiser chooses Remove for Rama, When the organiser cancels, Then Rama is still a member.
- Given a group organiser is the only member, When the organiser opens Remove a member, Then no one is listed.
- Given a group organiser removed Rama after Rama chanted today, When the organiser views today's readers, Then Rama is not listed and Rama's counts from today stay in the total.
- Given a group organiser opens About this group, When the organiser looks for Leave, Then Leave is not offered to the organiser.
- Given a group organiser removes a member who is offline with pending counts, When the organiser confirms, Then the member is removed and those pending counts never reach the group total.

#### Out of Scope
- Handing the group to a new organiser, out of scope for now
- Members leaving and reporting (US-11.1)
