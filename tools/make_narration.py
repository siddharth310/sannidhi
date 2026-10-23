#!/usr/bin/env python3
"""Generate story narration with Sarvam bulbul:v3, one audio file per beat per language.

Reads the story files in content/stories/**.md (one telling per beat, `#### te/hi/en` blocks),
speaks each sentence separately, joins them with gentle pauses, and writes:

  content/audio/stories/<story-id>/title.<lang>.m4a   (+ .json)
  content/audio/stories/<story-id>/b<N>.<lang>.m4a    (+ .json with sentence start/end times)

The .json sidecar lets the app light up each sentence as it is read.

Only Python's standard library is needed. ffmpeg (optional) converts to m4a and evens out loudness;
without it the .wav files are kept and the content build converts them.

Usage
  export SARVAM_API_KEY=...            # from your Sarvam dashboard
  python tools/make_narration.py --dry-run                 # show the sentences, no API calls
  python tools/make_narration.py --audition                # beat 1 of each story, every shortlist voice
  python tools/make_narration.py                           # all stories, te + hi + en
  python tools/make_narration.py --story ganesha.story.moon --lang te --force
"""
import argparse, base64, csv, io, json, os, re, shutil, subprocess, sys, time, urllib.request, urllib.error, wave
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SETTINGS = ROOT / 'narration' / 'bulbul-settings.json'
RESPELL = ROOT / 'narration' / 'pronunciations.csv'


def parse_story(path):
    text = path.read_text(encoding='utf-8')
    _, fm, body = text.split('---\n', 2)
    sid = re.search(r'^id:\s*(.+)$', fm, re.M).group(1).strip()
    t = re.search(r'^title:\s*\{\s*te:\s*(.+?),\s*hi:\s*(.+?),\s*en:\s*(.+?)\s*\}$', fm, re.M)
    title = dict(zip(['te', 'hi', 'en'], t.groups())) if t else {}
    beats = []
    for blk in re.split(r'\n(?=## beat )', '\n' + body):
        m = re.match(r'\s*## beat (\d+)', blk)
        if not m:
            continue
        langs, cur = {}, None
        for line in blk.split('\n')[1:]:
            h = re.match(r'#### (\w+)\s*$', line)
            if h:
                cur = h.group(1); langs[cur] = ''
            elif cur and line.strip():
                langs[cur] = (langs[cur] + ' ' + line.strip()).strip()
        beats.append((int(m.group(1)), langs))
    return sid, title, beats


def sentences(text):
    """Split into sentences for separate synthesis. Never splits after a closing quote, so a spoken
    line stays with its "said the moon" tag; never splits before a lowercase English word."""
    text = re.sub(r'\s*[—–]\s*', ', ', text)
    return [p.strip() for p in re.split(r'(?<=[.!?।])\s+(?=[^a-z])', text) if p.strip()]


def load_respell(lang):
    rules = []
    if RESPELL.exists():
        with RESPELL.open(encoding='utf-8') as f:
            for row in csv.DictReader(f):
                if row.get('lang') == lang and row.get('use_respelling', '').lower() == 'yes':
                    rules.append((row['word'], row['respelling']))
    return rules


def tts(cfg, lang, speaker, text, key):
    lc = cfg['languages'][lang]
    body = {
        'text': text, 'language_code': lc['language_code'], 'speaker': speaker,
        'model': cfg['model'], 'pace': lc['pace'], 'temperature': cfg['temperature'],
        'speech_sample_rate': cfg['speech_sample_rate'], 'output_audio_codec': 'wav',
    }
    if cfg.get('dict_id'):
        body['dict_id'] = cfg['dict_id']
    req = urllib.request.Request(cfg['endpoint'], data=json.dumps(body).encode(),
                                 headers={'Content-Type': 'application/json', 'api-subscription-key': key})
    for attempt in range(5):
        try:
            with urllib.request.urlopen(req, timeout=120) as r:
                data = json.loads(r.read())
            return base64.b64decode(data['audios'][0])
        except urllib.error.HTTPError as e:
            if e.code in (429, 500) and attempt < 4:
                time.sleep(2 ** attempt); continue
            raise SystemExit(f'Sarvam error {e.code}: {e.read().decode(errors="replace")}')


def read_wav(b):
    with wave.open(io.BytesIO(b)) as w:
        return w.getparams(), w.readframes(w.getnframes())


def silence(params, ms):
    n = int(params.framerate * ms / 1000)
    return b'\x00' * n * params.sampwidth * params.nchannels


def render(cfg, lang, speaker, lines, lead_ms, key, respell):
    """lines: list of sentences. Returns (wav_bytes, sentence_timings)."""
    pz = cfg['pauses_ms']; frames = b''; params = None; timings = []
    for i, s in enumerate(lines):
        spoken = s
        for w, r in respell:
            spoken = spoken.replace(w, r)
        p, pcm = read_wav(tts(cfg, lang, speaker, spoken, key))
        if params is None:
            params = p; frames += silence(p, lead_ms)
        elif i:
            frames += silence(p, pz['between_sentences'])
        start = len(frames) / (p.sampwidth * p.nchannels) / p.framerate
        frames += pcm
        end = len(frames) / (p.sampwidth * p.nchannels) / p.framerate
        timings.append({'start_ms': round(start * 1000), 'end_ms': round(end * 1000), 'text': s})
    frames += silence(params, pz['tail'])
    buf = io.BytesIO()
    with wave.open(buf, 'wb') as w:
        w.setparams(params); w.writeframes(frames)
    return buf.getvalue(), timings, len(frames) / (params.sampwidth * params.nchannels) / params.framerate


def save(wav_bytes, dest_stem, sidecar):
    dest_stem.parent.mkdir(parents=True, exist_ok=True)
    wav = dest_stem.with_suffix('.wav'); wav.write_bytes(wav_bytes)
    final = wav
    if shutil.which('ffmpeg'):
        m4a = dest_stem.with_suffix('.m4a')
        subprocess.run(['ffmpeg', '-y', '-loglevel', 'error', '-i', str(wav), '-ac', '1',
                        '-af', 'loudnorm=I=-16:TP=-1.5:LRA=7', '-c:a', 'aac', '-b:a', '64k', str(m4a)], check=True)
        wav.unlink(); final = m4a
    dest_stem.with_suffix('.json').write_text(json.dumps(sidecar, ensure_ascii=False, indent=1), encoding='utf-8')
    return final


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--stories', default=str(ROOT / 'content' / 'stories'))
    ap.add_argument('--out', default=str(ROOT / 'content' / 'audio' / 'stories'))
    ap.add_argument('--lang', default='te,hi,en')
    ap.add_argument('--story', default=None, help='only this story id')
    ap.add_argument('--audition', action='store_true')
    ap.add_argument('--dry-run', action='store_true')
    ap.add_argument('--force', action='store_true')
    a = ap.parse_args()
    cfg = json.loads(SETTINGS.read_text(encoding='utf-8'))
    key = os.environ.get('SARVAM_API_KEY', '')
    if not a.dry_run and not key:
        raise SystemExit('Set SARVAM_API_KEY first.')
    langs = a.lang.split(',')
    stories = sorted(Path(a.stories).rglob('*.md'))
    report = []
    for path in stories:
        sid, title, beats = parse_story(path)
        if a.story and sid != a.story:
            continue
        for lang in langs:
            respell = load_respell(lang)
            items = [('title', [title[lang]], cfg['pauses_ms']['lead_in'])] if lang in title else []
            for i, (n, texts) in enumerate(beats):
                lead = cfg['pauses_ms']['before_last_beat_line'] if i == len(beats) - 1 else cfg['pauses_ms']['lead_in']
                items.append((f'b{n}', sentences(texts.get(lang, '')), lead))
            if a.audition:
                items = items[1:2]  # beat 1 only
            if a.dry_run:
                print(f'\n== {sid} · {lang}')
                for name, lines, _ in items:
                    for s in lines:
                        print(f'  {name}: {s}')
                continue
            speakers = cfg['audition_shortlist'] if a.audition else [cfg['languages'][lang]['speaker']]
            for spk in speakers:
                for name, lines, lead in items:
                    if a.audition:
                        stem = ROOT / 'narration' / 'auditions' / f'{sid}.{name}.{lang}.{spk}'
                    else:
                        stem = Path(a.out) / sid / f'{name}.{lang}'
                    if not a.force and (stem.with_suffix('.m4a').exists() or stem.with_suffix('.wav').exists()):
                        continue
                    wav, timings, dur = render(cfg, lang, spk, lines, lead, key, respell)
                    final = save(wav, stem, {'story': sid, 'part': name, 'lang': lang, 'speaker': spk,
                                             'model': cfg['model'], 'duration_ms': round(dur * 1000),
                                             'sentences': timings})
                    report.append([sid, name, lang, spk, round(dur, 1), final.name])
                    print(f'{sid} {name} {lang} {spk}: {dur:.1f}s')
    if report:
        rp = ROOT / 'narration' / 'narration-report.csv'
        new = not rp.exists()
        with rp.open('a', newline='', encoding='utf-8') as f:
            w = csv.writer(f)
            if new:
                w.writerow(['story', 'part', 'lang', 'speaker', 'seconds', 'file'])
            w.writerows(report)


if __name__ == '__main__':
    main()
