package Dao;

import database.DBConnection;
import Models.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


 //Handles all database access for the suppliers table
public class SupplierDAO {

    // Rreturn every supplier in the database, ordered by name
    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                suppliers.add(mapRowToSupplier(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching suppliers: " + e.getMessage());
        }

        return suppliers;
    }

    // Adds a new supplier. Returns true if it was saved successfully.
    public boolean addSupplier(Supplier supplier) {
        String sql = "INSERT INTO suppliers (name, contact_person, phone, email, address) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getPhone());
            stmt.setString(4, supplier.getEmail());
            stmt.setString(5, supplier.getAddress());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding supplier: " + e.getMessage());
            return false;
        }
    }

    // Updates an existing supplier's details 
    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE suppliers SET name = ?, contact_person = ?, phone = ?, "
                   + "email = ?, address = ? WHERE supplier_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getPhone());
            stmt.setString(4, supplier.getEmail());
            stmt.setString(5, supplier.getAddress());
            stmt.setInt(6, supplier.getSupplierId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating supplier: " + e.getMessage());
            return false;
        }
    }

    
    //Deletes a supplier by ID.
         public boolean deleteSupplier(int supplierId) {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplierId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting supplier: " + e.getMessage());
            return false;
        }
    }

    private Supplier mapRowToSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(rs.getInt("supplier_id"));
        supplier.setName(rs.getString("name"));
        supplier.setContactPerson(rs.getString("contact_person"));
        supplier.setPhone(rs.getString("phone"));
        supplier.setEmail(rs.getString("email"));
        supplier.setAddress(rs.getString("address"));
        return supplier;
    }
}
