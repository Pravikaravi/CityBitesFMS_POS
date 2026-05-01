package citybitesfms;

import citybitesfms.ui.LoginSelectionFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main — application entry point for the City Bites Food Management System.
 *
 * Responsibilities:
 *   1. Apply the Nimbus Look-and-Feel for a modern, non-grey Swing appearance.
 *   2. Schedule the first frame (LoginFrame) on the Event Dispatch Thread (EDT).
 *
 * The EDT requirement is standard Java Swing practice: all UI creation and
 * updates must run on the single dedicated GUI thread to avoid race conditions.
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 2.0
 */
public class Main {

    /**
     * Launches the City Bites Food Management System.
     *
     * Sets Nimbus Look-and-Feel so the application looks modern and clean,
     * then opens the LoginFrame on the Event Dispatch Thread.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {

        // ── Apply Nimbus Look-and-Feel ─────────────────────────────────────
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Nimbus not available — fall back to the cross-platform default
            System.err.println("Nimbus LAF not available: " + e.getMessage());
        }

        // ── Launch on the Event Dispatch Thread ────────────────────────────
        SwingUtilities.invokeLater(() -> new LoginSelectionFrame());
    }
}
