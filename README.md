# Property Tax Checker — v1.3.0

Pakistan FBR advance tax calculator for buying / selling property.
UI follows the user's screenshot set: Welcome, Home dashboard,
Calculator, Tax Result, Calculation History, Transaction Details,
Tax Rates, Settings (About / Tax Rates / FAQs / Privacy / Contact).

## Calculation — FBR table (user's screenshot)

Sale of Property — Section 236C:
- Filer: 2.75% (all values)
- Non-Filer: 11.5% (all values)

Purchase of Property — Section 236K:
- Filer: 1.25% (all values)
- Non-Filer: under 50,000,000 → 10.5%
- Non-Filer: 50,000,000 to under 100,000,000 → 14.5%
- Non-Filer: 100,000,000 and above → 18.5%

Boundary note: the table words the buying non-filer slabs as
"up to 50 Million", "50 Million to 100 Million", "100 Million & above",
which overlap at exactly 50M and 100M. The thresholds above reproduce
the screenshot's own history example (Buying Plot PKR 50,000,000 at
14.5% = PKR 7,250,000). See TaxCalc.java for the one-line change if
50,000,000 itself should instead fall in the 10.5% slab.

Property type and province do NOT change the FBR advance-tax rate in
this table; they are recorded with the calculation. Provincial duties,
stamp duty and registration fees are NOT included — the app says so
on the Result screen and in Tax Rates.

## Verified examples (test/TestTaxCalc.java, 15/15 pass)
- Buying 10,000,000 filer → 125,000 (1.25%)
- Selling 25,000,000 filer → 687,500 (2.75%)
- Buying 50,000,000 non-filer → 7,250,000 (14.5%)
- Selling 18,000,000 filer → 495,000 (2.75%)
- Buying 10,000,000 non-filer → 1,050,000 (10.5%)
- Buying 120,000,000 non-filer → 22,200,000 (18.5%)
- Selling 25,000,000 non-filer → 2,875,000 (11.5%)

## Build
./build.sh  (manual aapt2/javac/d8 pipeline, no Gradle; JDK 17 + Android SDK 34)
Output: your_files/property-tax-app/property-tax-checker-v1.1.0.apk

Package: com.hatch.propertytax — versionCode 1, versionName 1.0.0,
minSdk 24, targetSdk 34. Debug-signed. History is stored only on-device
(SharedPreferences); no account, no network.

## v1.2.0 — Blue theme + new Home (user palette, green dropped)
Palette (user-supplied): Primary #173F7A, Secondary #2456A6, Accent Blue #4F8FE8,
Main Background #F7FAFE, Card #FFFFFF, Hero #EAF3FF, Soft Blue #D9E9FF,
Border #D7E5FA, Primary Text #18365F, Secondary Text #536B8E,
Muted Text #7588A3, Gold Accent #D9A441 (logo document badge).
Home now matches the user's second (blue) homepage: header with house+gold-badge
logo and profile circle, hero card (YOUR PROPERTY / Know Your Property Tax /
Calculate Tax button), info strip, Quick Actions (Tax Rates + History only),
slogan strip (Smart Tax Decisions for a Better Tomorrow). Settings/Calculator/
Result/History use the same blue palette; green theme is gone (illustration
trees stay green, as in the design image itself).
Images/icons: hero house photo is Pexels photo 106399 (Pexels License, free to
use, https://images.pexels.com/photos/106399/); other icons are Material-style
outline vectors drawn in-app (Google Material Icons' GitHub paths returned 404
for direct download, and unclear-license real-estate images from web search were
not copied).

## v1.3.0 — Homepage rebuilt from the asset sheet (blue reference)
Home rebuilt from the user's asset sheet + blue homepage reference:
- Logo: asset-sheet App Icon / Logo (blue house outline + gold leaf) — header logo
  and launcher icon both use it now (gold document badge from v1.2 retired).
- Hero: illustration cropped from the asset sheet itself
  ("Illustration (Home Screen House)": white house, blue roof, trees, clouds),
  near-black sheet background made transparent, saved as
  res/drawable-nodpi/hero_house_illustration.png. The Pexels photo used in v1.2
  is retired from the app (it did not match the design illustration).
- Nav icons: filled Home, outline Calculator, clock History, gear Settings —
  as in the sheet's "Navigation Icons (Bottom Tab)".
- Buttons/cards/info strip/Quick Actions/slogan follow the sheet + blue
  homepage: Calculate Tax (primary, white), info strip, Tax Rates + History
  cards with soft-blue icon circles, slogan "Smart Tax Decisions for a Better
  Tomorrow". Palette unchanged from v1.2 (user blue palette).
- Note: the asset sheet is a single composite image on a black background, not
  separate transparent asset files, so assets were cropped (hero) or redrawn as
  vectors (logo/icons) to match it. If separate transparent PNGs are supplied,
  they can be swapped in 1:1.

## v1.4.0 — Proper Lucide icons + Province removed
- Icons: all visible icons are now real Lucide library icons (ISC license, free),
  downloaded from github.com/lucide-icons/lucide (icons/ folder) and converted to
  Android vector drawables (res/drawable/lucide_*.xml), tinted at runtime with the
  user's blue palette (Primary #173F7A, Secondary #2456A6, Muted #7588A3, White).
  No more text-symbol icons (⌂/▤/◷/⚙ etc.) in nav, header, Quick Actions,
  Calculator fields, Settings rows, or top-bar actions. Nav: house / calculator /
  clock / settings. Fields: house / user / house. Settings: info / percent /
  circle-question-mark / shield-check / mail. Top bar: calculator / share-2 /
  trash / settings.
- Top header logo: now the asset-sheet logo itself, cropped from the sheet
  (blue house + gold leaf, light tile, black corners made transparent,
  res/drawable-nodpi/logo_asset.png) — not a redraw. The user also offered to
  send a single top icon; when it arrives it will replace this crop 1:1.
- Province REMOVED (user, 2026-10-26): same rate applies in every province, so
  Calculator no longer asks for Province, Details no longer shows it, and FAQs
  say the app does not ask for province. Old saved history (v1.0-v1.3, with
  province) still loads — fromLine handles both formats, province ignored.

## v1.5.0 — New hero banner image, slogan removed, Property Type removed
- Hero banner: user's new house image (white house, blue roof, trees, clouds)
  used in the Home hero card. The sent file has a solid black background, so
  pure-black pixels were made transparent (threshold 25; roof is blue and door
  is grey, so neither is affected) and the image was trimmed and saved as
  WebP (res/drawable-nodpi/hero_banner.webp, 900x607, 162 KB) to keep the APK
  small. The v1.3 asset-sheet hero illustration is retired.
- Slogan REMOVED (user, 2026-10-26): the "Smart Tax Decisions for a Better
  Tomorrow" strip at the bottom of Home is gone, logo + divider with it. Home
  now ends at Quick Actions.
- Property Type REMOVED (user, 2026-10-26): like Province, it does not change
  the rate. Calculator no longer asks for it, Details no longer shows it,
  History/Result titles and share text show only Buying/Selling, info strip
  and FAQs updated. Old history (with type/province) still loads — fromLine
  handles v1.0-v1.3 (8 fields), v1.4 (7 fields) and new (6 fields) formats.
- Calculator now: Buying/Selling toggle, Property Value, Taxpayer Status only.
