package Gui;

import Dao.MedicineDAO;
import Dao.ReportDAO;
import Models.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Reports panel
public class ReportsPanel extends JPanel {

    private final ReportDAO reportDAO = new ReportDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ReportsPanel() {
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Sales Report", buildSalesReportTab());
        tabs.addTab("Item-Wise Report", buildItemWiseReportTab());
        tabs.addTab("Low Stock Report", buildLowStockReportTab());
        tabs.addTab("Expiry Report", buildExpiryReportTab());

        add(tabs, BorderLayout.CENTER);
    }

    // Sales Report
    private JPanel buildSalesReportTab() {
        String[] columns = {"Sale ID", "Sale Date", "Cashier", "Total Amount"};
        DefaultTableModel model = readOnlyModel(columns);
        JTable table = new JTable(model);
        table.setRowHeight(24);

        Runnable loadData = () -> {
            model.setRowCount(0);
            List<ReportDAO.SalesReportRow> rows = reportDAO.getSalesReport();
            for (ReportDAO.SalesReportRow row : rows) {
                model.addRow(new Object[]{
                        row.saleId,
                        row.saleDate.toLocalDateTime().format(DATETIME_FORMAT),
                        row.cashierName,
                        String.format("R%.2f", row.totalAmount)
                });
            }
        };
        loadData.run();

        return wrapWithRefresh(table, loadData);
    }

    // Item-Wise Report
    private JPanel buildItemWiseReportTab() {
        String[] columns = {"Medicine", "Quantity Sold", "Revenue"};
        DefaultTableModel model = readOnlyModel(columns);
        JTable table = new JTable(model);
        table.setRowHeight(24);

        Runnable loadData = () -> {
            model.setRowCount(0);
            List<ReportDAO.ItemWiseReportRow> rows = reportDAO.getItemWiseReport();
            for (ReportDAO.ItemWiseReportRow row : rows) {
                model.addRow(new Object[]{
                        row.medicineName,
                        row.totalQuantitySold,
                        String.format("R%.2f", row.totalRevenue)
                });
            }
        };
        loadData.run();

        return wrapWithRefresh(table, loadData);
    }
 
    // Low Stock Report
    private JPanel buildLowStockReportTab() {
        String[] columns = {"Medicine", "Current Stock", "Reorder Level", "Supplier"};
        DefaultTableModel model = readOnlyModel(columns);
        JTable table = new JTable(model);
        table.setRowHeight(24);

        Runnable loadData = () -> {
            model.setRowCount(0);
            List<Medicine> medicines = medicineDAO.getLowStockMedicines();
            for (Medicine m : medicines) {
                model.addRow(new Object[]{
                        m.getName(), m.getQuantityInStock(), m.getReorderLevel(),
                        m.getSupplierName() != null ? m.getSupplierName() : "(none)"
                });
            }
        };
        loadData.run();

        return wrapWithRefresh(table, loadData);
    }

    // Expiry Report
    private JPanel buildExpiryReportTab() {
        String[] columns = {"Medicine", "Expiry Date", "Current Stock", "Supplier"};
        DefaultTableModel model = readOnlyModel(columns);
        JTable table = new JTable(model);
        table.setRowHeight(24);

        Runnable loadData = () -> {
            model.setRowCount(0);
            List<Medicine> medicines = medicineDAO.getExpiringMedicines();
            for (Medicine m : medicines) {
                model.addRow(new Object[]{
                        m.getName(),
                        m.getExpiryDate() != null ? m.getExpiryDate().format(DATE_FORMAT) : "",
                        m.getQuantityInStock(),
                        m.getSupplierName() != null ? m.getSupplierName() : "(none)"
                });
            }
        };
        loadData.run();

        return wrapWithRefresh(table, loadData);
    }

    // HELPERS
    private DefaultTableModel readOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // Wraps a report table with a scroll pane and a "Refresh" button at the bottom
    private JPanel wrapWithRefresh(JTable table, Runnable loadDataAction) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(AppColors.BACKGROUND);

        // Colored table header so every report looks consistent with the rest of the app
        table.getTableHeader().setBackground(AppColors.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadDataAction.run());
        refreshButton.setBackground(AppColors.TEAL_DARK);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setOpaque(true);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(AppColors.BACKGROUND);
        bottomPanel.add(refreshButton);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }
}