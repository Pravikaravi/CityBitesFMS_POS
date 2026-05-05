package citybitesfms;

import citybitesfms.ui.LoginSelectionFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            
            System.err.println("Nimbus LAF not available: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> new LoginSelectionFrame());
    }
}
