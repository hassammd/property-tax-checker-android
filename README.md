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

## v1.6.0 — New hero image, text lines fixed, Home icons matched to reference
- Hero banner: user's latest house image (light blue blob background) replaces
  the v1.5 black-background image, saved as hero_banner.webp 900x600 (53 KB).
  Image SHRUNK to 130x87dp in the hero card (was 180x122) so the text column is
  wider.
- Text lines: hero heading "Know Your / Property Tax" is now 24sp with
  maxLines=2 (was 28sp, wrapping to more lines on narrow text column); subtext
  is 13sp with maxLines=3 (was 14sp, wrapping to 4+ lines). Reference layout
  (heading 2 lines, subtext 3 lines) now holds.
- Home icons matched to the blue reference, re-checked against it:
  * Top header logo UNCHANGED (user: perfect) — asset-sheet house + gold leaf.
  * Quick Actions icons are now PROPER CIRCLES: 56dp OVAL Soft #D9E9FF circle,
    28dp Primary icon centered (was a 14dp-radius rounded square). Tax Rates =
    file-text document icon, History = clock-with-arrow icon (new
    lucide_history: Lucide history geometry, arc + arrowhead + hands), matching
    the reference History icon which is not a plain clock.
  * Bottom nav: Home = SOLID filled house when selected (new
    lucide_house_filled, door cutout in drawable, untinted so the cutout stays),
    outline house when unselected; Calculator = calculator outline; History =
    clock-with-arrow; Settings = gear outline — as in the reference nav.
  * Info strip: proper filled Primary 48dp circle with white "i" (reference
    style), was an outline-info icon in a pill.
  * Profile: proper Soft 48dp circle with FILLED person silhouette (new
    lucide_user_filled), matching the reference profile circle.
  * Calculator field icons and Settings row icons are now plain Primary icons
    (no box), as in the mockup field/icon rows.

## v1.6.1 — Hero image enlarged (user screenshot feedback)
- Problem in v1.6.0 (user screenshot): hero image looked too small because the
  new light image has ~26% empty light area on its left, and the box was only
  130x87dp, so the house itself filled only ~78dp of the card.
- Fix: image CROPPED to the house+blob (left 400px, top 80px, right/bottom
  edges kept; 1136x900, aspect 1.26) so the house fills the frame, then saved
  as hero_banner.webp 900x713 (89 KB). Box enlarged to 160x127dp (was
  130x87dp) — house is now ~2x wider on screen than in v1.6.0.
- Text lines protected despite the bigger image: hero padding 20->16dp,
  heading 24->22sp (still maxLines 2), subtext 13->12sp (still maxLines 3),
  8dp gap between text and image. On the user's phone width the heading
  stays 2 lines and the subtext stays 3 lines.

## v1.6.2 — Light colour behind hero image removed (user screenshot, orange mark)
- User's screenshot (orange mark at top of hero image) showed a pale-blue blob
  + light background patch behind the house, distinct from the hero card
  (#EAF3FF). Size was perfect in v1.6.1, so size/layout are UNCHANGED
  (160x127dp, heading 2 lines, subtext 3 lines).
- Fix: hero_banner.webp reprocessed from the v1.6.1 crop — (1) flood-fill
  background from the 4 corners (thresh 40, stops at the blob), (2) pale-blue
  blob pixels removed (R 140-230, B-R > 35, G-R > 15, B > 200 -> transparent).
  House, roof, windows, door, trees, bushes and grass are intact; the house
  now sits directly on the card background with no light patch behind it.
  A stronger blob rule was tried and REJECTED — it ate the house-wall shading
  and the clouds, so the gentler rule above was kept. Side effect to know:
  clouds are white, so on the card's very light background they now look
  fainter than they did on the darker blob — that is inherent to removing
  the blob, not a defect in the house.

## v1.6.3 — Sky + clouds restored, only the colour behind them removed (user correction)
- CORRECTION to v1.6.2: v1.6.2 removed too much — it removed the pale-blue
  sky blob AND the clouds. User clarified: sky and clouds must STAY; only the
  light colour BEHIND them was to be removed.
- Fix: hero_banner.webp rebuilt from the v1.6.1 crop with ONLY flood-fill
  background removal from the 4 corners (thresh 40, stops at the sky blob).
  No blob-pixel removal at all. Result, verified on the card colour #EAF3FF:
  sky blob intact, clouds solid white intact, house/trees/grass intact, outer
  background transparent (card colour shows around the blob, no white
  rectangular patch). Size/layout UNCHANGED (160x127dp, heading 2 lines,
  subtext 3 lines). Banner is now in place; next page work can follow.

## v1.7.0 — Reference font (Poppins) app-wide + info icon matched
- Font identified from the reference (orange-circled heading, zoomed):
  Poppins, Google Fonts (OFL, free). Evidence: geometric sans, near-perfect
  circle "o", straight "y" descender, flat terminals, double-storey "a" —
  Nunito's softer rounded "y"/"g" do not match the reference letters.
  Downloaded from github.com/google/fonts (ofl/poppins): Poppins-Regular.ttf
  + Poppins-Bold.ttf (+ OFL.txt), verified with fontTools, stored in
  assets/fonts/ and shipped inside the APK (build.sh now zips assets in).
- Applied app-wide: every TextView (tv helper), Buttons, EditText and
  RadioButtons now use Poppins — Regular for body, Bold for headings/buttons.
  No screen is left on the default Android font.
- Info icon matched to reference: filled Primary 48dp circle with a white
  "i" — now in Poppins Bold, so the "i" dot/stem shape matches the reference
  "i" exactly (previously Roboto Bold). Circle size, divider and strip
  layout unchanged from v1.6 (which already matched the reference strip).

## v1.7.1 — Home compacted for banner ad space + info icon centered
- Home compacted (user screenshot, orange marks on info strip + Quick
  Actions): info strip padding 16->12dp, text 14->13sp, divider 40->32dp;
  Quick Actions heading 22->19sp, cards padding 16->12dp, icon circles
  56->48dp with 24dp icons (was 28dp), titles 16->15sp, subs 13->12sp,
  arrows 18->16dp, card gap 12->8dp; header/hero spacings also trimmed.
  Total Home height saved is roughly the height of the banner slot below.
- Info icon: smaller AND properly centered — was a 48dp circle with a
  20sp "i" inside a nested LinearLayout (font padding shifted the glyph
  off-center). Now the TextView ITSELF is the circle: 40dp oval, 16sp
  Poppins Bold "i", includeFontPadding=false, gravity CENTER — so the
  "i" sits on the circle's center line, not above/below it.
- Banner ad slot: fixed 50dp (standard banner height) strip at the bottom
  of Home, above the bottom nav, outside the ScrollView so it never
  scrolls away — Soft background with "Banner Ad" label for now. This is
  a RESERVED SLOT, not a live AdMob ad: a live banner needs AdMob SDK +
  real ad unit IDs, which this app does not have yet. When IDs arrive,
  the real AdView replaces this placeholder 1:1 in the same slot.

## v1.7.2 — Home info strip removed
- Home se info section poora hata diya gaya (user order): blue "i"
  circle + divider + "Tax rates depend on transaction type, filer status
  and property value." text — sab khatam. Home ab: header -> hero card
  -> Quick Actions -> banner ad slot -> bottom nav. Verified: source
  aur APK dex dono me wo text maujood nahi hai (v1.7.1 dex me 1 dafa
  tha, v1.7.2 dex me 0). Tax Rates screen, FAQs aur baqi screens
  unchanged — sirf Home ka info strip hata hai.

## v1.7.3 — Calculator page rebuilt from reference (100% like design, Home header)
- Calculator screen rebuilt from user's reference image (2026-10-27):
  Home header (logo + Property Tax/Checker + profile circle) INSTEAD of
  the reference's back-arrow + title + calculator-icon top bar, per user
  order — that top bar is gone, navigation is via bottom nav like Home.
- Title "Calculate Your Tax" 28sp + subtext 15sp; new banner card
  (LIGHT, radius 20): chip "TAX CALCULATION" (SOFT pill, Secondary bold
  11sp) + "Know Your Property Tax" 22sp + illustration on the right.
- Illustration: cropped from the reference screenshot itself (house +
  blue calculator + TAX paper + gold coins + bushes, 449x257, corners
  flood-filled transparent like hero_banner, saved as
  res/drawable-nodpi/calc_banner.webp, 12,284 bytes). Honest limit: it
  is a crop, not a separate transparent asset — if the user sends the
  original illustration, it swaps 1:1.
- Toggle now a true pill: container radius 24, buttons radius 20,
  48dp min height, selected Primary / unselected grey. Field labels
  16sp with 24dp icons; Property Value input radius 16; radios 16sp
  with Primary tint when checked / grey ring when not; Calculate Tax
  button unchanged pill (already matched). Bottom nav Calculator
  selected unchanged. Calculation + tests unchanged (15/15).
