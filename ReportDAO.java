package Dao;

import database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Handles the  sales report and item-wise report
public class ReportDAO {

    // One row of the Sales Report showing one completed transaction
    public static class SalesReportRow {
        public int saleId;
        public Timestamp saleDate;
        public String cashierName;
        public double totalAmount;
    }

    //One row of the Item-Wise Report showing the total performance of one medicine
    public static class ItemWiseReportRow {
        public String medicineName;
        public int totalQuantitySold;
        public double totalRevenue;
    }

    // Returns all the sales made and shows which cashier processed the sales
    public List<SalesReportRow> getSalesReport() {
        List<SalesReportRow> rows = new ArrayList<>();
        String sql = "SELECT sa.sale_id, sa.sale_date, sa.total_amount, u.full_name " +
                     "FROM sales sa JOIN users u ON sa.user_id = u.user_id " +
                     "ORDER BY sa.sale_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SalesReportRow row = new SalesReportRow();
                row.saleId = rs.getInt("sale_id");
                row.saleDate = rs.getTimestamp("sale_date");
                row.totalAmount = rs.getDouble("total_amount");
                row.cashierName = rs.getString("full_name");
                rows.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error generating sales report: " + e.getMessage());
        }

        return rows;
    }

    // Returns the total units sold for each medicine
    public List<ItemWiseReportRow> getItemWiseReport() {
        List<ItemWiseReportRow> rows = new ArrayList<>();
        String sql = "SELECT m.name AS medicine_name, " +
                     "       SUM(si.quantity_sold) AS total_quantity, " +
                     "       SUM(si.quantity_sold * si.price_at_sale) AS total_revenue " +
                     "FROM sale_items si JOIN medicines m ON si.medicine_id = m.medicine_id " +
                     "GROUP BY m.medicine_id, m.name " +
                     "ORDER BY total_revenue DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ItemWiseReportRow row = new ItemWiseReportRow();
                row.medicineName = rs.getString("medicine_name");
                row.totalQuantitySold = rs.getInt("total_quantity");
                row.totalRevenue = rs.getDouble("total_revenue");
                rows.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error generating item-wise report: " + e.getMessage());
        }

        return rows;
    }
}
