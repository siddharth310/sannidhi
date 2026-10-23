# P2d2 — Stories: one telling, narration, music, touch sounds

Update the Stories module to `docs/stories-audio.md`. Report back with every acceptance check as pass/fail.

READ FIRST:
- `docs/stories-audio.md` (all of it)
- `content/stories/ganesha/*.md` (new format: one text per language per beat, `music`, `narration`, `touch.sound`)
- `narration/NARRATION-GUIDE.md` §7 (audio files and the `.json` sentence times)
- `content/music/MUSIC-BRIEF.md` (levels)

STEP 1 — Content build (`tools/build_content.py`)
- Parse the new story format; drop `### short` / `### more` support.
- Add the checks in stories-audio.md "Content build checks"; copy `content/audio/stories/`, `content/music/`,
  `content/sfx/` into the pack; add them to the story JSON (`narration: {lang: {file, sentences[]}}`, `music`,
  `musicLow`, per-beat `musicLow: bool`, `touch.sound`).
- Until the real narration exists, `narration: yes` with no files is a warning in dev builds and an error in
  release builds.

STEP 2 — Remove the age split
- Remove the "Older children" choice, the `more` paragraph and any code reading `short`/`more`.

STEP 3 — Story start
- Cover, title, **Read to me** and **Music** switches (DataStore, default on, remembered), **Start**.

STEP 4 — Player audio
- Media3: one player for narration, one for the music bed (looping, crossfade), one SoundPool for touch sounds.
- Narration per beat; sentence highlight from the `.json` times, polling the narration position every 50 ms.
- Auto-turn 1.5 s after narration ends when Read to me is on; a swipe or → at any time moves on and starts that
  beat's narration.
- Music ducks 5 dB while narration plays; crossfade to the `.low` file on `musicLow` beats; fade out 3 s after
  the last beat.
- Pause/Play button pauses narration and music together.
- Language switch restarts the current beat in the new language.
- Pause on background, screen lock, audio focus loss and becoming noisy; never auto-resume.
- Read to me hidden when the story has no narration for the current language.

DO NOT:
- Run any text-to-speech on the phone or fetch audio from the network at play time.
- Play music louder than the voice, or any sound on story end other than the closing music.

ACCEPTANCE:
1. No "Older children" choice anywhere; each beat shows one text, and the words match the story file.
2. With the placeholder music and test narration: Start reads the title, then beat 1; sentences light up in step.
3. Beat ends → next beat 1.5 s later; swiping mid-beat starts the next beat's narration at once.
4. Elephant-head beats 3 and 4 and moon beats 3 and 5 crossfade to the low music; other beats use the full bed.
5. Touching the mouse in the moon story plays the wiggle sound without stopping the narration.
6. Music off: no bed at all; Read to me off: no narration, manual turning, touch sounds still play.
7. Switching te → hi on beat 4 restarts beat 4 in Hindi.
8. Locking the screen pauses the story; it stays paused when unlocked until Play.
9. Airplane mode: every story plays fully.
