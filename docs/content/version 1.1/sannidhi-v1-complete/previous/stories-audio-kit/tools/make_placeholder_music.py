# Synthesises the placeholder story music and touch sounds (testing only). Usage: python tools/make_placeholder_music.py content
# Needs numpy, scipy and ffmpeg.
import numpy as np, subprocess, os, sys
from scipy.signal import fftconvolve, butter, lfilter
SR=44100; rng=np.random.default_rng(7)
OUT=sys.argv[1]; os.makedirs(OUT+'/music',exist_ok=True); os.makedirs(OUT+'/sfx',exist_ok=True)
RAT={'S':1,'R':9/8,'G':5/4,'M':4/3,'P':3/2,'D':5/3,'N':15/8}
def note(sym,base):
    o=1
    while sym.endswith("'"): o*=2; sym=sym[:-1]
    while sym.endswith(','): o/=2; sym=sym[:-1]
    return base*RAT[sym]*o
def env(n,a,r):
    e=np.ones(n); a=int(a*SR); r=int(r*SR)
    e[:a]=np.linspace(0,1,a); e[-r:]*=np.linspace(1,0,r); return e
def reverb(x,t=1.8,wet=.28):
    n=int(t*SR); ir=rng.standard_normal(n)*np.exp(-np.linspace(0,7,n)); ir/=np.abs(ir).sum()/6
    y=fftconvolve(x,ir)[:len(x)]; return x*(1-wet)+y*wet
def tanpura(dur,sa):
    x=np.zeros(int(dur*SR)); pat=[sa*3/4,sa,sa,sa/2]; t=0.0; i=0
    while t<dur:
        f=pat[i%4]; n=int(5.5*SR); tt=np.arange(n)/SR; s=np.zeros(n)
        for h in range(1,24):
            bloom=np.exp(-tt*(0.5+h*0.06))*(1+0.9*np.sin(np.pi*np.clip(tt/2.2,0,1))*(h>4))
            s+= (1/h**0.85)*bloom*np.sin(2*np.pi*f*h*tt*(1+0.0004*np.sin(2*np.pi*0.3*tt))+rng.random()*6)
        s*=np.minimum(1,tt/0.045)
        a=int(t*SR); b=min(len(x),a+n); x[a:b]+=s[:b-a]*0.085; t+=1.05; i+=1
    return x
def flute(seq,base,bpm):
    beat=60/bpm; total=sum(d for _,d in seq)*beat; x=np.zeros(int(total*SR)+SR); t=0
    for sym,d in seq:
        dur=d*beat
        if sym!='-':
            f=note(sym,base); n=int(dur*SR); tt=np.arange(n)/SR
            vib=1+0.006*np.sin(2*np.pi*5.2*tt)*np.minimum(1,tt/0.4)
            ph=2*np.pi*np.cumsum(f*vib)/SR
            s=np.sin(ph)+0.18*np.sin(2*ph)+0.05*np.sin(3*ph)
            b,a=butter(2,[f*0.9/(SR/2),min(0.99,f*3/(SR/2))],btype='band')
            s+=0.05*lfilter(b,a,rng.standard_normal(n))
            s*=env(n,min(.09,dur/3),min(.25,dur/2))
            a0=int(t*SR); x[a0:a0+n]+=s*0.22
        t+=dur
    return x
def bells(dur,base,count,seed=1):
    r=np.random.default_rng(seed); x=np.zeros(int(dur*SR))
    for _ in range(count):
        t=r.uniform(1,dur-3); f=note(r.choice(["S''","G'","P'","D'","R''"]),base); n=int(3*SR); tt=np.arange(n)/SR
        s=(np.sin(2*np.pi*f*tt)+0.4*np.sin(2*np.pi*f*2.76*tt))*np.exp(-tt*2.2)*0.07
        a=int(t*SR); x[a:a+n]+=s[:len(x)-a]
    return x
def drum(dur,bpm):
    x=np.zeros(int(dur*SR)); beat=60/bpm; t=0; k=0
    while t<dur-0.5:
        n=int(0.25*SR); tt=np.arange(n)/SR; acc=1.0 if k%4==0 else 0.55
        s=np.sin(2*np.pi*(85+40*np.exp(-tt*30))*tt)*np.exp(-tt*16)*0.16*acc
        a=int(t*SR); x[a:a+n]+=s; t+=beat; k+=1
    return x
def fit(x,n):
    return np.pad(x,(0,max(0,n-len(x))))[:n]
def write(name,x,lufs):
    x=x/ (np.max(np.abs(x))+1e-9)*0.8
    # loop-friendly: 1.5 s fade at both ends so the app can crossfade the loop
    f=int(1.5*SR); x[:f]*=np.linspace(0,1,f); x[-f:]*=np.linspace(1,0,f)
    st=np.stack([x, np.roll(x,int(0.012*SR))*0.96],1)
    wav=f'/tmp/{os.path.basename(name)}.raw'
    (st*32767).astype('<i2').tofile(wav)
    subprocess.run(['ffmpeg','-y','-loglevel','error','-f','s16le','-ar',str(SR),'-ac','2','-i',wav,
                    '-af',f'loudnorm=I={lufs}:TP=-2:LRA=9','-ar',str(SR),'-c:a','aac','-b:a','96k',name+'.m4a'],check=True)
    os.remove(wav)
DUR=64
SA=130.81  # tanpura Sa (C3); flute an octave up
stories={
 'elephant-head':dict(bpm=66,seq="S 1 R 1 G 2 P 2 N 1 S' 3 - 2 N 1 P 1 G 2 R 1 S 3 - 3 G 1 P 1 N 2 S' 1 R' 1 S' 2 N 1 P 3 - 2 P 1 G 1 R 1 S 1 R 1 G 2 P 4 - 3 G 1 R 1 S 4 - 4",drum=False,bell=4,seed=3),
 'moon':dict(bpm=54,seq="G 2 R 2 S 3 D, 3 - 3 S 2 R 2 G 4 - 3 P 2 G 2 R 2 S 4 - 4 D, 2 S 2 R 3 S 5 - 5",drum=False,bell=9,seed=5),
 'mango':dict(bpm=76,seq="S 1 R 1 G 1 P 1 D 2 P 1 G 1 R 2 S 2 - 2 G 1 P 1 D 1 S' 1 R' 2 S' 2 D 1 P 1 G 2 - 2 P 1 D 1 P 1 G 1 R 1 S 1 R 1 G 1 P 4 - 3 G 1 R 1 S 4 - 4",drum=True,bell=3,seed=8),
}
for nm,c in stories.items():
    n=DUR*SR
    seq=c['seq'].split(); pairs=[(seq[i],float(seq[i+1])) for i in range(0,len(seq),2)]
    mel=flute(pairs,SA*2,c['bpm']); mel=np.tile(mel, int(np.ceil(n/len(mel))))
    drone=fit(tanpura(DUR,SA),n); bl=fit(bells(DUR,SA*2,c['bell'],c['seed']),n)
    full=drone+fit(mel,n)+bl+(fit(drum(DUR,c['bpm']),n) if c['drum'] else 0)
    write(f'{OUT}/music/mus.ganesha.{nm}',reverb(full),-24)
    write(f'{OUT}/music/mus.ganesha.{nm}.low',reverb(drone+bl*0.6),-28)
# touch sounds
def sfxw(name,x): write_s(name,x)
def write_s(name,x):
    x=x/(np.max(np.abs(x))+1e-9)*0.7; f=int(0.01*SR); x[-f:]*=np.linspace(1,0,f)
    raw=f'/tmp/{os.path.basename(name)}.raw'; (np.stack([x,x],1)*32767).astype('<i2').tofile(raw)
    subprocess.run(['ffmpeg','-y','-loglevel','error','-f','s16le','-ar',str(SR),'-ac','2','-i',raw,'-af','loudnorm=I=-22:TP=-3','-ar',str(SR),'-c:a','aac','-b:a','96k',name+'.m4a'],check=True)
    os.remove(raw)
def tone(f,d,decay,partials=((1,1),(2.76,.4))):
    tt=np.arange(int(d*SR))/SR; return sum(a*np.sin(2*np.pi*f*m*tt) for m,a in partials)*np.exp(-tt*decay)
S=f'{OUT}/sfx'
g=np.zeros(int(1.2*SR))
for i,f in enumerate([1046.5,1318.5,1568]): a=int(i*0.09*SR); s=tone(f,1.0,4); g[a:a+len(s)]+=s
write_s(f'{S}/sfx.glow',reverb(g,1.2,.3))
b=np.zeros(int(0.6*SR))
for i,(f,amp) in enumerate([(420,1),(560,.7)]): a=int(i*0.16*SR); s=tone(f,0.25,28,((1,1),(1.5,.3))); b[a:a+len(s)]+=s*amp
write_s(f'{S}/sfx.bounce',b)
w=np.zeros(int(0.7*SR))
for k in range(9):
    a=int(k*0.06*SR); n=int(0.12*SR); tt=np.arange(n)/SR
    s=rng.standard_normal(n)*np.exp(-tt*45); bb,aa=butter(2,[5000/(SR/2),9000/(SR/2)],btype='band'); w[a:a+n]+=lfilter(bb,aa,s)*0.8
write_s(f'{S}/sfx.wiggle',w)
n=int(1.0*SR); tt=np.arange(n)/SR
ap=sum(np.sin(2*np.pi*f*tt)*np.exp(-((tt-0.12*i)%1)*5)*(tt>0.12*i) for i,f in enumerate([1568,1760,2093,2349]))*0.3
write_s(f'{S}/sfx.appear',reverb(ap,1.0,.35))
noise=rng.standard_normal(int(0.8*SR)); bb,aa=butter(2,[600/(SR/2),3000/(SR/2)],btype='band'); h=lfilter(bb,aa,noise)
tt=np.arange(len(h))/SR; h*=np.sin(np.pi*tt/0.8)**2*np.linspace(1,0.3,len(h))
write_s(f'{S}/sfx.hide',h)
print('done')
