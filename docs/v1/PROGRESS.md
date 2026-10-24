# v1 progress

## Images

`python3 tools/build_content.py --check` fails when a drawing marked supplied or approved is missing, or when a drawing on disk will not convert. Pictures still marked missing stay in `content/content_status.csv` and the screen shows the deity symbol on the module colour.

## M2 — Stories

One telling is in. The old short/more stories are in `docs/_archive/2026-10-24/content/stories/`. Placeholder music and touch sounds are in the pack. Narration files are not in the repo; dev check warns, and a release build errors only if a reviewed or published story is missing them.

Checked on the emulator (debug date 14 Sep 2026, English, Hyderabad):

| Check | Result |
| --- | --- |
| No age choice; one text, matching the story file | Pass. Elephant-head beat 1 and the moon story's last line match the files. |
| Read to me and Music default on, and Music is remembered | Music starts on. Read to me is hidden until that language has narration, so the remembered off/on for Read to me waits for the files. |
| Title, then beat 1, sentences lit | Waiting for audio. |
| Next beat 1.5 s after narration | Waiting for audio. The timer is in the player. |
| Swipe starts the next beat at once | Page change starts that beat. Not listened to with a voice. |
| Read to me off: manual pages, music and touch sounds remain | Read to me is hidden. Music plays (Pause is shown). Touch sounds are wired. |
| Music ducks while the voice speaks | About 5 dB in the player. No voice to hear yet. |
| Elephant-head beats 3–4 and moon beats 3 and 5 use the low bed | The story files mark those beats, and both `.low` files are in the pack. |
| Moon beat 2 touch plays wiggle without stopping the voice | Sound is wired on the tap. Not heard over a voice yet. |
| Telugu to Hindi restarts the same beat | Hindi title on the last beat reads गणेश जी. Hindi narration waits for audio. |
| Pause and Play resume together | Pause is on screen while the bed plays. Resume continues the same players. |
| Lock pauses and stays paused | The player pauses on leave and does not start again by itself. Not locked in this pass. |
| Missing narration hides Read to me | Pass. No narration files, so the switch is hidden in English and Hindi. |
| Missing pictures use the symbol | Pass. Beat art is the modaka on the story colour. 22 story images are listed missing. |
| Last beat: more stories and the puja, no score | Pass. "More stories of Ganesha" and "Do the puja". |
| For today on 14 Sep 2026 | Pass. Stories shows FOR TODAY · Vinayaka Chavithi and How Ganesha Got His Elephant Head. |
| All three stories offline in te, hi, en with narration | Music and touch sounds are on the phone. Narration waits for the Sarvam files. |

`python3 tools/build_content.py --check`: 0 errors, 3 warnings (narration missing for the three Ganesha stories).

## M3 — Puja

The offering page replaces the empty placeholder. For a Telangana profile it opens as Undrallu, then the other forms as equal cards (Kudumulu, Kozhukattai, Kadubu, Modak) with the state name and the note. Maharashtra puts Modak first by the same rule. Why, Offered on (Sankashti, Vinayaka Chavithi, and the puja), and the elder line are on the page. Search is on the screen while the emulator is online. The button is hidden when the phone has no connection; that hide was not tapped through after airplane mode.

A step with a mantra and a count shows "Chant 21 times" and a tap counter. The count stays in memory for that visit and is not written as practice. Samagri still says "No durva? Akshata is fine." Clear ticks removes that puja's ticks for the day. Guided mode remembers the step for 12 hours and asks "Continue from step N?". Leaving it scrolls the puja page back to that step. The screen stays on in guided mode, as before.

`python3 tools/build_content.py --check` prints every draft row. A `--release` build already drops draft deities, pujas, mantras, texts, naivedyam, observances and stories.

Not walked on the device this pass: the 21-count up to 21, Clear ticks after a restart, the 12-hour continue question, and a Maharashtra profile. The Telangana offering page was opened on the emulator.

## M4 — Calendar

`python3 tools/build_content.py --export-calendar hyderabad 2026` and the same for vijayawada write `build/check/<city>-2026.csv` (also copied to `tests/calendar-export/`). Each row is one day: weekday, sunrise, sunset, tithi, paksha, māsam, nakṣatra, moon rāśi, observances and good-time windows.

Compared with the acceptance dates, from the Hyderabad file (Vijayawada agrees on the tithi):

| Date | Computed sunrise tithi | Observance |
| --- | --- | --- |
| 19 Mar 2026 | kṛṣṇa 15, Phālguna. Tithi ends 06:54. | none. There is no Ugadi observance in the content, and Chaitra śukla 1 is not the sunrise tithi. 20 Mar is already śukla 2. |
| 14 Sep 2026 | śukla 3, Bhādrapada | obs.vinayaka-chavithi. The good window 07:18–07:35 matches the Home card. |
| 11 Oct 2026 | śukla 1, Āśvayuja | none attached. The tithi is the one asked for. |

These CSVs need a comparison with the TTD panchāṅgam. A difference goes in `content/calendar/overrides.csv`, not in code. I have not added an override.

A missing te, hi or en line in a rāśi profile or gochara note fails the check, and so does a fear or remedy word in any language. The current notes pass. The observance page, today's note only, and the good-time list were already in the app. The good-time strings do not name rāhu kālam, yamagaṇḍam, durmuhūrtam, varjyam, avoid, bad or inauspicious.

## M5 — Home and Profile

Home on the emulator (English, Hyderabad, debug date 14 Sep) shows the greeting, the date · tithi · city line, Vinayaka Chavithi with Start the puja, the four module cards, the language pill, and the sādhana bar "Ganapati mula mantra and 3 more".

Profile now has About, Licences and Privacy, each with Back and Home. About shows the app version and the content-pack version from the manifest. Licences lists the four fonts, the open-source libraries, and the music and sound licence files packed with the app. Privacy shows the English draft from `docs/v1/release/privacy-policy.md`, copied into the app so it opens with no network. Telugu and Hindi say that the body is still the English draft. Delete my data already clears the settings and the practice database, and now also clears the app cache, then returns to onboarding. I did not press it on this emulator, because that phone already holds sankalpam practice.

Not done on a device: a full language switch walk, 200% font screenshots, TalkBack, contrast measurement, airplane mode, a 30-minute monkey run, and a network inspector. The manifest has no internet permission and no analytics SDK. The Search button on an offering page is the one place that opens the browser, and only while the phone is online.

## Images v1

The 60 drawings and 3 god cards from the image pack are in `content/images/`, and `manifest.csv` now marks every row supplied. The figure sources in `content/images/_characters/` stay in the repo and are not packed into the app. `--check` reports 0 missing images and 0 conversion errors. A `used_by` of `future` (Śiva, Pārvatī, Kārtikeya cards) does not warn. On the emulator, the first puja step shows the courtyard drawing. Each story beat that can be tapped uses the `touch-…` group in its drawing as the tap area.

## Stories by god, and the Chavithi katha

Stories opens on the same god cards as Puja. Only gods with a story in this build are shown, and the grid stays even when there is one god. Search is hidden until there are more than eight gods. A debug row, Show story search, forces the field so that case can be checked. On Vināyaka Chavithi and on Saṅkaṣṭi, For today is one card, The Chavithi katha: the moon story, then the Syamantaka jewel. The Gaṇeśa page lists the four stories by `order`.

The Vināyaka Chavithi katha step, and the Vināyaka Chavithi observance page under the families-do lines, show the same two-part card. Part 1 is the moon story and part 2 is the Syamantaka jewel. The end of part 1 offers Continue, and continues by itself after three seconds only while Read to me is on. The end of part 2 offers only Back to the puja, or Back when the katha was opened from the observance or from Stories. Back returns to the same step. A place left in the middle is kept for 12 hours. No other step shows a story. Syamantaka has `narration: yes` and no audio yet, so Read to me stays hidden, the same as the other three stories.

`python3 tools/build_content.py --check`: 0 errors, 4 narration warnings, and the lines `ganesha: 4` and `Vināyaka Chavithi katha: moon → syamantaka`. 0 missing images. Rows for the god grid, the search flag, god-page order, the back path and For today are in `tests/M2-stories.xlsx`. Rows for the katha card, part 1 to part 2, auto-continue, guided Back and the 12-hour resume are in `tests/M3-puja.xlsx`.

## Device pass on the skipped checks

Emulator, debug date 14 Sep 2026, Hyderabad. Results are in column L of `tests/M2-stories.xlsx` and `tests/M3-puja.xlsx`. Audio rows stay “waiting for audio”.

Telugu on the story chip showed the beat in Telugu and left the chrome in English. Switching the app language to Telugu while the story was open changed the chrome and the story together. The language is English again.

At 200% the god grid, the god page and the katha card scroll and stay readable. The katha part title had been squeezed to one letter a line, so it now sits on its own line under the chips. With TalkBack on, the god card reads “Ganesha, 4 stories” and the katha card reads “Chavithi katha, 2 parts”. A system notification sheet blocked the spoken double-tap and was closed without allowing notifications.

Airplane mode: Stories and the moon story open, and the music plays. The offering Search button was not opened, so that row is not a pass. Show story search, with one god, shows the field; `zzzz` hides the list and `gane` shows Ganesha. The flag was turned off again afterwards.

Back from the katha in guided mode stays on step 24 of 32. The first try had restarted the puja and asked “Continue from step 24?”; the guided page is now kept while the story is open, and a fresh entry still asks. The Vināyaka Chavithi observance page, under What families do, shows the same two-part card.

A missed tap during the language sheet started one extra empty japam. It was left in place, because it cannot be told apart from an earlier empty row without deleting practice. Delete my data was not pressed.
