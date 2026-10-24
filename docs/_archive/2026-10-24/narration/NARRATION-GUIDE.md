# Story narration — guide for Sarvam Bulbul (v1, 23 Oct 2026)

Three Gaṇeśa stories, one telling each (no age split), narrated in Telugu, Hindi and Indian English. The audio is
made once on your computer and shipped as files in the content pack; the phone only plays files and never needs
the internet or any voice model.

Files in this kit:

| File | What it is |
|---|---|
| `narration/bulbul-settings.json` | Model, voice per language, pace, temperature, pauses, output format |
| `narration/scripts/<story>.<lang>.md` | The exact lines Bulbul reads, numbered, per beat |
| `narration/pronunciations.csv` | Names to listen for, with a respelling to switch on if a voice says one wrong |
| `tools/make_narration.py` | Generates every file, with sentence timings for the highlight |

---

## 1. The voice

**One storyteller per language** for all three stories: warm, unhurried, smiling, like a grandmother telling a
story at bedtime. Not a radio voice, not a cartoon voice.

- **One voice for everyone in the story.** Dialogue ("You are my son.") is read in the same voice with a little
  more warmth, not acted as a different character.
- **Never dramatic.** No gasps, no frightened tone, no sad music in the voice at the hard moments (beat 3 of the
  elephant-head story, the snake in the moon story). Calm and steady carries the child through.
- **Gentle lift at joyful lines** ("He opened his eyes and smiled!", "The boy woke again…!").
- **The last beat is said slowly**, as something to keep. The script adds a longer pause before it.

## 2. Bulbul settings and why

| Setting | Value | Why |
|---|---|---|
| `model` | `bulbul:v3` | Telugu, Hindi and Indian English in one model; stable |
| `speaker` | te `kavitha`, hi `ritu`, en `priya` — **first picks, audition first** | Sarvam's list gives names only, no gender or age; choose by ear |
| `pace` | 0.85 (en 0.88) | A little slower than normal speech, so children and elders can follow |
| `temperature` | 0.45 | Lower than the 0.6 default: steadier, fewer odd emphases; raise to 0.55 if it sounds flat |
| `speech_sample_rate` | 44100 | Full quality before compression |
| output | WAV from Bulbul → m4a (AAC, mono, 64 kbps) | Small files; the script converts with ffmpeg |
| loudness | −16 LUFS, peak −1.5 dB | Every beat equally loud; music sits about 8 dB below |

**Pitch.** `bulbul:v3` has no pitch or loudness control (only the older v2 had them). Get the tone you want by
choosing the speaker. If a voice you love is a touch too high or low, shift it after generation by at most one
semitone, and only after listening:
`ffmpeg -i in.m4a -af "asetrate=44100*0.944,aresample=44100,atempo=1.059" out.m4a` (down one semitone, same
length; for up one semitone use `1.059` and `0.944` the other way round).

**Text.** Bulbul reads punctuation: commas give small pauses, "!" lifts, quotes are read naturally. The script
sends one sentence at a time and adds the pauses itself (450 ms between sentences, 1.2 s after the title, 0.9 s
before the last beat), which keeps the pace calm and gives exact timings for the highlight.

## 3. Steps

1. Get an API key from your Sarvam dashboard. On your computer: `export SARVAM_API_KEY=...`
   (Windows: `set SARVAM_API_KEY=...`). Install ffmpeg if you don't have it.
2. **Check the lines:** `python tools/make_narration.py --dry-run`
3. **Audition voices:** `python tools/make_narration.py --audition`
   → `narration/auditions/` has beat 1 of each story in every shortlist voice, per language. Pick one voice
   per language and write it into `bulbul-settings.json`.
4. **Generate everything:** `python tools/make_narration.py`
   → `content/audio/stories/<story-id>/title.<lang>.m4a`, `b1.<lang>.m4a` … with a `.json` beside each.
5. **Listen to every file** with the checklist below. Fix and regenerate one file with, for example,
   `python tools/make_narration.py --story ganesha.story.moon --lang te --force`.
6. When all files pass, set `reviewed_by:` in each story file.

Cost and time are small: 3 stories × 3 languages is about 60 short requests.

## 4. Pronunciation

Listen for the names in `pronunciations.csv`. If a voice says one wrong:
- set `use_respelling` to `yes` for that row (the respelling is used only for the audio, never shown in the app),
  or
- create a pronunciation dictionary in Sarvam and put its ID in `dict_id` in the settings.

## 5. Listening checklist (every file)

- Names said right (Pārvatī, Śiva, Gaṇapati, Kārtikeya, Nārada, Kailāsa, modaka).
- No clipped first or last word; no long silence in the middle.
- Calm at the hard moments; nothing sounds scary or sad.
- Same voice and loudness across all beats of a story.
- Open the `.json`: sentence times look right (each sentence's start is just before its first word).

## 6. Story by story: tone notes

Bulbul can't be directed line by line, so these notes are what to listen for, which beats to regenerate if they
sound wrong, and the brief for a human narrator later.

**How Ganesha Got His Elephant Head** · feeling: brave, safe · about 4 min
| Beat | Tone |
|---|---|
| 1 | Tender and wondering. Smile on "He opened his eyes and smiled!" Soft on "You are my son." |
| 2 | Warm instruction from the mother; then steady and proud: "The boy stood bravely at the door." |
| 3 | Even and calm. The boy's line is firm, not angry. The last sentence is quiet and plain, no drama. |
| 4 | Gentle, slow. "They found a kind elephant" turns toward hope. |
| 5 | Joy returns: lift on "woke again". Warm and proud on "You will be worshipped before everyone." |
| 6 | Slow, close, like a blessing. |

**Ganesha and the Moon** · feeling: kind, safe · about 3 min
| Beat | Tone |
|---|---|
| 1 | Playful and happy; enjoy "round and full". |
| 2 | Light, a little comic; the mouse working hard. |
| 3 | Quick but not frightening: a small surprise, then the fun of modakas rolling everywhere. |
| 4 | The moon's laugh is light teasing. Gaṇeśa's question is calm and kind, never cross. |
| 5 | Firm and gentle on "Go and hide". Hushed on "the sky went dark". |
| 6 | Soft on "I'm sorry!"; warm forgiveness. |
| 7 | Slow and clear: the lesson. |

**The Fruit of Wisdom** · feeling: wise, loved · about 3 min
| Beat | Tone |
|---|---|
| 1 | Storyteller's wonder; Nārada stately. |
| 2 | Clear and even; the challenge set. |
| 3 | Brisk and excited for the peacock; slow and thoughtful on "looked at his little mouse and thought". |
| 4 | Slow, reverent: "You are my whole world." |
| 5 | Tender comfort for Kārtikeya; no winner or loser in the voice. |
| 6 | Slow, warm, close. |

## 7. Files and timings

```
content/audio/stories/ganesha.story.moon/
  title.te.m4a   title.te.json
  b1.te.m4a      b1.te.json   … b7.te.m4a
  (same for hi and en)
```

`b3.en.json`:
```json
{ "story": "ganesha.story.moon", "part": "b3", "lang": "en", "speaker": "priya", "model": "bulbul:v3",
  "duration_ms": 9120,
  "sentences": [ { "start_ms": 300, "end_ms": 1740, "text": "A snake on the path!" }, … ] }
```
The app lights up the sentence being read using these times.

## 8. Moving to a human narrator later

Use the same scripts and file names. Ask the narrator to record each beat as one take, with a short pause between
sentences; the content build then finds sentence times by alignment. Nothing in the app changes.
