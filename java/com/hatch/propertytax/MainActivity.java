package com.hatch.propertytax;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Property Tax Checker v1.1.0 — UI rebuilt to match the user's screenshot set
 * screen by screen: Welcome, Home, Calculator, Tax Result, History,
 * Transaction Details, Tax Rates, Settings. Same texts, same order,
 * same green/mint palette, same bottom navigation.
 * Calculation logic lives in TaxCalc (unchanged from v1.0.0).
 */
public class MainActivity extends Activity {
    static final int GREEN = 0xFF173F7A; // Primary (user palette)
    static final int DARK = 0xFF173F7A;
    static final int SECONDARY = 0xFF2456A6;
    static final int ACCENT = 0xFF4F8FE8;
    static final int SOFT = 0xFFD9E9FF;
    static final int MUTED = 0xFF7588A3;
    static final int GOLD = 0xFFD9A441;
    static final int BG = 0xFFF7FAFE; // Main Background
    static final int LIGHT = 0xFFEAF3FF; // Hero Background
    static final int BORDER = 0xFFD7E5FA;
    static final int GREY = 0xFF536B8E; // Secondary Text
    static final int TEXT = 0xFF18365F; // Primary Text
    static final int WHITE = 0xFFFFFFFF;


    boolean buying = true;
    boolean filer = true;
    String current = "welcome";
    String ratesBack = "home";
    Calc lastCalc;
    Button buyBtn, sellBtn;

    static class Calc {
        boolean buying; boolean filer;
        long value, tax, time; double rate;
        Calc(boolean b, boolean f, long v, double r, long tx, long tm) {
            buying = b; filer = f; value = v; rate = r; tax = tx; time = tm;
        }
        String section() { return TaxCalc.section(buying); }
        String txn() { return buying ? "Buying" : "Selling"; }
        String txnLong() { return buying ? "Buying (Buyer)" : "Selling (Seller)"; }
        String toLine() {
            return time + "|" + (buying ? 1 : 0) + "|" + (filer ? 1 : 0)
                    + "|" + value + "|" + rate + "|" + tax;
        }
        static Calc fromLine(String s) {
            try {
                String[] p = s.split("\\|", -1);
                if (p.length == 8) {
                    // Old v1.0-v1.3 format (type at p[2], province at p[3]); both now dropped
                    return new Calc(p[1].equals("1"), p[4].equals("1"),
                            Long.parseLong(p[5]), Double.parseDouble(p[6]), Long.parseLong(p[7]),
                            Long.parseLong(p[0]));
                }
                if (p.length == 7) {
                    // v1.4 format (type at p[2]); type now dropped
                    return new Calc(p[1].equals("1"), p[3].equals("1"),
                            Long.parseLong(p[4]), Double.parseDouble(p[5]), Long.parseLong(p[6]),
                            Long.parseLong(p[0]));
                }
                return new Calc(p[1].equals("1"), p[2].equals("1"),
                        Long.parseLong(p[3]), Double.parseDouble(p[4]), Long.parseLong(p[5]),
                        Long.parseLong(p[0]));
            } catch (Exception e) { return null; }
        }
    }

    int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density + 0.5f); }

    GradientDrawable round(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(radiusDp)); return d;
    }
    GradientDrawable inputBg() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(WHITE); d.setCornerRadius(dp(12)); d.setStroke(dp(1), BORDER); return d;
    }

    static android.graphics.Typeface poppinsR, poppinsB;
    android.graphics.Typeface poppins(boolean bold) {
        try {
            if (bold) {
                if (poppinsB == null) poppinsB = android.graphics.Typeface.createFromAsset(getAssets(), "fonts/Poppins-Bold.ttf");
                return poppinsB;
            }
            if (poppinsR == null) poppinsR = android.graphics.Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
            return poppinsR;
        } catch (Exception e) {
            return bold ? android.graphics.Typeface.DEFAULT_BOLD : android.graphics.Typeface.DEFAULT;
        }
    }

    TextView tv(String text, int sp, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(text); t.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        t.setTextColor(color); t.setTypeface(poppins(bold));
        return t;
    }

    LinearLayout col() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(20), dp(16), dp(20), dp(16));
        return l;
    }

    LinearLayout card(LinearLayout parent) {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setBackground(round(WHITE, 20));
        c.setElevation(dp(2));
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = dp(12); c.setLayoutParams(p);
        parent.addView(c); return c;
    }

    void space(LinearLayout parent, int h) {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(h)));
        parent.addView(v);
    }

    TextView iconBox(String icon) {
        TextView t = tv(icon, 22, GREEN, false);
        t.setBackground(round(SOFT, 14));
        t.setPadding(dp(12), dp(10), dp(12), dp(10));
        t.setGravity(Gravity.CENTER);
        return t;
    }

    ImageView iconImg(int res, int tint, int sizeDp) {
        ImageView iv = new ImageView(this);
        iv.setImageResource(res);
        iv.setColorFilter(tint, PorterDuff.Mode.SRC_IN);
        iv.setLayoutParams(new LinearLayout.LayoutParams(dp(sizeDp), dp(sizeDp)));
        return iv;
    }

    LinearLayout iconBoxImg(int res) {
        LinearLayout box = new LinearLayout(this);
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(SOFT);
        box.setBackground(bg);
        box.setLayoutParams(new LinearLayout.LayoutParams(dp(48), dp(48)));
        box.setGravity(Gravity.CENTER);
        box.addView(iconImg(res, GREEN, 24));
        return box;
    }

    void fieldLabel(LinearLayout parent, int iconRes, String label) {
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.CENTER_VERTICAL);
        r.addView(iconImg(iconRes, GREEN, 24));
        TextView l = tv(label, 16, TEXT, true);
        l.setPadding(dp(10), 0, 0, 0);
        r.addView(l);
        parent.addView(r); space(parent, 8);
    }

    Button greenBtn(String text, View.OnClickListener onClick) {
        Button b = new Button(this);
        b.setText(text); b.setAllCaps(false);
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        b.setTypeface(poppins(true));
        b.setTextColor(WHITE); b.setBackground(round(GREEN, 28));
        b.setMinHeight(dp(52));
        b.setPadding(dp(16), dp(14), dp(16), dp(14));
        b.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        b.setOnClickListener(onClick); return b;
    }

    Button outlineBtn(String text, View.OnClickListener onClick) {
        Button b = new Button(this);
        b.setText(text); b.setAllCaps(false);
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        b.setTypeface(poppins(true));
        b.setTextColor(GREEN);
        GradientDrawable d = new GradientDrawable();
        d.setColor(WHITE); d.setCornerRadius(dp(28)); d.setStroke(dp(1), GREEN);
        b.setBackground(d);
        b.setMinHeight(dp(52));
        b.setPadding(dp(16), dp(14), dp(16), dp(14));
        b.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        b.setOnClickListener(onClick); return b;
    }

    void topBar(LinearLayout parent, String title, View.OnClickListener back, int rightRes, View.OnClickListener rightClick) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        if (back != null) {
            TextView b = tv("‹", 30, TEXT, true);
            b.setPadding(dp(2), dp(0), dp(12), dp(0));
            b.setOnClickListener(back); row.addView(b);
        }
        TextView t = tv(title, 19, TEXT, true);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        row.addView(t);
        if (rightRes != 0) {
            LinearLayout rb = new LinearLayout(this);
            rb.setBackground(round(SOFT, 12));
            rb.setPadding(dp(10), dp(8), dp(10), dp(8));
            rb.setGravity(Gravity.CENTER);
            rb.addView(iconImg(rightRes, GREEN, 22));
            if (rightClick != null) rb.setOnClickListener(rightClick);
            row.addView(rb);
        }
        parent.addView(row); space(parent, 14);
    }

    LinearLayout bottomNav(String selected) {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setBackgroundColor(WHITE);
        nav.setPadding(dp(4), dp(8), dp(4), dp(8));
        String[] items = {"Home", "Calculator", "History", "Settings"};
        int[] icons = {R.drawable.lucide_house, R.drawable.lucide_calculator, R.drawable.lucide_history, R.drawable.lucide_settings};
        for (int i = 0; i < items.length; i++) {
            final String item = items[i];
            LinearLayout col = new LinearLayout(this);
            col.setOrientation(LinearLayout.VERTICAL);
            col.setGravity(Gravity.CENTER);
            col.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            boolean sel = item.equals(selected);
            ImageView ic;
            if (i == 0 && sel) {
                // Reference Home tab is a SOLID house (door cutout is in the drawable);
                // do NOT tint it, or the cutout would tint too.
                ic = new ImageView(this);
                ic.setImageResource(R.drawable.lucide_house_filled);
                ic.setLayoutParams(new LinearLayout.LayoutParams(dp(22), dp(22)));
            } else {
                ic = iconImg(icons[i], sel ? GREEN : MUTED, 22);
            }
            TextView lb = tv(item, 11, sel ? GREEN : MUTED, sel);
            lb.setGravity(Gravity.CENTER);
            col.addView(ic); col.addView(lb);
            if (sel) {
                View bar = new View(this);
                bar.setBackground(round(GREEN, 2));
                bar.setLayoutParams(new LinearLayout.LayoutParams(dp(28), dp(3)));
                col.addView(bar);
            }
            col.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (item.equals("Home")) showHome();
                    else if (item.equals("Calculator")) showCalculator();
                    else if (item.equals("History")) showHistory();
                    else showSettings();
                }
            });
            nav.addView(col);
        }
        return nav;
    }

    View shell(LinearLayout content, String navSelected) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        ScrollView sv = new ScrollView(this);
        sv.addView(content);
        root.addView(sv, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        if (navSelected != null) root.addView(bottomNav(navSelected));
        return root;
    }

    void row(LinearLayout parent, String label, String value) {
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.HORIZONTAL);
        TextView l = tv(label, 14, GREY, false);
        l.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        TextView v = tv(value, 14, TEXT, true);
        r.addView(l); r.addView(v); parent.addView(r); space(parent, 6);
    }

    FrameLayout houseIllustration() {
        FrameLayout f = new FrameLayout(this);
        f.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(180)));
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.house_illustration);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        f.addView(iv, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView badge = tv("%", 26, WHITE, true);
        badge.setBackground(round(GREEN, 30));
        badge.setGravity(Gravity.CENTER);
        badge.setPadding(dp(14), dp(10), dp(14), dp(10));
        FrameLayout.LayoutParams bp = new FrameLayout.LayoutParams(dp(56), dp(56));
        bp.gravity = Gravity.BOTTOM | Gravity.END;
        bp.rightMargin = dp(28); bp.bottomMargin = dp(6);
        f.addView(badge, bp);
        return f;
    }

    List<Calc> loadHistory() {
        List<Calc> out = new ArrayList<Calc>();
        String data = getSharedPreferences("pt_history", MODE_PRIVATE).getString("data", "");
        if (data.isEmpty()) return out;
        for (String line : data.split("\n")) {
            Calc c = Calc.fromLine(line);
            if (c != null) out.add(c);
        }
        return out;
    }
    void saveCalc(Calc c) {
        SharedPreferences p = getSharedPreferences("pt_history", MODE_PRIVATE);
        String old = p.getString("data", "");
        String data = old.isEmpty() ? c.toLine() : c.toLine() + "\n" + old;
        p.edit().putString("data", data).apply();
    }
    boolean alreadySaved(Calc c) {
        for (Calc x : loadHistory()) if (x.time == c.time) return true;
        return false;
    }
    String shareText(Calc c) {
        return "Property Tax Checker\n" + c.txn() + "\n"
                + "Property Value: " + TaxCalc.formatPkr(c.value) + "\n"
                + "Applicable Rate: " + TaxCalc.formatRate(c.rate) + "% (Section " + c.section() + ")\n"
                + "Estimated FBR Advance Tax: " + TaxCalc.formatPkr(c.tax) + "\n"
                + "Note: FBR advance tax estimate only. Provincial duties, stamp duty and registration fees are not included.";
    }
    void shareCalc(Calc c) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain"); i.putExtra(Intent.EXTRA_TEXT, shareText(c));
        startActivity(Intent.createChooser(i, "Share Result"));
    }
    String dateStr(long t) {
        return new SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.US).format(new Date(t));
    }

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        showWelcome();
    }

    @Override public void onBackPressed() {
        if (current.equals("welcome") || current.equals("home")) { finish(); return; }
        if (current.equals("calculator") || current.equals("history") || current.equals("settings")) showHome();
        else if (current.equals("result")) showCalculator();
        else if (current.equals("details")) showHistory();
        else if (current.equals("rates")) { if (ratesBack.equals("settings")) showSettings(); else showHome(); }
        else if (current.equals("info")) showSettings();
        else showHome();
    }

    // ---------- screens ----------
    void showWelcome() {
        current = "welcome";
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        ScrollView sv = new ScrollView(this);
        LinearLayout c = col();
        c.setGravity(Gravity.CENTER_HORIZONTAL);
        space(c, 20);
        c.addView(houseIllustration());
        space(c, 10);
        TextView t = tv("Property\nTax Checker", 34, TEXT, true);
        t.setGravity(Gravity.CENTER); c.addView(t); space(c, 8);
        TextView s = tv("Calculate your buying or selling\nproperty tax in seconds.", 15, GREY, false);
        s.setGravity(Gravity.CENTER); c.addView(s); space(c, 20);
        LinearLayout chips = new LinearLayout(this);
        chips.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout ch1 = new LinearLayout(this); ch1.setOrientation(LinearLayout.VERTICAL);
        ch1.setBackground(round(WHITE, 16)); ch1.setPadding(dp(16), dp(12), dp(16), dp(12));
        ch1.setGravity(Gravity.CENTER);
        ch1.addView(iconImg(R.drawable.lucide_shield_check, GREEN, 22));
        ch1.addView(tv("FBR Rates\n& Rules", 13, TEXT, true));
        LinearLayout ch2 = new LinearLayout(this); ch2.setOrientation(LinearLayout.VERTICAL);
        ch2.setBackground(round(WHITE, 16)); ch2.setPadding(dp(16), dp(12), dp(16), dp(12));
        ch2.setGravity(Gravity.CENTER);
        ch2.addView(iconImg(R.drawable.lucide_zap, GREEN, 22));
        ch2.addView(tv("Quick\n& Accurate", 13, TEXT, true));
        chips.addView(ch1, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        LinearLayout sp = new LinearLayout(this);
        sp.setLayoutParams(new LinearLayout.LayoutParams(dp(8), 1));
        chips.addView(sp);
        chips.addView(ch2, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        c.addView(chips); space(c, 28);
        c.addView(greenBtn("Get Started  →", new View.OnClickListener() {
            public void onClick(View v) { showHome(); }
        }));
        sv.addView(c);
        root.addView(sv, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        GradientDrawable wave = new GradientDrawable();
        wave.setColor(GREEN);
        wave.setCornerRadii(new float[]{dp(48), dp(48), dp(48), dp(48), 0, 0, 0, 0});
        View wv = new View(this);
        wv.setBackground(wave);
        wv.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(64)));
        root.addView(wv);
        setContentView(root);
    }

    LinearLayout homeCard(LinearLayout row, String icon, String title, String sub, View.OnClickListener onClick) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(round(WHITE, 20));
        card.setElevation(dp(2));
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        card.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        card.addView(iconBox(icon));
        space(card, 8);
        card.addView(tv(title, 16, TEXT, true));
        card.addView(tv(sub, 12, GREY, false));
        card.setOnClickListener(onClick);
        row.addView(card); return card;
    }

    void showHome() {
        current = "home";
        LinearLayout c = col();
        LinearLayout head = new LinearLayout(this);
        head.setOrientation(LinearLayout.HORIZONTAL); head.setGravity(Gravity.CENTER_VERTICAL);
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo_asset);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setLayoutParams(new LinearLayout.LayoutParams(dp(48), dp(56)));
        head.addView(logo);
        LinearLayout ht = new LinearLayout(this); ht.setOrientation(LinearLayout.VERTICAL);
        ht.setPadding(dp(10), 0, 0, 0);
        ht.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        ht.addView(tv("Property Tax", 21, TEXT, true));
        ht.addView(tv("Checker", 16, GREY, false));
        head.addView(ht);
        LinearLayout prof = new LinearLayout(this);
        GradientDrawable pg = new GradientDrawable();
        pg.setShape(GradientDrawable.OVAL);
        pg.setColor(SOFT);
        prof.setBackground(pg);
        prof.setLayoutParams(new LinearLayout.LayoutParams(dp(48), dp(48)));
        prof.setGravity(Gravity.CENTER);
        prof.addView(iconImg(R.drawable.lucide_user_filled, GREEN, 24));
        head.addView(prof);
        c.addView(head); space(c, 12);
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setBackground(round(LIGHT, 20)); hero.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hp.bottomMargin = dp(8); hero.setLayoutParams(hp);
        LinearLayout top = new LinearLayout(this); top.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout txt = new LinearLayout(this); txt.setOrientation(LinearLayout.VERTICAL);
        txt.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        txt.setPadding(0, 0, dp(8), 0);
        TextView cap = tv("YOUR PROPERTY", 11, ACCENT, true);
        cap.setLetterSpacing(0.2f); txt.addView(cap); space(txt, 6);
        TextView hd = tv("Know Your\nProperty Tax", 22, TEXT, true); hd.setMaxLines(2); txt.addView(hd); space(txt, 8);
        TextView sb = tv("Get a quick and accurate estimate of your property tax in seconds.", 12, GREY, false); sb.setMaxLines(3); txt.addView(sb);
        top.addView(txt);
        ImageView heroImg = new ImageView(this);
        heroImg.setImageResource(R.drawable.hero_banner);
        heroImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        heroImg.setLayoutParams(new LinearLayout.LayoutParams(dp(160), dp(127)));
        top.addView(heroImg);
        hero.addView(top); space(hero, 14);
        LinearLayout calcBtn = new LinearLayout(this);
        calcBtn.setOrientation(LinearLayout.HORIZONTAL); calcBtn.setGravity(Gravity.CENTER_VERTICAL);
        calcBtn.setBackground(round(GREEN, 16)); calcBtn.setPadding(dp(20), dp(16), dp(20), dp(16));
        calcBtn.addView(iconImg(R.drawable.lucide_calculator, WHITE, 22));
        TextView ct = tv("Calculate Tax", 17, WHITE, true);
        ct.setPadding(dp(12), 0, 0, 0);
        ct.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        calcBtn.addView(ct);
        calcBtn.addView(iconImg(R.drawable.lucide_arrow_right, WHITE, 22));
        calcBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showCalculator(); }
        });
        hero.addView(calcBtn);
        c.addView(hero);
        space(c, 8);
        c.addView(tv("Quick Actions", 19, TEXT, true)); space(c, 8);
        LinearLayout r1 = new LinearLayout(this); r1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout qa1 = new LinearLayout(this); qa1.setOrientation(LinearLayout.VERTICAL);
        qa1.setBackground(inputBg()); qa1.setPadding(dp(12), dp(12), dp(12), dp(12));
        qa1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        qa1.addView(iconBoxImg(R.drawable.lucide_file_text));
        space(qa1, 6);
        LinearLayout t1 = new LinearLayout(this); t1.setOrientation(LinearLayout.HORIZONTAL);
        TextView tt1 = tv("Tax Rates", 15, TEXT, true);
        tt1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        t1.addView(tt1); t1.addView(iconImg(R.drawable.lucide_arrow_right, SECONDARY, 16));
        qa1.addView(t1);
        qa1.addView(tv("View current FBR rates and guidelines.", 12, GREY, false));
        qa1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { ratesBack = "home"; showRates(); }
        });
        LinearLayout qa2 = new LinearLayout(this); qa2.setOrientation(LinearLayout.VERTICAL);
        qa2.setBackground(inputBg()); qa2.setPadding(dp(12), dp(12), dp(12), dp(12));
        qa2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        qa2.addView(iconBoxImg(R.drawable.lucide_history));
        space(qa2, 6);
        LinearLayout t2 = new LinearLayout(this); t2.setOrientation(LinearLayout.HORIZONTAL);
        TextView tt2 = tv("History", 15, TEXT, true);
        tt2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        t2.addView(tt2); t2.addView(iconImg(R.drawable.lucide_arrow_right, SECONDARY, 16));
        qa2.addView(t2);
        qa2.addView(tv("Check your past calculations.", 12, GREY, false));
        qa2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showHistory(); }
        });
        r1.addView(qa1);
        LinearLayout sp = new LinearLayout(this);
        sp.setLayoutParams(new LinearLayout.LayoutParams(dp(8), 1));
        r1.addView(sp); r1.addView(qa2);
        c.addView(r1);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        ScrollView sv = new ScrollView(this);
        sv.addView(c);
        root.addView(sv, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        LinearLayout ad = new LinearLayout(this);
        ad.setBackgroundColor(SOFT);
        ad.setGravity(Gravity.CENTER);
        ad.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(50)));
        ad.addView(tv("Banner Ad", 12, MUTED, false));
        root.addView(ad);
        root.addView(bottomNav("Home"));
        setContentView(root);
    }

    void updateToggle() {
        if (buyBtn == null) return;
        buyBtn.setBackground(buying ? round(GREEN, 20) : round(WHITE, 20));
        buyBtn.setTextColor(buying ? WHITE : GREY);
        sellBtn.setBackground(!buying ? round(GREEN, 20) : round(WHITE, 20));
        sellBtn.setTextColor(!buying ? WHITE : GREY);
    }

    void showCalculator() {
        current = "calculator";
        LinearLayout c = col();
        LinearLayout head = new LinearLayout(this);
        head.setOrientation(LinearLayout.HORIZONTAL);
        head.setGravity(Gravity.CENTER_VERTICAL);
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo_asset);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setLayoutParams(new LinearLayout.LayoutParams(dp(48), dp(56)));
        head.addView(logo);
        LinearLayout ht = new LinearLayout(this); ht.setOrientation(LinearLayout.VERTICAL);
        ht.setPadding(dp(10), 0, 0, 0);
        ht.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        ht.addView(tv("Property Tax", 21, TEXT, true));
        ht.addView(tv("Checker", 16, GREY, false));
        head.addView(ht);
        LinearLayout prof = new LinearLayout(this);
        GradientDrawable pg = new GradientDrawable();
        pg.setShape(GradientDrawable.OVAL);
        pg.setColor(SOFT);
        prof.setBackground(pg);
        prof.setLayoutParams(new LinearLayout.LayoutParams(dp(48), dp(48)));
        prof.setGravity(Gravity.CENTER);
        prof.addView(iconImg(R.drawable.lucide_user_filled, GREEN, 24));
        head.addView(prof);
        c.addView(head); space(c, 12);
        c.addView(tv("Calculate Your Tax", 28, TEXT, true));
        c.addView(tv("Select transaction type and enter property details to get an estimate.", 15, GREY, false));
        space(c, 14);
        LinearLayout banner = new LinearLayout(this);
        banner.setOrientation(LinearLayout.HORIZONTAL);
        banner.setGravity(Gravity.CENTER_VERTICAL);
        banner.setBackground(round(LIGHT, 20)); banner.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bp.bottomMargin = dp(16); banner.setLayoutParams(bp);
        LinearLayout bt = new LinearLayout(this); bt.setOrientation(LinearLayout.VERTICAL);
        bt.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        TextView chip = tv("TAX CALCULATION", 11, SECONDARY, true);
        chip.setBackground(round(SOFT, 8)); chip.setPadding(dp(10), dp(4), dp(10), dp(4));
        bt.addView(chip); space(bt, 8);
        bt.addView(tv("Know Your\nProperty Tax", 22, TEXT, true));
        banner.addView(bt);
        ImageView bi = new ImageView(this);
        bi.setImageResource(R.drawable.calc_banner);
        bi.setScaleType(ImageView.ScaleType.FIT_CENTER);
        bi.setLayoutParams(new LinearLayout.LayoutParams(dp(170), dp(104)));
        banner.addView(bi);
        c.addView(banner);
        LinearLayout toggle = new LinearLayout(this); toggle.setOrientation(LinearLayout.HORIZONTAL);
        toggle.setBackground(round(WHITE, 24)); toggle.setPadding(dp(4), dp(4), dp(4), dp(4));
        buyBtn = new Button(this); buyBtn.setText("Buying"); buyBtn.setAllCaps(false);
        buyBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        buyBtn.setTypeface(poppins(true));
        sellBtn = new Button(this); sellBtn.setText("Selling"); sellBtn.setAllCaps(false);
        sellBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        sellBtn.setTypeface(poppins(true));
        buyBtn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        sellBtn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        buyBtn.setMinHeight(dp(48)); sellBtn.setMinHeight(dp(48));
        buyBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { buying = true; updateToggle(); }
        });
        sellBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { buying = false; updateToggle(); }
        });
        toggle.addView(buyBtn); toggle.addView(sellBtn); c.addView(toggle);
        updateToggle(); space(c, 16);
        fieldLabel(c, R.drawable.lucide_house, "Property Value (PKR)");
        final EditText valueEt = new EditText(this); valueEt.setTypeface(poppins(false));
        valueEt.setHint("Enter property value  e.g. 10,000,000");
        valueEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        GradientDrawable vb = new GradientDrawable();
        vb.setColor(WHITE); vb.setCornerRadius(dp(16)); vb.setStroke(dp(1), BORDER);
        valueEt.setBackground(vb); valueEt.setPadding(dp(14), dp(14), dp(14), dp(14));
        c.addView(valueEt); space(c, 16);
        fieldLabel(c, R.drawable.lucide_user, "Taxpayer Status");
        RadioGroup rg = new RadioGroup(this);
        RadioButton fr = new RadioButton(this); fr.setTypeface(poppins(false)); fr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); fr.setTextColor(TEXT); fr.setButtonTintList(new android.content.res.ColorStateList(new int[][]{{android.R.attr.state_checked},{}}, new int[]{GREEN, GREY})); fr.setText("Filer (Active Taxpayer List)"); fr.setId(1001);
        RadioButton nf = new RadioButton(this); nf.setTypeface(poppins(false)); nf.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); nf.setTextColor(TEXT); nf.setButtonTintList(new android.content.res.ColorStateList(new int[][]{{android.R.attr.state_checked},{}}, new int[]{GREEN, GREY})); nf.setText("Non-Filer (Not on ATL)"); nf.setId(1002);
        rg.addView(fr); rg.addView(nf);
        rg.check(filer ? 1001 : 1002);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup g, int id) { filer = (id == 1001); }
        });
        c.addView(rg); space(c, 12);
        c.addView(greenBtn("Calculate Tax", new View.OnClickListener() {
            public void onClick(View v) {
                String raw = valueEt.getText().toString().replaceAll("[^0-9]", "");
                if (raw.isEmpty()) { Toast.makeText(MainActivity.this, "Please enter property value", Toast.LENGTH_SHORT).show(); return; }
                long val;
                try { val = Long.parseLong(raw); } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Value is too large", Toast.LENGTH_SHORT).show(); return;
                }
                if (val <= 0) { Toast.makeText(MainActivity.this, "Please enter property value", Toast.LENGTH_SHORT).show(); return; }
                double r = TaxCalc.rate(buying, filer, val);
                lastCalc = new Calc(buying, filer, val, r, TaxCalc.tax(val, r), System.currentTimeMillis());
                showResult(lastCalc);
            }
        }));
        setContentView(shell(c, "Calculator"));
    }

    void showResult(final Calc calc) {
        current = "result";
        LinearLayout c = col();
        topBar(c, "Tax Result", new View.OnClickListener() {
            public void onClick(View v) { showCalculator(); }
        }, R.drawable.lucide_share_2, new View.OnClickListener() {
            public void onClick(View v) { shareCalc(calc); }
        });
        LinearLayout green = new LinearLayout(this);
        green.setOrientation(LinearLayout.VERTICAL);
        green.setBackground(round(GREEN, 20)); green.setPadding(dp(20), dp(20), dp(20), dp(20));
        LinearLayout chk = new LinearLayout(this); chk.setOrientation(LinearLayout.HORIZONTAL);
        chk.setGravity(Gravity.CENTER_VERTICAL);
        TextView ck = tv("✓", 18, GREEN, true);
        ck.setBackground(round(WHITE, 20)); ck.setPadding(dp(10), dp(6), dp(10), dp(6));
        chk.addView(ck);
        TextView et = tv("Estimated FBR Advance Tax", 14, WHITE, true);
        et.setPadding(dp(10), 0, 0, 0); chk.addView(et);
        green.addView(chk); space(green, 6);
        green.addView(tv(TaxCalc.formatPkr(calc.tax), 32, WHITE, true));
        space(green, 12);
        LinearLayout rr = new LinearLayout(this); rr.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout a1 = new LinearLayout(this); a1.setOrientation(LinearLayout.VERTICAL);
        a1.addView(tv("Applicable Rate", 12, WHITE, false));
        a1.addView(tv(TaxCalc.formatRate(calc.rate) + "%", 16, WHITE, true));
        LinearLayout a2 = new LinearLayout(this); a2.setOrientation(LinearLayout.VERTICAL);
        a2.addView(tv("Section", 12, WHITE, false));
        a2.addView(tv(calc.section(), 16, WHITE, true));
        rr.addView(a1, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        rr.addView(a2, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        green.addView(rr); space(green, 10);
        green.addView(tv("Transaction", 12, WHITE, false));
        green.addView(tv(calc.txnLong(), 16, WHITE, true));
        LinearLayout.LayoutParams gp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gp.bottomMargin = dp(12); green.setLayoutParams(gp);
        c.addView(green);
        LinearLayout bd = card(c);
        bd.addView(tv("Calculation Breakdown", 16, TEXT, true)); space(bd, 8);
        row(bd, "Property Value", TaxCalc.formatPkr(calc.value));
        row(bd, "Applicable Rate", TaxCalc.formatRate(calc.rate) + "%");
        row(bd, "Calculated Tax", TaxCalc.formatPkr(calc.tax));
        LinearLayout note = card(c);
        note.setBackground(round(LIGHT, 20));
        note.addView(tv("ⓘ  This is an FBR advance tax estimate. Provincial duties, stamp duty, registration fees and other charges are not included.", 13, GREY, false));
        c.addView(greenBtn("↗  Save / Share Result", new View.OnClickListener() {
            public void onClick(View v) {
                if (!alreadySaved(calc)) { saveCalc(calc); Toast.makeText(MainActivity.this, "Saved to History", Toast.LENGTH_SHORT).show(); }
                shareCalc(calc);
            }
        }));
        setContentView(shell(c, "Calculator"));
    }

    void showDetails(final Calc calc, final boolean fromHistory) {
        current = "details";
        LinearLayout c = col();
        topBar(c, "Transaction Details", new View.OnClickListener() {
            public void onClick(View v) { if (fromHistory) showHistory(); else showResult(calc); }
        }, 0, null);
        LinearLayout head = card(c);
        LinearLayout hr = new LinearLayout(this); hr.setOrientation(LinearLayout.HORIZONTAL);
        hr.setGravity(Gravity.CENTER_VERTICAL);
        hr.addView(iconBoxImg(R.drawable.lucide_house));
        LinearLayout ht = new LinearLayout(this); ht.setOrientation(LinearLayout.VERTICAL);
        ht.setPadding(dp(10), 0, 0, 0);
        ht.addView(tv(calc.txn(), 16, TEXT, true));
        TextView badge = tv(calc.filer ? "Filer" : "Non-Filer", 12, GREEN, true);
        badge.setBackground(round(LIGHT, 12)); badge.setPadding(dp(10), dp(4), dp(10), dp(4));
        ht.addView(badge); hr.addView(ht); head.addView(hr);
        LinearLayout det = card(c);
        row(det, "Property Value", TaxCalc.formatPkr(calc.value));
        row(det, "Applied Rate", TaxCalc.formatRate(calc.rate) + "%");
        row(det, "FBR Section", calc.section());
        LinearLayout green = new LinearLayout(this);
        green.setOrientation(LinearLayout.VERTICAL);
        green.setBackground(round(GREEN, 20)); green.setPadding(dp(20), dp(20), dp(20), dp(20));
        green.addView(tv("Estimated FBR Advance Tax", 13, WHITE, false));
        green.addView(tv(TaxCalc.formatPkr(calc.tax), 26, WHITE, true));
        LinearLayout.LayoutParams gp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gp.bottomMargin = dp(12); green.setLayoutParams(gp);
        c.addView(green);
        TextView link = tv("View Calculation Breakdown  ›", 14, GREEN, true);
        link.setPadding(0, dp(4), 0, dp(12));
        link.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showResult(calc); }
        });
        c.addView(link);
        LinearLayout btns = new LinearLayout(this); btns.setOrientation(LinearLayout.HORIZONTAL);
        Button sh = outlineBtn("↗  Share", new View.OnClickListener() {
            public void onClick(View v) { shareCalc(calc); }
        });
        sh.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        Button sv = greenBtn("↓  Save", new View.OnClickListener() {
            public void onClick(View v) {
                if (alreadySaved(calc)) Toast.makeText(MainActivity.this, "Already saved in History", Toast.LENGTH_SHORT).show();
                else { saveCalc(calc); Toast.makeText(MainActivity.this, "Saved to History", Toast.LENGTH_SHORT).show(); }
            }
        });
        sv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        btns.addView(sh);
        LinearLayout bsp = new LinearLayout(this);
        bsp.setLayoutParams(new LinearLayout.LayoutParams(dp(12), 1));
        btns.addView(bsp);
        btns.addView(sv); c.addView(btns);
        setContentView(shell(c, null));
    }

    void showHistory() {
        current = "history";
        LinearLayout c = col();
        topBar(c, "Calculation History", null, R.drawable.lucide_trash, new View.OnClickListener() {
            public void onClick(View v) {
                getSharedPreferences("pt_history", MODE_PRIVATE).edit().remove("data").apply();
                Toast.makeText(MainActivity.this, "History cleared", Toast.LENGTH_SHORT).show();
                showHistory();
            }
        });
        List<Calc> list = loadHistory();
        if (list.isEmpty()) {
            LinearLayout e = card(c);
            e.addView(tv("No calculations yet.", 16, TEXT, true));
            e.addView(tv("Calculate your first property tax from the Calculator tab.", 14, GREY, false));
        }
        for (final Calc calc : list) {
            LinearLayout item = card(c);
            LinearLayout hr = new LinearLayout(this); hr.setOrientation(LinearLayout.HORIZONTAL);
            hr.setGravity(Gravity.CENTER_VERTICAL);
            hr.addView(iconBoxImg(R.drawable.lucide_house));
            LinearLayout mid = new LinearLayout(this); mid.setOrientation(LinearLayout.VERTICAL);
            mid.setPadding(dp(10), 0, 0, 0);
            mid.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            mid.addView(tv(calc.txn(), 15, TEXT, true));
            mid.addView(tv(TaxCalc.formatPkr(calc.value), 14, TEXT, false));
            mid.addView(tv("Tax: " + TaxCalc.formatPkr(calc.tax) + " (" + TaxCalc.formatRate(calc.rate) + "%)", 14, GREEN, true));
            mid.addView(tv(dateStr(calc.time), 12, GREY, false));
            hr.addView(mid);
            hr.addView(tv("›", 26, GREY, false));
            item.addView(hr);
            item.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { showDetails(calc, true); }
            });
        }
        setContentView(shell(c, "History"));
    }

    void rateRow(LinearLayout parent, String label, String rate) {
        LinearLayout r = new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL);
        TextView l = tv(label, 14, TEXT, false);
        l.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        TextView v = tv(rate, 14, TEXT, true);
        r.addView(l); r.addView(v); parent.addView(r); space(parent, 6);
    }

    void showRates() {
        current = "rates";
        LinearLayout c = col();
        topBar(c, "Tax Rates", new View.OnClickListener() {
            public void onClick(View v) { if (ratesBack.equals("settings")) showSettings(); else showHome(); }
        }, 0, null);
        LinearLayout toggle = new LinearLayout(this); toggle.setOrientation(LinearLayout.HORIZONTAL);
        toggle.setBackground(round(WHITE, 24)); toggle.setPadding(dp(4), dp(4), dp(4), dp(4));
        final Button tb = new Button(this); tb.setText("Buying"); tb.setAllCaps(false);
        tb.setTypeface(poppins(true));
        tb.setBackground(round(GREEN, 12)); tb.setTextColor(WHITE);
        final Button ts = new Button(this); ts.setText("Selling"); ts.setAllCaps(false);
        ts.setTypeface(poppins(true));
        ts.setBackground(round(WHITE, 12)); ts.setTextColor(GREEN);
        tb.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        ts.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        toggle.addView(tb); toggle.addView(ts); c.addView(toggle); space(c, 12);
        final LinearLayout buySec = new LinearLayout(this); buySec.setOrientation(LinearLayout.VERTICAL);
        LinearLayout buyCard = new LinearLayout(this); buyCard.setOrientation(LinearLayout.VERTICAL);
        buyCard.setBackground(round(WHITE, 20)); buyCard.setPadding(dp(16), dp(16), dp(16), dp(16));
        buyCard.addView(tv("Buying (Section 236K)", 16, TEXT, true)); space(buyCard, 8);
        rateRow(buyCard, "Filer (ATL)", "1.25%");
        rateRow(buyCard, "Non-Filer (Up to 50M)", "10.5%");
        rateRow(buyCard, "Non-Filer (50M – 100M)", "14.5%");
        rateRow(buyCard, "Non-Filer (Above 100M)", "18.5%");
        buySec.addView(buyCard);
        final LinearLayout sellSec = new LinearLayout(this); sellSec.setOrientation(LinearLayout.VERTICAL);
        LinearLayout sellCard = new LinearLayout(this); sellCard.setOrientation(LinearLayout.VERTICAL);
        sellCard.setBackground(round(WHITE, 20)); sellCard.setPadding(dp(16), dp(16), dp(16), dp(16));
        sellCard.addView(tv("Selling (Section 236C)", 16, TEXT, true)); space(sellCard, 8);
        rateRow(sellCard, "Filer (ATL)", "2.75%");
        rateRow(sellCard, "Non-Filer (Not on ATL)", "11.5%");
        sellSec.addView(sellCard);
        c.addView(buySec); space(c, 4); c.addView(sellSec); space(c, 12);
        LinearLayout note = card(c);
        note.setBackground(round(LIGHT, 20));
        note.addView(tv("ⓘ  Rates are subject to change. Always confirm the latest rates from FBR official sources.", 13, GREY, false));
        note.addView(tv("Provincial duties, stamp duty, registration fees and other charges are not included in the FBR advance tax.", 13, GREY, false));
        final View root = shell(c, "");
        tb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tb.setBackground(round(GREEN, 12)); tb.setTextColor(WHITE);
                ts.setBackground(round(WHITE, 12)); ts.setTextColor(GREEN);
                final ScrollView sv = (ScrollView) ((LinearLayout) root).getChildAt(0);
                sv.post(new Runnable() { public void run() { sv.scrollTo(0, 0); } });
            }
        });
        ts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ts.setBackground(round(GREEN, 12)); ts.setTextColor(WHITE);
                tb.setBackground(round(WHITE, 12)); tb.setTextColor(GREEN);
                final ScrollView sv = (ScrollView) ((LinearLayout) root).getChildAt(0);
                sv.post(new Runnable() { public void run() { sv.scrollTo(0, sellSec.getTop()); } });
            }
        });
        setContentView(root);
    }

    void showInfo(String title, String body) {
        current = "info";
        LinearLayout c = col();
        topBar(c, title, new View.OnClickListener() {
            public void onClick(View v) { showSettings(); }
        }, 0, null);
        LinearLayout card = card(c);
        card.addView(tv(body, 15, TEXT, false));
        setContentView(shell(c, "Settings"));
    }

    void settingsRow(LinearLayout parent, int iconRes, String title, View.OnClickListener onClick) {
        LinearLayout item = card(parent);
        LinearLayout r = new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.CENTER_VERTICAL);
        r.addView(iconImg(iconRes, GREEN, 22));
        TextView l = tv(title, 16, TEXT, true);
        l.setPadding(dp(10), 0, 0, 0);
        l.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        r.addView(l); r.addView(tv("›", 24, GREY, false));
        item.addView(r);
        item.setOnClickListener(onClick);
    }

    void showSettings() {
        current = "settings";
        LinearLayout c = col();
        topBar(c, "Settings", null, R.drawable.lucide_settings, null);
        settingsRow(c, R.drawable.lucide_info, "About the App", new View.OnClickListener() {
            public void onClick(View v) { showInfo("About the App", "Property Tax Checker calculates the FBR advance tax on buying or selling property in Pakistan.\n\nBuying: Section 236K. Selling: Section 236C.\n\nFiler rates: Buying 1.25%, Selling 2.75%.\nNon-Filer rates: Buying 10.5% / 14.5% / 18.5% by value slab, Selling 11.5%.\n\nCalculations are done on your phone. Your history is stored only on your device."); }
        });
        settingsRow(c, R.drawable.lucide_percent, "Tax Rates (Reference)", new View.OnClickListener() {
            public void onClick(View v) { ratesBack = "settings"; showRates(); }
        });
        settingsRow(c, R.drawable.lucide_circle_question_mark, "FAQs", new View.OnClickListener() {
            public void onClick(View v) { showInfo("FAQs", "Q: Is this the full cost of buying/selling?\nA: No. This is only the FBR advance tax. Provincial duties, stamp duty, registration fees and other charges are not included.\n\nQ: Does province change the rate?\nA: No - the same rate applies in every province, so the app does not ask for province at all. The rate depends only on Buying/Selling, Filer/Non-Filer, and (for buying non-filers) the property value slab. Property type is not asked either - it does not change the rate.\n\nQ: What is a filer?\nA: A person on the Active Taxpayer List (ATL). Non-filers pay the higher rates shown in Tax Rates.\n\nQ: Are rates fixed forever?\nA: No. FBR rates can change with the budget. Always confirm from FBR official sources."); }
        });
        settingsRow(c, R.drawable.lucide_shield_check, "Privacy Policy", new View.OnClickListener() {
            public void onClick(View v) { showInfo("Privacy Policy", "This app does not collect, send or sell your personal data.\n\nCalculations are performed on your device. Calculation history is saved only in this app on your phone, and you can delete it any time from History (trash icon).\n\nNo account, no sign-in, no tracking."); }
        });
        settingsRow(c, R.drawable.lucide_mail, "Contact Us", new View.OnClickListener() {
            public void onClick(View v) { showInfo("Contact Us", "For feedback or corrections to rates, please use the developer contact shown on this app's Play Store listing.\n\nIf a rate has changed, tell us the new FBR rate and we will update the app."); }
        });
        setContentView(shell(c, "Settings"));
    }
}
