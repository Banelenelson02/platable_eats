"""Pixel-perfect lossless WebP optimisation for referenced Plateable Eats photos.

Every converted image is byte-for-byte equivalent in decoded RGBA pixels to its
original PNG. When lossless WebP is not smaller, retain the original PNG.
Original PNGs always remain in Git. Never modify the intro animation GIF.
"""
import json
import re
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
HTML = ROOT / "src/main/resources/static/index.html"
IMAGES = ROOT / "src/main/resources/static/images"
markup = HTML.read_text(encoding="utf-8")
paths = list(dict.fromkeys(re.findall(r'src="images/([^"]+)\.(?:png|webp)"', markup)))
if not paths:
    raise RuntimeError("No PNG/WebP image references found in the HTML.")

original_bytes = optimized_bytes = converted = retained_png = 0
for stem in paths:
    source = IMAGES / (stem + ".png")
    target = IMAGES / (stem + ".webp")
    if not source.is_file():
        raise FileNotFoundError(f"Original PNG missing: {source}")

    with Image.open(source) as raw:
        # Do not resize, change orientation, or alter any colour channels.
        has_alpha = "A" in raw.getbands() or "transparency" in raw.info
        pixels = raw.convert("RGBA" if has_alpha else "RGB")
        save_options = {"format": "WEBP", "lossless": True, "method": 6, "exact": True}
        if raw.info.get("icc_profile"):
            save_options["icc_profile"] = raw.info["icc_profile"]
        pixels.save(target, **save_options)
        reference_pixels = raw.convert("RGBA").tobytes()
        reference_size = raw.size
        reference_icc = raw.info.get("icc_profile")

    with Image.open(target) as decoded:
        if decoded.size != reference_size or decoded.convert("RGBA").tobytes() != reference_pixels:
            raise AssertionError(f"Pixel mismatch in lossless WebP: {stem}")
        if reference_icc and decoded.info.get("icc_profile") != reference_icc:
            raise AssertionError(f"Colour profile changed: {stem}")

    original_bytes += source.stat().st_size
    use_webp = target.stat().st_size < source.stat().st_size
    if use_webp:
        optimized_bytes += target.stat().st_size
        converted += 1
        chosen = stem + ".webp"
    else:
        optimized_bytes += source.stat().st_size
        retained_png += 1
        target.unlink()
        chosen = stem + ".png"

    for extension in (".png", ".webp"):
        markup = markup.replace('images/' + stem + extension + '"', 'images/' + chosen + '"')

if 'images/loader.gif' not in markup:
    raise AssertionError("Original loader GIF must not be altered or removed.")
HTML.write_text(markup, encoding="utf-8")
print("LOSSLESS_IMAGE_SUMMARY " + json.dumps({
    "photos": len(paths),
    "losslessWebP": converted,
    "originalPNG": retained_png,
    "originalKB": round(original_bytes / 1024, 1),
    "resultKB": round(optimized_bytes / 1024, 1),
    "savedPercent": round(100 * (original_bytes - optimized_bytes) / original_bytes, 1)
}), flush=True)
