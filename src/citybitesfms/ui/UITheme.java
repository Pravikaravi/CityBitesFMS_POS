package citybitesfms.ui;

import java.awt.*;

public final class UITheme {
    private UITheme() {}

    // ── Brand orange (matches Restro POS palette) ──────────────────────────────
    public static final Color PRIMARY       = new Color(249, 115,  22);   // #F97316
    public static final Color PRIMARY_DARK  = new Color(234,  88,  12);   // #EA580C
    public static final Color PRIMARY_LIGHT = new Color(254, 215, 170);   // #FED7AA

    // ── Sidebar ────────────────────────────────────────────────────────────────
    public static final Color SIDEBAR_BG    = new Color( 30,  41,  59);   // slate-800
    public static final Color SIDEBAR_HOVER = new Color( 51,  65,  85);   // slate-700
    public static final Color SIDEBAR_ICON  = new Color(148, 163, 184);   // slate-400

    // ── Neutrals ───────────────────────────────────────────────────────────────
    public static final Color SECONDARY     = new Color( 71,  85, 105);   // slate-600
    public static final Color SECONDARY2    = new Color(100, 116, 139);   // slate-500

    // ── Status ─────────────────────────────────────────────────────────────────
    public static final Color SUCCESS       = new Color( 34, 197,  94);   // #22C55E
    public static final Color SUCCESS_DARK  = new Color( 21, 128,  61);   // #15803D
    public static final Color DANGER        = new Color(239,  68,  68);   // #EF4444
    public static final Color DANGER_DARK   = new Color(185,  28,  28);   // #B91C1C
    public static final Color WARNING       = new Color(245, 158,  11);   // #F59E0B

    // ── Surfaces ───────────────────────────────────────────────────────────────
    public static final Color BG            = new Color(248, 250, 252);   // slate-50
    public static final Color SURFACE       = Color.WHITE;
    public static final Color BORDER        = new Color(226, 232, 240);   // slate-200

    // ── Text ───────────────────────────────────────────────────────────────────
    public static final Color TEXT_PRI      = new Color( 15,  23,  42);   // slate-900
    public static final Color TEXT_SEC      = new Color(100, 116, 139);   // slate-500
    public static final Color TEXT_HINT     = new Color(203, 213, 225);   // slate-300

    // ── Table ──────────────────────────────────────────────────────────────────
    public static final Color TABLE_ODD     = Color.WHITE;
    public static final Color TABLE_EVEN    = new Color(248, 250, 252);
    public static final Color TABLE_SEL     = new Color(255, 237, 213);   // orange-100
    public static final Color TABLE_HDR_BG  = new Color( 30,  41,  59);   // sidebar dark
    public static final Color TABLE_HDR_FG  = Color.WHITE;

    // ── Typography ─────────────────────────────────────────────────────────────
    private static final String FONT = "Segoe UI";
    public static final Font F_HERO    = new Font(FONT, Font.BOLD,  52);
    public static final Font F_TITLE   = new Font(FONT, Font.BOLD,  28);
    public static final Font F_HEADING = new Font(FONT, Font.BOLD,  20);
    public static final Font F_SUBHEAD = new Font(FONT, Font.BOLD,  14);
    public static final Font F_BODY    = new Font(FONT, Font.PLAIN, 13);
    public static final Font F_LABEL   = new Font(FONT, Font.BOLD,  12);
    public static final Font F_SMALL   = new Font(FONT, Font.PLAIN, 11);
    public static final Font F_BUTTON  = new Font(FONT, Font.BOLD,  13);
    public static final Font F_MONO    = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    public static final Font F_TOTAL   = new Font(FONT, Font.BOLD,  18);
}
