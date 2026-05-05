package citybitesfms.ui;

import java.awt.*;

public final class UITheme {
    private UITheme() {}

    // main green colour used across the app
    public static final Color PRIMARY        = new Color(  0, 200,  83);
    public static final Color PRIMARY_DARK   = new Color(  0, 150,  60);
    public static final Color PRIMARY_LIGHT  = new Color(200, 255, 220);

    // dark green for the sidebar background
    public static final Color SIDEBAR_BG     = new Color( 27,  94,  32);
    public static final Color SIDEBAR_HOVER  = new Color( 46, 125,  50);
    public static final Color SIDEBAR_ICON   = new Color(165, 214, 167);

    public static final Color SECONDARY      = new Color( 69,  90, 100);
    public static final Color SECONDARY2     = new Color( 96, 125, 139);

    // red used for delete button and error messages
    public static final Color SUCCESS        = new Color(  0, 200,  83);
    public static final Color SUCCESS_DARK   = new Color(  0, 150,  60);
    public static final Color DANGER         = new Color(211,  47,  47);
    public static final Color DANGER_DARK    = new Color(183,  28,  28);
    public static final Color WARNING        = new Color(245, 158,  11);

    // page and card background colours
    public static final Color BG             = new Color(245, 245, 245);
    public static final Color SURFACE        = Color.WHITE;
    public static final Color BORDER         = new Color(220, 220, 220);

    // text colours for different levels
    public static final Color TEXT_PRI       = new Color( 33,  33,  33);
    public static final Color TEXT_SEC       = new Color( 97,  97,  97);
    public static final Color TEXT_HINT      = new Color(189, 189, 189);

    // alternating row colours for tables
    public static final Color TABLE_ODD      = Color.WHITE;
    public static final Color TABLE_EVEN     = new Color(245, 250, 245);
    public static final Color TABLE_SEL      = new Color(200, 230, 200);
    public static final Color TABLE_HDR_BG   = new Color( 27,  94,  32);
    public static final Color TABLE_HDR_FG   = Color.WHITE;

    // font sizes used throughout the UI
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
