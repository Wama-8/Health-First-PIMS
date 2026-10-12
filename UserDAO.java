package Dao;

import database.DBConnection;
import Models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    //Checks a username/password combination against the database.
    public User login(String username, String password) {
        // Using a PreparedStatement instead of building the SQL string by hand 
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Found a matching row and build a User object from it
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        }

        // No match founde(Login failes)
        return null;
    }

    // Returns a list of every Cashier account in the system
    public List<User> getAllCashiers() {
        List<User> cashiers = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'Cashier' ORDER BY full_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cashiers.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching cashiers: " + e.getMessage());
        }

        return cashiers;
    }

    
    //Creates a new Cashier account.
    public boolean createCashier(User user) {
        String sql = "INSERT INTO users (username, password, role, full_name) VALUES (?, ?, 'Cashier', ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.err.println("Error creating cashier: " + e.getMessage());
            return false;
        }
    }

    // Deletes a Cashier account by its user_id. 
    public boolean deleteCashier(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ? AND role = 'Cashier'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting cashier: " + e.getMessage());
            return false;
        }
    }

   
     //Checks if a username is already taken 
    public boolean usernameExists(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true if at least one row was found
            }
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            return false;
        }
    }

    /**
     * Small private helper that converts one row of a ResultSet into
     * a User object. Pulling this out avoids repeating the same
     * five lines in every method above.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setFullName(rs.getString("full_name"));
        return user;
    }
}
