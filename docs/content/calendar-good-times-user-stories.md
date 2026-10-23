# Good times and Find my nakṣatra — user stories

Sannidhi · Calendar module · v1 · 15 Oct 2026

One file for the feature. Part 1 is the flow the stories are written against (the decisions from 15 Oct 2026).
Part 2 is the index: stories, coverage, traceability and open questions. Part 3 holds the 5 stories in the house
user-story template.

Guardrail: the app never spreads negativity. It works out every window of the day but names only the good ones.

---

# Part 1 · Flow rules

**Project Goal**

- Devotees can see the good times of each day for prayer and new beginnings, worked out for their own city.
- Devotees who do not know their nakṣatra can find it from their birth details without the app keeping those details.

**Requirement 01 – Show today's good times**

- The Today panel shows today's good times with their start and end times: Brahma muhūrtam, Abhijit muhūrtam, Amṛta kālam and Sandhyā.
- Each good time is labelled in plain words: Early morning sādhana, Good time to begin, Good time today and Evening lamp.
- Times are worked out from sunrise and sunset in the devotee's saved city, in India and abroad.
- Abhijit muhūrtam is not shown on Wednesdays and no reason is given on screen.
- Amṛta kālam is shown only on days when it falls between sunrise and the next sunrise.

**Requirement 02 – Show only the good**

- Rāhu kālam, yamagaṇḍam, durmuhūrtam and varjyam are worked out only so that good times can leave out any overlap with them.
- Any part of a good time that overlaps a time kept aside is left out of the good time shown.
- The words rāhu kālam, yamagaṇḍam, durmuhūrtam, varjyam, avoid, bad and inauspicious never appear with the good times.
- No red, warning icon or alert colour is used for any time of the day.
- When overlaps leave no good time in a day, the good times section is not shown.

**Requirement 03 – Good times on Puja and Home**

- The Puja page header shows the good time for that puja when the puja falls today.
- On a festival day the Coming up card on Home shows the next good time of the day.
- After the last good time of the day has passed, the Coming up card shows no good time.

**Requirement 04 – Find my nakṣatra**

- A devotee who does not know their nakṣatra can find it from birth date, birth time and birth place, in onboarding and in Profile.
- The app works out the nakṣatra, pada and rāśi on the phone and saves only those three.
- The birth date, time and place are discarded once the result is shown and are never uploaded.
- When the birth time is close to a change of pada or nakṣatra, both possible answers are shown with a note to check the jātakam.
- The devotee can save the result to Profile or leave without saving anything.

**Requirement 05 – My good days this māsam**

- When a nakṣatra is saved, the māsam view marks the devotee's good days this māsam.
- Only good days are marked; other days carry no mark or label.
- Without a saved nakṣatra, no personal good days are shown and the māsam view offers Find my nakṣatra once, quietly.

**Non-Functional Requirements**

- Good times and the nakṣatra finder work offline on the phone.
- Times follow the TTD Āsthāna Siddhānti panchāṅgam and are checked every Ugadi with the tithis.
- The nakṣatra finder uses the same ayanāmśa as the panchāṅgam the Calendar is checked against.
- Text is at least 16 sp, touch targets at least 56 dp, and screens work at 200% font scale.

**Assumptions**

- Whether Profile offers a toggle that shows times families keep aside is not decided yet.
- The choice of ephemeris library waits on a licence check.
- The phase for the nakṣatra finder, Phase 1 or Phase 3, is not decided yet.

---

# Part 2 · Index

**Source:** Good times and nakṣatra flow (decisions of 15 Oct 2026) **Prepared:** 15 Oct 2026 **Stories:** 5 · **Validator:** 5/5 pass, 30/30 flow rules traced

| Story | Title | Complexity | ACs |
|---|---|---|---|
| US-01.1 | Devotee sees today's good times for their own city in plain words | medium | 8 |
| US-02.1 | Devotee sees only the good times of the day, with nothing named as bad | medium | 8 |
| US-03.1 | Devotee sees the good time for today's puja on the puja page and Home | low | 5 |
| US-04.1 | Devotee finds their nakṣatra from birth details the app never keeps | high | 10 |
| US-05.1 | Devotee sees their own good days this māsam, with no other day marked | low | 5 |

## Coverage map
| Story | Positive | Negative | Boundary | Edge | Exception |
|---|---|---|---|---|---|
| US-01.1 | 1 | 2,3 | 4,5 | 6 | 7 |
| US-02.1 | 1 | 2,3 | 4,5 | 6 | 8 |
| US-03.1 | 1 | 2 | 3 | 4 | 5 |
| US-04.1 | 1 | 2,3,4 | 5,6 | 9 | 10 |
| US-05.1 | 1 | 2 | 3 | 4 | 5 |

## Flow traceability
| ID | Flow text | Covered by |
|---|---|---|
| GOAL.01 | Good times of each day for their own city | US-01.1 AC1; US-01.1 AC6 |
| GOAL.02 | Find the nakṣatra without the app keeping birth details | US-04.1 AC1; US-04.1 AC7 |
| R01.01 | Today panel shows the good times with start and end | US-01.1 AC1; US-01.1 AC4 |
| R01.02 | Plain-word labels | US-01.1 AC1; US-01.1 AC4 |
| R01.03 | From sunrise and sunset in the saved city, India and abroad | US-01.1 AC6; US-01.1 AC7; US-01.1 AC2 |
| R01.04 | Abhijit not shown on Wednesdays, no reason given | US-01.1 AC3 |
| R01.05 | Amṛta kālam only when it falls before the next sunrise | US-01.1 AC4; US-01.1 AC5 |
| R02.01 | Times kept aside worked out only to leave out overlaps | US-02.1 AC1; US-02.1 AC4 |
| R02.02 | Overlapping part left out | US-02.1 AC1; US-02.1 AC6 |
| R02.03 | Never-shown words | US-02.1 AC2; US-02.1 AC7 |
| R02.04 | No red, warning icon or alert colour | US-02.1 AC3 |
| R02.05 | No good time left: section not shown | US-02.1 AC5; US-03.1 AC5 |
| R03.01 | Puja page header shows the good time when the puja falls today | US-03.1 AC1; US-03.1 AC2 |
| R03.02 | Coming up card shows the next good time on a festival day | US-03.1 AC4 |
| R03.03 | After the last good time, no good time on the card | US-03.1 AC3 |
| R04.01 | Find from birth date, time and place in onboarding and Profile | US-04.1 AC1; US-04.1 AC2; US-04.1 AC4 |
| R04.02 | Works out on the phone, saves only nakṣatra, pada and rāśi | US-04.1 AC7 |
| R04.03 | Birth details discarded, never uploaded | US-04.1 AC8; US-04.1 AC10 |
| R04.04 | Near a change, both answers with a note | US-04.1 AC5; US-04.1 AC6 |
| R04.05 | Save to Profile or leave without saving | US-04.1 AC7; US-04.1 AC8 |
| R05.01 | Saved nakṣatra marks good days this māsam | US-05.1 AC1; US-05.1 AC5 |
| R05.02 | Only good days marked | US-05.1 AC2 |
| R05.03 | No nakṣatra: no marks, quiet Find my nakṣatra once | US-05.1 AC3; US-05.1 AC4 |
| NFR.01 | Offline | US-01.1 AC8; US-04.1 AC9 |
| NFR.02 | TTD panchāṅgam, checked every Ugadi | Context only — proved by the Ugadi check against the TTD panchāṅgam named in US-01.1 Done when |
| NFR.03 | Same ayanāmśa as the panchāṅgam | Context only — proved by the 20 sample births in US-04.1 Done when |
| NFR.04 | 16 sp, 56 dp, 200% font scale | Context only — proved for every screen by the 200% screenshot tests |
| ASM.01 | Profile toggle for times kept aside not decided | Out of scope — a toggle story is added only if the business chooses it |
| ASM.02 | Ephemeris library waits on a licence check | Out of scope — library choice is owned by business before US-04.1 starts |
| ASM.03 | Phase for the nakṣatra finder not decided | Out of scope — phasing is a planning decision, not a behaviour |

## Open questions (TBC with business)
| # | Story | Question |
|---|---|---|
| 1 | US-02.1 | Offer a quiet Profile toggle "Show times families keep aside", or never show them at all? |
| 2 | US-04.1 | Which ephemeris library, given Swiss Ephemeris is AGPL or paid? |
| 3 | US-04.1 | Is 20 minutes the right window for "close to a change"? |
| 4 | US-04.1 | Phase 1 (onboarding) or Phase 3 (with the full calendar)? |
| 5 | US-05.1 | Which tārābalam rule marks a good day: the classic good tārās only, or with chandrabalam too? |

## Split notes
- Good times split by what is shown (US-01.1), what is left out (US-02.1) and where else it appears (US-03.1).
- The nakṣatra finder (US-04.1) and personal good days (US-05.1) are split so the finder can ship alone.

---

# Part 3 · Stories


## US-01.1 · Devotee sees today's good times for their own city in plain words

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-01.1 | medium | Sannidhi | Calendar | Requirement 01 – Show today's good times |

#### Description
As a devotee, I want to see today's good times for prayer and new beginnings in my own city, so that I can plan a puja or a new start without opening a printed panchāngam.

Today elders look up these times in a printed panchāngam made for one city, and families abroad have no local times at all.

Terms: Today panel — the top of Calendar with today's date, tithi and nakṣatra; good time — one of Brahma muhūrtam (Early morning sādhana), Abhijit muhūrtam (Good time to begin), Amṛta kālam (Good time today) and Sandhyā (Evening lamp); saved city — the state and city chosen in onboarding or Profile.

Dependencies: Calendar Today panel — exists; saved city — exists from onboarding; sunrise and sunset per city — delivered by this story; leaving out overlaps — US-02.1.

Sizing: one sprint; covers working out and showing the four good times in the Today panel. Puja and Home placements are excluded.

Done when: every acceptance criterion passes offline on a real phone in te, hi and en at 200% font scale, times match the TTD panchāṅgam for Hyderabad and one city abroad, and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Calendar › Requirement 01 – Show today's good times

Source: Good times and nakṣatra flow, 15 Oct 2026, Requirement 01.

The flow requires that "The Today panel shows today's good times with their start and end times: Brahma muhūrtam, Abhijit muhūrtam, Amṛta kālam and Sandhyā." and "Times are worked out from sunrise and sunset in the devotee's saved city, in India and abroad."

Outcome: today these times come from a printed panchāngam for one city; after this story the app shows them for the devotee's own city every day.

Success measure: days where a shown good time differs from the TTD panchāṅgam for the same city; baseline: not tracked today; target: zero beyond the tolerance agreed at the Ugadi check.

Priority: Must — the good times are the first thing elders look for in a panchāngam.

#### Acceptance Criteria
- Given a devotee's saved city is Hyderabad on a Friday, When the devotee opens Calendar, Then the Today panel lists Early morning sādhana, Good time to begin and Evening lamp, each with its start and end time.
- Given a devotee has no saved city, When the devotee opens Calendar, Then the Today panel shows "Pick your city to see today's good times" and no times.
- Given a devotee's saved city is Hyderabad on a Wednesday, When the devotee opens Calendar, Then Good time to begin is not listed and no reason is shown for it.
- Given a devotee's Amṛta kālam falls before the next sunrise, When the devotee opens Calendar, Then Good time today is listed with its start and end time.
- Given a devotee's Amṛta kālam does not fall between sunrise and the next sunrise, When the devotee opens Calendar, Then Good time today is not listed and nothing is said about it.
- Given a devotee's saved city is Toronto, When the devotee opens Calendar, Then every good time is shown in Toronto local time from Toronto's sunrise and sunset.
- Given a devotee changes the saved city from Hyderabad to Chennai in Profile, When the devotee opens Calendar again, Then the good times shown are those for Chennai.
- Given a devotee's phone has no internet, When the devotee opens Calendar, Then today's good times are shown with no connection message.

#### Out of Scope
- Leaving out overlaps and the words never shown (US-02.1)
- Good times on the Puja page and Home (US-03.1)


## US-02.1 · Devotee sees only the good times of the day, with nothing named as bad

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-02.1 | medium | Sannidhi | Calendar | Requirement 02 – Show only the good |

#### Description
As a devotee, I want to see only the good times of the day, with any time kept aside quietly left out, so that I can choose when to pray without being told that something will go wrong.

Today panchāngam apps lead with rāhu kālam and warnings in red, which brings fear into a daily act of prayer.

Terms: time kept aside — rāhu kālam, yamagaṇḍam, durmuhūrtam or varjyam, worked out by the app but never shown; overlap — the part of a good time that falls inside a time kept aside.

Dependencies: good times in the Today panel — delivered by US-01.1; panchāṅgam calculations — exists in Calendar; the Profile toggle decision — owned by business.

Sizing: one sprint; covers trimming good times by overlaps, the words never shown and the empty-day rule. A Profile toggle for times kept aside is excluded.

Done when: every acceptance criterion passes on a real phone in te, hi and en, a copy review finds none of the never-shown words, and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Calendar › Requirement 02 – Show only the good

Source: Good times and nakṣatra flow, 15 Oct 2026, Requirement 02.

The flow requires that "Any part of a good time that overlaps a time kept aside is left out of the good time shown." and "The words rāhu kālam, yamagaṇḍam, durmuhūrtam, varjyam, avoid, bad and inauspicious never appear with the good times."

Outcome: today devotees see warnings first; after this story they see only the good times and the rest is left out without comment.

Success measure: never-shown words or warning colours on Calendar, Puja and Home; baseline: not applicable; target: zero.

Priority: Must — the app's first rule is that it never spreads negativity.

#### Acceptance Criteria
- Given a devotee's Good time today overlaps a time kept aside by 20 minutes at its end, When the devotee opens Calendar, Then Good time today is shown ending where the overlap begins.
- Given a devotee opens Calendar on any day, When the devotee reads the Today panel, Then none of the words rāhu kālam, yamagaṇḍam, durmuhūrtam, varjyam, avoid, bad or inauspicious is shown.
- Given a devotee opens Calendar on any day, When the devotee views the good times, Then no red, warning icon or alert colour is shown for any time of the day.
- Given a devotee's good time lies wholly inside a time kept aside, When the devotee opens Calendar, Then that good time is not listed and nothing is said about it.
- Given a devotee's day has no good time left after overlaps, When the devotee opens Calendar, Then the good times section is not shown and the rest of the Today panel is unchanged.
- Given a devotee's Good time today is cut in two by a time kept aside, When the devotee opens Calendar, Then both remaining parts are listed as Good time today with their own start and end times.
- Given a devotee's app language is Telugu, When the devotee opens Calendar, Then the Telugu labels carry none of the never-shown words or their Telugu forms.
- Given a devotee has the Today panel open as midnight passes, When the devotee views the good times again, Then the good times shown are those for the new day, with overlaps left out in the same way.

#### Out of Scope
- Showing the four good times (US-01.1)
- A Profile toggle for times families keep aside, not decided yet


## US-03.1 · Devotee sees the good time for today's puja on the puja page and Home

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-03.1 | low | Sannidhi | Calendar | Requirement 03 – Good times on Puja and Home |

#### Description
As a devotee, I want to see the good time for a puja on its page and on Home on the day it falls, so that I can begin the puja in a good time without opening Calendar.

Today a devotee preparing a festival puja has to find the time separately, often by phoning an elder or a priest.

Terms: Puja page header — the top of the puja page with its date and tithi; Coming up card — the card on Home with today's or the next observance.

Dependencies: good times — delivered by US-01.1 and US-02.1; Puja page and Coming up card — exist.

Sizing: one sprint; covers the line in the Puja page header and the Coming up card. The Calendar Today panel is excluded.

Done when: every acceptance criterion passes on a real phone on a festival date and on an ordinary date, and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Calendar › Requirement 03 – Good times on Puja and Home

Source: Good times and nakṣatra flow, 15 Oct 2026, Requirement 03.

The flow requires that "The Puja page header shows the good time for that puja when the puja falls today." and "On a festival day the Coming up card on Home shows the next good time of the day."

Outcome: today the time is found outside the app; after this story it sits where the devotee starts the puja.

Success measure: puja pages for today's puja without a good time line when one remains; baseline: not applicable; target: zero.

Priority: Should — it saves a step on the busiest mornings of the year.

#### Acceptance Criteria
- Given a devotee opens the Vināyaka Chavithi puja on the festival day, When the devotee views the page header, Then it shows "Good time for this puja today" with the next good time's start and end.
- Given a devotee opens a puja whose next date is in 3 days, When the devotee views the page header, Then no good time line is shown.
- Given a devotee opens Home on a festival day after the last good time has passed, When the devotee views the Coming up card, Then the card shows the festival and no good time.
- Given a devotee opens Home on a festival day during a good time, When the devotee views the Coming up card, Then the card shows that good time with its end time.
- Given a devotee's festival day has no good time left after overlaps, When the devotee opens the puja page, Then the header shows the date and tithi only and nothing is said about the time.

#### Out of Scope
- The Calendar Today panel (US-01.1)
- Leaving out overlaps (US-02.1)


## US-04.1 · Devotee finds their nakṣatra from birth details the app never keeps

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-04.1 | high | Sannidhi | Calendar | Requirement 04 – Find my nakṣatra |

#### Description
As a devotee, I want to find my nakṣatra, pada and rāśi from my birth date, time and place, so that I can use them in puja saṅkalpam and for my good days without the app keeping my birth details.

Today a devotee who does not know their nakṣatra must find their jātakam or ask a priest, and many families abroad have neither.

Terms: nakṣatra — the Moon's star at birth; pada — one quarter of a nakṣatra; rāśi — the Moon's sign at birth; jātakam — the birth chart; Find my nakṣatra — the helper in onboarding and Profile next to "I don't know".

Dependencies: Moon position on the phone — delivered by this story using the Calendar's panchāṅgam engine; Profile nakṣatra and rāśi fields — exist; ephemeris library — owned by business pending a licence check.

Sizing: one sprint; covers the helper, the result, the near-boundary answer and saving to Profile. Personal good days are excluded.

Done when: every acceptance criterion passes offline on a real phone, results match a TTD-aligned panchāṅgam for 20 sample births, and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Calendar › Requirement 04 – Find my nakṣatra

Source: Good times and nakṣatra flow, 15 Oct 2026, Requirement 04.

The flow requires that "The app works out the nakṣatra, pada and rāśi on the phone and saves only those three." and "The birth date, time and place are discarded once the result is shown and are never uploaded."

Outcome: today devotees without a jātakam leave the nakṣatra empty; after this story they can find it in one step and the app keeps only the result.

Success measure: birth dates, times or places stored or uploaded; baseline: not applicable; target: zero.

Priority: Should — it unlocks personal good days and the saṅkalpam fields for families abroad.

#### Acceptance Criteria
- Given a devotee was born on 27 Jan 1992 at 5:00 AM in Hyderabad, When the devotee enters these in Find my nakṣatra, Then the result shows Swāti, Tula rāśi and the pada from the calculation.
- Given a devotee has not entered a birth time, When the devotee asks for the result, Then no result is shown and the form shows "Enter the time of birth, even if it is approximate".
- Given a devotee enters a birth date in the future, When the devotee asks for the result, Then no result is shown and the date field shows "Choose a date that has passed".
- Given a devotee has not chosen a birth place, When the devotee asks for the result, Then no result is shown and the form shows "Choose the town or city of birth".
- Given a devotee's birth time falls within 20 minutes before a change of pada, When the devotee asks for the result, Then both padas are shown with "Close to a change. Check your jātakam to be sure."
- Given a devotee's birth time falls within 20 minutes after a change of nakṣatra, When the devotee asks for the result, Then both nakṣatras are shown with the same note.
- Given a devotee sees the result, When the devotee saves it to Profile, Then Profile shows the nakṣatra, pada and rāśi and no birth date, time or place.
- Given a devotee sees the result, When the devotee leaves without saving, Then Profile is unchanged and nothing from the helper is kept.
- Given a devotee's phone has no internet, When the devotee uses Find my nakṣatra, Then the result is shown with no connection message.
- Given a devotee closes the app while the helper is open, When the devotee opens the app again, Then the helper is empty and no birth details are shown.

#### Out of Scope
- Personal good days this māsam (US-05.1)
- Choosing the ephemeris library, waiting on a licence check


## US-05.1 · Devotee sees their own good days this māsam, with no other day marked

| Story | Complexity | Theme | Epic | Feature |
|---|---|---|---|---|
| US-05.1 | low | Sannidhi | Calendar | Requirement 05 – My good days this māsam |

#### Description
As a devotee, I want to see the days in this māsam that are good for me by my nakṣatra, so that I can choose a day for a vratam or a new start with confidence.

Today a devotee must ask an elder or a priest which days suit their nakṣatra.

Terms: māsam view — the Calendar month by lunar māsam; my good days — days marked by tārābalam for the saved nakṣatra; saved nakṣatra — from Profile or Find my nakṣatra.

Dependencies: māsam view — exists; saved nakṣatra — delivered by US-04.1 or entered in Profile.

Sizing: one sprint; covers marking good days and the quiet Find my nakṣatra link. Daily good times are excluded.

Done when: every acceptance criterion passes on a real phone for two nakṣatras across a māsam change, and the product owner accepts the demo.

#### Business Context
Hierarchy: Sannidhi › Calendar › Requirement 05 – My good days this māsam

Source: Good times and nakṣatra flow, 15 Oct 2026, Requirement 05.

The flow requires that "When a nakṣatra is saved, the māsam view marks the devotee's good days this māsam." and "Only good days are marked; other days carry no mark or label."

Outcome: today devotees ask others which days suit them; after this story their good days are marked in the māsam view.

Success measure: days marked or labelled as not good; baseline: not applicable; target: zero.

Priority: Could — it builds on the nakṣatra finder and can follow it.

#### Acceptance Criteria
- Given a devotee has saved Swāti as their nakṣatra, When the devotee opens the māsam view, Then that devotee's good days this māsam carry a small "Good for you" mark.
- Given a devotee has saved a nakṣatra, When the devotee opens a day that is not a good day, Then the day bubble shows no mark, label or note about the devotee.
- Given a devotee has no saved nakṣatra, When the devotee opens the māsam view, Then no personal marks are shown and a single quiet "Find my nakṣatra" link appears once.
- Given a devotee has dismissed the Find my nakṣatra link, When the devotee opens the māsam view again, Then the link is not shown.
- Given a devotee changes the saved nakṣatra in Profile, When the devotee opens the māsam view again, Then the marks show the good days for the new nakṣatra.

#### Out of Scope
- Finding the nakṣatra (US-04.1)
- Daily good times (US-01.1)
