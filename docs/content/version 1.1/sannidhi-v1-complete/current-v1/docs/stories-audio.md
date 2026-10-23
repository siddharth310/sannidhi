# Stories — narration, music and touch sounds (v1, 23 Oct 2026)

Changes `docs/app-flow.md` §7 and the story format in `docs/content-architecture.md`.

## What changed

- **One telling per story.** No "Older children" choice and no short/more split: each beat has one text per
  language. The words are unchanged; the old short and more texts are joined.
- **Read to me:** every beat is narrated (te, hi, en), with the sentence being read lit up.
- **Music:** a soft bed per story under the voice, and a low version at the hard moments.
- **Touch sounds** on touch moments.
- Everything plays from files in the content pack. The phone needs no internet and no voice model.

## Story start

Cover image, title, then two large switches, remembered for the next story:
- **Read to me** — on by default
- **Music** — on by default

Primary **Start**: the title is read (if Read to me is on), then beat 1 begins.

## In the story

| | Read to me on | Read to me off |
|---|---|---|
| Narration | Beat's audio plays; the sentence being read is lit (the Stories module's accent tint behind it), using the beat's `.json` times | none |
| Turning | 1.5 s after the narration ends the next beat slides in; touching the picture or swiping first is fine | swipe or the → button |
| Touch moment | plays its sound; narration is not interrupted | plays its sound |
| Pause | one large Pause/Play button, bottom corner; pauses voice and music together | music pause only |

- **Music:** the bed loops (1.5 s crossfade at the loop point), dips 5 dB while a sentence is read, and
  crossfades to the `.low` file over 1.5 s on beats marked `music: low`, back again after. If an opening or
  closing file exists, it plays under the title and after the last beat; otherwise the bed fades out over 3 s at the end.
- **Language switch** inside the story: the same beat restarts in the new language.
- **Screen locked or app in the background:** the story pauses (stories are for looking at, unlike japa).
- **Phone call or unplugged headphones:** pause; never resumes by itself.
- **No narration for a language:** Read to me is hidden for that language; music and touch sounds still play.
- Last beat: narration, a short pause, closing music, then the parents' links (puja, names) as before.

## Story file format

```markdown
---
id: ganesha.story.moon
…
music: mus.ganesha.moon        # content/music/mus.ganesha.moon.m4a (+ .low.m4a)
narration: yes                 # content/audio/stories/<id>/title.<lang>.m4a, b<N>.<lang>.m4a (+ .json)
---
## beat 3
image: img.story.ganesha.moon.b3
touch: { target: modakas, effect: appear, sound: sfx.appear }
music: low
#### te
…one text…
#### hi
…
#### en
…
```

## Content build checks

- Every beat has te, hi and en text.
- With `narration: yes`: `title.<lang>` and every `b<N>.<lang>` exist as `.m4a` with a `.json` whose sentences
  are rising, inside the file length, and whose texts join back to the beat text.
- `music:` names a file in `content/music/` with its `.low` version and a licence file.
- Every `touch.sound` exists in `content/sfx/`.
- Output: narration and music are copied into the pack under `audio/stories/`, `music/`, `sfx/`.

## Never

Scary sounds, thunder, battle drums, sad music at hard moments, music louder than the voice, applause or
"well done" sounds, a voice model on the phone.
