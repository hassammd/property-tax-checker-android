package com.hatch.propertytax;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
    static final int GREEN = 0xFF0E6B4F;
    static final int DARK = 0xFF0A5340;
    static final int BG = 0xFFF2F8F4;
    static final int LIGHT = 0xFFE2F1E8;
    static final int BORDER = 0xFFDCE9DF;
    static final int GREY = 0xFF66796F;
    static final int TEXT = 0xFF122B22;
    static final int WHITE = 0xFFFFFFFF;

    static final String[] PROVINCES = {"Punjab", "Sindh", "Khyber Pakhtunkhwa", "Balochistan",
            "Islamabad Capital Territory", "Gilgit-Baltistan", "Azad Jammu & Kashmir"};
    static final String[] TYPES = {"Residential (House)", "Residential (Flat)", "Commercial", "Plot"};

    boolean buying = true;
    boolean filer = true;
    String province = PROVINCES[0];
    String ptype = TYPES[0];
    String current = "welcome";
    String ratesBack = "home";
    Calc lastCalc;
    Button buyBtn, sellBtn;

    static class Calc {
        boolean buying; String type, province; boolean filer;
        long value, tax, time; double rate;
        Calc(boolean b, String t, String p, boolean f, long v, double r, long tx, long tm) {
            buying = b; type = t; province = p; filer = f; value = v; rate = r; tax = tx; time = tm;
        }
        String section() { return TaxCalc.section(buying); }
        String txn() { return buying ? "Buying" : "Selling"; }
        String txnLong() { return buying ? "Buying (Buyer)" : "Selling (Seller)"; }
        String toLine() {
            return time + "|" + (buying ? 1 : 0) + "|" + type + "|" + province + "|" + (filer ? 1 : 0)
                    + "|" + value + "|" + rate + "|" + tax;
        }
        static Calc fromLine(String s) {
            try {
                String[] p = s.split("\\|", -1);
                return new Calc(p[1].equals("1"), p[2], p[3], p[4].equals("1"),
                        Long.parseLong(p[5]), Double.parseDouble(p[6]), Long.parseLong(p[7]),
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

    TextView tv(String text, int sp, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(text); t.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        t.setTextColor(color); if (bold) t.setTypeface(null, android.graphics.Typeface.BOLD);
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
        t.setBackground(round(LIGHT, 14));
        t.setPadding(dp(12), dp(10), dp(12), dp(10));
        t.setGravity(Gravity.CENTER);
        return t;
    }

    void fieldLabel(LinearLayout parent, String icon, String label) {
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.CENTER_VERTICAL);
        r.addView(iconBox(icon));
        TextView l = tv(label, 14, TEXT, true);
        l.setPadding(dp(10), 0, 0, 0);
        r.addView(l);
        parent.addView(r); space(parent, 8);
    }

    Button greenBtn(String text, View.OnClickListener onClick) {
        Button b = new Button(this);
        b.setText(text); b.setAllCaps(false);
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        b.setTypeface(null, android.graphics.Typeface.BOLD);
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
        b.setTypeface(null, android.graphics.Typeface.BOLD);
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

    void topBar(LinearLayout parent, String title, View.OnClickListener back, String right, View.OnClickListener rightClick) {
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
        if (right != null) {
            TextView r = tv(right, 20, GREEN, true);
            r.setBackground(round(LIGHT, 12));
            r.setPadding(dp(12), dp(8), dp(12), dp(8));
            if (rightClick != null) r.setOnClickListener(rightClick);
            row.addView(r);
        }
        parent.addView(row); space(parent, 14);
    }

    LinearLayout bottomNav(String selected) {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setBackgroundColor(WHITE);
        nav.setPadding(dp(4), dp(8), dp(4), dp(8));
        String[] items = {"Home", "Calculator", "History", "Settings"};
        String[] icons = {"⌂", "▤", "◷", "⚙"};
        for (int i = 0; i < items.length; i++) {
            final String item = items[i];
            LinearLayout col = new LinearLayout(this);
            col.setOrientation(LinearLayout.VERTICAL);
            col.setGravity(Gravity.CENTER);
            col.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            boolean sel = item.equals(selected);
            TextView ic = tv(icons[i], 22, sel ? GREEN : GREY, sel);
            ic.setGravity(Gravity.CENTER);
            TextView lb = tv(item, 11, sel ? GREEN : GREY, sel);
            lb.setGravity(Gravity.CENTER);
            col.addView(ic); col.addView(lb);
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
        return "Property Tax Checker\n" + c.txn() + " · " + c.type + "\n"
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
        ch1.addView(tv("🛡", 22, GREEN, false));
        ch1.addView(tv("FBR Rates\n& Rules", 13, TEXT, true));
        LinearLayout ch2 = new LinearLayout(this); ch2.setOrientation(LinearLayout.VERTICAL);
        ch2.setBackground(round(WHITE, 16)); ch2.setPadding(dp(16), dp(12), dp(16), dp(12));
        ch2.setGravity(Gravity.CENTER);
        ch2.addView(tv("⚡", 22, GREEN, false));
        ch2.addView(tv("Quick\n& Accurate", 13, TEXT, true));
        chips.addView(ch1, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        LinearLayout sp = new LinearLayout(this);
        sp.setLayoutParams(new LinearLayout.LayoutParams(dp(12), 1));
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
        TextView ic = tv("⌂", 26, WHITE, true);
        ic.setBackground(round(GREEN, 20)); ic.setPadding(dp(12), dp(8), dp(12), dp(8));
        head.addView(ic);
        LinearLayout ht = new LinearLayout(this); ht.setOrientation(LinearLayout.VERTICAL);
        ht.setPadding(dp(10), 0, 0, 0);
        ht.addView(tv("Property Tax Checker", 18, TEXT, true));
        ht.addView(tv("Buying & Selling", 13, GREY, false));
        head.addView(ht); c.addView(head); space(c, 16);
        LinearLayout banner = card(c);
        banner.addView(tv("Your Property.\nOur Calculation.", 22, TEXT, true));
        banner.addView(tv("Know your tax before you buy or sell.", 14, GREY, false));
        space(banner, 6);
        LinearLayout ill = new LinearLayout(this);
        ill.setOrientation(LinearLayout.HORIZONTAL);
        ill.addView(iconBox("⌂"));
        TextView calcIc = tv("▤", 26, GREEN, false);
        calcIc.setPadding(dp(12), 0, 0, 0);
        ill.addView(calcIc);
        banner.addView(ill);
        LinearLayout r1 = new LinearLayout(this); r1.setOrientation(LinearLayout.HORIZONTAL);
        homeCard(r1, "▤", "Calculate Tax", "Get instant estimate", new View.OnClickListener() {
            public void onClick(View v) { showCalculator(); }
        });
        LinearLayout spacer = new LinearLayout(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(dp(12), 1));
        r1.addView(spacer);
        homeCard(r1, "%", "Tax Rates", "View current rates", new View.OnClickListener() {
            public void onClick(View v) { ratesBack = "home"; showRates(); }
        });
        c.addView(r1);
        LinearLayout r2 = new LinearLayout(this); r2.setOrientation(LinearLayout.HORIZONTAL);
        homeCard(r2, "◷", "History", "Your past calculations", new View.OnClickListener() {
            public void onClick(View v) { showHistory(); }
        });
        LinearLayout sp2 = new LinearLayout(this);
        sp2.setLayoutParams(new LinearLayout.LayoutParams(dp(12), 1));
        r2.addView(sp2);
        homeCard(r2, "⚙", "Settings", "App preferences", new View.OnClickListener() {
            public void onClick(View v) { showSettings(); }
        });
        c.addView(r2);
        setContentView(shell(c, "Home"));
    }

    void updateToggle() {
        if (buyBtn == null) return;
        buyBtn.setBackground(buying ? round(GREEN, 12) : round(WHITE, 12));
        buyBtn.setTextColor(buying ? WHITE : GREY);
        sellBtn.setBackground(!buying ? round(GREEN, 12) : round(WHITE, 12));
        sellBtn.setTextColor(!buying ? WHITE : GREY);
    }

    void showCalculator() {
        current = "calculator";
        LinearLayout c = col();
        topBar(c, "Property Tax Checker", new View.OnClickListener() {
            public void onClick(View v) { showHome(); }
        }, "▤", null);
        c.addView(tv("Calculate Your Tax", 22, TEXT, true));
        c.addView(tv("Select transaction type and enter property details to get an estimate.", 14, GREY, false));
        space(c, 14);
        LinearLayout toggle = new LinearLayout(this); toggle.setOrientation(LinearLayout.HORIZONTAL);
        toggle.setBackground(round(WHITE, 16)); toggle.setPadding(dp(4), dp(4), dp(4), dp(4));
        buyBtn = new Button(this); buyBtn.setText("⌂  Buying"); buyBtn.setAllCaps(false);
        buyBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        buyBtn.setTypeface(null, android.graphics.Typeface.BOLD);
        sellBtn = new Button(this); sellBtn.setText("◉  Selling"); sellBtn.setAllCaps(false);
        sellBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        sellBtn.setTypeface(null, android.graphics.Typeface.BOLD);
        buyBtn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        sellBtn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        buyBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { buying = true; updateToggle(); }
        });
        sellBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { buying = false; updateToggle(); }
        });
        toggle.addView(buyBtn); toggle.addView(sellBtn); c.addView(toggle);
        updateToggle(); space(c, 16);
        fieldLabel(c, "⌂", "Property Value (PKR)");
        final EditText valueEt = new EditText(this);
        valueEt.setHint("Enter property value  e.g. 10,000,000");
        valueEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        valueEt.setBackground(inputBg()); valueEt.setPadding(dp(14), dp(14), dp(14), dp(14));
        c.addView(valueEt); space(c, 16);
        fieldLabel(c, "◉", "Taxpayer Status");
        RadioGroup rg = new RadioGroup(this);
        RadioButton fr = new RadioButton(this); fr.setText("Filer (Active Taxpayer List)"); fr.setId(1001);
        RadioButton nf = new RadioButton(this); nf.setText("Non-Filer (Not on ATL)"); nf.setId(1002);
        rg.addView(fr); rg.addView(nf);
        rg.check(filer ? 1001 : 1002);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup g, int id) { filer = (id == 1001); }
        });
        c.addView(rg); space(c, 12);
        fieldLabel(c, "◎", "Province");
        Spinner prov = new Spinner(this);
        prov.setBackground(inputBg());
        prov.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, PROVINCES));
        prov.setSelection(0);
        prov.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> a, View v, int pos, long id) { province = PROVINCES[pos]; }
            public void onNothingSelected(AdapterView<?> a) {}
        });
        c.addView(prov); space(c, 14);
        fieldLabel(c, "⌂", "Property Type");
        Spinner typ = new Spinner(this);
        typ.setBackground(inputBg());
        typ.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TYPES));
        typ.setSelection(0);
        typ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> a, View v, int pos, long id) { ptype = TYPES[pos]; }
            public void onNothingSelected(AdapterView<?> a) {}
        });
        c.addView(typ); space(c, 20);
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
                lastCalc = new Calc(buying, ptype, province, filer, val, r, TaxCalc.tax(val, r), System.currentTimeMillis());
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
        }, "↗", new View.OnClickListener() {
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
        }, null, null);
        LinearLayout head = card(c);
        LinearLayout hr = new LinearLayout(this); hr.setOrientation(LinearLayout.HORIZONTAL);
        hr.setGravity(Gravity.CENTER_VERTICAL);
        hr.addView(iconBox("⌂"));
        LinearLayout ht = new LinearLayout(this); ht.setOrientation(LinearLayout.VERTICAL);
        ht.setPadding(dp(10), 0, 0, 0);
        ht.addView(tv(calc.txn() + " · " + calc.type, 16, TEXT, true));
        TextView badge = tv(calc.filer ? "Filer" : "Non-Filer", 12, GREEN, true);
        badge.setBackground(round(LIGHT, 12)); badge.setPadding(dp(10), dp(4), dp(10), dp(4));
        ht.addView(badge); hr.addView(ht); head.addView(hr);
        LinearLayout det = card(c);
        row(det, "Property Value", TaxCalc.formatPkr(calc.value));
        row(det, "Province", calc.province);
        row(det, "Property Type", calc.type);
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
        topBar(c, "Calculation History", null, "🗑", new View.OnClickListener() {
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
            hr.addView(iconBox("⌂"));
            LinearLayout mid = new LinearLayout(this); mid.setOrientation(LinearLayout.VERTICAL);
            mid.setPadding(dp(10), 0, 0, 0);
            mid.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            mid.addView(tv(calc.txn() + " · " + calc.type, 15, TEXT, true));
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
        }, null, null);
        LinearLayout toggle = new LinearLayout(this); toggle.setOrientation(LinearLayout.HORIZONTAL);
        toggle.setBackground(round(WHITE, 16)); toggle.setPadding(dp(4), dp(4), dp(4), dp(4));
        final Button tb = new Button(this); tb.setText("Buying"); tb.setAllCaps(false);
        tb.setTypeface(null, android.graphics.Typeface.BOLD);
        tb.setBackground(round(GREEN, 12)); tb.setTextColor(WHITE);
        final Button ts = new Button(this); ts.setText("Selling"); ts.setAllCaps(false);
        ts.setTypeface(null, android.graphics.Typeface.BOLD);
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
        }, null, null);
        LinearLayout card = card(c);
        card.addView(tv(body, 15, TEXT, false));
        setContentView(shell(c, "Settings"));
    }

    void settingsRow(LinearLayout parent, String icon, String title, View.OnClickListener onClick) {
        LinearLayout item = card(parent);
        LinearLayout r = new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.CENTER_VERTICAL);
        r.addView(iconBox(icon));
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
        topBar(c, "Settings", null, "⚙", null);
        settingsRow(c, "ⓘ", "About the App", new View.OnClickListener() {
            public void onClick(View v) { showInfo("About the App", "Property Tax Checker calculates the FBR advance tax on buying or selling property in Pakistan.\n\nBuying: Section 236K. Selling: Section 236C.\n\nFiler rates: Buying 1.25%, Selling 2.75%.\nNon-Filer rates: Buying 10.5% / 14.5% / 18.5% by value slab, Selling 11.5%.\n\nCalculations are done on your phone. Your history is stored only on your device."); }
        });
        settingsRow(c, "%", "Tax Rates (Reference)", new View.OnClickListener() {
            public void onClick(View v) { ratesBack = "settings"; showRates(); }
        });
        settingsRow(c, "?", "FAQs", new View.OnClickListener() {
            public void onClick(View v) { showInfo("FAQs", "Q: Is this the full cost of buying/selling?\nA: No. This is only the FBR advance tax. Provincial duties, stamp duty, registration fees and other charges are not included.\n\nQ: Does province or property type change the rate?\nA: No, under this FBR table the rate depends on Buying/Selling, Filer/Non-Filer, and (for buying non-filers) the property value slab. Province and type are recorded with your calculation.\n\nQ: What is a filer?\nA: A person on the Active Taxpayer List (ATL). Non-filers pay the higher rates shown in Tax Rates.\n\nQ: Are rates fixed forever?\nA: No. FBR rates can change with the budget. Always confirm from FBR official sources."); }
        });
        settingsRow(c, "🛡", "Privacy Policy", new View.OnClickListener() {
            public void onClick(View v) { showInfo("Privacy Policy", "This app does not collect, send or sell your personal data.\n\nCalculations are performed on your device. Calculation history is saved only in this app on your phone, and you can delete it any time from History (trash icon).\n\nNo account, no sign-in, no tracking."); }
        });
        settingsRow(c, "✉", "Contact Us", new View.OnClickListener() {
            public void onClick(View v) { showInfo("Contact Us", "For feedback or corrections to rates, please use the developer contact shown on this app's Play Store listing.\n\nIf a rate has changed, tell us the new FBR rate and we will update the app."); }
        });
        setContentView(shell(c, "Settings"));
    }
}
