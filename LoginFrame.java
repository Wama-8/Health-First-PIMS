package Gui;

import Dao.UserDAO;
import Models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * LoginFrame
 * -----------
 * The very first screen the user sees. It collects a username and
 * password, checks them against the database via UserDAO, and then
 * opens either the AdminDashboard or the CashierDashboard depending
 * on the logged-in user's role.
 *
 * VISUAL DESIGN
 * -------------
 * The window is split into two halves:
 *   - LEFT  : a teal/green gradient "branding" panel with a pharmacy
 *             cross icon, the app name and a short tagline.
 *   - RIGHT : a plain white "card" panel with the actual login form
 *             (modern underline-style text fields, a colored button).
 * This two-panel look is a common, clean pattern for login screens
 * and reads as more "designed" than a single stack of gray fields.
 */
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    private final UserDAO userDAO = new UserDAO();

    // Colors used just for the underline-style text field borders.
    // (Kept here rather than AppColors since they're specific to this one effect.)
    private static final Color FIELD_LINE_NORMAL = new Color(210, 214, 220);
    private static final Color FIELD_LINE_FOCUS  = AppColors.TEAL_DARK;

    public LoginFrame() {
        setTitle("HealthFirst Pharmacy - Login");
        setSize(820, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        buildInterface();
    }

    // Builds all the visual components of the login screen
    private void buildInterface() {
        setLayout(new BorderLayout());

        add(buildBrandingPanel(), BorderLayout.WEST);
        add(buildFormPanel(), BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------
    // LEFT SIDE: the colored branding panel with icon, name and tagline
    // ------------------------------------------------------------------
    private JPanel buildBrandingPanel() {
        // GradientPanel is a small inner class (defined at the bottom of
        // this file) that paints a smooth teal gradient background instead
        // of a flat color, which is what gives this panel its "designed" look.
        GradientPanel brandingPanel = new GradientPanel(AppColors.TEAL_DARK, AppColors.TEAL_LIGHT);
        brandingPanel.setPreferredSize(new Dimension(320, 0));
        brandingPanel.setLayout(new BoxLayout(brandingPanel, BoxLayout.Y_AXIS));
        brandingPanel.setBorder(new EmptyBorder(0, 40, 0, 40));

        brandingPanel.add(Box.createVerticalGlue());

        // Pharmacy cross icon, drawn with plain shapes (no image file needed)
        PharmacyCrossIcon crossIcon = new PharmacyCrossIcon();
        crossIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        crossIcon.setMaximumSize(new Dimension(90, 90));
        crossIcon.setPreferredSize(new Dimension(90, 90));

        JLabel appName = new JLabel("HealthFirst");
        appName.setFont(new Font("SansSerif", Font.BOLD, 26));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appName2 = new JLabel("Pharmacy");
        appName2.setFont(new Font("SansSerif", Font.BOLD, 26));
        appName2.setForeground(Color.WHITE);
        appName2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Smart Inventory. Healthier Business.");
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tagline.setForeground(new Color(255, 255, 255, 210)); // slightly transparent white
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandingPanel.add(crossIcon);
        brandingPanel.add(Box.createRigidArea(new Dimension(0, 18)));
        brandingPanel.add(appName);
        brandingPanel.add(appName2);
        brandingPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        brandingPanel.add(tagline);

        brandingPanel.add(Box.createVerticalGlue());

        return brandingPanel;
    }

    // ------------------------------------------------------------------
    // RIGHT SIDE: the white card with the actual username/password form
    // ------------------------------------------------------------------
    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(AppColors.CARD_BACKGROUND);
        formPanel.setBorder(new EmptyBorder(60, 70, 60, 70));

        JLabel heading = new JLabel("Welcome Back");
        heading.setFont(new Font("SansSerif", Font.BOLD, 24));
        heading.setForeground(AppColors.TEXT_DARK);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subheading = new JLabel("Sign in to manage your pharmacy");
        subheading.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subheading.setForeground(AppColors.TEXT_MUTED);
        subheading.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username field
        JLabel usernameLabel = buildFieldLabel("USERNAME");
        usernameField = new JTextField();
        styleUnderlineField(usernameField);

        // Password field
        JLabel passwordLabel = buildFieldLabel("PASSWORD");
        passwordField = new JPasswordField();
        styleUnderlineField(passwordField);

        // Error message (hidden until something goes wrong)
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(AppColors.DANGER);
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login button - solid teal, full width, rounded via custom painting
        RoundedButton loginButton = new RoundedButton("Login", AppColors.TEAL_DARK, Color.WHITE);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginButton.addActionListener(this::handleLogin);

        // Clear button - plain text-style "link" instead of a second heavy button
        JButton clearButton = new JButton("Clear fields");
        clearButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        clearButton.setBorderPainted(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setForeground(AppColors.TEXT_MUTED);
        clearButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.setHorizontalAlignment(SwingConstants.LEFT);
        clearButton.setMargin(new Insets(0, 0, 0, 0));
        clearButton.addActionListener(e -> clearFields());

        passwordField.addActionListener(this::handleLogin);

        formPanel.add(heading);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        formPanel.add(subheading);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(errorLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(loginButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(clearButton);

        return formPanel;
    }

    // Small helper: builds the little gray "USERNAME" / "PASSWORD" style labels
    private JLabel buildFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(AppColors.TEXT_MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // Small helper: gives a text field the modern "underline only" look,
    // and makes the underline turn teal when the field is focused.
    private void styleUnderlineField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        field.setBorder(new MatteBorder(0, 0, 2, 0, FIELD_LINE_NORMAL));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Change the underline color on focus so the field feels "alive"
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new MatteBorder(0, 0, 2, 0, FIELD_LINE_FOCUS));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new MatteBorder(0, 0, 2, 0, FIELD_LINE_NORMAL));
            }
        });
    }

    // Called when the user clicks Login or presses Enter
    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // input validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        // Check credentials against the database
        User loggedInUser = userDAO.login(username, password);

        if (loggedInUser == null) {
            showError("Invalid username or password.");
            passwordField.setText(""); // clear password for security
            return;
        }

        // Success: open the correct dashboard based on role
        if (loggedInUser.isAdmin()) {
            new AdminDashboard(loggedInUser).setVisible(true);
        } else {
            new CashierDashboard(loggedInUser).setVisible(true);
        }

        // close the login window
        dispose();
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
        usernameField.requestFocus();
    }

    // ==================================================================
    // Small reusable inner classes used only by this screen's design.
    // Keeping them here (instead of separate files) is fine since they're
    // tiny and only ever used by LoginFrame.
    // ==================================================================

    /**
     * GradientPanel - a JPanel that paints a smooth diagonal gradient
     * between two colors instead of a flat background color.
     */
    private static class GradientPanel extends JPanel {
        private final Color topColor;
        private final Color bottomColor;

        GradientPanel(Color topColor, Color bottomColor) {
            this.topColor = topColor;
            this.bottomColor = bottomColor;
            setOpaque(false); // we paint our own background in paintComponent
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gradient = new GradientPaint(
                    0, 0, topColor,
                    getWidth(), getHeight(), bottomColor
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    /**
     * PharmacyCrossIcon - draws a simple white circle with a green plus
     * sign inside it, standing in for a pharmacy/medical cross logo
     * without needing an external image file.
     */
    private static class PharmacyCrossIcon extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            // White circular badge
            g2.setColor(Color.WHITE);
            g2.fillOval(x, y, size, size);

            // Teal plus sign in the middle of the badge
            g2.setColor(AppColors.TEAL_DARK);
            int armThickness = size / 4;
            int armLength = (int) (size * 0.6);
            int cx = x + size / 2;
            int cy = y + size / 2;

            // Vertical bar of the plus
            g2.fillRoundRect(cx - armThickness / 2, cy - armLength / 2, armThickness, armLength, 6, 6);
            // Horizontal bar of the plus
            g2.fillRoundRect(cx - armLength / 2, cy - armThickness / 2, armLength, armThickness, 6, 6);

            g2.dispose();
        }
    }

    /**
     * RoundedButton - a JButton with rounded corners and a solid fill
     * color, since plain Swing buttons are square and gray by default.
     */
    private static class RoundedButton extends JButton {
        private final Color fillColor;

        RoundedButton(String text, Color fillColor, Color textColor) {
            super(text);
            this.fillColor = fillColor;
            setForeground(textColor);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setFocusPainted(false);
            setContentAreaFilled(false); // we paint the background ourselves
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}