# Naamalu — design reference (source of truth for UI)

Each `.dc.html` file is one phone screen from the design canvas. Read it as a redline:
- Canvas width 390 px = 390 dp. **1 px = 1 dp**, font-size px = **sp**.
- Colours, radii, gradients, shadows, spacing: take the exact values from the inline styles.
- `<svg>` paths (icons, landscape, moon, tide, globe) → convert to `ImageVector` or Compose `Canvas` paths, same coordinates.
- `{{ … }}` = data bound at runtime; the script at the bottom (`class Component`) shows the logic (moon phase, tide curve, bubble placement, globe projection, language switch).
- `[deity.ganesha]`, `[date]`, `[N]` = placeholders → real content or the missing-asset fallback. Never ship the bracket text.
- Sample names, cities and counts are illustrative only.

| Screen | Primary file (English/IAST + Hindi toggle) | Telugu version |
|---|---|---|
| Home | — | V3Home.dc.html |
| Puja overview | — | V3Puja.dc.html |
| Calendar (māsam) | V3CalendarEN.dc.html | V3Calendar.dc.html |
| Calendar spec (moon phases, tide, cell states) | — | V3CalendarSpec.dc.html |
| Naamalu library | V3NaamaluEN.dc.html | V3Naamalu.dc.html |
| Reader / Player | V3ReaderEN.dc.html | V3Reader.dc.html |
| Japa counter | — | S4Japa.dc.html |
| Guided puja | — | S3Guided.dc.html |
| Start a pārāyaṇam | V3GroupCreateEN.dc.html | V3GroupCreate.dc.html |
| Globe (open pārāyaṇams) | V3GlobeEN.dc.html | V3Globe.dc.html |
| Group pārāyaṇam | V3GroupEN.dc.html | V3Group.dc.html (S5Sankalpa = older) |
| Naivedyam offering | — | S6Dish.dc.html (recipe parts replaced by "why this offering" — see design-system.md §6.22) |
| Colours, icons, principles | — | V3System.dc.html |
