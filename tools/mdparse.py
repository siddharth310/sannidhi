"""Tiny parser for the content .md format (see docs/content-architecture.md §5–6).

A file is YAML front matter + headings. Under any heading:
  * leading `key: value` lines (YAML, may be multi-line) → `keys`
  * a markdown table → `table` (list of dicts)
  * `#### te` / `#### hi` / `#### en` blocks → `langs`; a line with only `---` splits variants
"""
from __future__ import annotations

import re
from dataclasses import dataclass, field

import yaml

LANGS = ("te", "hi", "en")
_HEADING = re.compile(r"^(#{2,4})\s+(.*?)\s*$")


class ContentError(Exception):
    pass


@dataclass
class Section:
    level: int
    title: str
    lines: list[str] = field(default_factory=list)
    children: list["Section"] = field(default_factory=list)

    # -- accessors --
    def child(self, title: str) -> "Section | None":
        return next((c for c in self.children if c.title == title), None)

    def keys(self, path: str) -> dict:
        block: list[str] = []
        for ln in self.lines:
            if ln.startswith("|"):
                break
            block.append(ln)
        text = "\n".join(block).strip()
        if not text:
            return {}
        try:
            data = yaml.safe_load(text)
        except yaml.YAMLError as e:
            raise ContentError(f"{path}: bad key lines under '{self.title}': {e}") from e
        if not isinstance(data, dict):
            raise ContentError(f"{path}: expected 'key: value' lines under '{self.title}', got prose")
        return data

    def table(self) -> list[dict]:
        rows = [ln for ln in self.lines if ln.startswith("|")]
        if len(rows) < 2:
            return []
        split = lambda r: [c.strip() for c in r.strip().strip("|").split("|")]
        head = split(rows[0])
        return [dict(zip(head, split(r))) for r in rows[2:]]

    def langs(self, variants: bool = False) -> dict:
        out: dict = {}
        for c in self.children:
            if c.level == 4 and c.title in LANGS:
                body = "\n".join(c.lines).strip()
                parts = [p.strip() for p in re.split(r"(?m)^---\s*$", body) if p.strip()]
                if variants:
                    out[c.title] = parts
                elif parts:
                    out[c.title] = "\n\n".join(parts) if len(parts) > 1 else parts[0]
        return out


def parse(path: str) -> tuple[dict, Section]:
    text = open(path, encoding="utf-8").read()
    fm: dict = {}
    if text.startswith("---"):
        end = text.find("\n---", 3)
        if end < 0:
            raise ContentError(f"{path}: front matter not closed with ---")
        try:
            fm = yaml.safe_load(text[3:end]) or {}
        except yaml.YAMLError as e:
            raise ContentError(f"{path}: front matter: {e}") from e
        text = text[end + 4:]
    root = Section(1, "")
    stack = [root]
    for ln in text.splitlines():
        m = _HEADING.match(ln)
        if m:
            level = len(m.group(1))
            title = re.sub(r"\s+#\s.*$", "", m.group(2)).strip()   # drop trailing "# comment"
            sec = Section(level, title)
            while stack[-1].level >= level:
                stack.pop()
            stack[-1].children.append(sec)
            stack.append(sec)
        else:
            stack[-1].lines.append(ln)
    return fm, root


def slug_title(title: str) -> tuple[str, str]:
    """'mandapam | Maṇḍapa sthāpana' → ('mandapam', 'Maṇḍapa sthāpana')."""
    if "|" in title:
        a, b = title.split("|", 1)
        return a.strip(), b.strip()
    return title.strip(), ""
