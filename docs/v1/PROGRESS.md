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
