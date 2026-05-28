import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatDarkLaf;

public class Main {
    public static void main(String[] args) {
        // 1. Database tables (users aur tasks) ko startup par hi initialize/create kar denge
        DBConnection.initializeDatabase();

        // 2. Swing GUI ko FlatLaf Dark theme ke sath initialize karenge
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.out.println("Failed to initialize FlatLaf theme! Using default Look & Feel.");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 3. Login Screen open karenge
                LoginUI loginScreen = new LoginUI();
                loginScreen.setVisible(true);
            }
        });
    }
}
