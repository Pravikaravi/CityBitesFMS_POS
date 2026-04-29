package citybitesfms;

import citybitesfms.ui.LoginSelectionFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Application entry point for the City Bites Food Management System.
 *
 * This class contains only the {@code main} method, which is the first
 * method the JVM calls when the program starts.
 *
 * Key design decisions:
 *  1. {@code UIManager.setLookAndFeel()} applies the platform's native
 *     look-and-feel so the application blends in with Windows.
 *  2. {@code SwingUtilities.invokeLater()} schedules all UI work on the
 *     Event Dispatch Thread (EDT) — the single thread Swing requires for
 *     thread-safe GUI updates.  This is standard Java Swing practice.
 *
 * @author NovaSoft Solutions (PVT) Ltd
 * @version 1.0
 */
public class Main {

    /**
     * Launches the City Bites Food Management System.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {

        // Apply the host operating system's native look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to the default Swing cross-platform look and feel
            System.err.println("Could not set system look-and-feel: " + e.getMessage());
        }

        // Schedule the first frame on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginSelectionFrame frame = new LoginSelectionFrame();
            frame.setVisible(true);
        });
    }
}
