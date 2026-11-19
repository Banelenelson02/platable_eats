"""Optimise photographs without changing the Plateable Eats animated GIF.

Converts only PNGs referenced by the HTML page to WebP when that output is
smaller. Runs once on an isolated GitHub Actions branch and commits results.
The original PNGs are retained for rollback.
"""
import json
import re
from pathlib import Path
from PIL import Image, ImageOps

ROOT = Path(__file__).resolve().parents[1]
HTML = ROOT / "src/main/resources/static/index.html"
IMAGES = ROOT / "src/main/resources/static/images"
markup = HTML.read_text(encoding="utf-8")
paths = list(dict.fromkeys(re.findall(r'src="images/([^"]+\.png)"', markup)))
if not paths:
    raise RuntimeError("No PNG references found in the HTML; check the source before continuing")

before = after = converted = 0
for name in paths:
    source = IMAGES / name
    if not source.is_file():
        raise FileNotFoundError(source)
    target = source.with_suffix(".webp")
    with Image.open(source) as raw:
        img = ImageOps.exif_transpose(raw)
        if img.mode not in ("RGB", "RGBA"):
            img = img.convert("RGBA" if "A" in img.getbands() else "RGB")
        img.save(target, "WEBP", quality=78, method=6)
    initial = source.stat().st_size
    optimized = target.stat().st_size
    before += initial
    if optimized >= initial:
        target.unlink()
        after += initial
        continue
    after += optimized
    markup = markup.replace('images/' + name + '"', 'images/' + target.name + '"')
    converted += 1

HTML.write_text(markup, encoding="utf-8")
summary = {
    "converted": converted,
    "considered": len(paths),
    "beforeKB": round(before/1024,1),
    "afterKB": round(after/1024,1),
    "savedPercent": round(100*(before-after)/before,1) if before else 0
}
print("IMAGE_OPTIMIZATION_SUMMARY " + json.dumps(summary), flush=True)
assert 'images/loader.gif' in markup, "Original loader GIF missing"
assert converted > 0, "No images were converted"
