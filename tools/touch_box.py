"""Bounds of a story drawing's touch group, as fractions of the picture."""
from __future__ import annotations

import math
import re
import xml.etree.ElementTree as ET

IDENT = (1, 0, 0, 1, 0, 0)
NUM = r"[-+]?(?:\d*\.\d+|\d+)(?:[eE][-+]?\d+)?"


def _mul(m1, m2):
    a1, b1, c1, d1, e1, f1 = m1
    a2, b2, c2, d2, e2, f2 = m2
    return (
        a1 * a2 + c1 * b2, b1 * a2 + d1 * b2,
        a1 * c2 + c1 * d2, b1 * c2 + d1 * d2,
        a1 * e2 + c1 * f2 + e1, b1 * e2 + d1 * f2 + f1,
    )


def _apply(m, x, y):
    a, b, c, d, e, f = m
    return a * x + c * y + e, b * x + d * y + f


def _transform(text):
    m = IDENT
    if not text:
        return m
    for kind, args in re.findall(r"(matrix|translate|scale|rotate|skewX|skewY)\s*\(([^)]*)\)", text):
        nums = [float(x) for x in re.findall(NUM, args)]
        if kind == "translate":
            local = (1, 0, 0, 1, nums[0], nums[1] if len(nums) > 1 else 0)
        elif kind == "scale":
            sx = nums[0]
            local = (sx, 0, 0, nums[1] if len(nums) > 1 else sx, 0, 0)
        elif kind == "rotate":
            ang = math.radians(nums[0])
            ca, sa = math.cos(ang), math.sin(ang)
            local = (ca, sa, -sa, ca, 0, 0)
            if len(nums) >= 3:
                cx, cy = nums[1], nums[2]
                local = _mul(_mul((1, 0, 0, 1, cx, cy), local), (1, 0, 0, 1, -cx, -cy))
        elif kind == "matrix" and len(nums) >= 6:
            local = tuple(nums[:6])
        else:
            local = IDENT
        m = _mul(m, local)
    return m


def _path_points(d):
    tokens = re.findall(r"[MmLlHhVvCcSsQqTtAaZz]|" + NUM, d)
    i = 0
    cmd = None
    cx = cy = sx = sy = 0.0
    pts = []

    def num():
        nonlocal i
        value = float(tokens[i])
        i += 1
        return value

    while i < len(tokens):
        if re.match(r"[A-Za-z]", tokens[i]):
            cmd = tokens[i]
            i += 1
            if cmd in "Zz":
                cx, cy = sx, sy
                pts.append((cx, cy))
            continue
        if cmd is None:
            break
        rel = cmd.islower()
        kind = cmd.upper()
        try:
            if kind == "M":
                x, y = num(), num()
                if rel:
                    x, y = cx + x, cy + y
                cx, cy = sx, sy = x, y
                pts.append((cx, cy))
                cmd = "l" if rel else "L"
            elif kind == "L":
                x, y = num(), num()
                if rel:
                    x, y = cx + x, cy + y
                cx, cy = x, y
                pts.append((cx, cy))
            elif kind == "H":
                x = num()
                cx = cx + x if rel else x
                pts.append((cx, cy))
            elif kind == "V":
                y = num()
                cy = cy + y if rel else y
                pts.append((cx, cy))
            elif kind == "C":
                got = [(num(), num()) for _ in range(3)]
                if rel:
                    got = [(cx + x, cy + y) for x, y in got]
                pts.extend(got)
                cx, cy = got[-1]
            elif kind in "SQ":
                got = [(num(), num()) for _ in range(2)]
                if rel:
                    got = [(cx + x, cy + y) for x, y in got]
                pts.extend(got)
                cx, cy = got[-1]
            elif kind == "T":
                x, y = num(), num()
                if rel:
                    x, y = cx + x, cy + y
                cx, cy = x, y
                pts.append((cx, cy))
            elif kind == "A":
                for _ in range(5):
                    num()
                x, y = num(), num()
                if rel:
                    x, y = cx + x, cy + y
                cx, cy = x, y
                pts.append((cx, cy))
            else:
                break
        except IndexError:
            break
    return pts


def _one(value):
    if value is None or not re.fullmatch(r"\s*" + NUM + r"\s*", value):
        return None
    return float(value)


def _add(el, matrix, pts):
    tag = el.tag.split("}")[-1]
    attrs = el.attrib

    def num(name, default=0.0):
        value = _one(attrs.get(name))
        return default if value is None else value

    if tag == "rect":
        x, y, w, h = num("x"), num("y"), num("width"), num("height")
        corners = ((x, y), (x + w, y), (x, y + h), (x + w, y + h))
        pts.extend(_apply(matrix, px, py) for px, py in corners)
    elif tag == "circle":
        cx, cy, r = num("cx"), num("cy"), num("r")
        pts.extend(_apply(matrix, px, py) for px, py in ((cx - r, cy), (cx + r, cy), (cx, cy - r), (cx, cy + r)))
    elif tag == "ellipse":
        cx, cy, rx, ry = num("cx"), num("cy"), num("rx"), num("ry")
        pts.extend(_apply(matrix, px, py) for px, py in ((cx - rx, cy), (cx + rx, cy), (cx, cy - ry), (cx, cy + ry)))
    elif tag == "line":
        pts.extend(_apply(matrix, px, py) for px, py in ((num("x1"), num("y1")), (num("x2"), num("y2"))))
    elif tag in ("polygon", "polyline"):
        nums = [float(x) for x in re.findall(NUM, attrs.get("points", ""))]
        pts.extend(_apply(matrix, nums[i], nums[i + 1]) for i in range(0, len(nums) - 1, 2))
    elif tag == "path" and attrs.get("d"):
        pts.extend(_apply(matrix, x, y) for x, y in _path_points(attrs["d"]))


def _walk(el, parent, pts):
    matrix = _mul(parent, _transform(el.get("transform")))
    _add(el, matrix, pts)
    for child in list(el):
        _walk(child, matrix, pts)


def touch_box(path):
    """Return [x, y, width, height] as fractions of the viewBox, or None."""
    try:
        root = ET.parse(path).getroot()
    except ET.ParseError:
        return None
    view = root.get("viewBox")
    if not view:
        return None
    vx, vy, vw, vh = (float(n) for n in view.split())
    if vw <= 0 or vh <= 0:
        return None
    groups = [el for el in root.iter() if (el.get("id") or "").startswith("touch-")]
    if len(groups) != 1:
        return None
    pts = []
    _walk(groups[0], IDENT, pts)
    if not pts:
        return None
    xs = [p[0] for p in pts]
    ys = [p[1] for p in pts]
    x0, y0, x1, y1 = min(xs) - 12, min(ys) - 12, max(xs) + 12, max(ys) + 12
    x0, y0 = max(vx, x0), max(vy, y0)
    x1, y1 = min(vx + vw, x1), min(vy + vh, y1)
    if x1 <= x0 or y1 <= y0:
        return None
    return [round((x0 - vx) / vw, 3), round((y0 - vy) / vh, 3), round((x1 - x0) / vw, 3), round((y1 - y0) / vh, 3)]
