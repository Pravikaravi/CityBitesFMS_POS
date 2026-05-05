package citybitesfms;

import citybitesfms.ui.LoginSelectionFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        // apply nimbus look and feel for better UI appearance
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // fallback to default if nimbus not available
            System.err.println("Nimbus LAF not available: " + e.getMessage());
        }

        // launch the login screen on the event dispatch thread
        SwingUtilities.invokeLater(() -> new LoginSelectionFrame());
    }
}
