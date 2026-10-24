"""Good windows for a calendar export. Same cuts as the app, and none of the times kept aside are named."""
from __future__ import annotations

import datetime as dt
from dataclasses import dataclass

VISH_FROM = [50, 24, 30, 40, 14, 11, 30, 20, 32, 30, 20, 18, 22, 20, 14, 14, 10, 14, 20, 24, 20, 10, 10, 18, 16, 24, 30]
VISH_TO = [54, 28, 34, 44, 18, 15, 34, 24, 36, 34, 24, 22, 26, 24, 18, 18, 14, 18, 24, 28, 24, 14, 14, 22, 20, 28, 34]
AMRITA_FROM = [42, 48, 54, 52, 38, 35, 54, 44, 56, 54, 44, 43, 45, 44, 38, 38, 34, 38, 44, 48, 44, 34, 34, 43, 40, 48, 54]
AMRITA_TO = [46, 52, 58, 56, 42, 39, 58, 48, 60, 58, 48, 46, 49, 48, 42, 42, 38, 42, 48, 52, 48, 38, 38, 46, 44, 52, 58]
RAHU = {6: 8, 0: 2, 1: 7, 2: 5, 3: 6, 4: 4, 5: 3}  # Sunday=6 in Python weekday() is Monday=0; Sunday=6
YAMA = {6: 5, 0: 4, 1: 3, 2: 2, 3: 1, 4: 7, 5: 6}
DUR = {6: 14, 0: 9, 1: 4, 2: 8, 3: 6, 4: 4, 5: 1}


@dataclass
class Window:
    kind: str
    start: dt.datetime
    end: dt.datetime


def _clock(day: dt.date, hhmm: str | None) -> dt.datetime | None:
    if not hhmm:
        return None
    hour, minute = (int(x) for x in hhmm[:5].split(":"))
    return dt.datetime(day.year, day.month, day.day, hour, minute)


def _portion(total: dt.timedelta, num: int, den: int) -> dt.timedelta:
    return dt.timedelta(milliseconds=int(total.total_seconds() * 1000) * num // den)


def _span(start: dt.datetime, total: dt.timedelta, index: int, parts: int) -> tuple[dt.datetime, dt.datetime] | None:
    if index < 1 or index > parts:
        return None
    return start + _portion(total, index - 1, parts), start + _portion(total, index, parts)


def _ghati(start: dt.datetime, end: dt.datetime, frm: int, to: int) -> tuple[dt.datetime, dt.datetime] | None:
    ms = int((end - start).total_seconds() * 1000)
    if ms <= 0 or to <= frm:
        return None
    return start + dt.timedelta(milliseconds=ms * frm // 60), start + dt.timedelta(milliseconds=ms * to // 60)


def _clip(start, end, frm, to):
    a = frm if start < frm else start
    b = to if end > to else end
    return (a, b) if b > a else None


def _subtract(start, end, blocks):
    parts = [(start, end)]
    for block_start, block_end in blocks:
        nxt = []
        for a, b in parts:
            if block_end <= a or block_start >= b:
                nxt.append((a, b))
            elif block_start <= a and block_end >= b:
                continue
            elif a < block_start < block_end < b:
                nxt.extend([(a, block_start), (block_end, b)])
            elif block_start <= a:
                nxt.append((block_end, b))
            else:
                nxt.append((a, block_start))
        parts = nxt
    return [(a, b) for a, b in parts if (b - a) >= dt.timedelta(minutes=1)]


def windows(days: list[dict], date: dt.date) -> list[Window]:
    by_date = {dt.date.fromisoformat(d["date"]): d for d in days}
    today = by_date.get(date)
    if today is None:
        return []
    sunrise = _clock(date, today.get("sunrise"))
    sunset = _clock(date, today.get("sunset"))
    if sunrise is None or sunset is None or sunset <= sunrise:
        return []
    previous = by_date.get(date - dt.timedelta(days=1))
    nxt = by_date.get(date + dt.timedelta(days=1))
    next_sunrise = _clock(date + dt.timedelta(days=1), nxt.get("sunrise")) if nxt else sunrise + dt.timedelta(hours=24)
    prev_sunset = _clock(date - dt.timedelta(days=1), previous.get("sunset")) if previous else None
    day_length = sunset - sunrise
    night_length = (sunrise - prev_sunset) if prev_sunset and sunrise > prev_sunset else None
    weekday = date.weekday()  # Monday 0 … Sunday 6

    ends = []
    for day in days:
        raw = day.get("nakshatra_ends")
        if not raw:
            continue
        try:
            end = dt.datetime.fromisoformat(raw)
        except ValueError:
            continue
        ends.append((day["nakshatra"], end))
    ends = sorted({end: n for n, end in ends}.items())
    spans = []
    if ends:
        start = ends[0][0] - dt.timedelta(hours=26)
        for end, n in ends:
            if 1 <= n <= 27 and end > start:
                spans.append((n, start, end))
            start = end

    blocks = []
    for index, parts in ((RAHU[weekday], 8), (YAMA[weekday], 8)):
        block = _span(sunrise, day_length, index, parts)
        if block:
            blocks.append(block)
    block = _span(sunrise, day_length, DUR[weekday], 15)
    if block:
        blocks.append(block)
    for n, start, end in spans:
        block = _ghati(start, end, VISH_FROM[n - 1], VISH_TO[n - 1])
        if block:
            blocks.append(block)

    raw: list[Window] = []
    if night_length is not None:
        raw.append(Window("early", sunrise - _portion(night_length, 2, 15), sunrise - _portion(night_length, 1, 15)))
    if weekday != 2:  # not Wednesday
        raw.append(Window("begin", sunrise + _portion(day_length, 7, 15), sunrise + _portion(day_length, 8, 15)))
    for n, start, end in spans:
        window = _ghati(start, end, AMRITA_FROM[n - 1], AMRITA_TO[n - 1])
        if window is None:
            continue
        clipped = _clip(window[0], window[1], sunrise, next_sunrise)
        if clipped:
            raw.append(Window("today", clipped[0], clipped[1]))
    night_after = next_sunrise - sunset
    if night_after > dt.timedelta(0):
        raw.append(Window("lamp", sunset - _portion(day_length, 2, 30), sunset + _portion(night_after, 2, 30)))

    out = []
    for window in raw:
        for start, end in _subtract(window.start, window.end, blocks):
            if end - start >= dt.timedelta(minutes=1):
                out.append(Window(window.kind, start, end))
    return sorted(out, key=lambda w: w.start)
