# Calendar content pipeline

1. Edit `docs/naamalu-calendar-core.xlsx`
2. Export vocabulary sheets:

   ```bash
   python3 scripts/export_calendar_core.py
   ```

3. Rebuild precomputed days (FIXTURE until ephemeris):

   ```bash
   python3 scripts/calendar/build_dates_fixture.py
   ```

Output: `core/content/src/main/assets/content/calendar/`

`dates-2026-27.json` is marked FIXTURE in `_manifest.json` — replace with pipeline output when Swiss Ephemeris integration lands.
