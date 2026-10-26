package com.hatch.propertytax;

import java.util.Locale;

/**
 * FBR advance tax on property sale / purchase.
 *
 * Source: user's FBR table screenshot (Company / Individual / AOP).
 *
 * Sale of Property - Section 236C:
 *   Filer:     2.75% (all values)
 *   Non-Filer: 11.5% (all values)
 *
 * Purchase of Property - Section 236K:
 *   Filer:     1.25% (all values)
 *   Non-Filer: < 50,000,000          -> 10.5%
 *              50,000,000 - 99,999,999 -> 14.5%
 *              >= 100,000,000        -> 18.5%
 *
 * Boundary note: the table words the buying non-filer slabs as
 * "up to 50 Million", "50 Million to 100 Million", "100 Million & above",
 * which overlap at exactly 50M and 100M. The thresholds above are the
 * ones used here, because they also reproduce the screenshot's own
 * history example: Buying Plot PKR 50,000,000 at 14.5% = PKR 7,250,000.
 * If the user wants 50,000,000 itself to fall in the 10.5% slab instead,
 * change the first comparison to value <= 50_000_000L.
 *
 * Property type and province do NOT change the FBR advance-tax rate in
 * this table; they are recorded with the calculation (and shown in
 * history / details), exactly as in the screenshot UI. Provincial
 * duties, stamp duty and registration fees are NOT included.
 */
public final class TaxCalc {
    private TaxCalc() {}

    public static final long SLAB_50M = 50_000_000L;
    public static final long SLAB_100M = 100_000_000L;

    public static String section(boolean buying) {
        return buying ? "236K" : "236C";
    }

    public static double rate(boolean buying, boolean filer, long value) {
        if (buying) {
            if (filer) return 1.25;
            if (value < SLAB_50M) return 10.5;
            if (value < SLAB_100M) return 14.5;
            return 18.5;
        } else {
            if (filer) return 2.75;
            return 11.5;
        }
    }

    public static long tax(long value, double rate) {
        return Math.round(value * rate / 100.0);
    }

    public static String formatPkr(long amount) {
        return "PKR " + String.format(Locale.US, "%,d", amount);
    }

    public static String formatRate(double rate) {
        // 1.25 -> "1.25", 10.5 -> "10.5", 11.5 -> "11.5" (no trailing .0)
        if (rate == Math.floor(rate)) return String.format(Locale.US, "%.0f", rate);
        return String.valueOf(rate);
    }
}
