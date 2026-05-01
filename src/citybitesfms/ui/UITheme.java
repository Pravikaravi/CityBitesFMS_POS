package citybitesfms.ui;

import java.awt.*;

/**
 * UITheme — centralised colour palette, font definitions and spacing constants
 * for the City Bites application.
 *
 * All UI classes reference this class so the visual style can be changed in
 * one place without touching any layout code.
 *
 * Colour scheme: green (#00C853) primary, dark green (#1B5E20) sidebar,
 * light grey (#F5F5F5) background — matching the modern food-ordering dashboard
 * design specification.
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 3.0
 */
public final class UITheme {
    private UITheme() {}

    // ── Primary green ──────────────────────────────────────────────────────────
    public static final Color PRIMARY        = new Color(  0, 200,  83);  // #00C853
    public static final Color PRIMARY_DARK   = new Color(  0, 150,  60);  // darker hover
    public static final Color PRIMARY_LIGHT  = new Color(200, 255, 220);  // tint

    // ── Sidebar ────────────────────────────────────────────────────────────────
    public static final Color SIDEBAR_BG     = new Color( 27,  94,  32);  // #1B5E20
    public static final Color SIDEBAR_HOVER  = new Color( 46, 125,  50);  // #2E7D32
    public static final Color SIDEBAR_ICON   = new Color(165, 214, 167);  // muted green

    // ── Neutrals ───────────────────────────────────────────────────────────────
    public static final Color SECONDARY      = new Color( 69,  90, 100);  // blue-grey
    public static final Color SECONDARY2     = new Color( 96, 125, 139);

    // ── Status ─────────────────────────────────────────────────────────────────
    public static final Color SUCCESS        = new Color(  0, 200,  83);  // same as PRIMARY
    public static final Color SUCCESS_DARK   = new Color(  0, 150,  60);
    public static final Color DANGER         = new Color(211,  47,  47);  // #D32F2F
    public static final Color DANGER_DARK    = new Color(183,  28,  28);
    public static final Color WARNING        = new Color(245, 158,  11);

    // ── Surfaces ───────────────────────────────────────────────────────────────
    public static final Color BG             = new Color(245, 245, 245);  // #F5F5F5
    public static final Color SURFACE        = Color.WHITE;
    public static final Color BORDER         = new Color(220, 220, 220);

    // ── Text ───────────────────────────────────────────────────────────────────
    public static final Color TEXT_PRI       = new Color( 33,  33,  33);
    public static final Color TEXT_SEC       = new Color( 97,  97,  97);
    public static final Color TEXT_HINT      = new Color(189, 189, 189);

    // ── Table ──────────────────────────────────────────────────────────────────
    public static final Color TABLE_ODD      = Color.WHITE;
    public static final Color TABLE_EVEN     = new Color(245, 250, 245);
    public static final Color TABLE_SEL      = new Color(200, 230, 200);
    public static final Color TABLE_HDR_BG   = new Color( 27,  94,  32);  // sidebar dark
    public static final Color TABLE_HDR_FG   = Color.WHITE;

    // ── Typography (Segoe UI) ──────────────────────────────────────────────────
    private static final String FONT = "Segoe UI";
    public static final Font F_HERO    = new Font(FONT, Font.BOLD,  48);
    public static final Font F_TITLE   = new Font(FONT, Font.BOLD,  26);
    public static final Font F_HEADING = new Font(FONT, Font.BOLD,  18);
    public static final Font F_SUBHEAD = new Font(FONT, Font.BOLD,  14);
    public static final Font F_BODY    = new Font(FONT, Font.PLAIN, 13);
    public static final Font F_LABEL   = new Font(FONT, Font.BOLD,  12);
    public static final Font F_SMALL   = new Font(FONT, Font.PLAIN, 11);
    public static final Font F_BUTTON  = new Font(FONT, Font.BOLD,  13);
    public static final Font F_MONO    = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    public static final Font F_TOTAL   = new Font(FONT, Font.BOLD,  16);
}
