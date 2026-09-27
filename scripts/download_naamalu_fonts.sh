#!/usr/bin/env bash
# Downloads OFL fonts for Naamalu v3 into app/src/main/res/font/
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
FONT_DIR="$ROOT/app/src/main/res/font"
mkdir -p "$FONT_DIR"

# Google Fonts git raw (subset files — adjust commits as needed)
BASE="https://github.com/google/fonts/raw/main"

download() {
  local path="$1"
  local out="$2"
  if [[ -f "$FONT_DIR/$out" ]]; then
    echo "skip $out"
    return
  fi
  echo "fetch $out"
  curl -fsSL "$BASE/$path" -o "$FONT_DIR/$out"
}

download "ofl/fraunces/Fraunces%5Bopsz,wght%5D.ttf" "fraunces_variable.ttf"
download "ofl/instrumentsans/InstrumentSans%5Bwght%5D.ttf" "instrument_sans_variable.ttf"
download "ofl/notoseriftelugu/NotoSerifTelugu%5Bwght%5D.ttf" "noto_serif_telugu.ttf"
download "ofl/notoserifdevanagari/NotoSerifDevanagari%5Bwght%5D.ttf" "noto_serif_devanagari.ttf"
download "ofl/notoseriftamil/NotoSerifTamil%5Bwght%5D.ttf" "noto_serif_tamil.ttf"
download "ofl/notoserifkannada/NotoSerifKannada%5Bwght%5D.ttf" "noto_serif_kannada.ttf"
download "ofl/notosanstelugu/NotoSansTelugu%5Bwght%5D.ttf" "noto_sans_telugu.ttf"
download "ofl/notosansdevanagari/NotoSansDevanagari%5Bwght%5D.ttf" "noto_sans_devanagari.ttf"

echo "Done. Update NaamaluFontFamilies.kt to reference R.font.* when ready."
