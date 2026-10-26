# Property Tax Checker — v1.2.0

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
