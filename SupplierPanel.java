package Gui;

import Dao.SupplierDAO;
import Models.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

// Sippliers panel
public class SupplierPanel extends JPanel {

    private final SupplierDAO supplierDAO = new SupplierDAO();

    // validating email addresses
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    // Form fields
    private JTextField nameField;
    private JTextField contactPersonField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextArea addressArea;

    // Table 
    private JTable table;
    private DefaultTableModel tableModel;
    private int selectedSupplierId = -1;

    public SupplierPanel() {
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
        // Colored titled border so the form reads as its own "card"
        javax.swing.border.TitledBorder titledBorder = BorderFactory.createTitledBorder("Supplier Details");
        titledBorder.setTitleColor(AppColors.PRIMARY_DARK);
        titledBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 13));
        formPanel.setBorder(titledBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(18);
        contactPersonField = new JTextField(18);
        phoneField = new JTextField(15);
        emailField = new JTextField(18);
        addressArea = new JTextArea(2, 18);
        addressArea.setLineWrap(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Supplier Name:"), gbc);
        gbc.gridx = 1; formPanel.add(nameField, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Contact Person:"), gbc);
        gbc.gridx = 3; formPanel.add(contactPersonField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; formPanel.add(phoneField, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3; formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; formPanel.add(addressScroll, gbc);
        gbc.gridwidth = 1;

        JButton addButton = new JButton("Add Supplier");
        JButton updateButton = new JButton("Update Supplier");
        JButton deleteButton = new JButton("Delete Supplier");
        JButton clearButton = new JButton("Clear Form");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> handleAdd());
        updateButton.addActionListener(e -> handleUpdate());
        deleteButton.addActionListener(e -> handleDelete());
        clearButton.addActionListener(e -> clearForm());
        refreshButton.addActionListener(e -> refreshTable());

        // Color each button by what it does, so the action is obvious at a glance
        colorButton(addButton, AppColors.SUCCESS);
        colorButton(updateButton, AppColors.PRIMARY);
        colorButton(deleteButton, AppColors.DANGER);
        colorButton(clearButton, AppColors.TEXT_MUTED);
        colorButton(refreshButton, AppColors.TEAL_DARK);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setBackground(AppColors.CARD_BACKGROUND);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
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

    private JScrollPane buildTablePanel() {
        String[] columnNames = {"ID", "Name", "Contact Person", "Phone", "Email", "Address"};

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
                populateFormFromSelectedRow();
            }
        });

        return new JScrollPane(table);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        for (Supplier s : suppliers) {
            tableModel.addRow(new Object[]{
                    s.getSupplierId(), s.getName(), s.getContactPerson(),
                    s.getPhone(), s.getEmail(), s.getAddress()
            });
        }
    }

    private void populateFormFromSelectedRow() {
        int row = table.getSelectedRow();
        selectedSupplierId = (int) tableModel.getValueAt(row, 0);

        nameField.setText((String) tableModel.getValueAt(row, 1));
        contactPersonField.setText((String) tableModel.getValueAt(row, 2));
        phoneField.setText((String) tableModel.getValueAt(row, 3));
        emailField.setText((String) tableModel.getValueAt(row, 4));
        addressArea.setText((String) tableModel.getValueAt(row, 5));
    }

    private void handleAdd() {
        Supplier supplier = buildSupplierFromForm();
        if (supplier == null) return;

        boolean success = supplierDAO.addSupplier(supplier);
        if (success) {
            JOptionPane.showMessageDialog(this, "Supplier added successfully.");
            clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add supplier. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedSupplierId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier from the table first.",
                    "No Supplier Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Supplier supplier = buildSupplierFromForm();
        if (supplier == null) return;
        supplier.setSupplierId(selectedSupplierId);

        boolean success = supplierDAO.updateSupplier(supplier);
        if (success) {
            JOptionPane.showMessageDialog(this, "Supplier updated successfully.");
            clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update supplier. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (selectedSupplierId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier from the table first.",
                    "No Supplier Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this supplier?\n"
                        + "(Medicines linked to this supplier will keep their other details, "
                        + "but will show \"no supplier\" until reassigned.)",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = supplierDAO.deleteSupplier(selectedSupplierId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Supplier deleted.");
                clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete supplier. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Supplier buildSupplierFromForm() {
        String name = nameField.getText().trim();
        String contactPerson = contactPersonField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressArea.getText().trim();

        if (name.isEmpty()) {
            showValidationError("Please enter a supplier name.");
            return null;
        }

        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            showValidationError("Please enter a valid email address (e.g. name@example.com).");
            return null;
        }

        if (!phone.isEmpty() && !phone.matches("[0-9+()\\-\\s]+")) {
            showValidationError("Phone number should only contain digits and symbols like + ( ) -.");
            return null;
        }

        return new Supplier(name, contactPerson, phone, email, address);
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }

    private void clearForm() {
        selectedSupplierId = -1;
        nameField.setText("");
        contactPersonField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressArea.setText("");
        table.clearSelection();
    }
}