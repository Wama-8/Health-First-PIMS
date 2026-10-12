package Models;

// Users
public class User {

    private int userId;
    private String username;
    private String password;
    private String role;      // Will always be "Admin" or "Cashier"
    private String fullName;

    // Constructors
    public User() {
    }

    // Full constructor 
    public User(int userId, String username, String password, String role, String fullName) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }

    // Constructor without userId, useful when creating a NEW user
    public User(String username, String password, String role, String fullName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Convenience methods used a lot in the GUI to decide what to show
    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(role);
    }

    public boolean isCashier() {
        return "Cashier".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        // Useful for debugging and for showing users in a JComboBox/JList
        return fullName + " (" + username + ") - " + role;
    }
}
