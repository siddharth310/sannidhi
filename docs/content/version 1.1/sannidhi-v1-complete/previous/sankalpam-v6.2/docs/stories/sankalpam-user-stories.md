# Sankalpam — user stories (solo and groups)

Sannidhi · Sankalpam module · v6.2 · 18 Oct 2026

One file for the whole module. Part 1 is the flow the stories are written against (decisions of 14 and 18 Oct 2026).
Part 2 is the index: stories, coverage, traceability and open questions. Part 3 holds the 18 stories in the house
user-story template.

**v6.2 (18 Oct 2026):** Chant along restored for mantras (new US-01.3); japa and sankalpam are one count (R02.03,
US-02.1 AC6); Listen is for texts only (US-01.2).

Screens: `docs/design/sankalpam-design.md` and `docs/design/v6/png/3*.png`, `5*.png`.

---

# Part 1 · Flow rules

**Project Goal**

- Devotees can track the japa and pārāyaṇam they do every day, alone or with a group.
- Devotees can join a group, share their count, see the group's count and see other running groups.

**Requirement 01 – Practise a nāmalu**

- In Count mode, one press on the counter adds one: one reading for a text, one repetition for a mantra.
- Count mode is for reading or chanting outside the app and recording it here.
- For a mantra, the counter shows a ring of 108 and the round number; each press still adds one repetition.
- Read mode adds one reading when the devotee finishes the text and confirms Done.
- Listen mode adds one reading automatically when the recitation plays to the end.
- Leaving Read or Listen before the end adds nothing.
- Leaving Count with a count above zero saves that count; Start over asks for confirmation.
- Listen is shown only when the text has a recording.
- A text is practised with Read, Listen or Count; a mantra is practised with Chant along or Count.
- In Chant along the mantra's recording plays with the mantra shown on screen, and each recitation that plays through adds one repetition.
- Chant along stops by itself at the chosen count of 11, 21, 108 or 1,008, or keeps going; the default is the mantra's usual count.
- Leaving Chant along saves the repetitions finished so far; a recitation cut off part-way adds nothing.
- Chant along keeps playing and counting when the screen is locked, with play and pause on the lock screen.
- Chant along is shown only when the mantra has a recording, and it is the first mode offered when it is.

**Requirement 02 – Counts for one sankalpam per session**

- A practice session counts for the devotee's own practice and, optionally, for one sankalpam chosen in Counts for.
- A session never counts for two sankalpams at once.
- Japa and sankalpam share one count: every reading or repetition from any mode is one practice entry, and a sankalpam total is the sum of the entries made for it.

**Requirement 03 – Start a personal sankalpam**

- A devotee starts a personal sankalpam by choosing a text, an optional total to reach, optional days, a start date and an optional private intent.
- Days can be 11, 21 or 41, one māsam, or no end.
- The start date defaults to today and can be a future date such as Ugadi or a festival.
- A sankalpam with a future start date is listed as Starts on its date and accepts no counts before then.
- The intent is private to the devotee and stays on the phone.

**Requirement 04 – See and manage my sankalpams**

- My sankalpams has two tabs, Active and Completed.
- Each active card shows the text, Just me or the group name, the total so far, the target if set and day N of M if days are set.
- The Home sādhana bar lists active sankalpams and opens My sankalpams.
- The sankalpam page shows this month's days with practice and the count on each day, and never shows days without practice.
- A devotee can change the target or the days of a personal sankalpam at any time; for a group only the organiser can.
- There is no pause; the devotee practises when they choose and the app never shows gaps.
- A devotee can add a count by hand for today or up to 7 days back, marked added by hand.

**Requirement 05 – Complete a sankalpam**

- When the total reaches the target, a gentle completion message shows the total offered and the sankalpam moves to Completed.
- When the days end before the target, the sankalpam moves to Completed with the total reached and an option to extend.
- A devotee can also mark a sankalpam complete at any time.
- The Completed tab lists personal sankalpams and the group pārāyaṇams the devotee was part of.
- A completed sankalpam can still be practised; new counts go to the devotee's own practice only.

**Requirement 06 – Sign in for groups only**

- Starting or joining a group needs Google sign-in; everything else works without an account.
- There is no OTP, password or phone number anywhere.

**Requirement 07 – Start a group**

- An organiser starts a group by choosing a text, a group name, a purpose of up to 140 characters without links or numbers, a start date, a length and an optional target.
- The organiser chooses Open, shown on Discover, or Invite only, joined with an invite code.
- The group shows the organiser's display name and city, never an exact location.
- A group holds up to 200 members.
- A text group counts readings and a mantra group counts repetitions.
- There are no duplicate checks and many groups can chant the same text.

**Requirement 08 – Discover and join a group**

- Discover shows open groups worldwide by city, with filters Starting soon, Near me and Running.
- Each group card shows the name, text, purpose, organiser and city, people joined, day N of M and the total.
- A member joins with a display name and an optional intent, and chooses Show to the group or Keep private.
- An invite-only group is joined only with its invite code.
- A full group shows that it is full and cannot be joined.

**Requirement 09 – Practise together**

- When Counts for is set to a group, the count is added to the member's own practice and to the group total.
- The group page shows the purpose, day N of M, the group total and today's readers with their counts, never ranked.
- Member names and counts are visible to members only; Discover shows group totals only.
- Counts made offline are kept on the phone and added to the group when the phone is back online.
- When the group total reaches its target, the group shows a gentle completion message to every member.

**Requirement 10 – Organiser tools, leaving and reporting**

- The organiser can change the target or the days, remove a member or close the group.
- Any member can leave the group; their past counts stay in the group total.
- Every public group card has a Report action.
- There is no chat, photos, private messages, phone numbers or rankings in a group.

**Non-Functional Requirements**

- Practice, personal sankalpams and intents work offline and stay on the phone.
- Text is at least 16 sp, touch targets at least 56 dp, and screens work at 200% font scale.
- No reminders, notifications, streaks or words that suggest the devotee missed anything.
- Screens follow docs/design-v6.md and the Sankalpam design spec.

**Assumptions**

- Moving practice data to a new phone is out of scope for now.
- Handing a group to a new organiser is out of scope for now.

---

# Part 2 · Index

**Source:** Sankalpam flow v6.2 (docs/app-flow.md §6 and decisions of 14 and 18 Oct 2026) **Prepared:** 18 Oct 2026 **Stories:** 18 · **Validator:** 18/18 pass, 64/64 flow rules traced

| Story | Title | Complexity | ACs |
|---|---|---|---|
| US-01.1 | Devotee records each reading or repetition done outside the app with one count | medium | 9 |
| US-01.2 | Devotee is counted automatically on finishing a text in Read or Listen | medium | 9 |
| US-01.3 | Devotee chants a mantra along with its recording and every recitation is counted | high | 13 |
| US-02.1 | Devotee chooses the one sankalpam a practice session counts towards | low | 6 |
| US-03.1 | Devotee starts a personal sankalpam with an optional total, days and start date | high | 11 |
| US-04.1 | Devotee sees active sankalpams at a glance on My sankalpams and Home | medium | 8 |
| US-04.2 | Devotee sees this month's practice days on a sankalpam page, never the gaps | medium | 7 |
| US-04.3 | Devotee adds practice done without the phone for today or the last 7 days | medium | 8 |
| US-04.4 | Devotee changes the total or days of a personal sankalpam at any time | low | 6 |
| US-05.1 | Devotee completes a sankalpam gently and finds it under Completed | high | 12 |
| US-06.1 | Devotee signs in with Google only when starting or joining a group | low | 6 |
| US-07.1 | Group organiser starts a group pārāyaṇam that others can join | high | 11 |
| US-08.1 | Devotee discovers open group pārāyaṇams by city | medium | 8 |
| US-08.2 | Devotee joins a group with an intent they show or keep private | medium | 9 |
| US-09.1 | Group member adds each session to the group total, even when offline | medium | 8 |
| US-09.2 | Group member sees today's readers and the group total, never ranked | medium | 7 |
| US-10.1 | Group organiser changes the plan, removes a member or closes the group | medium | 8 |
| US-10.2 | Devotee leaves a group or reports a public group, with no chat anywhere | medium | 7 |

## Coverage map
| Story | Positive | Negative | Boundary | Edge | Exception |
|---|---|---|---|---|---|
| US-01.1 | 1 | 4,5 | 2,3 | 7 | 8 |
| US-01.2 | 1 | 2,3 | 4,5 | 6 | 7 |
| US-01.3 | 1,12,13 | 2,3,4 | 5,6 | 7,8,10,11 | 9 |
| US-02.1 | 1,6 | 2 | 3 | 4 | 5 |
| US-03.1 | 1 | 2,3,4 | 5,6 | 7 | 8 |
| US-04.1 | 1 | 2,3 | 4,5 | 6 | 7 |
| US-04.2 | 1 | 2,3 | 4,5 | 6 | 7 |
| US-04.3 | 1 | 2,3 | 4,5 | 6 | 7 |
| US-04.4 | 1 | 2 | 3 | 4 | 5 |
| US-05.1 | 1 | 2,3,4 | 5,6 | 8 | 10 |
| US-06.1 | 1 | 2 | 3 | 4 | 5 |
| US-07.1 | 1 | 2,3,4 | 5,6 | 7 | 8 |
| US-08.1 | 1 | 2,3 | 4,5 | 6 | 7 |
| US-08.2 | 1 | 3,4 | 5,6 | 7 | 8 |
| US-09.1 | 1 | 2,3 | 4,5 | 6 | 7 |
| US-09.2 | 1 | 2,3 | 4,5 | 6 | 7 |
| US-10.1 | 1 | 2,3 | 4,5 | 6 | 7 |
| US-10.2 | 1 | 2,3 | 4,5 | 6 | 7 |

## Flow traceability
| ID | Flow text | Covered by |
|---|---|---|
| GOAL.01 | Devotees can track the japa and pārāyaṇam they do every day, alone or with a group. | US-01.1 AC1; US-01.3 AC1; US-03.1 AC1 |
| GOAL.02 | Devotees can join a group, share their count, see the group's count and see other running groups. | US-08.1 AC1; US-08.2 AC1; US-09.1 AC1 |
| R01.01 | In Count mode, one press on the counter adds one | US-01.1 AC1; US-01.1 AC2 |
| R01.02 | Count mode is for reading or chanting outside the app | US-01.1 AC9 |
| R01.03 | For a mantra, the counter shows a ring of 108 and the round number | US-01.1 AC2; US-01.1 AC3 |
| R01.04 | Read mode adds one reading when the devotee confirms Done | US-01.2 AC1 |
| R01.05 | Listen mode adds one reading automatically at the end | US-01.2 AC8 |
| R01.06 | Leaving Read or Listen before the end adds nothing | US-01.2 AC2; US-01.2 AC3; US-01.2 AC5 |
| R01.07 | Leaving Count saves; Start over asks for confirmation | US-01.1 AC4; US-01.1 AC5; US-01.1 AC6; US-01.1 AC8 |
| R01.08 | Listen is shown only when the text has a recording | US-01.2 AC6 |
| R01.09 | Texts: Read, Listen, Count; mantras: Chant along, Count | US-01.2 AC6; US-01.3 AC12 |
| R01.10 | Chant along plays the recording with the mantra shown; each recitation adds one | US-01.3 AC1 |
| R01.11 | Chant along stops at 11, 21, 108 or 1,008, or keeps going; default is the usual count | US-01.3 AC5; US-01.3 AC6; US-01.3 AC7; US-01.3 AC13 |
| R01.12 | Leaving Chant along saves finished repetitions; a cut-off recitation adds nothing | US-01.3 AC3; US-01.3 AC4; US-01.3 AC9 |
| R01.13 | Chant along keeps playing and counting with the screen locked | US-01.3 AC8 |
| R01.14 | Chant along only with a recording, and first when it exists | US-01.3 AC2; US-01.3 AC12 |
| R02.01 | A session counts for own practice and optionally one sankalpam | US-02.1 AC1; US-02.1 AC3 |
| R02.02 | A session never counts for two sankalpams at once | US-02.1 AC2 |
| R02.03 | Japa and sankalpam share one count | US-02.1 AC6; US-01.3 AC11 |
| R03.01 | Start a personal sankalpam: text, optional total, days, start date, intent | US-03.1 AC1; US-03.1 AC2; US-03.1 AC5 |
| R03.02 | Days can be 11, 21 or 41, one māsam, or no end | US-03.1 AC9 |
| R03.03 | Start date defaults to today and can be in the future | US-03.1 AC6; US-03.1 AC11 |
| R03.04 | Future start: listed as Starts, no counts before then | US-03.1 AC4; US-04.1 AC6 |
| R03.05 | The intent is private and stays on the phone | US-03.1 AC10 |
| R04.01 | My sankalpams has Active and Completed tabs | US-04.1 AC1; US-05.1 AC1 |
| R04.02 | Each active card shows text, owner, total, target, day N of M | US-04.1 AC1; US-04.1 AC4 |
| R04.03 | Home sādhana bar lists active sankalpams | US-04.1 AC5; US-04.1 AC8 |
| R04.04 | Sankalpam page shows this month's practice days, never gaps | US-04.2 AC1; US-04.2 AC2; US-04.2 AC3 |
| R04.05 | Change target or days; for a group only the organiser | US-04.4 AC1; US-04.4 AC5 |
| R04.06 | No pause; the app never shows gaps | US-04.4 AC6; US-04.1 AC3 |
| R04.07 | Add by hand for today or up to 7 days back | US-04.3 AC1; US-04.3 AC2; US-04.3 AC4 |
| R05.01 | Target reached: gentle completion message, moves to Completed | US-05.1 AC1; US-05.1 AC6 |
| R05.02 | Days end first: Completed with total and an option to extend | US-05.1 AC5; US-05.1 AC8 |
| R05.03 | Mark complete at any time | US-05.1 AC7 |
| R05.04 | Completed lists personal and group sankalpams | US-05.1 AC12 |
| R05.05 | A completed sankalpam can still be practised | US-05.1 AC9; US-05.1 AC11 |
| R06.01 | Google sign-in only to start or join a group | US-06.1 AC1; US-06.1 AC2 |
| R06.02 | No OTP, password or phone number anywhere | US-06.1 AC6 |
| R07.01 | Start a group: text, name, purpose ≤ 140 without links or numbers, dates, target | US-07.1 AC1; US-07.1 AC2; US-07.1 AC3; US-07.1 AC5 |
| R07.02 | Open on Discover or Invite only with a code | US-07.1 AC1; US-07.1 AC7 |
| R07.03 | Display name and city, never an exact location | US-07.1 AC11 |
| R07.04 | A group holds up to 200 members | US-08.2 AC3; US-08.2 AC5 |
| R07.05 | Text groups count readings, mantra groups repetitions | US-07.1 AC9; US-09.1 AC6 |
| R07.06 | No duplicate checks | US-07.1 AC10 |
| R08.01 | Discover by city with Starting soon, Near me, Running | US-08.1 AC1; US-08.1 AC4; US-08.1 AC5 |
| R08.02 | Group card contents | US-08.1 AC1 |
| R08.03 | Join with display name, optional intent, show or keep private | US-08.2 AC1; US-08.2 AC2 |
| R08.04 | Invite-only joined only with its code | US-08.2 AC4; US-08.2 AC9; US-08.1 AC2 |
| R08.05 | A full group cannot be joined | US-08.2 AC3 |
| R09.01 | Counts for a group adds to own practice and the group total | US-09.1 AC1 |
| R09.02 | Group page: purpose, day, total, today's readers, never ranked | US-09.2 AC1; US-09.2 AC2 |
| R09.03 | Names and counts for members only; Discover shows totals | US-08.1 AC8; US-09.2 AC5 |
| R09.04 | Offline counts added to the group when back online | US-09.1 AC7 |
| R09.05 | Group target reached: gentle message to every member | US-09.1 AC4 |
| R10.01 | Organiser changes target or days, removes a member, closes | US-10.1 AC1; US-10.1 AC6; US-10.1 AC8 |
| R10.02 | Any member can leave; past counts stay | US-10.2 AC1; US-09.2 AC7 |
| R10.03 | Every public group card has Report | US-10.2 AC6 |
| R10.04 | No chat, photos, private messages, phone numbers or rankings | US-10.2 AC3 |
| NFR.01 | Practice and personal sankalpams work offline | US-03.1 AC8; US-08.1 AC7 |
| NFR.02 | 16 sp text, 56 dp targets, 200% font scale | Context only — proved for every screen by the 200% screenshot tests and docs/design/sankalpam-design.md |
| NFR.03 | No reminders, notifications, streaks or missed words | US-04.1 AC3; US-05.1 AC4 |
| NFR.04 | Screens follow design-v6 and the Sankalpam spec | Context only — visual match is proved by screenshot tests against docs/design/v6 |
| ASM.01 | Moving practice data to a new phone is out of scope | Out of scope — moving practice data to a new phone is deferred by decision |
| ASM.02 | Handing a group to a new organiser is out of scope | Out of scope — handing a group to a new organiser is deferred by decision |

## Open questions
| # | Story | Question |
|---|---|---|
| 1 | US-10.2 | Who reviews reports, and what happens to a group after reports (hide from Discover, close)? |
| 2 | US-10.2 | Are the four report reasons right: Not about chanting, Hurtful words, Asks for money, Something else? |
| 3 | US-07.1, US-08.1 | Groups are worldwide but the city list holds Indian cities only. Add world cities for organisers abroad? |
| 4 | US-08.1 | Near me = same city first, then same state. Confirm, and what Near me means outside India. |
| 5 | US-09.1 | When a group reaches its target, does it stay open until its days end, or move to Completed at once? |
| 6 | US-07.1 | Invite code: length, and does it ever expire? |
| 7 | US-01.3 | Mantra recordings: one clean repetition that the app loops (default), or long recordings of many repetitions with marks? |
| 8 | US-01.3 | Who records the mantras, and should a male and a female voice both be offered? |

## Split notes
- Practice modes split by how a count is made: by hand on the counter (US-01.1), on finishing a text in Read or Listen (US-01.2), and by each recitation of a mantra in Chant along (US-01.3).
- Managing a sankalpam split into the list (US-04.1), the page and its history (US-04.2), adding by hand (US-04.3) and editing (US-04.4).
- Groups split by actor and moment: sign-in (US-06.1), organiser starts (US-07.1), devotee finds and joins (US-08.1, US-08.2), members practise and see the gathering (US-09.1, US-09.2), organiser moderates and members leave or report (US-10.1, US-10.2).

---

# Part 3 · Stories


## US-01.1 · Devotee records each reading or repetition done outside the app with one count

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-01.1 | medium | Sannidhi | Sankalpam | Requirement 01 – Practise a nāmalu |

#### Description
As a devotee, I want to add one to my count each time I finish a reading or a repetition away from the app, so that every reading I do from a book or a mala is counted without typing totals.

Today elders keep counts on paper or in their heads and lose them between days.

Terms: Count mode — the counter screen of a text or mantra; reading — one full reading of a text such as the aṣṭottaram; repetition — one recitation of a mantra; round — 108 repetitions of a mantra.

Dependencies: texts and mantras from the content build — exists; practice storage on the phone — delivered by this story; Counts for — US-02.1.

Sizing: one sprint; covers the counter, −1, Start over and saving on exit. Read, Listen and Chant along are excluded.

Done when: every acceptance criterion passes on a real phone in te, hi and en at 200% font scale and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 01 – Practise a nāmalu

Source: Sankalpam flow v6.2, Requirement 01.

The flow requires that "In Count mode, one press on the counter adds one: one reading for a text, one repetition for a mantra." and "Count mode is for reading or chanting outside the app and recording it here."

Outcome: today counts done from a book or mala are not recorded anywhere; after this story each one is recorded with a single action.

Success measure: share of counted sessions saved without loss; baseline: not tracked today; target: every session with a count above zero appears in My practice.

Priority: Must — the elder japa groups depend on it and every sankalpam total is built from it.

#### Acceptance Criteria
- Given a devotee has opened Count for the Gaṇeśa aṣṭottaram with no counts yet, When the devotee registers one count on the counter, Then the counter shows 1 reading.
- Given a devotee has opened Count for the Gaṇeśa mūla mantra with 107 counts, When the devotee registers one more count, Then the ring is full, the round shows 2 and the total shows 108 repetitions.
- Given a devotee has opened Count for a mantra with no counts, When the devotee registers the first count, Then the counter shows 1 of 108 and round 1.
- Given a devotee has opened Count and registered nothing, When the devotee leaves the screen, Then no entry is saved and My practice is unchanged.
- Given a devotee has 54 counts in the session, When the devotee chooses Start over and cancels the confirmation, Then the counter still shows 54.
- Given a devotee has 54 counts in the session, When the devotee chooses Start over and confirms, Then the counter shows 0 and nothing from the cleared session is saved.
- Given a devotee registered one count by mistake, When the devotee uses −1, Then the counter goes down by one and never shows a number below 0.
- Given a devotee has 30 counts and the phone closes the app in the background, When the devotee opens the app again, Then My practice shows the 30 counts for today.
- Given a devotee has opened Count for a text, When the devotee looks at the counter, Then the caption reads "Count each reading you finish" and no names of the text are listed.

#### Out of Scope
- Read and Listen counting (US-01.2)
- Chant along for mantras (US-01.3)
- Choosing a sankalpam for the session (US-02.1)


## US-01.2 · Devotee is counted automatically on finishing a text in Read or Listen

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-01.2 | medium | Sannidhi | Sankalpam | Requirement 01 – Practise a nāmalu |

#### Description
As a devotee, I want to have a full reading or a full recitation of a text in the app counted for me, so that I never have to remember to add it myself.

Today a reading done in the app leaves no record unless the devotee writes it down.

Terms: text — a nāmalu such as the aṣṭottaram, sahasranāmam or a stotram; Read — the names of a text shown one per row with Done at the end; Listen — the recorded recitation of a text with the current name highlighted; recording — an audio file supplied with the text.

Dependencies: names and audio from the content build — names for the aṣṭottaram arrive with the verified import; practice storage — delivered by US-01.1.

Sizing: one sprint; covers Read with Done and Listen to the end for texts. Count mode and Chant along for mantras are excluded.

Done when: every acceptance criterion passes on a real phone and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 01 – Practise a nāmalu

Source: Sankalpam flow v6.2, Requirement 01.

The flow requires that "Listen mode adds one reading automatically when the recitation plays to the end." and "Leaving Read or Listen before the end adds nothing."

Outcome: today in-app readings are not counted; after this story each finished reading or recitation is counted once.

Success measure: finished readings counted exactly once; baseline: not counted today; target: one entry per finished Read or Listen and none for unfinished ones.

Priority: Must — Listen is the easiest way for elders to practise and must count without effort.

#### Acceptance Criteria
- Given a devotee has read the aṣṭottaram to the last name in Read, When the devotee confirms Done, Then one reading is added to My practice for today.
- Given a devotee has opened Read, When the devotee leaves before confirming Done, Then nothing is added to My practice.
- Given a devotee has started Listen for a text with a recording, When the devotee stops playback before the end, Then nothing is added to My practice.
- Given a devotee has finished Listen once, When the devotee plays the recitation again to the end, Then My practice shows two readings for the session.
- Given a devotee has listened up to the second-last name, When the devotee leaves the screen, Then nothing is added to My practice.
- Given a devotee opens a text that has no recording, When the devotee views the practice modes, Then only Read and Count are shown.
- Given a devotee is listening and a phone call pauses playback, When the devotee resumes and lets it play to the end, Then one reading is added to My practice.
- Given a devotee has started Listen for the Gaṇeśa aṣṭottaram, When the devotee lets the recitation play to the end, Then one reading is added with no confirmation asked.
- Given a devotee opened Read from a puja step, When the devotee confirms Done, Then one reading is added and the devotee is back on the same puja step.

#### Out of Scope
- Count mode (US-01.1)
- Chant along for mantras (US-01.3)
- Choosing a sankalpam for the session (US-02.1)


## US-01.3 · Devotee chants a mantra along with its recording and every recitation is counted

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-01.3 | high | Sannidhi | Sankalpam | Requirement 01 – Practise a nāmalu |

#### Description
As a devotee, I want to chant a mantra along with its recording while the mantra is shown on screen, so that every repetition I chant with it is counted without pressing anything.

Today a devotee chanting with a recording on another app or a speaker counts on fingers or a mala and often loses the count.

Terms: Chant along — the mantra screen that plays the recording on a loop with the mantra shown; recitation — one play of the recording, which is one repetition of the mantra; Stop after — the chosen count at which playback ends by itself; text view — the script choice: the devotee's language script, संस्कृतम् or IAST.

Dependencies: mantra recordings of one repetition with word timings — delivered by the content build (mantra audio field); ring counter and practice storage — delivered by US-01.1; Counts for — delivered by US-02.1.

Sizing: one sprint; covers Chant along for mantras, Stop after, playback with the screen locked, and leaving. Listen for texts is excluded.

Done when: every acceptance criterion passes on a real phone with the screen locked and unlocked, in te, hi and en at 200% font scale, and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 01 – Practise a nāmalu

Source: Sankalpam flow v6.2, Requirement 01.

The flow requires that "In Chant along the mantra's recording plays with the mantra shown on screen, and each recitation that plays through adds one repetition." and "Leaving Chant along saves the repetitions finished so far; a recitation cut off part-way adds nothing."

Outcome: today mantra japa in the app is counted only by pressing the counter; after this story chanting along with the recording counts each repetition by itself.

Success measure: recitations counted that did not play through, plus recitations that played through and were not counted; baseline: not tracked today; target: zero.

Priority: Must — chanting along with a recording is how many elders do japa every day, and the earlier app had it.

#### Acceptance Criteria
- Given a devotee opens the Gaṇeśa mūla mantra, which has a recording, When the devotee starts Chant along, Then the mantra is shown in the chosen script with the current word highlighted and the ring adds one as each recitation ends.
- Given a devotee opens a mantra that has no recording, When the devotee views the practice modes, Then only Count is shown.
- Given a devotee has finished 40 recitations and paused part-way through the next one, When the devotee leaves Chant along, Then 40 repetitions are saved to My practice.
- Given a devotee started Chant along and no recitation has finished yet, When the devotee leaves the screen, Then nothing is saved and My practice is unchanged.
- Given a devotee chose Stop after 108 and 107 recitations have finished, When the devotee lets the next recitation finish, Then the ring is full, 108 is shown and playback fades out and stops.
- Given a devotee chose Stop after 11, When the devotee lets the eleventh recitation finish, Then playback stops and the screen shows "11 offered" with Finish and save.
- Given a devotee chose Keep going, When the devotee lets 216 recitations finish, Then the counter shows round 3 and keeps rising until the devotee pauses.
- Given a devotee locks the phone while Chant along is playing, When the devotee unlocks it five minutes later, Then the count includes every recitation finished while locked and the lock screen showed the mantra name with play and pause.
- Given a devotee is in Chant along and a phone call arrives part-way through a recitation, When the devotee returns after the call, Then playback is paused, the count is unchanged and the cut-off recitation is not added.
- Given a devotee switches the text view to IAST while Chant along is playing, When the devotee looks at the mantra, Then it is shown in IAST and playback and the count carry on without a break.
- Given a devotee opened Chant along from a puja step that asks for 21 repetitions, When the devotee lets the 21st recitation finish, Then playback stops, 21 repetitions are added to My practice and the devotee is back on the same puja step.
- Given a devotee opens the Gaṇeśa mūla mantra, When the devotee views the practice modes, Then Chant along is shown first, Count second, and Listen is not offered.
- Given a devotee opens Chant along for the mūla mantra, whose usual count is 108, When the devotee views Stop after, Then 108 is already chosen and 11, 21, 1,008 and Keep going are offered.

#### Out of Scope
- Read and Listen for texts (US-01.2)
- Count mode (US-01.1)
- Choosing a sankalpam for the session (US-02.1)


## US-02.1 · Devotee chooses the one sankalpam a practice session counts towards

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-02.1 | low | Sannidhi | Sankalpam | Requirement 02 – Counts for one sankalpam per session |

#### Description
As a devotee, I want to choose which of my sankalpams a session counts towards, so that each sankalpam total grows only from the practice I meant for it.

Today one count cannot be tied to a particular vow, so a person keeping two vows mixes them.

Terms: sankalpam — a personal vow or a group pārāyaṇam with an optional total and days; Counts for — the control above the practice modes that names where the count goes; My practice — every reading and repetition the devotee has done, from any mode.

Dependencies: active sankalpams — delivered by US-03.1 and US-08.2; practice modes — delivered by US-01.1, US-01.2 and US-01.3.

Sizing: one sprint; covers the Counts for control, where a saved count goes, and the one shared count for japa and sankalpam. The practice modes themselves are excluded.

Done when: every acceptance criterion passes and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 02 – Counts for one sankalpam per session

Source: Sankalpam flow v6.2, Requirement 02.

The flow requires that "A session never counts for two sankalpams at once." and "Japa and sankalpam share one count: every reading or repetition from any mode is one practice entry, and a sankalpam total is the sum of the entries made for it."

Outcome: today counts cannot be tied to a vow; after this story every session goes to My practice and to at most one sankalpam, from one shared count.

Success measure: sessions counted into more than one sankalpam; baseline: not tracked today; target: zero.

Priority: Must — sankalpam and group totals depend on it.

#### Acceptance Criteria
- Given a devotee has an active sankalpam for the mūla mantra, When the devotee saves a session of 54 with Counts for set to that sankalpam, Then the sankalpam total and My practice both rise by 54.
- Given a devotee has two active sankalpams for the same text, When the devotee opens Counts for, Then only one of them can be selected at a time.
- Given a devotee leaves Counts for on Just my practice, When the devotee saves a session of 54, Then only My practice rises by 54 and no sankalpam total changes.
- Given a devotee opens a text from one of their sankalpam cards, When the devotee views the practice screen, Then Counts for is already set to that sankalpam.
- Given a devotee's chosen sankalpam was completed in another session minutes ago, When the devotee saves this session, Then the count goes to My practice only and the screen says the sankalpam is complete.
- Given a devotee chanted 108 with Chant along on Just my practice and then counted 54 for a mūla mantra sankalpam, When the devotee opens that sankalpam page, Then the sankalpam total shows 54 and My practice shows 162 for today.

#### Out of Scope
- Practice modes themselves (US-01.1, US-01.2, US-01.3)
- Starting a sankalpam (US-03.1)


## US-03.1 · Devotee starts a personal sankalpam with an optional total, days and start date

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-03.1 | high | Sannidhi | Sankalpam | Requirement 03 – Start a personal sankalpam |

#### Description
As a devotee, I want to start a personal sankalpam for a text with a total, a number of days and a start date of my choosing, so that I can keep a vow such as one lakh repetitions in 41 days alone.

Today a vow is kept in memory or on paper and its progress is easy to lose.

Terms: personal sankalpam — a vow kept by one devotee on the phone; total to reach — the target count, optional; māsam — the lunar month from the calendar; intent — a private reason for the vow.

Dependencies: texts from the content build — exists; calendar for māsam end dates — exists; My sankalpams list — delivered by US-04.1.

Sizing: one sprint; covers the New sankalpam form for Just me. Starting a group is excluded.

Done when: every acceptance criterion passes offline on a real phone and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 03 – Start a personal sankalpam

Source: Sankalpam flow v6.2, Requirement 03.

The flow requires that "A devotee starts a personal sankalpam by choosing a text, an optional total to reach, optional days, a start date and an optional private intent." and "The start date defaults to today and can be a future date such as Ugadi or a festival."

Outcome: today vows live on paper; after this story a vow is created in one form and tracked on the phone.

Success measure: personal sankalpams started; baseline: none today; target: a devotee can start one in under five fields with only the text required.

Priority: Must — the solo vow is the first job elders asked for.

#### Acceptance Criteria
- Given a devotee has chosen the Gaṇeśa mūla mantra, When the devotee sets a total of 1,00,000 and 41 days starting today and begins, Then the sankalpam is listed under Active with 0 so far, of 1,00,000 and day 1 of 41.
- Given a devotee has not chosen a text, When the devotee tries to begin, Then Begin stays disabled and the form shows "Choose what you will chant".
- Given a devotee enters a total of 0, When the devotee tries to begin, Then the total is not accepted and the field shows "Enter a number above zero, or leave it empty".
- Given a devotee has a sankalpam that starts on a future date, When the devotee opens Counts for on that text before the date, Then that sankalpam is not offered.
- Given a devotee leaves the total and the days empty, When the devotee begins, Then the sankalpam is listed under Active with only the total so far.
- Given a devotee picks Ugadi, Wed 7 Apr 2027, as the start date, When the devotee begins, Then the card under Active shows "Starts Wed 7 Apr".
- Given a devotee already has an active sankalpam for the same text, When the devotee begins another one, Then both are listed separately under Active.
- Given a devotee's phone has no internet, When the devotee begins a personal sankalpam, Then it is listed under Active and no connection message is shown.
- Given a devotee is choosing days, When the devotee opens the day choices, Then the choices shown are 11, 21, 41, one māsam and no end.
- Given a devotee writes the intent "for a peaceful home", When the devotee begins, Then the intent is shown only on that sankalpam's page on this phone.
- Given a devotee opens the start date picker, When the devotee looks for a date before today, Then no past date is offered.

#### Out of Scope
- Starting a group (US-07.1)
- Changing the total or days later (US-04.4)


## US-04.1 · Devotee sees active sankalpams at a glance on My sankalpams and Home

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-04.1 | medium | Sannidhi | Sankalpam | Requirement 04 – See and manage my sankalpams |

#### Description
As a devotee, I want to see every sankalpam I am keeping in one calm list, so that I can pick up any of them in one step without being reminded of what I did not do.

Today there is no single place that shows a person's vows and group pārāyaṇams together.

Terms: Active — sankalpams still running or starting later; Completed — sankalpams that ended; sādhana bar — the bar at the bottom of Home.

Dependencies: personal sankalpams — delivered by US-03.1; group membership — delivered by US-08.2; Completed tab — delivered by US-05.1.

Sizing: one sprint; covers the Active tab and the Home sādhana bar. The sankalpam page is excluded.

Done when: every acceptance criterion passes and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 04 – See and manage my sankalpams

Source: Sankalpam flow v6.2, Requirement 04.

The flow requires that "Each active card shows the text, Just me or the group name, the total so far, the target if set and day N of M if days are set." and "There is no pause; the devotee practises when they choose and the app never shows gaps."

Outcome: today vows are scattered; after this story they sit in one list reachable from Home.

Success measure: words or marks suggesting missed practice on these screens; baseline: not applicable today; target: zero.

Priority: Must — the sādhana bar is the main way back into practice.

#### Acceptance Criteria
- Given a devotee has a personal sankalpam and a group sankalpam, When the devotee opens My sankalpams, Then the Active tab lists both with the text, Just me or the group name, the total so far, the target if set and day N of M if days are set.
- Given a devotee has no active sankalpams, When the devotee opens My sankalpams, Then the Active tab shows only Begin a sankalpam and no empty counters.
- Given a devotee has not practised for five days, When the devotee opens My sankalpams, Then no card shows missed days, streaks or gaps.
- Given a devotee's sankalpam is on its last day, When the devotee opens My sankalpams, Then its card shows day 41 of 41 and stays under Active until that day ends.
- Given a devotee has exactly one active sankalpam, When the devotee opens Home, Then the sādhana bar shows its title alone.
- Given a devotee has a sankalpam that starts on a future date, When the devotee opens My sankalpams, Then its card shows "Starts" with the date instead of a day count.
- Given a group the devotee belongs to was closed by its organiser, When the devotee opens My sankalpams, Then that group is not under Active and is listed under Completed as closed by the organiser.
- Given a devotee has three active sankalpams, When the devotee opens Home, Then the sādhana bar shows the first title and "and 2 more" and opens My sankalpams.

#### Out of Scope
- The sankalpam page and its practice days (US-04.2)
- The Completed tab and completion (US-05.1)


## US-04.2 · Devotee sees this month's practice days on a sankalpam page, never the gaps

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-04.2 | medium | Sannidhi | Sankalpam | Requirement 04 – See and manage my sankalpams |

#### Description
As a devotee, I want to see the days I practised this month for a sankalpam with the count on each day, so that I feel the vow growing without being shown the days I did not practise.

Today there is no record of which days a vow was kept.

Terms: sankalpam page — the page opened from a card under My sankalpams; This month — the list of civil dates this month with practice.

Dependencies: practice entries — delivered by US-01.1, US-01.2 and US-01.3; cards — delivered by US-04.1.

Sizing: one sprint; covers the page header, total and This month. Editing and adding by hand are excluded.

Done when: every acceptance criterion passes and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 04 – See and manage my sankalpams

Source: Sankalpam flow v6.2, Requirement 04.

The flow requires that "The sankalpam page shows this month's days with practice and the count on each day, and never shows days without practice."

Outcome: today there is no history; after this story the month's practice days are visible on one page.

Success measure: days without practice shown anywhere on the page; baseline: not applicable; target: zero.

Priority: Should — it gives meaning to the total without adding guilt.

#### Acceptance Criteria
- Given a devotee practised on 3, 7 and 11 September for a sankalpam, When the devotee opens that sankalpam page, Then This month lists 11, 7 and 3 September with each day's count, newest first.
- Given a devotee practised on 3 and 7 September only, When the devotee opens that sankalpam page, Then 4, 5 and 6 September are not listed and no zero count is shown.
- Given a devotee has not practised this month for a sankalpam, When the devotee opens that sankalpam page, Then the This month section is not shown and the total so far is shown.
- Given a devotee practised on the first and on the last day of the month, When the devotee opens that sankalpam page, Then both days are listed under This month.
- Given a devotee practised on 31 August and 1 September, When the devotee opens that sankalpam page on 2 September, Then only 1 September is listed under This month.
- Given a devotee added a count by hand for a day, When the devotee opens that sankalpam page, Then that day is labelled added by hand.
- Given a devotee recorded practice in India and then travelled abroad, When the devotee opens that sankalpam page, Then each entry stays on the date shown when it was recorded.

#### Out of Scope
- Adding a count by hand (US-04.3)
- Changing the target or days (US-04.4)


## US-04.3 · Devotee adds practice done without the phone for today or the last 7 days

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-04.3 | medium | Sannidhi | Sankalpam | Requirement 04 – See and manage my sankalpams |

#### Description
As a devotee, I want to add a count by hand for today or a recent day, so that practice done at a temple or without the phone still counts towards my sankalpam.

Today practice done away from the phone is lost from the total.

Terms: added by hand — a count entered as a number with a date instead of counted live; it is labelled so on the sankalpam page.

Dependencies: sankalpam page — delivered by US-04.2; group totals — delivered by US-09.1.

Sizing: one sprint; covers the Add by hand sheet for personal and group sankalpams. Editing the target is excluded.

Done when: every acceptance criterion passes and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 04 – See and manage my sankalpams

Source: Sankalpam flow v6.2, Requirement 04.

The flow requires that "A devotee can add a count by hand for today or up to 7 days back, marked added by hand."

Outcome: today offline practice is lost; after this story it is added to the right day and labelled.

Success measure: hand-added counts outside the allowed days; baseline: not applicable; target: zero.

Priority: Should — elders often chant with a physical mala and enter the count later.

#### Acceptance Criteria
- Given a devotee read the aṣṭottaram 3 times yesterday without the phone, When the devotee adds 3 by hand for yesterday, Then the sankalpam total rises by 3 and yesterday is listed under This month labelled added by hand.
- Given a devotee opens Add by hand, When the devotee opens the date choices, Then no date older than 7 days back is offered.
- Given a devotee enters a count of 0, When the devotee tries to save, Then Save stays disabled.
- Given a devotee picks the date exactly 7 days back, When the devotee saves a count of 108, Then the count is listed under that date labelled added by hand.
- Given a devotee opens the date choices, When the devotee looks for tomorrow, Then no future date is offered.
- Given a devotee's sankalpam started 3 days ago, When the devotee opens the date choices, Then only the start date and later are offered.
- Given a devotee's sankalpam was completed while the sheet was open, When the devotee saves the count, Then the count goes to My practice only and the sheet says the sankalpam is complete.
- Given a devotee in a group adds 2 by hand for today on the group sankalpam, When the devotee saves, Then the group total rises by 2 and the devotee's count today on the group page shows 2.

#### Out of Scope
- Changing the target or days (US-04.4)
- Counting live (US-01.1, US-01.2, US-01.3)


## US-04.4 · Devotee changes the total or days of a personal sankalpam at any time

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-04.4 | low | Sannidhi | Sankalpam | Requirement 04 – See and manage my sankalpams |

#### Description
As a devotee, I want to change the total or the number of days of my personal sankalpam, so that a vow can grow or be eased without starting it again.

Today a changed vow means starting over and losing the count.

Terms: total — the target count; days — the length from the start date; options — the actions on the sankalpam page.

Dependencies: sankalpam page — delivered by US-04.2; completion — delivered by US-05.1; organiser changes for groups — delivered by US-10.1.

Sizing: one sprint; covers the Change total or days sheet for personal sankalpams. Group changes are excluded.

Done when: every acceptance criterion passes and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 04 – See and manage my sankalpams

Source: Sankalpam flow v6.2, Requirement 04.

The flow requires that "A devotee can change the target or the days of a personal sankalpam at any time; for a group only the organiser can."

Outcome: today a vow cannot change; after this story total and days change while the count is kept.

Success measure: counts lost when a vow changes; baseline: every count today; target: zero.

Priority: Should — vows are often extended at festivals.

#### Acceptance Criteria
- Given a devotee's personal sankalpam has a total of 1,00,000, When the devotee changes the total to 1,25,000 and saves, Then the card shows of 1,25,000 and the total so far is unchanged.
- Given a devotee's sankalpam has 12,960 so far, When the devotee enters a total of 10,000, Then the total is not accepted and the sheet shows "Already 12,960 offered. Choose a higher total or mark it complete".
- Given a devotee's sankalpam has 12,960 so far, When the devotee sets the total to exactly 12,960, Then the sankalpam completes with the gentle completion message.
- Given a devotee's sankalpam has no days set, When the devotee adds 41 days, Then day N counts from the original start date.
- Given a devotee who is a member but not the organiser opens a group sankalpam page, When the devotee opens its options, Then no option to change the total or days is shown.
- Given a devotee opens the options of an active sankalpam, When the devotee views them, Then they are Add by hand, Change total or days, Mark complete and Leave, with no pause.

#### Out of Scope
- Changes to a group by its organiser (US-10.1)
- Completion and the Completed tab (US-05.1)


## US-05.1 · Devotee completes a sankalpam gently and finds it under Completed

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-05.1 | high | Sannidhi | Sankalpam | Requirement 05 – Complete a sankalpam |

#### Description
As a devotee, I want to see a quiet completion moment when a sankalpam ends and keep it under Completed, so that a finished vow is honoured and every vow I kept stays with me.

Today a finished vow leaves no record.

Terms: completion message — a short full-screen moment showing the total offered; Completed — the second tab of My sankalpams; extend — adding days to a sankalpam that ended by days.

Dependencies: active cards — delivered by US-04.1; group completion — delivered by US-09.1; days from the calendar — exists.

Sizing: one sprint; covers completion by total, by days and by choice, the Completed tab and extend. Group closure by an organiser is excluded.

Done when: every acceptance criterion passes and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 05 – Complete a sankalpam

Source: Sankalpam flow v6.2, Requirement 05.

The flow requires that "When the total reaches the target, a gentle completion message shows the total offered and the sankalpam moves to Completed." and "When the days end before the target, the sankalpam moves to Completed with the total reached and an option to extend."

Outcome: today finished vows vanish; after this story each one is marked, kept and can be extended.

Success measure: completion screens with words of shortfall or failure; baseline: not applicable; target: zero.

Priority: Must — completing a vow is the moment the whole module exists for.

#### Acceptance Criteria
- Given a devotee's sankalpam has 99,946 of 1,00,000, When the devotee saves a session of 54, Then "Sankalpam complete · 1,00,000 offered" is shown and the sankalpam is listed under Completed.
- Given a devotee's sankalpam has 99,945 of 1,00,000, When the devotee saves a session of 54, Then it stays under Active with 99,999 and no completion message is shown.
- Given a devotee's sankalpam has no total and no days, When the devotee saves any count, Then it stays under Active.
- Given a devotee's sankalpam ended by days with 60,000 of 1,00,000, When the devotee opens Completed, Then the card shows 60,000 offered with no word of shortfall or failure.
- Given a devotee's sankalpam reached its last day of 41, When the devotee opens the app the next morning, Then it is listed under Completed with the total reached and an Extend option.
- Given a devotee's sankalpam has 99,950 of 1,00,000, When the devotee saves a session of 108, Then it completes and the Completed card shows 1,00,058 offered.
- Given a devotee has an active sankalpam, When the devotee marks it complete and confirms, Then it is listed under Completed with the total so far.
- Given a devotee's sankalpam ended by days on day 41, When the devotee extends it by 21 days, Then it returns to Active as day 42 of 62 with the same total.
- Given a devotee has a completed sankalpam, When the devotee practises its text again, Then new counts go to My practice only and the completed total stays the same.
- Given a devotee's phone was off through the last day, When the devotee opens the app days later, Then the sankalpam is listed under Completed with its last day as the end date and no message about the time away.
- Given a devotee has a completed personal sankalpam of Gaṇeśa aṣṭottaram with 108 readings over 11 days, When the devotee chooses Start again, Then New sankalpam opens with the same text, total and days already filled and Today as the start.
- Given a devotee was part of a group pārāyaṇam that ended, When the devotee opens Completed, Then it is listed with the group name, the group total and the devotee's own count.

#### Out of Scope
- A group closed early by its organiser (US-10.1)
- Group target completion for all members (US-09.1)


## US-06.1 · Devotee signs in with Google only when starting or joining a group

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-06.1 | low | Sannidhi | Sankalpam | Requirement 06 – Sign in for groups only |

#### Description
As a devotee, I want to be asked to sign in only when I start or join a group, so that the rest of the app stays open to me without an account and no one can ask me for a phone number or OTP.

Today scams target elders through OTPs and phone numbers, so any sign-in is a risk.

Terms: sign-in step — a single screen offering Continue with Google; signed in — the phone holds the Google account link for groups.

Dependencies: Google sign-in on Android — exists; group start and join — delivered by US-07.1 and US-08.2.

Sizing: one sprint; covers the sign-in step and where it appears. Account deletion is excluded.

Done when: every acceptance criterion passes and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 06 – Sign in for groups only

Source: Sankalpam flow v6.2, Requirement 06.

The flow requires that "Starting or joining a group needs Google sign-in; everything else works without an account." and "There is no OTP, password or phone number anywhere."

Outcome: today any account is a scam surface; after this story an account exists only for group members and holds no phone number.

Success measure: screens outside groups that ask for sign-in; baseline: not applicable; target: zero.

Priority: Must — groups cannot keep shared counts without it, and elders must stay safe.

#### Acceptance Criteria
- Given a devotee who has never signed in, When the devotee chooses Join on a group, Then a sign-in step shows Continue with Google and the line "No phone number or OTP is needed".
- Given a devotee who is not signed in, When the devotee uses Puja, Calendar, Stories or a personal sankalpam, Then no sign-in is asked for.
- Given a devotee signed in once, When the devotee joins a second group, Then no sign-in step appears.
- Given a devotee cancels the Google sign-in, When the devotee returns to the group card, Then the group is not joined and nothing else changes.
- Given a devotee's Google access to the app was removed, When the devotee opens a group page, Then the page asks to sign in again and personal practice still shows every count.
- Given a devotee completes the sign-in step, When the devotee looks at what was asked, Then no password, OTP or phone number was requested.

#### Out of Scope
- Account and data deletion (Profile, Delete my data)
- Starting a group (US-07.1) and joining one (US-08.2)


## US-07.1 · Group organiser starts a group pārāyaṇam that others can join

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-07.1 | high | Sannidhi | Sankalpam | Requirement 07 – Start a group |

#### Description
As a group organiser, I want to start a group pārāyaṇam for a text with a purpose, dates and an optional target, so that relatives and friends anywhere can chant together towards one shared count.

Today elders run these groups on messaging apps and add counts by hand.

Terms: group pārāyaṇam — a group chanting one text towards a shared total; purpose — a short line on why the group exists; Open — listed on Discover; Invite only — joined with an invite code; invite code — a short code the organiser shares.

Dependencies: sign-in — delivered by US-06.1; texts — exists; cities from the master list — exists; group page — delivered by US-09.2.

Sizing: one sprint; covers the Start a group form and the invite code. Changing the group later is excluded.

Done when: every acceptance criterion passes with two phones and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 07 – Start a group

Source: Sankalpam flow v6.2, Requirement 07.

The flow requires that "An organiser starts a group by choosing a text, a group name, a purpose of up to 140 characters without links or numbers, a start date, a length and an optional target." and "The group shows the organiser's display name and city, never an exact location."

Outcome: today group counts are tallied by hand in chats; after this story a group keeps its own total.

Success measure: groups created that expose a location finer than city; baseline: not applicable; target: zero.

Priority: Must — the elder japa groups are a primary user need.

#### Acceptance Criteria
- Given a signed-in group organiser chose the Gaṇeśa aṣṭottaram, When the organiser creates Kārtika Aṣṭottaram with a purpose, start today, 41 days, a target of 1,008 readings, Open and Hyderabad, Then the group page opens with the organiser as the first member and the group is listed on Discover under Hyderabad.
- Given a group organiser writes a purpose that contains a link, When the organiser tries to create the group, Then it is not created and the purpose field shows "Purpose can't have links or numbers".
- Given a group organiser writes a purpose of 141 characters, When the organiser types the last character, Then the field keeps 140 characters and shows 140 of 140.
- Given a group organiser has not written a group name, When the organiser tries to create the group, Then Create stays disabled.
- Given a group organiser writes a purpose of exactly 140 characters, When the organiser creates the group, Then the full purpose is shown on the group page.
- Given a group organiser picks a start date of Ugadi, Wed 7 Apr 2027, When the organiser creates the group, Then the group page and Discover show "Starts Wed 7 Apr" and no counts are accepted before then.
- Given a group organiser chooses Invite only, When the organiser creates the group, Then it is not listed on Discover and the group page shows its invite code with a Share action.
- Given the organiser's phone loses internet while creating, When the organiser creates the group, Then it is not created, every value stays in the form and the form shows "No internet. Try again when connected".
- Given a group organiser chose the mūla mantra, When the organiser creates the group, Then the group total is labelled repetitions.
- Given a group organiser knows another open group already chants the same text in Hyderabad, When the organiser creates the group, Then it is created with no warning.
- Given a group organiser set the city to Hyderabad, When the organiser creates the group, Then the group shows Hyderabad and never a street, area or map pin.

#### Out of Scope
- Changing the target or days, removing members, closing (US-10.1)
- Handing the group to another organiser, out of scope for now


## US-08.1 · Devotee discovers open group pārāyaṇams by city

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-08.1 | medium | Sannidhi | Sankalpam | Requirement 08 – Discover and join a group |

#### Description
As a devotee, I want to see open group pārāyaṇams around the world by city, so that I can find one to join for the text and dates that suit me.

Today a person only hears of a group through word of mouth.

Terms: Discover — the third tab of Sankalpam; globe — the picture of cities with open groups at the top of Discover; Starting soon, Near me, Running — the filters.

Dependencies: open groups — delivered by US-07.1; internet — needed for Discover only; joining — delivered by US-08.2.

Sizing: one sprint; covers Discover, its filters and group cards. Joining is excluded.

Done when: every acceptance criterion passes and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 08 – Discover and join a group

Source: Sankalpam flow v6.2, Requirement 08.

The flow requires that "Discover shows open groups worldwide by city, with filters Starting soon, Near me and Running." and "Member names and counts are visible to members only; Discover shows group totals only."

Outcome: today groups are found by chance; after this story every open group is visible by city.

Success measure: member names shown on Discover; baseline: not applicable; target: zero.

Priority: Should — Discover connects families abroad with groups at home.

#### Acceptance Criteria
- Given a devotee is signed out and open groups exist in Hyderabad and Chennai, When the devotee opens Discover, Then the globe marks both cities and each group card shows the name, text, purpose, organiser and city, people joined, day N of M and the total.
- Given a devotee is not a member of an invite-only group, When the devotee opens Discover, Then that group is not listed anywhere.
- Given a devotee had seen a group that its organiser has since closed, When the devotee opens Discover, Then that group is not listed.
- Given a devotee sees one group starting tomorrow and another that started today, When the devotee chooses Starting soon, Then only the group starting tomorrow is listed and the other is under Running.
- Given the devotee's city is Hyderabad, When the devotee chooses Near me, Then groups in Hyderabad are listed first, then others in Telangana.
- Given a devotee chose a filter that no open group matches, When the devotee views Discover, Then it shows "No groups here yet" with Start a group.
- Given the devotee's phone has no internet, When the devotee opens Discover, Then it shows "Discover needs internet. Your own practice works offline." and personal sankalpams still open.
- Given a devotee is not a member of a group that has members, When the devotee views its card on Discover, Then only people joined and the total are shown and no member names.

#### Out of Scope
- Joining a group (US-08.2)
- Starting a group (US-07.1)


## US-08.2 · Devotee joins a group with an intent they show or keep private

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-08.2 | medium | Sannidhi | Sankalpam | Requirement 08 – Discover and join a group |

#### Description
As a devotee, I want to join a group with a display name and an intent that I choose to show or keep private, so that I can chant with others while sharing only what I want.

Today joining a chanting group means sharing a phone number in a chat.

Terms: display name — the name other members see; intent — an optional reason for joining; Show to the group / Keep private — the member's choice for the intent.

Dependencies: sign-in — delivered by US-06.1; Discover — delivered by US-08.1; invite code — delivered by US-07.1.

Sizing: one sprint; covers the Join sheet and the invite-code path. Practising in the group is excluded.

Done when: every acceptance criterion passes with two phones and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 08 – Discover and join a group

Source: Sankalpam flow v6.2, Requirement 08.

The flow requires that "A member joins with a display name and an optional intent, and chooses Show to the group or Keep private." and "A full group shows that it is full and cannot be joined."

Outcome: today joining exposes a phone number; after this story a member shares only a display name and, if chosen, an intent.

Success measure: joins that expose a phone number; baseline: every join today; target: zero.

Priority: Must — joining is how the elder groups grow.

#### Acceptance Criteria
- Given a signed-in devotee views an open group, When the devotee joins as Lakshmi with the intent "for my family's peace" and Keep private, Then the group is listed under Active and members see Lakshmi but not the intent.
- Given a signed-in devotee chose Show to the group, When the devotee joins, Then members see the intent under Lakshmi on the group page.
- Given a devotee finds a group that has 200 members, When the devotee views it, Then Join is disabled and the card shows "This group is full".
- Given a devotee enters an invite code that matches no group, When the devotee submits it, Then nothing is joined and the sheet shows "This code doesn't match a group".
- Given a devotee finds a group that has 199 members, When the devotee joins, Then the devotee is the 200th member and the card then shows "This group is full" to others.
- Given a devotee leaves the intent empty, When the devotee joins, Then no intent is stored or shown.
- Given a devotee is already a member, When the devotee views the group card, Then it shows Joined and no Join action.
- Given a devotee has the join sheet open while the organiser closes the group, When the devotee joins, Then nothing is joined and the sheet shows "This group has closed".
- Given a devotee has a valid invite code for an invite-only group, When the devotee submits it, Then the group summary opens with Join.

#### Out of Scope
- Practising in the group (US-09.1)
- Leaving the group (US-10.2)


## US-09.1 · Group member adds each session to the group total, even when offline

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-09.1 | medium | Sannidhi | Sankalpam | Requirement 09 – Practise together |

#### Description
As a group member, I want to have my practice add to the group total as well as my own, so that the group's shared vow grows with every reading I do.

Today group totals are added up by hand from messages and often go wrong.

Terms: group total — the sum of all members' counts for the group; pending — a count saved on the phone that has not reached the group yet.

Dependencies: Counts for — delivered by US-02.1; joining — delivered by US-08.2; internet — needed only to send counts.

Sizing: one sprint; covers adding to the group, offline sending and group completion. The group page layout is excluded.

Done when: every acceptance criterion passes with two phones, one offline, and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 09 – Practise together

Source: Sankalpam flow v6.2, Requirement 09.

The flow requires that "When Counts for is set to a group, the count is added to the member's own practice and to the group total." and "Counts made offline are kept on the phone and added to the group when the phone is back online."

Outcome: today totals are tallied by hand; after this story the group total adds itself.

Success measure: counts lost between a member's phone and the group total; baseline: not tracked today; target: zero.

Priority: Must — the group total is the reason the group exists.

#### Acceptance Criteria
- Given a group member set Counts for to Kārtika Aṣṭottaram, When the member saves a session of 3 readings, Then the group total and the member's own practice both rise by 3.
- Given a group member left Counts for on Just my practice, When the member saves a session of 3, Then the group total is unchanged.
- Given a group member's group starts on a future date, When the member opens Counts for, Then that group is not offered.
- Given a group member's group needs 2 more readings to reach its target of 1,008, When a group member saves a session of 2, Then every member sees "Target reached · 1,008 readings" on the group page.
- Given a group member's group is on its last day, When the member saves a session, Then it is added; on the day after, the group is not offered in Counts for.
- Given a group member is in a mantra group, When the member saves a session of 108 counts, Then the group total rises by 108 repetitions.
- Given a group member is offline, When the member saves a session of 54, Then My practice shows 54 at once and the group page shows "Will add to the group when online" until it is sent.
- Given a group member was removed while offline with a pending count, When the member's phone comes back online, Then the pending count stays in My practice and is not added to the group.

#### Out of Scope
- The group page and today's readers (US-09.2)
- Organiser tools (US-10.1)


## US-09.2 · Group member sees today's readers and the group total, never ranked

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-09.2 | medium | Sannidhi | Sankalpam | Requirement 09 – Practise together |

#### Description
As a group member, I want to see who read today and how the group total is growing, so that I feel part of the gathering without anyone being compared or ranked.

Today members only see counts scattered through chat messages.

Terms: today's readers — members who added a count today, with that count; bubble — a small brief note on the group page when another member adds a count.

Dependencies: group counts — delivered by US-09.1; join and intent choice — delivered by US-08.2.

Sizing: one sprint; covers the group page for members. Organiser tools are excluded.

Done when: every acceptance criterion passes with two phones and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 09 – Practise together

Source: Sankalpam flow v6.2, Requirement 09.

The flow requires that "The group page shows the purpose, day N of M, the group total and today's readers with their counts, never ranked."

Outcome: today members piece the picture together from chat; after this story one page shows the gathering.

Success measure: rank positions, medals or top labels on the page; baseline: not applicable; target: zero.

Priority: Must — this page is the virtual gathering.

#### Acceptance Criteria
- Given a group has 38 members, When a group member opens the group page, Then it shows the purpose, run by and city, day N of M, the group total and today's readers with each person's count today.
- Given several members read today, When a group member opens the group page, Then today's readers are listed by when they last added, with no positions, medals or top labels.
- Given some members have not read today, When a group member opens the group page, Then those members are not listed among today's readers and no "not yet" status is shown.
- Given a group member's group has no readers yet today, When a group member opens the group page, Then the readers area shows "Today's readers will appear here".
- Given one member chose Show to the group for their intent, When a group member opens the group page, Then only that member's intent is shown, under their name.
- Given a group member has the group page open, When another group member adds 3 readings, Then a bubble "Lakshmi · +3" appears briefly and the total rises by 3.
- Given a member left the group, When a group member opens the group page, Then that member's past counts are still in the total and their name is not in the member list.

#### Out of Scope
- Organiser tools (US-10.1)
- Leaving and reporting (US-10.2)


## US-10.1 · Group organiser changes the plan, removes a member or closes the group

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-10.1 | medium | Sannidhi | Sankalpam | Requirement 10 – Organiser tools, leaving and reporting |

#### Description
As a group organiser, I want to change the target or days, remove a member or close the group, so that I can keep the gathering safe and on the plan the members agreed.

Today an organiser has no way to remove someone from a chanting chat without leaving it.

Terms: remove — take a member out of the group; close — end the group for everyone; Closed by organiser — the label on a closed group.

Dependencies: group page — delivered by US-09.2; completion — delivered by US-05.1.

Sizing: one sprint; covers the organiser's options. Handing the group to another organiser is excluded.

Done when: every acceptance criterion passes with two phones and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 10 – Organiser tools, leaving and reporting

Source: Sankalpam flow v6.2, Requirement 10.

The flow requires that "The organiser can change the target or the days, remove a member or close the group."

Outcome: today organisers cannot moderate; after this story they can adjust, remove and close.

Success measure: past counts lost when a member is removed or a group closes; baseline: not applicable; target: zero.

Priority: Must — groups open to anyone need someone who can remove a member.

#### Acceptance Criteria
- Given a group organiser's group has a target of 1,008, When the organiser changes it to 2,016 and saves, Then every member sees of 2,016 on the group page.
- Given a group organiser's group has 1,200 so far, When the organiser enters a target of 1,008, Then it is not accepted and the sheet shows "Already 1,200 offered. Choose a higher total or close the group".
- Given a group organiser opens the options, When the organiser looks for Leave, Then only Close group is offered to the organiser.
- Given a group organiser's group is on day 30 of 41, When the organiser changes the days to 30, Then the group shows day 30 of 30 and moves to Completed for every member when the day ends.
- Given a group organiser's group is on day 41 of 41, When the organiser extends it to 62 days, Then the group stays under Active as day 41 of 62.
- Given a group organiser removes a member, When the organiser confirms, Then that member's past counts stay in the group total and the group is listed under that member's Completed.
- Given a group organiser's members are practising offline, When the organiser closes the group, Then their pending counts stay in their own practice and the group shows Closed by organiser.
- Given a group organiser closes the group, When the organiser confirms, Then it is listed under Completed for every member and is no longer on Discover.

#### Out of Scope
- Handing the group to another organiser, out of scope for now
- Members leaving and reporting (US-10.2)


## US-10.2 · Devotee leaves a group or reports a public group, with no chat anywhere

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-10.2 | medium | Sannidhi | Sankalpam | Requirement 10 – Organiser tools, leaving and reporting |

#### Description
As a devotee, I want to leave a group whenever I choose and report a public group that is not about chanting, so that the gatherings stay safe and nobody is held in one.

Today leaving a chat group is visible to everyone and there is no way to flag misuse.

Terms: Report — the action on every public group card; reason — one of the reasons listed in the Report sheet.

Dependencies: group page — delivered by US-09.2; Discover — delivered by US-08.1; who reviews reports — owned by business.

Sizing: one sprint; covers Leave, Report and the absence of chat. Handling a report after it is sent is excluded.

Done when: every acceptance criterion passes and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Sankalpam › Requirement 10 – Organiser tools, leaving and reporting

Source: Sankalpam flow v6.2, Requirement 10.

The flow requires that "Any member can leave the group; their past counts stay in the group total." and "There is no chat, photos, private messages, phone numbers or rankings in a group."

Outcome: today there is no safe exit or report; after this story both exist and nothing in a group can carry a message.

Success measure: ways to send a message inside a group; baseline: every chat group today; target: zero.

Priority: Must — safety for elders is a launch condition for groups.

#### Acceptance Criteria
- Given a devotee is a member of a group, When the devotee leaves and confirms, Then the group is listed under Completed with the devotee's own count and their past counts stay in the group total.
- Given a devotee chose Leave, When the devotee cancels the confirmation, Then the devotee is still a member.
- Given a devotee is on a group page, When the devotee looks for a way to message others, Then no chat, photo, private message, phone number or ranking is shown.
- Given a devotee opens Report on a public group, When the devotee views the reasons, Then exactly four are listed: Not about chanting, Hurtful words, Asks for money, Something else.
- Given a devotee left a group, When the devotee joins the same group again, Then the devotee is a member again and their earlier counts are still in the group total.
- Given a devotee who is not signed in views a group on Discover, When the devotee sends a report with a reason, Then "We will look at this group. It stays hidden from your Discover from now on." is shown and the group is hidden from that devotee's Discover.
- Given a devotee's phone is offline, When the devotee sends a report, Then the sheet shows "We'll send this when you're online" and the report is sent later.

#### Out of Scope
- Who reviews reports and what happens next, owned by business
- Organiser removing a member (US-10.1)
