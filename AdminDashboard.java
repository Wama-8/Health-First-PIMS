package Gui;

import Models.User;

import javax.swing.*;
import java.awt.*;

// Admin Dashboard
public class AdminDashboard extends JFrame {

    private final User loggedInAdmin;

    // Kept as a field (instead of a local variable) so the colorful nav
    // tiles on the Home tab can switch tabs for the user.
    private JTabbedPane tabbedPane;

    public AdminDashboard(User loggedInAdmin) {
        this.loggedInAdmin = loggedInAdmin;

        setTitle("HealthFirst Pharmacy, Welcome to Admin Dashboard");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buildInterface();
    }

    private void buildInterface() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        // Give the top bar a solid brand-color background, like a real app header
        topBar.setBackground(AppColors.PRIMARY);
        topBar.setOpaque(true);

        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInAdmin.getFullName() + " (Administrator)");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        welcomeLabel.setForeground(Color.WHITE); // white text so it's readable on the colored bar

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());
        logoutButton.setBackground(AppColors.DANGER);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setOpaque(true);

        topBar.add(welcomeLabel, BorderLayout.WEST);
        topBar.add(logoutButton, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Tabbed navigation for the four admin areas
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(AppColors.BACKGROUND);
        tabbedPane.setForeground(AppColors.TEXT_DARK);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.addTab("Dashboard / Home", buildHomeTab());
        tabbedPane.addTab("Manage Medicines", new MedicinePanel());
        tabbedPane.addTab("Manage Suppliers", new SupplierPanel());
        tabbedPane.addTab("Manage Users", new UserPanel());
        tabbedPane.addTab("Reports", new ReportsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel buildHomeTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(AppColors.BACKGROUND);

        JLabel heading = new JLabel("HealthFirst Pharmacy Inventory Management System");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(AppColors.PRIMARY_DARK);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel info = new JLabel("<html>Use the tabs above, or the quick-access cards below, to manage "
                + "medicines, suppliers, and cashier accounts, or to view business reports.</html>");
        info.setFont(new Font("SansSerif", Font.PLAIN, 13));
        info.setForeground(AppColors.TEXT_MUTED);
        info.setBorder(BorderFactory.createEmptyBorder(10, 0, 25, 0));
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(heading);
        panel.add(info);

        // Quick-access nav tiles: colorful "tab-like" cards inside the body
        // itself, so the admin can jump to a section without scrolling back
        // up to the tab strip. Clicking a tile just switches tabbedPane to
        // that tab - no new functionality, just a second, friendlier way in.
        panel.add(buildNavTileGrid());

        return panel;
    }

    // Builds a 2x2 grid of colored, clickable cards, one per admin section
    private JPanel buildNavTileGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setBackground(AppColors.BACKGROUND);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(640, 220));

        grid.add(buildNavTile("Manage Medicines", "Add, update, and track stock levels", AppColors.SUCCESS, 1));
        grid.add(buildNavTile("Manage Suppliers", "Keep supplier contact details up to date", AppColors.TEAL_DARK, 2));
        grid.add(buildNavTile("Manage Users", "Create and remove cashier accounts", AppColors.PRIMARY, 3));
        grid.add(buildNavTile("Reports", "View sales, stock, and expiry reports", AppColors.WARNING, 4));

        return grid;
    }

    // Builds a single colored, clickable "tab tile" that jumps to the given tab index
    private JPanel buildNavTile(String title, String subtitle, Color color, int tabIndex) {
        JPanel tile = new JPanel();
        tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
        tile.setBackground(color);
        tile.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        tile.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("<html>" + subtitle + "</html>");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(255, 255, 255, 220));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        tile.add(titleLabel);
        tile.add(subtitleLabel);

        // Clicking anywhere on the tile switches to that tab, same as
        // clicking the tab strip at the top - just a second entry point.
        tile.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                tabbedPane.setSelectedIndex(tabIndex);
            }
        });

        return tile;
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}