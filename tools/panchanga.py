"""Panchāṅga computation for the content build.

Lunisolar, amānta, Lahiri (Chitrapakṣa) ayanāṁśa, IST civil days.
Uses PyEphem (pure, no data downloads). Accuracy: tithi / nakṣatra ends within ~1–2 minutes,
which is ample for a daily calendar. Disagreements with the reference panchāṅgam are fixed with
content/calendar/overrides.csv, never in this file.
"""
from __future__ import annotations

import datetime as dt
import math
from dataclasses import dataclass, field

import ephem

IST = dt.timezone(dt.timedelta(hours=5, minutes=30))
LAHIRI_J2000 = 23.857  # degrees; Lahiri ayanāṁśa at J2000 in the J2000 ecliptic frame
MINUTE = 1.0 / 1440.0


# ---------- low-level astronomy ----------

def _ecl_lon_j2000(body, t: float) -> float:
    body.compute(ephem.Date(t), epoch=ephem.J2000)
    return math.degrees(ephem.Ecliptic(body, epoch=ephem.J2000).lon)


_SUN, _MOON = ephem.Sun(), ephem.Moon()


def elongation(t: float) -> float:
    """Moon minus Sun longitude, 0..360 (ayanāṁśa cancels)."""
    return (_ecl_lon_j2000(_MOON, t) - _ecl_lon_j2000(_SUN, t)) % 360.0


def sid(lon: float) -> float:
    return (lon - LAHIRI_J2000) % 360.0


def sun_sid(t: float) -> float:
    return sid(_ecl_lon_j2000(_SUN, t))


def moon_sid(t: float) -> float:
    return sid(_ecl_lon_j2000(_MOON, t))


def tithi_at(t: float) -> int:          # 1..30 (1–15 śukla, 16–30 kṛṣṇa)
    return int(elongation(t) // 12.0) + 1


def nakshatra_at(t: float) -> int:      # 1..27
    return int(moon_sid(t) // (360.0 / 27.0)) + 1


def rashi_index(lon_sid: float) -> int:  # 0..11, Meṣa = 0
    return int(lon_sid // 30.0)


def moon_illumination(t: float) -> int:
    _MOON.compute(ephem.Date(t))
    return int(round(_MOON.phase))


def _next_change(fn, t0: float, step: float, limit: float) -> float | None:
    """First time after t0 at which integer fn changes value (to ~20 s)."""
    v0, a = fn(t0), t0
    while a < t0 + limit:
        b = a + step
        if fn(b) != v0:
            lo, hi = a, b
            while hi - lo > MINUTE / 3:
                mid = (lo + hi) / 2
                if fn(mid) == v0:
                    lo = mid
                else:
                    hi = mid
            return hi
        a = b
    return None


def elongation_crossing(target_deg: float, t_lo: float, t_hi: float) -> float:
    """Time in [t_lo, t_hi] when elongation reaches target_deg (monotonic within a lunation)."""
    def above(t):
        e = elongation(t)
        return ((e - target_deg + 180) % 360) - 180 >= 0
    lo, hi = t_lo, t_hi
    while hi - lo > MINUTE / 3:
        mid = (lo + hi) / 2
        if above(mid):
            hi = mid
        else:
            lo = mid
    return hi


# ---------- time helpers ----------

def to_ist(t: float) -> dt.datetime:
    return ephem.Date(t).datetime().replace(tzinfo=dt.timezone.utc).astimezone(IST)


def ist_midnight(d: dt.date) -> float:
    return float(ephem.Date(dt.datetime(d.year, d.month, d.day) - dt.timedelta(hours=5, minutes=30)))


def hhmm(t: float) -> str:
    return to_ist(t).strftime("%H:%M")


def iso(t: float) -> str:
    return to_ist(t).strftime("%Y-%m-%dT%H:%M")


# ---------- lunar months ----------

@dataclass
class LunarMonth:
    start: float           # new moon (ephem date)
    end: float             # next new moon
    masa: int              # 1 = Chaitra … 12 = Phālguna
    adhika: bool
    samvatsara_year: int = 0   # Gregorian year in which this samvatsara's Ugadi falls

    def tithi_span(self, k: int) -> tuple[float, float]:
        """Start and end of tithi k (1..30) within this month."""
        s = self.start if k == 1 else elongation_crossing((k - 1) * 12.0, self.start, self.end)
        e = self.end if k == 30 else elongation_crossing(k * 12.0, self.start, self.end)
        return s, e


def lunar_months(d_from: dt.date, d_to: dt.date) -> list[LunarMonth]:
    t = float(ephem.previous_new_moon(ephem.Date(ist_midnight(d_from) - 40)))
    end = ist_midnight(d_to) + 40
    nms = [t]
    while nms[-1] < end:
        nms.append(float(ephem.next_new_moon(ephem.Date(nms[-1] + 1))))
    months: list[LunarMonth] = []
    for a, b in zip(nms, nms[1:]):
        r0, r1 = rashi_index(sun_sid(a)), rashi_index(sun_sid(b))
        months.append(LunarMonth(a, b, (r0 + 1) % 12 + 1, r0 == r1))
    chaitras = [to_ist(m.start).year for m in months if m.masa == 1 and not m.adhika]
    year = (chaitras[0] - 1) if chaitras else to_ist(months[0].start).year
    for m in months:
        if m.masa == 1 and not m.adhika:
            year = to_ist(m.start).year
        m.samvatsara_year = year
    return months


def month_at(months: list[LunarMonth], t: float) -> LunarMonth:
    for m in months:
        if m.start <= t < m.end:
            return m
    raise ValueError("time outside computed months")


# ---------- daily records ----------

@dataclass
class Place:
    id: str
    lat: float
    lon: float
    state: str

    def observer(self, t: float) -> ephem.Observer:
        o = ephem.Observer()
        o.lat, o.lon, o.elevation = str(self.lat), str(self.lon), 0
        o.date = ephem.Date(t)
        return o


@dataclass
class Day:
    date: dt.date
    sunrise: float
    sunset: float
    next_sunrise: float
    tithi: int
    tithi_end: float | None
    nakshatra: int
    nakshatra_end: float | None
    moon_rashi: int        # 1..12
    moon_pct: int
    masa: int
    adhika: bool
    samvatsara_year: int
    observances: list[str] = field(default_factory=list)


def sunrise_on(place: Place, d: dt.date) -> float:
    o = place.observer(ist_midnight(d))
    return float(o.next_rising(ephem.Sun()))


def sunset_on(place: Place, d: dt.date) -> float:
    o = place.observer(ist_midnight(d))
    return float(o.next_setting(ephem.Sun()))


def moonrise_after(place: Place, t: float) -> float:
    o = place.observer(t)
    return float(o.next_rising(ephem.Moon()))


def build_days(place: Place, months: list[LunarMonth], d_from: dt.date, d_to: dt.date) -> dict[dt.date, Day]:
    out: dict[dt.date, Day] = {}
    d = d_from
    sr = sunrise_on(place, d)
    while d <= d_to:
        nsr = sunrise_on(place, d + dt.timedelta(days=1))
        m = month_at(months, sr)
        out[d] = Day(
            date=d, sunrise=sr, sunset=sunset_on(place, d), next_sunrise=nsr,
            tithi=tithi_at(sr), tithi_end=_next_change(tithi_at, sr, 0.25, 2.0),
            nakshatra=nakshatra_at(sr), nakshatra_end=_next_change(nakshatra_at, sr, 0.25, 2.0),
            moon_rashi=rashi_index(moon_sid(sr)) + 1, moon_pct=moon_illumination(sr),
            masa=m.masa, adhika=m.adhika, samvatsara_year=m.samvatsara_year,
        )
        d, sr = d + dt.timedelta(days=1), nsr
    return out


# ---------- observance rules ----------

KALAS = {"sunrise", "madhyahna", "pradosha", "nishita", "chandrodaya"}


def kala_window(place: Place, day: Day, kala: str) -> tuple[float, float]:
    sr, ss, nsr = day.sunrise, day.sunset, day.next_sunrise
    if kala == "sunrise":
        return sr, sr + MINUTE
    if kala == "madhyahna":                       # 3rd of 5 parts of daytime
        span = ss - sr
        return sr + 0.4 * span, sr + 0.6 * span
    if kala == "pradosha":                        # 2 muhūrtas after sunset
        return ss, ss + 96 * MINUTE
    if kala == "nishita":                         # midnight muhūrta
        mid = (ss + nsr) / 2
        return mid - 24 * MINUTE, mid + 24 * MINUTE
    if kala == "chandrodaya":
        mr = moonrise_after(place, sr)
        if mr > nsr:                              # no moonrise this civil day
            return nsr, nsr
        return mr, mr + MINUTE
    raise ValueError(kala)


def _overlap(a: tuple[float, float], b: tuple[float, float]) -> float:
    return max(0.0, min(a[1], b[1]) - max(a[0], b[0]))


def resolve_observance(place: Place, days: dict[dt.date, Day], months: list[LunarMonth],
                       masa: int | None, paksha: str, tithi: int, kala: str) -> list[dt.date]:
    """All civil dates in the computed range on which the rule falls for this place."""
    k = tithi + (15 if paksha == "krishna" else 0)
    if paksha == "krishna" and tithi == 15:
        k = 30
    found: list[dt.date] = []
    for m in months:
        if masa is not None and (m.adhika or m.masa != masa):   # yearly rules skip adhika; monthly keep it
            continue
        ts, te = m.tithi_span(k)
        d0, d1 = to_ist(ts).date() - dt.timedelta(days=1), to_ist(te).date()
        cands = [days[d0 + dt.timedelta(n)] for n in range((d1 - d0).days + 1)
                 if d0 + dt.timedelta(n) in days]
        cands = [c for c in cands if _overlap((c.sunrise, c.next_sunrise), (ts, te)) > 0]
        if not cands:
            continue
        scored = [(_overlap(kala_window(place, c, kala), (ts, te)), c) for c in cands]
        best = max(s for s, _ in scored)
        if best > 0:
            pick = next(c for s, c in scored if s == best)
        else:
            at_sunrise = [c for c in cands if ts <= c.sunrise < te]
            pick = at_sunrise[0] if at_sunrise else cands[0]
        found.append(pick.date)
    return found
