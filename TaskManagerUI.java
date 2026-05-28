import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class TaskManagerUI extends JFrame {
    private TaskDAO taskDAO;
    private User currentUser;
    
    // Components
    private JTextField txtTitle;
    private JTextArea txtDesc;
    private JTextField txtDeadline;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnComplete, btnDelete;
    private JButton btnThemeToggle;

    // Overloaded Constructor to receive user session
    public TaskManagerUI(User user) {
        this.currentUser = user;
        this.taskDAO = new TaskDAO();
        initUI();
        loadTasks();
    }

    // Default Constructor fallback
    public TaskManagerUI() {
        this(null);
    }

    private void initUI() {
        setTitle("Task & Productivity Manager");
        setSize(850, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Screen ke center me open hoga

        // Colors
        Color primaryColor = new Color(98, 0, 238); // Vibrant Purple
        Color btnGreen = new Color(46, 213, 115);   // Complete Green
        Color btnRed = new Color(255, 71, 87);      // Delete Red

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        // --- HEADER (With User Info, Theme Toggle and Logout) ---
        JPanel headerPanel = new JPanel(new BorderLayout(15, 5));
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel("Task & Productivity Manager");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // User, Theme and Logout Pane
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        String displayUsername = (currentUser != null) ? currentUser.getUsername() : "Guest";
        JLabel lblUser = new JLabel("Hi, " + displayUsername);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(Color.WHITE);
        userPanel.add(lblUser);

        // Theme Toggle Button
        String toggleText = FlatLaf.isLafDark() ? "☀️ Light Mode" : "🌙 Dark Mode";
        btnThemeToggle = new JButton(toggleText);
        btnThemeToggle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnThemeToggle.setFocusPainted(false);
        btnThemeToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThemeToggle.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        
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
                    FlatLaf.updateUI(); // Refreshes look-and-feel of all open components instantly
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        userPanel.add(btnThemeToggle);

        // Logout Button
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(235, 77, 75)); // Crimson Red
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(TaskManagerUI.this, 
                    "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    LoginUI loginUI = new LoginUI();
                    loginUI.setVisible(true);
                    TaskManagerUI.this.dispose(); // Close dashboard
                }
            }
        });
        userPanel.add(btnLogout);

        headerPanel.add(userPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- LEFT SIDE: INPUT FORM ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150, 80), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        formPanel.setPreferredSize(new Dimension(300, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);

        // Form Title
        JLabel lblFormTitle = new JLabel("Add New Task");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(primaryColor);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(lblFormTitle, gbc);

        // Title Input
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Task Title:"), gbc);
        txtTitle = new JTextField();
        txtTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 2;
        formPanel.add(txtTitle, gbc);

        // Description Input
        gbc.gridy = 3;
        formPanel.add(new JLabel("Description:"), gbc);
        txtDesc = new JTextArea(4, 20);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDesc);
        gbc.gridy = 4;
        formPanel.add(descScroll, gbc);

        // Deadline Input
        gbc.gridy = 5;
        formPanel.add(new JLabel("Deadline (YYYY-MM-DD):"), gbc);
        txtDeadline = new JTextField();
        txtDeadline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDeadline.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        gbc.gridy = 6;
        formPanel.add(txtDeadline, gbc);

        // Add Button
        btnAdd = new JButton("Add Task");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setBackground(primaryColor);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        gbc.gridy = 7; gbc.insets = new java.awt.Insets(15, 5, 5, 5);
        formPanel.add(btnAdd, gbc);

        mainPanel.add(formPanel, BorderLayout.WEST);

        // --- RIGHT SIDE: VIEW PANEL (TABLE) ---
        JPanel viewPanel = new JPanel(new BorderLayout(10, 10));
        viewPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150, 80), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Table Model Setup
        String[] columns = {"ID", "Title", "Description", "Deadline", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        taskTable = new JTable(tableModel);
        taskTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        taskTable.setRowHeight(28); // Generous padding for list items
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Hide ID Column from view
        taskTable.getColumnModel().getColumn(0).setMinWidth(0);
        taskTable.getColumnModel().getColumn(0).setMaxWidth(0);
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane tableScroll = new JScrollPane(taskTable);
        viewPanel.add(tableScroll, BorderLayout.CENTER);

        // Bottom Action Buttons Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        btnComplete = new JButton("Mark Completed");
        btnComplete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnComplete.setBackground(btnGreen);
        btnComplete.setForeground(Color.WHITE);
        btnComplete.setFocusPainted(false);

        btnDelete = new JButton("Delete Task");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDelete.setBackground(btnRed);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);

        actionPanel.add(btnComplete);
        actionPanel.add(btnDelete);
        viewPanel.add(actionPanel, BorderLayout.SOUTH);

        mainPanel.add(viewPanel, BorderLayout.CENTER);

        // --- BUTTON ACTION LISTENERS ---

        // Add Task Action
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = txtTitle.getText().trim();
                String desc = txtDesc.getText().trim();
                String deadline = txtDeadline.getText().trim();

                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(TaskManagerUI.this, 
                        "Please enter a task title!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Currently logged-in user's ID pass karenge
                int userId = (currentUser != null) ? currentUser.getId() : -1;
                Task newTask = new Task(title, desc, deadline, userId);
                
                if (taskDAO.addTask(newTask)) {
                    JOptionPane.showMessageDialog(TaskManagerUI.this, 
                        "Task added successfully!");
                    clearInputFields();
                    loadTasks();
                } else {
                    JOptionPane.showMessageDialog(TaskManagerUI.this, 
                        "Failed to add task!", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Mark Completed Action
        btnComplete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = taskTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(TaskManagerUI.this, 
                        "Please select a task from the table first!", "Selection Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int id = (int) tableModel.getValueAt(selectedRow, 0);
                if (taskDAO.markTaskAsCompleted(id)) {
                    JOptionPane.showMessageDialog(TaskManagerUI.this, "Task marked as completed!");
                    loadTasks();
                } else {
                    JOptionPane.showMessageDialog(TaskManagerUI.this, "Failed to update task status!");
                }
            }
        });

        // Delete Action
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = taskTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(TaskManagerUI.this, 
                        "Please select a task from the table first!", "Selection Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(TaskManagerUI.this, 
                    "Are you sure you want to delete this task?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = (int) tableModel.getValueAt(selectedRow, 0);
                    if (taskDAO.deleteTask(id)) {
                        JOptionPane.showMessageDialog(TaskManagerUI.this, "Task deleted successfully!");
                        loadTasks();
                    } else {
                        JOptionPane.showMessageDialog(TaskManagerUI.this, "Failed to delete task!");
                    }
                }
            }
        });
    }

    private void clearInputFields() {
        txtTitle.setText("");
        txtDesc.setText("");
        txtDeadline.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    private void loadTasks() {
        tableModel.setRowCount(0); 
        // Logged-in user ke ID ke basis pe tasks call karenge
        int userId = (currentUser != null) ? currentUser.getId() : -1;
        List<Task> tasks = taskDAO.getAllTasks(userId);
        for (Task task : tasks) {
            String status = task.isCompleted() ? "Completed ✅" : "Pending ⏳";
            tableModel.addRow(new Object[]{
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDeadline(),
                status
            });
        }
    }
}
