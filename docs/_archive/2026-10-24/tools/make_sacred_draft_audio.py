#!/usr/bin/env python3
"""DRAFT recitations of the nāmalu and mantras with Sarvam bulbul:v3, for testing the Sankalpam player only.

Sacred audio for release is a human recording (content-architecture rule). These drafts are marked
`"status": "draft"` and release builds leave them out, so the app falls back to Read/Done or the ring.

Writes, per text:
  content/audio/<text-id>.m4a          the recitation (texts: names one after another; mantras: one repetition)
  content/audio/<text-id>.json         timings the player uses:
      texts:   {"status": "draft", "names_ms": [start of name 1, name 2, ...], "length_ms": ...}
      mantras: {"status": "draft", "length_ms": ..., "words_ms": [...]}   (word starts estimated by syllables)

Reads:
  content/texts/<id>.yaml   (needs the names imported: run tools/import_ashtottara.py first)
  content/mantras/<deity>.md

Usage
  export SARVAM_API_KEY=...
  python tools/make_sacred_draft_audio.py --list
  python tools/make_sacred_draft_audio.py --text ganesha.ashtottara
  python tools/make_sacred_draft_audio.py --mantra ganesha.mantra.mula --as-text ganesha.mula-mantra
"""
import argparse, base64, io, json, os, re, shutil, subprocess, time, urllib.request, urllib.error, wave
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CFG = {
    'endpoint': 'https://api.sarvam.ai/text-to-speech', 'model': 'bulbul:v3',
    'language_code': 'hi-IN',      # Devanagari Sanskrit read with a Hindi voice: closest available
    'speaker': 'anand', 'pace': 0.8, 'temperature': 0.3, 'speech_sample_rate': 44100,
    'gap_ms': 350, 'lead_ms': 600, 'tail_ms': 800,
}


def tts(text, key):
    body = {k: CFG[k] for k in ('model', 'speaker', 'pace', 'temperature', 'speech_sample_rate', 'language_code')}
    body.update(text=text, output_audio_codec='wav')
    req = urllib.request.Request(CFG['endpoint'], data=json.dumps(body).encode(),
                                 headers={'Content-Type': 'application/json', 'api-subscription-key': key})
    for attempt in range(5):
        try:
            with urllib.request.urlopen(req, timeout=120) as r:
                return base64.b64decode(json.loads(r.read())['audios'][0])
        except urllib.error.HTTPError as e:
            if e.code in (429, 500) and attempt < 4:
                time.sleep(2 ** attempt); continue
            raise SystemExit(f'Sarvam error {e.code}: {e.read().decode(errors="replace")}')


def pcm(b):
    with wave.open(io.BytesIO(b)) as w:
        return w.getparams(), w.readframes(w.getnframes())


def sil(p, ms):
    return b'\x00' * int(p.framerate * ms / 1000) * p.sampwidth * p.nchannels


def ms(frames, p):
    return round(len(frames) / (p.sampwidth * p.nchannels) / p.framerate * 1000)


def save(stem, params, frames, meta):
    stem.parent.mkdir(parents=True, exist_ok=True)
    wav = stem.with_suffix('.wav')
    with wave.open(str(wav), 'wb') as w:
        w.setparams(params); w.writeframes(frames)
    out = wav
    if shutil.which('ffmpeg'):
        out = stem.with_suffix('.m4a')
        subprocess.run(['ffmpeg', '-y', '-loglevel', 'error', '-i', str(wav), '-ac', '1',
                        '-af', 'loudnorm=I=-16:TP=-1.5:LRA=7', '-c:a', 'aac', '-b:a', '64k', str(out)], check=True)
        wav.unlink()
    stem.with_suffix('.json').write_text(json.dumps(meta, ensure_ascii=False, indent=1), encoding='utf-8')
    print('wrote', out.name, f"{meta['length_ms']/1000:.1f}s")


def text_names(text_id):
    import yaml  # pip install pyyaml (Colab has it)
    y = yaml.safe_load((ROOT / 'content' / 'texts' / f'{text_id}.yaml').read_text(encoding='utf-8')) or {}
    names = y.get('names')
    if not isinstance(names, list) or not names:
        raise SystemExit(f'{text_id}: no names found. Import them first (tools/import_ashtottara.py).')
    return [str(x['sa']) for x in sorted(names, key=lambda x: int(x['n']))]


def mantra_sa(mantra_id):
    for f in (ROOT / 'content' / 'mantras').glob('*.md'):
        t = f.read_text(encoding='utf-8')
        m = re.search(r'^## ' + re.escape(mantra_id) + r'\s*\nsa:\s*(.+)$', t, re.M)
        if m:
            return m.group(1).strip()
    raise SystemExit(f'{mantra_id} not found in content/mantras/')


def syllables(word):
    return max(1, len(re.findall(r'[\u0905-\u0914]|[\u0915-\u0939][\u093e-\u094c\u0962\u0963]?(?!\u094d)', word)))


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--text'); ap.add_argument('--mantra'); ap.add_argument('--as-text')
    ap.add_argument('--list', action='store_true')
    ap.add_argument('--speaker', help='bulbul:v3 speaker to use (audition first; default anand)')
    a = ap.parse_args()
    if a.list:
        for f in sorted((ROOT / 'content' / 'texts').glob('*.yaml')):
            print('text  ', f.stem)
        for f in sorted((ROOT / 'content' / 'mantras').glob('*.md')):
            for m in re.findall(r'^## (\S+)', f.read_text(encoding='utf-8'), re.M):
                print('mantra', m)
        return
    if a.speaker:
        CFG['speaker'] = a.speaker
    key = os.environ.get('SARVAM_API_KEY')
    if not key:
        raise SystemExit('Set SARVAM_API_KEY first.')
    if a.text:
        names = text_names(a.text)
        frames = b''; params = None; starts = []
        for i, n in enumerate(names):
            p, data = pcm(tts(n, key))
            if params is None:
                params = p; frames += sil(p, CFG['lead_ms'])
            else:
                frames += sil(p, CFG['gap_ms'])
            starts.append(ms(frames, p)); frames += data
            print(f'  {i+1}/{len(names)}', end='\r')
        frames += sil(params, CFG['tail_ms'])
        save(ROOT / 'content' / 'audio' / a.text, params, frames,
             {'status': 'draft', 'source': 'bulbul:v3 ' + CFG['speaker'], 'names_ms': starts, 'length_ms': ms(frames, params)})
    elif a.mantra:
        sa = mantra_sa(a.mantra)
        p, data = pcm(tts(sa, key))
        frames = data + sil(p, 150)
        words = [w for w in re.split(r'\s+', sa.replace('।', ' ').replace('॥', ' ')) if w]
        total = ms(data, p); syl = [syllables(w) for w in words]; acc = 0; starts = []
        for s in syl:
            starts.append(round(acc / sum(syl) * total)); acc += s
        save(ROOT / 'content' / 'audio' / (a.as_text or a.mantra), p, frames,
             {'status': 'draft', 'source': 'bulbul:v3 ' + CFG['speaker'], 'length_ms': ms(frames, p),
              'words_ms': starts, 'words_estimated': True})
    else:
        ap.print_help()


if __name__ == '__main__':
    main()
