#!/usr/bin/env python3
"""Import a verified 108-name dataset into a content/texts/*.yaml file. Never type names by hand.

    python tools/import_ashtottara.py path/to/ganesha_ashtottara.json content/texts/ganesha.ashtottara.yaml

Accepts a JSON list (or {"names": [...]}) whose items carry the number in one of
`n | number | index | id` and the Devanagari in one of `sa | devanagari | deva | text | name`.
Items flagged in the source (`status`/`flag` = CORRECTED, FLAGGED …) keep that flag so review can see it.
The yaml header (id, title, status, audio …) is kept; only `names:` is replaced.
"""
import json
import re
import sys

import yaml

NUM_KEYS = ("n", "number", "index", "id")
SA_KEYS = ("sa", "devanagari", "deva", "text", "name")
FLAG_KEYS = ("flag", "status", "validation", "tier")


def pick(d, keys):
    for k in keys:
        if k in d and d[k] not in (None, ""):
            return d[k]
    return None


def main(src, dst):
    data = json.load(open(src, encoding="utf-8"))
    items = data.get("names", data) if isinstance(data, dict) else data
    names = []
    for i, it in enumerate(items, 1):
        sa = pick(it, SA_KEYS)
        if not sa or not re.search(r"[ऀ-ॿ]", str(sa)):
            sys.exit(f"item {i}: no Devanagari text found (looked for {SA_KEYS})")
        num = pick(it, NUM_KEYS)
        if num is not None and str(num).isdigit() and int(num) != i:
            sys.exit(f"item {i}: number says {num}; fix order in the source first")
        flag = pick(it, FLAG_KEYS)
        entry = {"n": i, "sa": " ".join(str(sa).split())}
        if flag and str(flag).upper() not in ("VERIFIED", "OK", "PASS"):
            entry["flag"] = str(flag)
        names.append(entry)
    head = open(dst, encoding="utf-8").read()
    head = re.split(r"(?m)^names:.*$", head)[0].rstrip() + "\n"
    body = yaml.safe_dump({"names": names}, allow_unicode=True, sort_keys=False, default_flow_style=None, width=200)
    open(dst, "w", encoding="utf-8").write(head + body)
    flagged = sum(1 for n in names if "flag" in n)
    print(f"imported {len(names)} names into {dst}; {flagged} carry a review flag")


if __name__ == "__main__":
    if len(sys.argv) != 3:
        sys.exit(__doc__)
    main(sys.argv[1], sys.argv[2])
