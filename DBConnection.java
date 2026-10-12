package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Gives the application connection to the MySQL database
public class DBConnection {

    // JDBC URL format: jdbc:mysql://<host>:<port>/<database_name>
    private static final String URL = "jdbc:mysql://localhost:3306/pharmacy_inventory_db";;
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Dokotela2005@!"; // change to your MySQL password

    // Creates Connection to the database
    public static Connection getConnection() throws SQLException {
        try {
            // Makes sure the MySQL JDBC driver class is loaded.
        Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. "
                    + "Make sure mysql-connector-j (.jar) is added to your project's classpath.", e);
        }

        // DriverManager does the work of opening the connection.
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    // Return true if a connection could be opened, false otherwise
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
