package Gui;

import Dao.MedicineDAO;
import Models.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Stock Panel
public class StockCheckPanel extends JPanel {

    private final MedicineDAO medicineDAO = new MedicineDAO();

    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public StockCheckPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(AppColors.BACKGROUND); // soft background instead of default gray

        JPanel searchBar = new JPanel(new BorderLayout(5, 5));
        searchBar.setBackground(AppColors.BACKGROUND);
        searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());

        // Color the search button so it stands out as the main action here
        searchButton.setBackground(AppColors.TEAL_DARK);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setOpaque(true);

        JLabel searchLabel = new JLabel("Medicine name: ");
        searchLabel.setForeground(AppColors.TEXT_DARK);

        searchBar.add(searchLabel, BorderLayout.WEST);
        searchBar.add(searchField, BorderLayout.CENTER);
        searchBar.add(searchButton, BorderLayout.EAST);

        String[] columns = {"Name", "Company", "Type", "Price", "Available Stock"};
        tableModel = new DefaultTableModel(columns, 0) {
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

        add(searchBar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Show the full list by default
        performSearch();
    }

    private void performSearch() {
        String term = searchField.getText().trim();
        tableModel.setRowCount(0);

        List<Medicine> results = term.isEmpty()
                ? medicineDAO.getAllMedicines()
                : medicineDAO.searchMedicinesByName(term);

        for (Medicine m : results) {
            tableModel.addRow(new Object[]{
                    m.getName(), m.getCompany(), m.getMedicineType(),
                    String.format("R%.2f", m.getPrice()), m.getQuantityInStock()
            });
        }
    }
}