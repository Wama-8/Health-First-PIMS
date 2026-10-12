package Gui;

import Models.User;

import javax.swing.*;
import java.awt.*;

// Cashier Dashboard
public class CashierDashboard extends JFrame {

    private final User loggedInCashier;

    public CashierDashboard(User loggedInCashier) {
        this.loggedInCashier = loggedInCashier;

        setTitle("HealthFirst Pharmacy - Cashier Dashboard");
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
        topBar.setBackground(AppColors.TEAL_DARK);
        topBar.setOpaque(true);

        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInCashier.getFullName() + " (Cashier)");
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

        // Tabbed navigation 
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(AppColors.BACKGROUND);
        tabbedPane.setForeground(AppColors.TEXT_DARK);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.addTab("Point of Sale", new POSPanel(loggedInCashier));
        tabbedPane.addTab("Stock Check", new StockCheckPanel());

        add(tabbedPane, BorderLayout.CENTER);
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