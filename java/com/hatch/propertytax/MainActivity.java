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

public class MainActivity extends Activity {
    static final int GREEN = 0xFF0E6B4F;
    static final int DARK = 0xFF0A5340;
    static final int MINT = 0xFFEFF7F2;
    static final int LIGHT = 0xFFE3F2EA;
    static final int GREY = 0xFF5A6B64;
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
    GradientDrawable outline(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(WHITE); d.setCornerRadius(dp(radiusDp));
        d.setStroke(dp(1), color); return d;
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
        l.setPadding(dp(16), dp(16), dp(16), dp(16));
        return l;
    }

    LinearLayout card(LinearLayout parent) {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setBackground(round(WHITE, 16));
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

    Button greenBtn(String text, View.OnClickListener onClick) {
        Button b = new Button(this);
        b.setText(text); b.setAllCaps(false);
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        b.setTextColor(WHITE); b.setBackground(round(GREEN, 28));
        b.setPadding(dp(16), dp(14), dp(16), dp(14));
        b.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        b.setOnClickListener(onClick); return b;
    }

    Button outlineBtn(String text, View.OnClickListener onClick) {
        Button b = new Button(this);
        b.setText(text); b.setAllCaps(false);
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        b.setTextColor(GREEN); b.setBackground(outline(GREEN, 28));
        b.setPadding(dp(16), dp(14), dp(16), dp(14));
        b.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        b.setOnClickListener(onClick); return b;
    }

    void topBar(LinearLayout parent, String title, View.OnClickListener back) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        if (back != null) {
            TextView b = tv("‹", 28, TEXT, true);
            b.setPadding(dp(4), dp(0), dp(12), dp(0));
            b.setOnClickListener(back); row.addView(b);
        }
        TextView t = tv(title, 19, TEXT, true);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        row.addView(t); parent.addView(row); space(parent, 12);
    }

    LinearLayout bottomNav(String selected) {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setBackgroundColor(WHITE);
        nav.setPadding(dp(4), dp(10), dp(4), dp(10));
        String[] items = {"Home", "Calculator", "History", "Settings"};
        String[] icons = {"🏠 ", "🧮 ", "🕒 ", "⚙️ "};
        for (int i = 0; i < items.length; i++) {
            final String item = items[i];
            Button b = new Button(this);
            b.setText(icons[i] + item); b.setAllCaps(false);
            b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            b.setBackgroundColor(Color.TRANSPARENT);
            b.setTextColor(item.equals(selected) ? GREEN : GREY);
            if (item.equals(selected)) b.setTypeface(null, android.graphics.Typeface.BOLD);
            b.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (item.equals("Home")) showHome();
                    else if (item.equals("Calculator")) showCalculator();
                    else if (item.equals("History")) showHistory();
                    else showSettings();
                }
            });
            nav.addView(b);
        }
        return nav;
    }

    View shell(LinearLayout content, String navSelected) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(MINT);
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

    // ---------- history ----------
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
        LinearLayout c = col();
        c.setGravity(Gravity.CENTER_HORIZONTAL);
        space(c, 24);
        TextView house = tv("🏠", 84, TEXT, false); house.setGravity(Gravity.CENTER); c.addView(house);
        TextView pct = tv("%", 28, WHITE, true);
        pct.setBackground(round(GREEN, 28)); pct.setGravity(Gravity.CENTER);
        pct.setPadding(dp(14), dp(8), dp(14), dp(8)); c.addView(pct);
        space(c, 16);
        TextView t = tv("Property\nTax Checker", 32, TEXT, true);
        t.setGravity(Gravity.CENTER); c.addView(t); space(c, 8);
        TextView s = tv("Calculate your buying or selling\nproperty tax in seconds.", 15, GREY, false);
        s.setGravity(Gravity.CENTER); c.addView(s); space(c, 20);
        LinearLayout chips = new LinearLayout(this);
        chips.setOrientation(LinearLayout.HORIZONTAL);
        TextView a = tv("🛡  FBR Rates\n& Rules", 13, TEXT, true); a.setBackground(round(WHITE, 14));
        a.setPadding(dp(14), dp(10), dp(14), dp(10)); a.setGravity(Gravity.CENTER);
        TextView b2 = tv("⚡  Quick\n& Accurate", 13, TEXT, true); b2.setBackground(round(WHITE, 14));
        b2.setPadding(dp(14), dp(10), dp(14), dp(10)); b2.setGravity(Gravity.CENTER);
        chips.addView(a, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        chips.addView(b2, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        c.addView(chips); space(c, 24);
        c.addView(greenBtn("Get Started  →", new View.OnClickListener() {
            public void onClick(View v) { showHome(); }
        }));
        setContentView(shell(c, null));
    }

    LinearLayout homeCard(LinearLayout row, String icon, String title, String sub, View.OnClickListener onClick) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(round(WHITE, 16));
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        card.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        card.addView(tv(icon, 28, TEXT, false));
        space(card, 6);
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
        TextView ic = tv("🏠", 30, WHITE, false); ic.setBackground(round(GREEN, 20)); ic.setPadding(dp(8), dp(6), dp(8), dp(6));
        head.addView(ic);
        LinearLayout ht = new LinearLayout(this); ht.setOrientation(LinearLayout.VERTICAL);
        ht.setPadding(dp(10), 0, 0, 0);
        ht.addView(tv("Property Tax Checker", 18, TEXT, true));
        ht.addView(tv("Buying & Selling", 13, GREY, false));
        head.addView(ht); c.addView(head); space(c, 16);
        LinearLayout banner = card(c);
        banner.addView(tv("Your Property.\nOur Calculation.", 22, TEXT, true));
        banner.addView(tv("Know your tax before you buy or sell.", 14, GREY, false));
        LinearLayout r1 = new LinearLayout(this); r1.setOrientation(LinearLayout.HORIZONTAL);
        homeCard(r1, "🧮", "Calculate Tax", "Get instant estimate", new View.OnClickListener() {
            public void onClick(View v) { showCalculator(); }
        });
        space(r1, 0);
        LinearLayout spacer = new LinearLayout(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(dp(12), 1));
        r1.addView(spacer);
        homeCard(r1, "%", "Tax Rates", "View current rates", new View.OnClickListener() {
            public void onClick(View v) { ratesBack = "home"; showRates(); }
        });
        c.addView(r1);
        LinearLayout r2 = new LinearLayout(this); r2.setOrientation(LinearLayout.HORIZONTAL);
        homeCard(r2, "🕒", "History", "Your past calculations", new View.OnClickListener() {
            public void onClick(View v) { showHistory(); }
        });
        LinearLayout sp2 = new LinearLayout(this);
        sp2.setLayoutParams(new LinearLayout.LayoutParams(dp(12), 1));
        r2.addView(sp2);
        homeCard(r2, "⚙️", "Settings", "App preferences", new View.OnClickListener() {
            public void onClick(View v) { showSettings(); }
        });
        c.addView(r2);
        setContentView(shell(c, "Home"));
    }

    void updateToggle() {
        if (buyBtn == null) return;
        buyBtn.setBackground(buying ? round(GREEN, 14) : round(WHITE, 14));
        buyBtn.setTextColor(buying ? WHITE : GREEN);
        sellBtn.setBackground(!buying ? round(GREEN, 14) : round(WHITE, 14));
        sellBtn.setTextColor(!buying ? WHITE : GREEN);
    }

    void showCalculator() {
        current = "calculator";
        LinearLayout c = col();
        topBar(c, "Property Tax Checker", new View.OnClickListener() {
            public void onClick(View v) { showHome(); }
        });
        c.addView(tv("Calculate Your Tax", 22, TEXT, true));
        c.addView(tv("Select transaction type and enter property details to get an estimate.", 14, GREY, false));
        space(c, 14);
        LinearLayout toggle = new LinearLayout(this); toggle.setOrientation(LinearLayout.HORIZONTAL);
        buyBtn = new Button(this); buyBtn.setText("🏠  Buying"); buyBtn.setAllCaps(false);
        buyBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        sellBtn = new Button(this); sellBtn.setText("💰  Selling"); sellBtn.setAllCaps(false);
        sellBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        buyBtn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        sellBtn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        buyBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { buying = true; updateToggle(); }
        });
        sellBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { buying = false; updateToggle(); }
        });
        toggle.addView(buyBtn); toggle.addView(sellBtn); c.addView(toggle);
        updateToggle(); space(c, 14);
        c.addView(tv("Property Value (PKR)", 14, TEXT, true));
        final EditText valueEt = new EditText(this);
        valueEt.setHint("Enter property value  e.g. 10,000,000");
        valueEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        valueEt.setBackground(round(WHITE, 12)); valueEt.setPadding(dp(14), dp(12), dp(14), dp(12));
        c.addView(valueEt); space(c, 14);
        c.addView(tv("Taxpayer Status", 14, TEXT, true));
        RadioGroup rg = new RadioGroup(this);
        RadioButton fr = new RadioButton(this); fr.setText("Filer (Active Taxpayer List)");
        RadioButton nf = new RadioButton(this); nf.setText("Non-Filer (Not on ATL)");
        rg.addView(fr); rg.addView(nf);
        rg.check(filer ? fr.getId() : nf.getId());
        // ids: assign explicitly so check works
        fr.setId(1001); nf.setId(1002);
        rg.check(filer ? 1001 : 1002);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup g, int id) { filer = (id == 1001); }
        });
        c.addView(rg); space(c, 10);
        c.addView(tv("Province", 14, TEXT, true));
        Spinner prov = new Spinner(this);
        prov.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, PROVINCES));
        prov.setSelection(0);
        prov.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> a, View v, int pos, long id) { province = PROVINCES[pos]; }
            public void onNothingSelected(AdapterView<?> a) {}
        });
        c.addView(prov); space(c, 10);
        c.addView(tv("Property Type", 14, TEXT, true));
        Spinner typ = new Spinner(this);
        typ.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TYPES));
        typ.setSelection(0);
        typ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> a, View v, int pos, long id) { ptype = TYPES[pos]; }
            public void onNothingSelected(AdapterView<?> a) {}
        });
        c.addView(typ); space(c, 18);
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
        space(c, 12);
        c.addView(tv("This is an FBR advance tax estimate. Provincial duties, stamp duty, registration fees and other charges are not included.", 12, GREY, false));
        setContentView(shell(c, "Calculator"));
    }

    void showResult(final Calc calc) {
        current = "result";
        LinearLayout c = col();
        topBar(c, "Tax Result", new View.OnClickListener() {
            public void onClick(View v) { showCalculator(); }
        });
        LinearLayout green = new LinearLayout(this);
        green.setOrientation(LinearLayout.VERTICAL);
        green.setBackground(round(GREEN, 16)); green.setPadding(dp(16), dp(16), dp(16), dp(16));
        green.addView(tv("✓   Estimated FBR Advance Tax", 14, WHITE, true));
        space(green, 4);
        green.addView(tv(TaxCalc.formatPkr(calc.tax), 30, WHITE, true));
        space(green, 10);
        LinearLayout rr = new LinearLayout(this); rr.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout a1 = new LinearLayout(this); a1.setOrientation(LinearLayout.VERTICAL);
        a1.addView(tv("Applicable Rate", 12, WHITE, false));
        a1.addView(tv(TaxCalc.formatRate(calc.rate) + "%", 16, WHITE, true));
        LinearLayout a2 = new LinearLayout(this); a2.setOrientation(LinearLayout.VERTICAL);
        a2.addView(tv("Section", 12, WHITE, false));
        a2.addView(tv(calc.section(), 16, WHITE, true));
        rr.addView(a1, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        rr.addView(a2, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        green.addView(rr); space(green, 8);
        green.addView(tv("Transaction", 12, WHITE, false));
        green.addView(tv(calc.txnLong(), 16, WHITE, true));
        c.addView(green); space(c, 12);
        LinearLayout bd = card(c);
        bd.addView(tv("Calculation Breakdown", 16, TEXT, true)); space(bd, 8);
        row(bd, "Property Value", TaxCalc.formatPkr(calc.value));
        row(bd, "Applicable Rate", TaxCalc.formatRate(calc.rate) + "%");
        row(bd, "Calculated Tax", TaxCalc.formatPkr(calc.tax));
        row(bd, "Province", calc.province);
        row(bd, "Property Type", calc.type);
        row(bd, "Taxpayer Status", calc.filer ? "Filer (ATL)" : "Non-Filer");
        LinearLayout note = card(c);
        note.addView(tv("ℹ  This is an FBR advance tax estimate. Provincial duties, stamp duty, registration fees and other charges are not included.", 13, GREY, false));
        c.addView(greenBtn("Save Result", new View.OnClickListener() {
            public void onClick(View v) {
                if (alreadySaved(calc)) Toast.makeText(MainActivity.this, "Already saved in History", Toast.LENGTH_SHORT).show();
                else { saveCalc(calc); Toast.makeText(MainActivity.this, "Saved to History", Toast.LENGTH_SHORT).show(); }
            }
        }));
        space(c, 10);
        c.addView(outlineBtn("Share Result", new View.OnClickListener() {
            public void onClick(View v) { shareCalc(calc); }
        }));
        space(c, 10);
        c.addView(outlineBtn("View Details", new View.OnClickListener() {
            public void onClick(View v) { showDetails(calc, false); }
        }));
        setContentView(shell(c, "Calculator"));
    }

    void showDetails(final Calc calc, final boolean fromHistory) {
        current = "details";
        LinearLayout c = col();
        topBar(c, "Transaction Details", new View.OnClickListener() {
            public void onClick(View v) { if (fromHistory) showHistory(); else showResult(calc); }
        });
        LinearLayout head = card(c);
        LinearLayout hr = new LinearLayout(this); hr.setOrientation(LinearLayout.HORIZONTAL);
        hr.addView(tv("🏠  ", 24, TEXT, false));
        LinearLayout ht = new LinearLayout(this); ht.setOrientation(LinearLayout.VERTICAL);
        ht.addView(tv(calc.txn() + " · " + calc.type, 16, TEXT, true));
        TextView badge = tv(calc.filer ? "Filer" : "Non-Filer", 12, GREEN, true);
        badge.setBackground(round(LIGHT, 12)); badge.setPadding(dp(10), dp(4), dp(10), dp(4));
        ht.addView(badge); hr.addView(ht); head.addView(hr);
        space(c, 4);
        LinearLayout det = card(c);
        row(det, "Property Value", TaxCalc.formatPkr(calc.value));
        row(det, "Province", calc.province);
        row(det, "Property Type", calc.type);
        row(det, "Applied Rate", TaxCalc.formatRate(calc.rate) + "%");
        row(det, "FBR Section", calc.section());
        LinearLayout green = new LinearLayout(this);
        green.setOrientation(LinearLayout.VERTICAL);
        green.setBackground(round(GREEN, 16)); green.setPadding(dp(16), dp(16), dp(16), dp(16));
        green.addView(tv("Estimated FBR Advance Tax", 13, WHITE, false));
        green.addView(tv(TaxCalc.formatPkr(calc.tax), 26, WHITE, true));
        c.addView(green); space(c, 12);
        c.addView(outlineBtn("View Calculation Breakdown  →", new View.OnClickListener() {
            public void onClick(View v) { showResult(calc); }
        }));
        space(c, 10);
        LinearLayout btns = new LinearLayout(this); btns.setOrientation(LinearLayout.HORIZONTAL);
        Button sh = outlineBtn("Share", new View.OnClickListener() {
            public void onClick(View v) { shareCalc(calc); }
        });
        sh.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        Button sv = greenBtn("Save", new View.OnClickListener() {
            public void onClick(View v) {
                if (alreadySaved(calc)) Toast.makeText(MainActivity.this, "Already saved in History", Toast.LENGTH_SHORT).show();
                else { saveCalc(calc); Toast.makeText(MainActivity.this, "Saved to History", Toast.LENGTH_SHORT).show(); }
            }
        });
        sv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        btns.addView(sh); btns.addView(sv); c.addView(btns);
        setContentView(shell(c, "History"));
    }

    void showHistory() {
        current = "history";
        LinearLayout c = col();
        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL); top.setGravity(Gravity.CENTER_VERTICAL);
        TextView t = tv("Calculation History", 19, TEXT, true);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        top.addView(t);
        TextView trash = tv("🗑", 22, TEXT, false);
        trash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getSharedPreferences("pt_history", MODE_PRIVATE).edit().remove("data").apply();
                Toast.makeText(MainActivity.this, "History cleared", Toast.LENGTH_SHORT).show();
                showHistory();
            }
        });
        top.addView(trash); c.addView(top); space(c, 12);
        List<Calc> list = loadHistory();
        if (list.isEmpty()) {
            LinearLayout e = card(c);
            e.addView(tv("No calculations yet.", 16, TEXT, true));
            e.addView(tv("Calculate your first property tax from the Calculator tab.", 14, GREY, false));
        }
        for (final Calc calc : list) {
            LinearLayout item = card(c);
            item.addView(tv("🏠  " + calc.txn() + " · " + calc.type, 15, TEXT, true));
            item.addView(tv(TaxCalc.formatPkr(calc.value), 14, TEXT, false));
            item.addView(tv("Tax: " + TaxCalc.formatPkr(calc.tax) + " (" + TaxCalc.formatRate(calc.rate) + "%)", 14, GREEN, true));
            item.addView(tv(dateStr(calc.time), 12, GREY, false));
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
        });
        final ScrollView[] holder = new ScrollView[1];
        LinearLayout buySec = new LinearLayout(this); buySec.setOrientation(LinearLayout.VERTICAL);
        LinearLayout buyCard = new LinearLayout(this); buyCard.setOrientation(LinearLayout.VERTICAL);
        buyCard.setBackground(round(WHITE, 16)); buyCard.setPadding(dp(16), dp(16), dp(16), dp(16));
        buyCard.addView(tv("Buying (Section 236K)", 16, TEXT, true)); space(buyCard, 8);
        rateRow(buyCard, "Filer (ATL)", "1.25%");
        rateRow(buyCard, "Non-Filer (Up to 50M)", "10.5%");
        rateRow(buyCard, "Non-Filer (50M – 100M)", "14.5%");
        rateRow(buyCard, "Non-Filer (Above 100M)", "18.5%");
        buySec.addView(buyCard);
        LinearLayout sellSec = new LinearLayout(this); sellSec.setOrientation(LinearLayout.VERTICAL);
        LinearLayout sellCard = new LinearLayout(this); sellCard.setOrientation(LinearLayout.VERTICAL);
        sellCard.setBackground(round(WHITE, 16)); sellCard.setPadding(dp(16), dp(16), dp(16), dp(16));
        sellCard.addView(tv("Selling (Section 236C)", 16, TEXT, true)); space(sellCard, 8);
        rateRow(sellCard, "Filer (ATL)", "2.75%");
        rateRow(sellCard, "Non-Filer (Not on ATL)", "11.5%");
        sellSec.addView(sellCard);
        // Toggle row (jumps to the chosen section, both tables stay visible like the screenshot)
        LinearLayout toggle = new LinearLayout(this); toggle.setOrientation(LinearLayout.HORIZONTAL);
        final Button tb = new Button(this); tb.setText("Buying"); tb.setAllCaps(false); tb.setBackground(round(GREEN, 14)); tb.setTextColor(WHITE);
        final Button ts = new Button(this); ts.setText("Selling"); ts.setAllCaps(false); ts.setBackground(round(WHITE, 14)); ts.setTextColor(GREEN);
        tb.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        ts.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        toggle.addView(tb); toggle.addView(ts); c.addView(toggle); space(c, 12);
        c.addView(buySec); space(c, 4); c.addView(sellSec); space(c, 12);
        LinearLayout note = card(c);
        note.addView(tv("ℹ  Rates are subject to change. Always confirm the latest rates from FBR official sources.", 13, GREY, false));
        note.addView(tv("Provincial duties, stamp duty, registration fees and other charges are not included in the FBR advance tax.", 13, GREY, false));
        final View root = shell(c, null);
        // Wire toggle after root exists: scroll to section via post on the ScrollView inside root
        tb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tb.setBackground(round(GREEN, 14)); tb.setTextColor(WHITE);
                ts.setBackground(round(WHITE, 14)); ts.setTextColor(GREEN);
                final ScrollView sv = (ScrollView) ((LinearLayout) root).getChildAt(0);
                sv.post(new Runnable() { public void run() { sv.scrollTo(0, 0); } });
            }
        });
        ts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ts.setBackground(round(GREEN, 14)); ts.setTextColor(WHITE);
                tb.setBackground(round(WHITE, 14)); tb.setTextColor(GREEN);
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
        });
        LinearLayout card = card(c);
        TextView b = tv(body, 15, TEXT, false);
        card.addView(b);
        setContentView(shell(c, "Settings"));
    }

    void showSettings() {
        current = "settings";
        LinearLayout c = col();
        topBar(c, "⚙️  Settings", null);
        String[][] items = {
            {"About the App", "Property Tax Checker calculates the FBR advance tax on buying or selling property in Pakistan.\n\nBuying: Section 236K. Selling: Section 236C.\n\nFiler rates: Buying 1.25%, Selling 2.75%.\nNon-Filer rates: Buying 10.5% / 14.5% / 18.5% by value slab, Selling 11.5%.\n\nCalculations are done on your phone. Your history is stored only on your device."},
            {"FAQs", "Q: Is this the full cost of buying/selling?\nA: No. This is only the FBR advance tax. Provincial duties, stamp duty, registration fees and other charges are not included.\n\nQ: Does province or property type change the rate?\nA: No, under this FBR table the rate depends on Buying/Selling, Filer/Non-Filer, and (for buying non-filers) the property value slab. Province and type are recorded with your calculation.\n\nQ: What is a filer?\nA: A person on the Active Taxpayer List (ATL). Non-filers pay the higher rates shown in Tax Rates.\n\nQ: Are rates fixed forever?\nA: No. FBR rates can change with the budget. Always confirm from FBR official sources."},
            {"Privacy Policy", "This app does not collect, send or sell your personal data.\n\nCalculations are performed on your device. Calculation history is saved only in this app on your phone, and you can delete it any time from History (trash icon).\n\nNo account, no sign-in, no tracking."},
            {"Contact Us", "For feedback or corrections to rates, please use the developer contact shown on this app's Play Store listing.\n\nIf a rate has changed, tell us the new FBR rate and we will update the app."}
        };
        for (final String[] it : items) {
            LinearLayout item = card(c);
            LinearLayout r = new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL);
            TextView l = tv(it[0], 16, TEXT, true);
            l.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            r.addView(l); r.addView(tv("›", 22, GREY, false));
            item.addView(r);
            if (it[0].equals("Tax Rates (Reference)")) {}
            item.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { showInfo(it[0], it[1]); }
            });
        }
        LinearLayout ratesItem = card(c);
        LinearLayout rr = new LinearLayout(this); rr.setOrientation(LinearLayout.HORIZONTAL);
        TextView rl = tv("Tax Rates (Reference)", 16, TEXT, true);
        rl.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        rr.addView(rl); rr.addView(tv("›", 22, GREY, false));
        ratesItem.addView(rr);
        ratesItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { ratesBack = "settings"; showRates(); }
        });
        // Settings order like the screenshot: About, Tax Rates, FAQs, Privacy, Contact
        c.removeView(ratesItem);
        c.addView(ratesItem, 3);
        setContentView(shell(c, "Settings"));
    }
}
