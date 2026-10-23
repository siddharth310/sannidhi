#!/usr/bin/env python3
"""Build FIXTURE dates-2026-27.json (IST, precomputed until ephemeris pipeline)."""

from __future__ import annotations

import json
from datetime import date, timedelta
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "core/content/src/main/assets/content/calendar/dates-2026-27.json"

# Ugadi / Chaitra śukla pratipadā ≈ 19 Mar 2026 (verify against panchangam)
CHAITRA_START = date(2026, 3, 19)

MAASAM_ORDER = [
    "chaitra", "vaishakha", "jyeshtha", "ashadha", "shravana", "bhadrapada",
    "ashvayuja", "kartika", "margashira", "pushya", "magha", "phalguna",
]

WEEKDAYS = ["monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"]


def vara(d: date) -> str:
    return WEEKDAYS[d.weekday()]


def lunar_day_index(paksha: str, tithi: int) -> int:
    if paksha == "shukla":
        return tithi if tithi < 15 else 15
    return 15 + tithi if tithi < 15 else 30


def generate_maasam_days(start: date, maasam_id: str) -> list[dict]:
    days: list[dict] = []
    d = start
    for i in range(30):
        if i < 15:
            paksha = "shukla"
            tithi = i + 1
        else:
            paksha = "krishna"
            tithi = i - 14
        days.append({
            "date": d.isoformat(),
            "maasamId": maasam_id,
            "paksha": paksha,
            "tithi": tithi,
            "lunarDay": lunar_day_index(paksha, tithi),
            "vara": vara(d),
            "observanceIds": [],
        })
        d += timedelta(days=1)
    return days


def attach_observances(days: list[dict], obs_path: Path) -> None:
    obs_list = json.loads(obs_path.read_text(encoding="utf-8"))
    by_key: dict[tuple[str, str, str], list[str]] = {}
    for o in obs_list:
        if o.get("type") != "day":
            continue
        oid = o["observance_id"]
        for mid in o.get("maasam_ids", "").split(","):
            mid = mid.strip()
            if not mid:
                continue
            key = (mid, o.get("paksha", ""), str(o.get("tithi", "")))
            by_key.setdefault(key, []).append(oid)

    for day in days:
        key = (day["maasamId"], day["paksha"], str(day["tithi"]))
        for oid in by_key.get(key, []):
            if oid not in day["observanceIds"]:
                day["observanceIds"].append(oid)


def add_periods(days: list[dict]) -> None:
    """Pitru / Mahalaya period on krishna paksha of bhadrapada."""
    start = None
    for day in days:
        if day["maasamId"] == "bhadrapada" and day["paksha"] == "krishna" and day["tithi"] == 1:
            start = day["date"]
        if start and day["maasamId"] == "bhadrapada" and day["paksha"] == "krishna":
            day.setdefault("periodIds", []).append("pitru-paksha")
        if day["maasamId"] == "bhadrapada" and day["paksha"] == "krishna" and day["tithi"] == 15:
            if "mahalaya-amavasya" not in day["observanceIds"]:
                day["observanceIds"].append("mahalaya-amavasya")


def align_bhadrapada_mock(days: list[dict]) -> None:
    """Shift Bhādrapada so mockup dates align: Vinayaka 14 Sep, Pūrṇimā 25 Sep, Amāvāsya 10 Oct."""
    target_padyami = date(2026, 9, 11)
    for i, day in enumerate(days):
        if day["maasamId"] != "bhadrapada":
            continue
        if day["paksha"] == "shukla" and day["tithi"] == 1:
            delta = (target_padyami - date.fromisoformat(day["date"])).days
            if delta != 0:
                for j in range(i, min(i + 30, len(days))):
                    days[j]["date"] = (
                        date.fromisoformat(days[j]["date"]) + timedelta(days=delta)
                    ).isoformat()
                    days[j]["vara"] = vara(date.fromisoformat(days[j]["date"]))
            break


def main() -> None:
    all_days: list[dict] = []
    start = CHAITRA_START
    for mid in MAASAM_ORDER:
        chunk = generate_maasam_days(start, mid)
        all_days.extend(chunk)
        start = date.fromisoformat(chunk[-1]["date"]) + timedelta(days=1)

    obs_path = ROOT / "core/content/src/main/assets/content/calendar/observances.json"
    attach_observances(all_days, obs_path)
    add_periods(all_days)
    align_bhadrapada_mock(all_days)

    # Janmashtami 2026 — ensure shravana krishna 8 exists on same civil date for regional label test
    for day in all_days:
        if day["maasamId"] == "shravana" and day["paksha"] == "krishna" and day["tithi"] == 8:
            day["date"] = "2026-08-15"
            day["vara"] = vara(date(2026, 8, 15))
            if "krishna-janmashtami" not in day["observanceIds"]:
                day["observanceIds"].append("krishna-janmashtami")

    payload = {
        "meta": {
            "status": "FIXTURE",
            "timezone": "Asia/Kolkata",
            "yearKey": "2026-27",
            "note": "Precomputed until ephemeris pipeline; verify against published panchangam.",
        },
        "cities": {
            "hyderabad": {"days": all_days},
        },
    }
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Wrote {len(all_days)} days → {OUT}")


if __name__ == "__main__":
    main()
