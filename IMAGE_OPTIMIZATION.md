# Plateable Eats image quality and delivery

## Quality requirements

The original 18 referenced PNG food photos are preserved in this repository. The original animated \`images/loader.gif\` remains intact, and no images are resized or visually altered.

The conversion script uses **lossless WebP** with a full decoded RGBA pixel comparison and an ICC profile check. If the converted file is not smaller, the page keeps the original PNG.

GitHub Actions result on 19 November 2026:
- Original 18 food PNGs: **5909.1 KB**
- Final delivered equivalents: **5562.7 KB**
- Reduction: **5.9%**, without changing visible pixels
- **1** photo (Pizza) delivered as lossless WebP
- **17** photos delivered as original PNGs
- Intro GIF unchanged

This is a modest bandwidth improvement, not a measured page-load speed or Web Vitals improvement.

## Other loading improvements

- Below-the-fold photos use \`loading="lazy"\` and \`decoding="async"\`.
- The first featured photo and the animated intro receive high fetch priority.
- Existing Vercel CDN cache-control headers remain configured for \`/images/*\`, \`/css/*\`, and \`/script/*\`.
- The app remains a **single Spring Boot/Docker deployment**. No new frontend host or API separation has been introduced.
- A temporary \`public/\` routing probe was removed because CDN bypass behaviour could not be reliably verified in a database-less Vercel preview.

Do not replace these with lossy encodes, reduce photo dimensions, or change the existing intro animation without asking the project owner. Preserve image quality and the current visual design first.

## Validation

- All dessert cards use the same circular **+** button class and basket logic as the main dishes.
- Playwright checks the hero image, dessert image, basket and checkout confirmation in an emulated mobile viewport using mocked APIs.
- Java CI runs independently.
