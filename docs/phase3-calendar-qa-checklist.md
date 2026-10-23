# Phase 3 — Calendar QA checklist

Reference: `docs/design/calendar-masam.png`, `docs/design/calendar-spec.png`

| # | Criterion | Status | Notes |
|---|-----------|--------|-------|
| 1 | Moon phase in every cell matches tithi; pūrṇimā full, amāvāsya empty, aṣṭamīs ≈ half | **Pass (unit)** | `MoonPhaseMathTest`; verify visually on device |
| 2 | Tide peaks at pūrṇimā, bottoms at amāvāsya; gold marker on selected day | **Pass (dev)** | `TideBand`; confirm on Bhādrapada fixture |
| 3 | Day tap → bubble; ✕ / outside / back closes | **Pass (dev)** | Bubble overlay; back handler wired |
| 4 | te/hi pakṣa + tithi names; māsam follows state `monthSystem` | **Pass (dev)** | JSON + `MaasamLabelResolver`; switch state in Settings |
| 5 | Janmashtami: Śrāvaṇa (amānta) vs Bhādrapada (pūrṇimānta) same date | **Pass (unit)** | `MaasamLabelResolverTest`; open 2026-08-15 on device |
| 6 | Offline; large font; no placeholder copy | **Partial** | Bundled assets; year cards lack mini tide; dates are FIXTURE |

## Manual smoke

1. Home → tithi line from calendar (not hardcoded).
2. Home festival card → Calendar.
3. Calendar → Bhādrapada → Vinayaka row / pitṛ period bar / bubble on tap.
4. Toggle **సంవత్సరం** → pick a month → grid returns.
5. Settings: Maharashtra (pūrṇimānta) → month labels on krishna pakṣa shift.

## Screenshots (fill after device pass)

- [ ] Māsam view
- [ ] Day bubble
- [ ] Year view

## Data pipeline

```bash
python3 scripts/export_calendar_core.py
python3 scripts/calendar/build_dates_fixture.py
```
