package Gui;

import Dao.MedicineDAO;
import Dao.SupplierDAO;
import Models.Medicine;
import Models.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

// Medicine Panel
public class MedicinePanel extends JPanel {

    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    //  Form fields
    private JTextField nameField;
    private JTextField companyField;
    private JComboBox<String> typeCombo;
    private JTextField priceField;
    private JTextField quantityField;
    private JTextField reorderField;
    // format: yyyy-MM-dd
    private JTextField expiryField; 
    private JComboBox<Supplier> supplierCombo;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;

    // Keeps track of which medicine_id is currently selected for Update/Delete
    private int selectedMedicineId = -1;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public MedicinePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(AppColors.BACKGROUND); // soft background instead of default gray

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);

        loadSuppliersIntoCombo();
        refreshTable();
    }

    // BUILDING THE FORM 
    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(AppColors.CARD_BACKGROUND);
        // Colored titled border so the form reads as its own "card"
        javax.swing.border.TitledBorder titledBorder = BorderFactory.createTitledBorder("Medicine Details");
        titledBorder.setTitleColor(AppColors.PRIMARY_DARK);
        titledBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 13));
        formPanel.setBorder(titledBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(15);
        companyField = new JTextField(15);
        typeCombo = new JComboBox<>(new String[]{"Tablet", "Capsule", "Syrup", "Injection", "Cream"});
        typeCombo.setEditable(true); // allow custom types not in the list
        priceField = new JTextField(8);
        quantityField = new JTextField(8);
        reorderField = new JTextField(8);
        expiryField = new JTextField(10);
        expiryField.setToolTipText("Format: yyyy-MM-dd, e.g. 2027-01-31");
        supplierCombo = new JComboBox<>();

        // Row 0
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; formPanel.add(nameField, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Company:"), gbc);
        gbc.gridx = 3; formPanel.add(companyField, gbc);

        // Row 1
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1; formPanel.add(typeCombo, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Price (R):"), gbc);
        gbc.gridx = 3; formPanel.add(priceField, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Quantity in Stock:"), gbc);
        gbc.gridx = 1; formPanel.add(quantityField, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Reorder Level:"), gbc);
        gbc.gridx = 3; formPanel.add(reorderField, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Expiry Date:"), gbc);
        gbc.gridx = 1; formPanel.add(expiryField, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Supplier:"), gbc);
        gbc.gridx = 3; formPanel.add(supplierCombo, gbc);

        // Row 4: buttons
        JButton addButton = new JButton("Add Medicine");
        JButton updateButton = new JButton("Update Medicine");
        JButton deleteButton = new JButton("Delete Medicine");
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

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
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

    // BUILDING THE TABLE 
    private JScrollPane buildTablePanel() {
        String[] columnNames = {
                "ID", "Name", "Company", "Type", "Price",
                "Stock", "Reorder Lvl", "Expiry Date", "Supplier"
        };

        // A table model where no cell can be edited directly by typing -
        // all changes go through the form fields above instead.
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

        // Color each row based on stock vs reorder level, so low-stock
        // medicines jump out visually without reading every number.
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    try {
                        int stock = Integer.parseInt(tbl.getValueAt(row, 5).toString());        // "Stock" column
                        int reorderLevel = Integer.parseInt(tbl.getValueAt(row, 6).toString());  // "Reorder Lvl" column

                        if (stock <= reorderLevel) {
                            cell.setBackground(AppColors.ROW_LOW_STOCK);       // at/below reorder level
                        } else if (stock <= reorderLevel * 2) {
                            cell.setBackground(AppColors.ROW_MEDIUM_STOCK);   // getting close
                        } else {
                            cell.setBackground(AppColors.ROW_OK_STOCK);
                        }
                    } catch (NumberFormatException | NullPointerException ex) {
                        cell.setBackground(AppColors.ROW_OK_STOCK);
                    }
                } else {
                    cell.setBackground(AppColors.PRIMARY);
                    cell.setForeground(Color.WHITE);
                }
                return cell;
            }
        });

        return new JScrollPane(table);
    }

    // DATA LOADING
    // Refreshes the JTable with the current contents of the medicines table
    private void refreshTable() {
        tableModel.setRowCount(0); // clear existing rows

        List<Medicine> medicines = medicineDAO.getAllMedicines();
        for (Medicine m : medicines) {
            tableModel.addRow(new Object[]{
                    m.getMedicineId(),
                    m.getName(),
                    m.getCompany(),
                    m.getMedicineType(),
                    String.format("R%.2f", m.getPrice()),
                    m.getQuantityInStock(),
                    m.getReorderLevel(),
                    m.getExpiryDate() != null ? m.getExpiryDate().format(DATE_FORMAT) : "",
                    m.getSupplierName() != null ? m.getSupplierName() : "(none)"
            });
        }
    }

    // Loads all suppliers into the dropdown so the admin can pick one
    private void loadSuppliersIntoCombo() {
        supplierCombo.removeAllItems();
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        for (Supplier s : suppliers) {
            supplierCombo.addItem(s);
        }
    }

    //When a table row is clicked, copy its values into the form fields
    private void populateFormFromSelectedRow() {
        int row = table.getSelectedRow();
        selectedMedicineId = (int) tableModel.getValueAt(row, 0);

        nameField.setText((String) tableModel.getValueAt(row, 1));
        companyField.setText((String) tableModel.getValueAt(row, 2));
        typeCombo.setSelectedItem(tableModel.getValueAt(row, 3));

        String priceText = ((String) tableModel.getValueAt(row, 4)).replace("R", "");
        priceField.setText(priceText);

        quantityField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        reorderField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        expiryField.setText((String) tableModel.getValueAt(row, 7));

        String supplierName = (String) tableModel.getValueAt(row, 8);
        for (int i = 0; i < supplierCombo.getItemCount(); i++) {
            if (supplierCombo.getItemAt(i).getName().equals(supplierName)) {
                supplierCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    // CRUD ACTIONS
    private void handleAdd() {
        Medicine medicine = buildMedicineFromForm();
        if (medicine == null) {
            return; 
        }

        boolean success = medicineDAO.addMedicine(medicine);
        if (success) {
            JOptionPane.showMessageDialog(this, "Medicine added successfully.");
            clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add medicine. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedMedicineId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine from the table first.",
                    "No Medicine Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Medicine medicine = buildMedicineFromForm();
        if (medicine == null) {
            return;
        }
        medicine.setMedicineId(selectedMedicineId);

        boolean success = medicineDAO.updateMedicine(medicine);
        if (success) {
            JOptionPane.showMessageDialog(this, "Medicine updated successfully.");
            clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update medicine. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (selectedMedicineId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine from the table first.",
                    "No Medicine Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this medicine?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = medicineDAO.deleteMedicine(selectedMedicineId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Medicine deleted.");
                clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete medicine. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
     // Return a valid Medicine object, or null if validation failed
    private Medicine buildMedicineFromForm() {
        String name = nameField.getText().trim();
        String company = companyField.getText().trim();
        String type = (String) typeCombo.getEditor().getItem();
        String priceText = priceField.getText().trim();
        String quantityText = quantityField.getText().trim();
        String reorderText = reorderField.getText().trim();
        String expiryText = expiryField.getText().trim();
        Supplier selectedSupplier = (Supplier) supplierCombo.getSelectedItem();

        // Validation
        if (name.isEmpty()) {
            showValidationError("Please enter a medicine name.");
            return null;
        }
        if (selectedSupplier == null) {
            showValidationError("Please select a supplier.");
            return null;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showValidationError("Invalid price. Please enter a positive number (e.g. 45.50).");
            return null;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showValidationError("Quantity in stock must be a whole number (0 or more).");
            return null;
        }

        int reorderLevel;
        try {
            reorderLevel = Integer.parseInt(reorderText);
            if (reorderLevel < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showValidationError("Reorder level must be a whole number (0 or more).");
            return null;
        }

        LocalDate expiryDate;
        try {
            expiryDate = LocalDate.parse(expiryText, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            showValidationError("Invalid expiry date. Please use the format yyyy-MM-dd (e.g. 2027-01-31).");
            return null;
        }

        Medicine medicine = new Medicine();
        medicine.setName(name);
        medicine.setCompany(company);
        medicine.setMedicineType(type);
        medicine.setPrice(price);
        medicine.setQuantityInStock(quantity);
        medicine.setReorderLevel(reorderLevel);
        medicine.setExpiryDate(expiryDate);
        medicine.setSupplierId(selectedSupplier.getSupplierId());

        return medicine;
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }

    private void clearForm() {
        selectedMedicineId = -1;
        nameField.setText("");
        companyField.setText("");
        typeCombo.setSelectedIndex(0);
        priceField.setText("");
        quantityField.setText("");
        reorderField.setText("");
        expiryField.setText("");
        if (supplierCombo.getItemCount() > 0) {
            supplierCombo.setSelectedIndex(0);
        }
        table.clearSelection();
    }
}