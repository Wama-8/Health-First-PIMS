package Gui;

import Dao.UserDAO;
import Models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// User Panel
public class UserPanel extends JPanel {

    private final UserDAO userDAO = new UserDAO();

    // Form fields 
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;
    private int selectedUserId = -1;

    public UserPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(AppColors.BACKGROUND); // soft background instead of default gray

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);

        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(AppColors.CARD_BACKGROUND);
        javax.swing.border.TitledBorder titledBorder = BorderFactory.createTitledBorder("New Cashier Account");
        titledBorder.setTitleColor(AppColors.PRIMARY_DARK);
        titledBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 13));
        formPanel.setBorder(titledBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        fullNameField = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; formPanel.add(usernameField, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 3; formPanel.add(fullNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; formPanel.add(passwordField, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 3; formPanel.add(new JLabel("Cashier (fixed)"), gbc);

        JButton createButton = new JButton("Create Cashier");
        JButton deleteButton = new JButton("Delete Selected Cashier");
        JButton clearButton = new JButton("Clear Form");
        JButton refreshButton = new JButton("Refresh");

        createButton.addActionListener(e -> handleCreate());
        deleteButton.addActionListener(e -> handleDelete());
        clearButton.addActionListener(e -> clearForm());
        refreshButton.addActionListener(e -> refreshTable());

        // Color each button by what it does, so the action is obvious at a glance
        colorButton(createButton, AppColors.SUCCESS);
        colorButton(deleteButton, AppColors.DANGER);
        colorButton(clearButton, AppColors.TEXT_MUTED);
        colorButton(refreshButton, AppColors.TEAL_DARK);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setBackground(AppColors.CARD_BACKGROUND);
        buttonPanel.add(createButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    // Small helper: gives a button a solid background color + white text,
    // so buttons don't all look like the same plain gray Swing default.
    private void colorButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
    }

    private JPanel buildTablePanel() {
        String[] columnNames = {"User ID", "Username", "Full Name", "Role"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getTableHeader().setBackground(AppColors.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                selectedUserId = (int) tableModel.getValueAt(table.getSelectedRow(), 0);
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AppColors.CARD_BACKGROUND);
        javax.swing.border.TitledBorder wrapperBorder = BorderFactory.createTitledBorder("Existing Cashier Accounts");
        wrapperBorder.setTitleColor(AppColors.PRIMARY_DARK);
        wrapperBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 13));
        wrapper.setBorder(wrapperBorder);
        wrapper.add(new JScrollPane(table), BorderLayout.CENTER);

        return wrapper;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        List<User> cashiers = userDAO.getAllCashiers();
        for (User u : cashiers) {
            tableModel.addRow(new Object[]{
                    u.getUserId(), u.getUsername(), u.getFullName(), u.getRole()
            });
        }
    }

    private void handleCreate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String fullName = fullNameField.getText().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in username, password, and full name.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userDAO.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "That username is already taken. Please choose another.",
                    "Username Taken", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newCashier = new User(username, password, "Cashier", fullName);
        boolean success = userDAO.createCashier(newCashier);

        if (success) {
            JOptionPane.showMessageDialog(this, "Cashier account created successfully.");
            clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create cashier account. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a cashier from the table first.",
                    "No Cashier Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this cashier account?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userDAO.deleteCashier(selectedUserId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Cashier account deleted.");
                clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not delete this cashier - they may have existing sales on record.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedUserId = -1;
        usernameField.setText("");
        passwordField.setText("");
        fullNameField.setText("");
        table.clearSelection();
    }
}