import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class LoginUI extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnSignup;
    private UserDAO userDAO;
    private JButton btnThemeToggle;

    public LoginUI() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Study Planner Login");
        setSize(980, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Screen ke center me open hoga
        setResizable(false);

        // Container panel
        JPanel container = new JPanel(new GridLayout(1, 2));
        setContentPane(container);

        // --- LEFT PANE: FORM PANEL ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 40, 8, 40); // Padded margins

        // Row 0: Theme Toggle Button Panel (Align right)
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.setOpaque(false);
        
        // Toggle theme check dynamically
        String toggleText = FlatLaf.isLafDark() ? "☀️ Light Mode" : "🌙 Dark Mode";
        btnThemeToggle = new JButton(toggleText);
        btnThemeToggle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnThemeToggle.setFocusPainted(false);
        btnThemeToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnThemeToggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (FlatLaf.isLafDark()) {
                        UIManager.setLookAndFeel(new FlatLightLaf());
                        btnThemeToggle.setText("🌙 Dark Mode");
                    } else {
                        UIManager.setLookAndFeel(new FlatDarkLaf());
                        btnThemeToggle.setText("☀️ Light Mode");
                    }
                    FlatLaf.updateUI(); // Refreshes look-and-feel of all open UI components instantly
                } catch (Exception ex) {
                    System.out.println("Theme toggle failed!");
                    ex.printStackTrace();
                }
            }
        });
        topBar.add(btnThemeToggle);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(topBar, gbc);

        // Title: Welcome Back!
        JLabel lblTitle = new JLabel("Welcome Back!", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        gbc.gridy = 1;
        formPanel.add(lblTitle, gbc);

        // Subtitle
        JLabel lblSubtitle = new JLabel("Login to manage your study tasks", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(140, 140, 140));
        gbc.gridy = 2;
        formPanel.add(lblSubtitle, gbc);

        // Spacing
        gbc.gridy = 3;
        formPanel.add(Box.createVerticalStrut(15), gbc);

        // Username Label
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 4;
        formPanel.add(lblUsername, gbc);

        // Username Field
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtUsername.setPreferredSize(new Dimension(300, 38));
        gbc.gridy = 5;
        formPanel.add(txtUsername, gbc);

        // Password Label
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 6;
        formPanel.add(lblPassword, gbc);

        // Password Field
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPassword.setPreferredSize(new Dimension(300, 38));
        gbc.gridy = 7;
        formPanel.add(txtPassword, gbc);

        // Spacing
        gbc.gridy = 8;
        formPanel.add(Box.createVerticalStrut(15), gbc);

        // Buttons Panel (Horizontal layout for Login & Signup)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setOpaque(false);

        // Login Button
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(67, 107, 219)); // Aesthetic Blue
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Signup Button
        btnSignup = new JButton("Signup");
        btnSignup.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSignup.setBackground(new Color(138, 43, 226)); // Purple
        btnSignup.setForeground(Color.WHITE);
        btnSignup.setFocusPainted(false);
        btnSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSignup.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnSignup);

        gbc.gridy = 9;
        formPanel.add(buttonPanel, gbc);

        container.add(formPanel);

        // --- RIGHT PANE: IMAGE PANEL ---
        ImagePanel imgPanel = new ImagePanel("src/assets/study_bg.png");
        container.add(imgPanel);

        // --- BUTTON ACTION LISTENERS ---

        // Login Action
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginUI.this, 
                        "Username and Password cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                User user = userDAO.loginUser(username, password);
                if (user != null) {
                    JOptionPane.showMessageDialog(LoginUI.this, "Welcome " + user.getUsername() + "! Login Successful.");
                    // Open Task Manager UI and pass current user
                    TaskManagerUI dashboard = new TaskManagerUI(user);
                    dashboard.setVisible(true);
                    LoginUI.this.dispose(); // Close login window
                } else {
                    JOptionPane.showMessageDialog(LoginUI.this, 
                        "Invalid Username or Password!", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Signup Action
        btnSignup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginUI.this, 
                        "Username and Password cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (userDAO.registerUser(username, password)) {
                    JOptionPane.showMessageDialog(LoginUI.this, 
                        "Registration Successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    txtPassword.setText("");
                } else {
                    JOptionPane.showMessageDialog(LoginUI.this, 
                        "Username already exists or Registration failed!", "Registration Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Custom Panel to draw the background image responsively/safely
    private static class ImagePanel extends JPanel {
        private Image img;

        public ImagePanel(String path) {
            try {
                img = new ImageIcon(path).getImage();
            } catch (Exception e) {
                System.out.println("Could not load image: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null && img.getWidth(null) > 0) {
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            } else {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(44, 62, 80), getWidth(), getHeight(), new Color(76, 161, 175));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(Color.WHITE);
                g.setFont(new Font("Segoe UI", Font.BOLD, 28));
                g.drawString("Stay Disciplined", getWidth() / 2 - 100, getHeight() / 2);
            }
        }
    }
}
