package Dao;

import database.DBConnection;
import Models.Medicine;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// This class manages the dabase access to the medicine table in the sql script
public class MedicineDAO {

    // returns all medicine together with their supplier name instead of a raw ID
    public List<Medicine> getAllMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT m.*, s.name AS supplier_name " +
                     "FROM medicines m LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id " +
                     "ORDER BY m.name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                medicines.add(mapRowToMedicine(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching medicines: " + e.getMessage());
        }

        return medicines;
    }

    // searches medicine by the name of the medicine
    public List<Medicine> searchMedicinesByName(String searchTerm) {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT m.*, s.name AS supplier_name " +
                     "FROM medicines m LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id " +
                     "WHERE m.name LIKE ? ORDER BY m.name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + searchTerm + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    medicines.add(mapRowToMedicine(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching medicines: " + e.getMessage());
        }

        return medicines;
    }

    // Adds a new medicine and returns true if it was saved successfully. 
    public boolean addMedicine(Medicine medicine) {
        String sql = "INSERT INTO medicines (name, company, medicine_type, price, "
                   + "quantity_in_stock, reorder_level, expiry_date, supplier_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medicine.getName());
            stmt.setString(2, medicine.getCompany());
            stmt.setString(3, medicine.getMedicineType());
            stmt.setDouble(4, medicine.getPrice());
            stmt.setInt(5, medicine.getQuantityInStock());
            stmt.setInt(6, medicine.getReorderLevel());
            stmt.setDate(7, Date.valueOf(medicine.getExpiryDate()));
            stmt.setInt(8, medicine.getSupplierId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding medicine: " + e.getMessage());
            return false;
        }
    }

    //Updates an existing medicine details
    public boolean updateMedicine(Medicine medicine) {
        String sql = "UPDATE medicines SET name = ?, company = ?, medicine_type = ?, "
                   + "price = ?, quantity_in_stock = ?, reorder_level = ?, "
                   + "expiry_date = ?, supplier_id = ? WHERE medicine_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medicine.getName());
            stmt.setString(2, medicine.getCompany());
            stmt.setString(3, medicine.getMedicineType());
            stmt.setDouble(4, medicine.getPrice());
            stmt.setInt(5, medicine.getQuantityInStock());
            stmt.setInt(6, medicine.getReorderLevel());
            stmt.setDate(7, Date.valueOf(medicine.getExpiryDate()));
            stmt.setInt(8, medicine.getSupplierId());
            stmt.setInt(9, medicine.getMedicineId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating medicine: " + e.getMessage());
            return false;
        }
    }

    // Deletes a medicine by its ID
    public boolean deleteMedicine(int medicineId) {
        String sql = "DELETE FROM medicines WHERE medicine_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, medicineId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting medicine: " + e.getMessage());
            return false;
        }
    }

    // Decreases and reduces a medicine stock after a sale
    public boolean reduceStock(Connection conn, int medicineId, int quantitySold) throws SQLException {
        // The "AND quantity_in_stock >= ?" guard stops stock from ever
        // going negative, even if two cashiers somehow sell the last
        // few units at the exact same time.
        String sql = "UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? "
                   + "WHERE medicine_id = ? AND quantity_in_stock >= ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantitySold);
            stmt.setInt(2, medicineId);
            stmt.setInt(3, quantitySold);
            return stmt.executeUpdate() > 0;
        }
    }

    // Returns the current stock quantity in stock for a medicine.
    // used ath the POS to validate requested quantity
    public int getCurrentStock(int medicineId) {
        String sql = "SELECT quantity_in_stock FROM medicines WHERE medicine_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, medicineId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity_in_stock");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking stock: " + e.getMessage());
        }

        return 0; // if anything goes wrong, safest to report zero stock available
    }

    // Returns all medicine where the quantity is below the reorder level
    public List<Medicine> getLowStockMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT m.*, s.name AS supplier_name " +
                     "FROM medicines m LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id " +
                     "WHERE m.quantity_in_stock <= m.reorder_level " +
                     "ORDER BY m.quantity_in_stock ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                medicines.add(mapRowToMedicine(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching low stock medicines: " + e.getMessage());
        }

        return medicines;
    }

    // returns all medicines that are about to expire soon
    public List<Medicine> getExpiringMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        // DATE_ADD(CURDATE(), INTERVAL 1 MONTH) calculates "one month from today"
        // directly in MySQL, so the comparison always uses the current date.
        String sql = "SELECT m.*, s.name AS supplier_name " +
                     "FROM medicines m LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id " +
                     "WHERE m.expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 1 MONTH) " +
                     "ORDER BY m.expiry_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                medicines.add(mapRowToMedicine(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching expiring medicines: " + e.getMessage());
        }

        return medicines;
    }

    private Medicine mapRowToMedicine(ResultSet rs) throws SQLException {
        Medicine medicine = new Medicine();
        medicine.setMedicineId(rs.getInt("medicine_id"));
        medicine.setName(rs.getString("name"));
        medicine.setCompany(rs.getString("company"));
        medicine.setMedicineType(rs.getString("medicine_type"));
        medicine.setPrice(rs.getDouble("price"));
        medicine.setQuantityInStock(rs.getInt("quantity_in_stock"));
        medicine.setReorderLevel(rs.getInt("reorder_level"));

        Date expiry = rs.getDate("expiry_date");
        if (expiry != null) {
            medicine.setExpiryDate(expiry.toLocalDate());
        }

        medicine.setSupplierId(rs.getInt("supplier_id"));
        medicine.setSupplierName(rs.getString("supplier_name"));
        return medicine;
    }
}
