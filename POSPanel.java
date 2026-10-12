package Gui;

import Dao.MedicineDAO;
import Dao.SaleDAO;
import Models.Medicine;
import Models.Sale;
import Models.SaleItem;
import Models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// Point of Sale
public class POSPanel extends JPanel {

    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final SaleDAO saleDAO = new SaleDAO();
    private final User loggedInCashier;

    // Search section 
    private JTextField searchField;
    private JTable searchResultsTable;
    private DefaultTableModel searchResultsModel;
    private List<Medicine> lastSearchResults = new ArrayList<>();

    private JTextField quantityField;

    //  Cart section
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private final List<SaleItem> cart = new ArrayList<>();
    private JLabel totalLabel;

    public POSPanel(User loggedInCashier) {
        this.loggedInCashier = loggedInCashier;

        setLayout(new GridLayout(1, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(AppColors.BACKGROUND); // soft background instead of default gray

        add(buildSearchSection());
        add(buildCartSection());
    }

    // search for medicines
  
    private JPanel buildSearchSection() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(AppColors.CARD_BACKGROUND);
        javax.swing.border.TitledBorder searchBorder = BorderFactory.createTitledBorder("Search Medicines");
        searchBorder.setTitleColor(AppColors.PRIMARY_DARK);
        searchBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 13));
        panel.setBorder(searchBorder);

        // ----- Search bar -----
        JPanel searchBar = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch()); // Enter key also searches

        searchBar.add(searchField, BorderLayout.CENTER);
        searchBar.add(searchButton, BorderLayout.EAST);

        //  Results table 
        String[] columns = {"ID", "Name", "Type", "Price", "Available Stock"};
        searchResultsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        searchResultsTable = new JTable(searchResultsModel);
        searchResultsTable.setRowHeight(24);
        searchResultsTable.getTableHeader().setBackground(AppColors.PRIMARY);
        searchResultsTable.getTableHeader().setForeground(Color.WHITE);
        searchResultsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Quantity + Add to Cart
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        addPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField(5);
        quantityField.setText("1");
        addPanel.add(quantityField);

        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> handleAddToCart());
        addToCartButton.setBackground(AppColors.SUCCESS);
        addToCartButton.setForeground(Color.WHITE);
        addToCartButton.setFocusPainted(false);
        addToCartButton.setOpaque(true);
        addPanel.add(addToCartButton);

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(searchResultsTable), BorderLayout.CENTER);
        panel.add(addPanel, BorderLayout.SOUTH);

        return panel;
    }

    //  The shopping cart
    private JPanel buildCartSection() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(AppColors.CARD_BACKGROUND);
        javax.swing.border.TitledBorder cartBorder = BorderFactory.createTitledBorder("Cart");
        cartBorder.setTitleColor(AppColors.PRIMARY_DARK);
        cartBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 13));
        panel.setBorder(cartBorder);

        String[] columns = {"Medicine", "Quantity", "Price", "Line Total"};
        cartModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(24);
        cartTable.getTableHeader().setBackground(AppColors.SIDEBAR);
        cartTable.getTableHeader().setForeground(Color.WHITE);
        cartTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // total + action buttons 
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));

        totalLabel = new JLabel("TOTAL AMOUNT: R0.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLabel.setForeground(AppColors.PRIMARY_DARK); // make the running total pop
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JButton removeItemButton = new JButton("Remove Selected Item");
        JButton clearCartButton = new JButton("Clear Cart");
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.setFont(new Font("SansSerif", Font.BOLD, 13));

        removeItemButton.addActionListener(e -> handleRemoveItem());
        clearCartButton.addActionListener(e -> handleClearCart());
        checkoutButton.addActionListener(e -> handleCheckout());

        // Color each button by what it does, so the action is obvious at a glance
        removeItemButton.setBackground(AppColors.DANGER);
        removeItemButton.setForeground(Color.WHITE);
        removeItemButton.setFocusPainted(false);
        removeItemButton.setOpaque(true);

        clearCartButton.setBackground(AppColors.TEXT_MUTED);
        clearCartButton.setForeground(Color.WHITE);
        clearCartButton.setFocusPainted(false);
        clearCartButton.setOpaque(true);

        checkoutButton.setBackground(AppColors.SUCCESS);
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setOpaque(true);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        buttonRow.add(removeItemButton);
        buttonRow.add(clearCartButton);
        buttonRow.add(checkoutButton);

        bottomPanel.add(totalLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonRow, BorderLayout.SOUTH);

        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ACTIONS
    private void performSearch() {
        String term = searchField.getText().trim();
        searchResultsModel.setRowCount(0);

        // An empty search shows everything; otherwise search by name.
        lastSearchResults = term.isEmpty()
                ? medicineDAO.getAllMedicines()
                : medicineDAO.searchMedicinesByName(term);

        for (Medicine m : lastSearchResults) {
            searchResultsModel.addRow(new Object[]{
                    m.getMedicineId(), m.getName(), m.getMedicineType(),
                    String.format("R%.2f", m.getPrice()), m.getQuantityInStock()
            });
        }

        if (lastSearchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No medicines found matching \"" + term + "\".");
        }
    }

    private void handleAddToCart() {
        int selectedRow = searchResultsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine from the search results first.",
                    "No Medicine Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Medicine selectedMedicine = lastSearchResults.get(selectedRow);

        // Validate quantity
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid whole number for quantity.",
                    "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.",
                    "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Stock validation: re-check the live database stock, combined with whatever is already sitting in the cart
        int liveStock = medicineDAO.getCurrentStock(selectedMedicine.getMedicineId());
        int alreadyInCart = quantityAlreadyInCart(selectedMedicine.getMedicineId());

        if (quantity + alreadyInCart > liveStock) {
            JOptionPane.showMessageDialog(this,
                    "Insufficient stock available.\n"
                            + "In stock: " + liveStock + " | Already in cart: " + alreadyInCart,
                    "Insufficient Stock", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add to cart or merge with an existing line for the same medicine
        SaleItem existingItem = findCartItem(selectedMedicine.getMedicineId());
        if (existingItem != null) {
            existingItem.setQuantitySold(existingItem.getQuantitySold() + quantity);
        } else {
            SaleItem newItem = new SaleItem(selectedMedicine.getMedicineId(), quantity, selectedMedicine.getPrice());
            newItem.setMedicineName(selectedMedicine.getName());
            cart.add(newItem);
        }

        refreshCartTable();
        quantityField.setText("1");
    }

    private void handleRemoveItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item in the cart to remove.",
                    "No Item Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        cart.remove(selectedRow);
        refreshCartTable();
    }

    private void handleClearCart() {
        if (cart.isEmpty()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Clear the entire cart?",
                "Confirm Clear", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            cart.clear();
            refreshCartTable();
        }
    }

    private void handleCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add an item to the cart before checkout.",
                    "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = calculateTotal();

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Confirm checkout for a total of R%.2f?", total),
                "Confirm Checkout", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Sale sale = new Sale(total, loggedInCashier.getUserId());
        int newSaleId = saleDAO.processCheckout(sale, cart);

        if (newSaleId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Checkout failed - stock may have changed. Please review your cart and try again.",
                    "Checkout Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Success: show the bill, then reset for the next customer
        new BillFrame(newSaleId, loggedInCashier.getFullName(), total).setVisible(true);

        cart.clear();
        refreshCartTable();
        searchResultsModel.setRowCount(0);
        searchField.setText("");
    }

    // HELPERS
    private SaleItem findCartItem(int medicineId) {
        for (SaleItem item : cart) {
            if (item.getMedicineId() == medicineId) {
                return item;
            }
        }
        return null;
    }

    private int quantityAlreadyInCart(int medicineId) {
        SaleItem item = findCartItem(medicineId);
        return item != null ? item.getQuantitySold() : 0;
    }

    private void refreshCartTable() {
        cartModel.setRowCount(0);
        for (SaleItem item : cart) {
            cartModel.addRow(new Object[]{
                    item.getMedicineName(),
                    item.getQuantitySold(),
                    String.format("R%.2f", item.getPriceAtSale()),
                    String.format("R%.2f", item.getLineTotal())
            });
        }
        totalLabel.setText(String.format("TOTAL AMOUNT: R%.2f", calculateTotal()));
    }

    private double calculateTotal() {
        double total = 0;
        for (SaleItem item : cart) {
            total += item.getLineTotal();
        }
        return total;
    }
}