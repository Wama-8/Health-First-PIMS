package Dao;

import database.DBConnection;
import Models.Sale;
import Models.SaleItem;

import java.sql.*;
import java.util.List;

// Handles saving completed sales to the database
public class SaleDAO {

    private final MedicineDAO medicineDAO = new MedicineDAO();

    /**
     * Processes a full checkout: saves the sale, saves every item in
     * the cart, and reduces stock for each medicine sold.
     *
     * @param sale      the Sale header (total amount + which cashier)
     * @param cartItems the list of items being purchased
     * @return the generated sale_id if successful, or -1 if the
     *         checkout failed for any reason (nothing is saved in that case)
     */
    public int processCheckout(Sale sale, List<SaleItem> cartItems) {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();

            // Turn off auto-commit so we control exactly when changes are actually saved to the database.
            conn.setAutoCommit(false);

            // Insert the sale header
            int newSaleId = insertSale(conn, sale);
            if (newSaleId == -1) {
                conn.rollback();
                return -1;
            }

            // Insert each line item AND reduce stock 
            for (SaleItem item : cartItems) {

                // Re-check stock right before committing, in case it changed since the cashier first added it to the cart.
                boolean stockReduced = medicineDAO.reduceStock(
                        conn, item.getMedicineId(), item.getQuantitySold());

                if (!stockReduced) {
                    // Not enough stock anymore, cancels the whole sale
                    conn.rollback();
                    return -1;
                }

                boolean itemSaved = insertSaleItem(conn, newSaleId, item);
                if (!itemSaved) {
                    conn.rollback();
                    return -1;
                }
            }

            // Everything succeessfull and makes it permanent
            conn.commit();
            return newSaleId;

        } catch (SQLException e) {
            System.err.println("Error during checkout: " + e.getMessage());
            // Something went wrong, undo any partial changes
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            return -1;

        } finally {
            // Always restore auto-commit and close the connection, whether checkout succeeded or failed.
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                System.err.println("Error closing connection: " + closeEx.getMessage());
            }
        }
    }

    // Inserts the sale header row and returns its generated sale_id.
    private int insertSale(Connection conn, Sale sale) throws SQLException {
        String sql = "INSERT INTO sales (total_amount, user_id) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, sale.getTotalAmount());
            stmt.setInt(2, sale.getUserId());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted == 0) {
                return -1;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        return -1;
    }

    // Inserts one sale_items row linked to the given sale_id.
    private boolean insertSaleItem(Connection conn, int saleId, SaleItem item) throws SQLException {
        String sql = "INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) "
                   + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, saleId);
            stmt.setInt(2, item.getMedicineId());
            stmt.setInt(3, item.getQuantitySold());
            stmt.setDouble(4, item.getPriceAtSale());

            return stmt.executeUpdate() > 0;
        }
    }

    //Fetches every line item belonging to one sale 
    public List<SaleItem> getItemsForSale(int saleId) {
        List<SaleItem> items = new java.util.ArrayList<>();
        String sql = "SELECT si.*, m.name AS medicine_name " +
                     "FROM sale_items si JOIN medicines m ON si.medicine_id = m.medicine_id " +
                     "WHERE si.sale_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SaleItem item = new SaleItem();
                    item.setSaleItemId(rs.getInt("sale_item_id"));
                    item.setSaleId(rs.getInt("sale_id"));
                    item.setMedicineId(rs.getInt("medicine_id"));
                    item.setMedicineName(rs.getString("medicine_name"));
                    item.setQuantitySold(rs.getInt("quantity_sold"));
                    item.setPriceAtSale(rs.getDouble("price_at_sale"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sale items: " + e.getMessage());
        }

        return items;
    }
}
