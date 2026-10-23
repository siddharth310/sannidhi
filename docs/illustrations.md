# Naamalu — Illustrations: style, list, and pipeline

Every image in the app comes from this list and follows this style. No photos, no stock art, no forwarded
images. Owner supplies or generates each asset; Cursor wires it in via the manifest.

---

## 1. Style (applies to every asset)

- **Flat, layered vector** in the spirit of Alto's Adventure: 3–5 tonal layers, soft silhouettes, no outlines.
- **Palette only** from `design-system.md` §2 (base + six ritual colours + sky palettes). No other hues.
- **No gradients** except inside sky backgrounds. Optional paper grain ≤ 4% opacity.
- **No text inside images** (text must stay translatable).
- **Every origin, as it is:** vessels, setups and dishes drawn the way that region does them; one region's version is never drawn as "the" version. Where regions differ, commission one per region.
- **Deities: depicted in form**, in this same flat layered style. Each depiction conveys the deity's **nature, power, stature, wisdom and intellect** through posture, scale against the landscape, attributes (āyudhas, vāhana) and setting — calm and dignified, never cartoonish. Consistent proportions and eye-level across all deities.
- **Composition:** subject centred, generous empty space, ground line at the bottom third so assets sit on the sheet like the landscape.

**Prompt template (if generating):**
> Flat vector illustration, Alto's Adventure style, layered soft silhouettes, no outlines, no text, limited palette of ivory #FBF4E9, kumkum #7A1F2B/#B23A48, haldi #E0A93B, tulasi #6E9A5B/#3E5A34, neela #2F7188, kamala #E08BA0, chandan #C9A67A; calm, reverent, generous negative space; subject: **[subject]**, **[regional detail]**; background **[transparent / ivory]**.

---

## 2. Asset list

Status key: **Done** = drafted as vector in the design canvas · **Needed** = owner to supply.
Priority: **P0** = MVP blocker · **P1** = MVP nice · **P2** = later.

### 2.1 System (vector in code)
| ID | Asset | Format | Used in | Status | Pri |
|---|---|---|---|---|---|
| `sys.landscape` | Hill layers ×4, gopuram, coconut palm ×2, sun, moon, stars, birds — one shape set, recoloured per sky | SVG → ImageVector | SkyHeader everywhere | Done (canvas), refine | P0 |
| `sys.icons.*` | 12 module/section icons (kalaśam, tāḷapatra, mala, araṭi āku, tithi, diyas, lotus, home, mandapam, basket, rings, scroll) | SVG → ImageVector | Tiles, nav, sections | Done (design-system §5) | P0 |
| `sys.diya-glow` | Small diya + glow | SVG | Guided puja bottom | Done | P1 |

### 2.2 Identity
| ID | Asset | Format | Status | Pri |
|---|---|---|---|---|
| `id.app-icon` | Adaptive icon: kalaśam on kumkum-tint circle (fg + bg + monochrome) | SVG + PNG 432 | Needed | P0 |
| `id.splash` | Kalaśam + wordmark space on ivory | SVG | Needed | P0 |
| `id.play-feature` | Play Store feature graphic 1024×500 (landscape + kalaśam) | PNG | Needed | P0 |
| `id.play-screens` | 4–6 store screenshots from the final build | PNG | Needed | P0 |

### 2.3 Onboarding (4)
| ID | Scene | Status | Pri |
|---|---|---|---|
| `onb.welcome` | Dawn landscape, a lamp being lit on a doorstep | Needed | P0 |
| `onb.tradition` | Several doorways/homes side by side, each with its own kolam/rangoli style — no map, no borders | Needed | P0 |
| `onb.language` | Palm-leaf bundle open with three script ribbons (no readable text) | Needed | P1 |
| `onb.far-home` | Night window in a foreign city skyline with a diya on the sill | Needed | P1 |

### 2.4 Per puja (launch set)
For each puja: **hero** (festival card + puja header, 1080×720, transparent bg) and **setup diagram**
(1080×1080, positions numbered by overlay in UI, not in the image). Katha scenes optional.

| Puja | `puja.<id>.hero` | `puja.<id>.setup` (per region variant) | `puja.<id>.katha-1..3` |
|---|---|---|---|
| Vinayaka Chavithi | Needed P0 | Telugu Needed P0 · Marathi P1 | P2 |
| Varalakshmi Vratam | Needed P0 | Telugu Needed P0 · Kannada P1 · Tamil P1 | P2 |
| Satyanarayana Vratam | Needed P0 | Telugu Needed P0 · others P1 | P2 |
| [Other launch festivals] | Needed | Needed | P2 |

### 2.4b Deities (in form)
| ID | Asset | Size | Pri |
|---|---|---|---|
| `deity.ganesha` | Gaṇeśa in form — nature, power, stature, wisdom, intellect; with vāhana and attributes | 1080×1350, transparent | P0 |
| `deity.lakshmi` · `deity.venkateswara` · … | One per deity added to content | 1080×1350 | P1 |

### 2.5 Per dish (one per dish record, per regional form)
Shown on its regional serving surface (banana leaf, brass plate, etc.). 1080×720, transparent bg.

| ID | Dish | Region | Pri |
|---|---|---|---|
| `dish.undrallu` | Undrallu | Andhra / Telangana | P0 |
| `dish.kudumulu` | Kudumulu | Andhra / Telangana | P1 |
| `dish.modak` | Modak (steamed, ukadiche) | Maharashtra | P0 |
| `dish.kozhukattai` | Kozhukattai | Tamil Nadu | P1 |
| `dish.kadubu` | Kadubu | Karnataka | P1 |
| `dish.satyanarayana-prasadam` | Prasadam (sooji, banana, ghee) | per region | P0 |
| `dish.<id>` | Every other dish linked from a launch puja | — | P0/P1 |

### 2.6 Samagri items (shared set, 96×96 icon style, same as §5 icons)
Turmeric · kumkum · akṣatas · flowers · garland · betel leaves · areca nut · coconut · banana · other fruits ·
camphor · incense · diya · ghee · cotton wicks · kalaśam · mango leaves · garika (dūrvā) · bilva · tulasi ·
sandal paste · rice · jaggery · milk · clay idol · turmeric Gaṇapati · bell · plate · water vessel · cloth (vastram) ·
sacred thread · lotus. **~32 icons, P1** (text-only rows are acceptable for MVP).

### 2.7 Empty states (4, P1)
`empty.no-sankalpa` (three unlit diyas) · `empty.no-history` (a closed palm-leaf bundle) ·
`empty.offline` (landscape with moon, "saved on this phone") · `empty.no-festival` (quiet day landscape).

---

## 3. File spec

| Kind | Deliver as | Size |
|---|---|---|
| System, icons, samagri | SVG (flat shapes only, no filters/masks) | 48 or 96 viewBox |
| Heroes, setup, dishes, onboarding | SVG preferred; else PNG @3x → converted to WebP | 1080 wide, transparent |
| Store assets | PNG | Play Store sizes |

Naming: `<kind>.<id>[.<region>].svg` — e.g. `puja.vinayaka-chavithi.setup.telugu.svg`, `dish.undrallu.svg`.
Lowercase, hyphens, ASCII only.

---

## 4. Pipeline (MVP: repo folder, no upload UI)

1. Drop files into `content/illustrations/` in the repo.
2. Add an entry to `content/illustrations/manifest.json`:
   ```json
   { "id": "dish.undrallu", "file": "dish.undrallu.svg", "kind": "dish",
     "usedBy": ["dish:undrallu"], "alt": { "te": "…", "hi": "…", "en": "Undrallu on a banana leaf" },
     "status": "final" }
   ```
3. The content pipeline validates (file exists, palette check on SVG fills, size, alt text present) and packs
   illustrations into the **content pack**, so art can update without a Play Store release.
4. System icons and landscapes are compiled into the app (`ui/icons`, `ui/landscape`).
5. **Missing asset rule:** the UI shows the module-tint panel with the module icon — never a blank box or broken image.

Later (Phase 2): the admin dashboard gets an upload screen that writes the same manifest fields and storage path.
Nothing in the app changes.

---

## 5. Decisions needed from you
1. Full list of launch festivals and which region variants ship first (drives §2.4 and §2.5).
2. Who produces the art — you, an illustrator, or generated then cleaned up? (Generated art must be redrawn/cleaned to the palette before commit.)
