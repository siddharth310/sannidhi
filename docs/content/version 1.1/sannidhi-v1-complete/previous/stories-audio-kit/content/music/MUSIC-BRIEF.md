# Story music — brief (v1, 23 Oct 2026)

For a musician you commission, or for choosing licensed tracks. The placeholder files in this folder and in
`content/sfx/` were synthesised for testing the player only: a tanpura drone, a simple flute line in the rāga
below and a few bells. Replace them before release; keep the same file names.

## How music is used in a story

- One **bed** per story plays softly under the narration and loops for as long as the story is open.
- Each bed has a **low** version: drone and a little bell only, used on beats marked `music: low` in the story
  file. The music thins out at hard moments; it never turns dark or sad.
- **Touch sounds** play when the child touches the picture (glow, bounce, wiggle, appear, hide).
- Levels: narration −16 LUFS; bed about −24 LUFS (8 dB under the voice), and the app dips it by another 4–6 dB
  while a sentence is being read; low version about −28 LUFS; touch sounds about −22 LUFS, under 1.2 s.
- Never: vocals or lyrics under the narration, film songs, recorded bhajans, thunder, battle drums, horror
  stings, applause, AI music generators (rights unclear).

## Story by story

| | How Ganesha Got His Elephant Head | Ganesha and the Moon | The Fruit of Wisdom |
|---|---|---|---|
| File | `mus.ganesha.elephant-head` (+ `.low`) | `mus.ganesha.moon` (+ `.low`) | `mus.ganesha.mango` (+ `.low`) |
| Feeling | brave, safe | kind, safe; night-time | wise, loved; a race, then home |
| Rāga | **Hamsadhwani** (S R₂ G₃ P N₃), the rāga of *Vātāpi Gaṇapatim*, Gaṇeśa's own | **Nīlāmbari** (the lullaby rāga) or a slow **Mohanam** | **Mohanam** (S R₂ G₃ P D₂), bright and open |
| Instruments | tanpura, bansuri lead, light veena plucks | tanpura, soft veena or santoor, small bells like stars | tanpura, bansuri, very soft mridangam or kanjira pulse |
| Tempo | about 66 bpm, unhurried | about 54 bpm, rocking | about 76 bpm, light-footed |
| Low beats | 3 (the quarrel), 4 (Pārvatī weeps) | 3 (the snake), 5 (the sky goes dark) | none |
| Lifts | beat 5: the voice carries the joy; the bed stays the same | beat 6: forgiveness, bells a touch brighter if available | beat 3: optional short "peacock flight" flourish |

## What to deliver (per story)

1. **Bed**: 60–90 s, seamless loop (or a clean ending we can crossfade over 1.5 s). WAV, 48 kHz, 24-bit, stereo.
2. **Low version** of the same bed: drone with sparse bells or plucks, same key and length.
3. **Opening**: 3–5 s, theme stated once, ending on Sa (plays under the title).
4. **Closing**: 4–6 s, resolving to Sa (plays after the last beat).
All in the same Sa. Placeholders use C (Sa = C3 on tanpura); the musician can choose, but use one Sa for all
three stories so they feel like one family.

## Touch sounds (shared by all stories)

| File | Sound | Used for |
|---|---|---|
| `sfx.glow` | three soft rising bell notes | something lights up (the boy, the mango, the moon) |
| `sfx.bounce` | two light wood-block taps | the modaka, the peacock, the elephant |
| `sfx.wiggle` | a short ghungroo shake | the door, the mouse, Gaṇeśa |
| `sfx.appear` | a gentle shimmer | modakas or the moon appearing |
| `sfx.hide` | a soft downward whoosh | the moon hiding behind clouds |

## Rights

- **Commissioned:** a short written agreement that assigns all rights in the recordings and compositions to you,
  with permission to use them in the app, store listings and promotion, worldwide, with no time limit.
- **Licensed tracks:** the licence must cover use inside a mobile app. Keep the licence text beside each file as
  `<file>.licence.txt`; the content build refuses music without it (placeholders carry `placeholder.licence.txt`).
