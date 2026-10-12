package Gui;

import Dao.SaleDAO;
import Models.SaleItem;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

// BILL Frame
public class BillFrame extends JFrame {

    private final int saleId;
    private final String cashierName;
    private final double totalAmount;
    private JTextArea billTextArea;

    public BillFrame(int saleId, String cashierName, double totalAmount) {
        this.saleId = saleId;
        this.cashierName = cashierName;
        this.totalAmount = totalAmount;

        setTitle("HealthFirst Pharmacy - Bill / Receipt");
        setSize(420, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildInterface();
    }

    private void buildInterface() {
        setLayout(new BorderLayout(10, 10));

        billTextArea = new JTextArea();
        billTextArea.setEditable(false);
        billTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        billTextArea.setText(buildBillText());
        billTextArea.setBackground(Color.WHITE);
        billTextArea.setForeground(AppColors.TEXT_DARK);

        add(new JScrollPane(billTextArea), BorderLayout.CENTER);

        JButton printButton = new JButton("Print Bill");
        JButton saveButton = new JButton("Save Bill");
        JButton closeButton = new JButton("Close");

        printButton.addActionListener(e -> handlePrint());
        saveButton.addActionListener(e -> handleSave());
        closeButton.addActionListener(e -> dispose());

        // Color each button by what it does, so the action is obvious at a glance
        printButton.setBackground(AppColors.PRIMARY);
        printButton.setForeground(Color.WHITE);
        printButton.setFocusPainted(false);
        printButton.setOpaque(true);

        saveButton.setBackground(AppColors.TEAL_DARK);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setOpaque(true);

        closeButton.setBackground(AppColors.TEXT_MUTED);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(true);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(AppColors.BACKGROUND);
        buttonPanel.add(printButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Builds the plain text receipt content
    private String buildBillText() {
        SaleDAO saleDAO = new SaleDAO();
        List<SaleItem> items = saleDAO.getItemsForSale(saleId);

        StringBuilder sb = new StringBuilder();
        sb.append("        HEALTHFIRST PHARMACY\n");
        sb.append("========================================\n");
        sb.append("Sale ID:  ").append(saleId).append("\n");
        sb.append("Date:     ").append(java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        sb.append("Cashier:  ").append(cashierName).append("\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-18s %4s %8s %8s%n", "Medicine", "Qty", "Price", "Total"));
        sb.append("----------------------------------------\n");

        for (SaleItem item : items) {
            sb.append(String.format("%-18s %4d %8.2f %8.2f%n",
                    truncate(item.getMedicineName(), 18),
                    item.getQuantitySold(),
                    item.getPriceAtSale(),
                    item.getLineTotal()));
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("%30s R%8.2f%n", "TOTAL:", totalAmount));
        sb.append("========================================\n");
        sb.append("     Thank you for your purchase!\n");

        return sb.toString();
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) : text;
    }

    private void handlePrint() {
        try {
            billTextArea.print();
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Unable to print bill: " + e.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSave() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("bill_" + saleId + ".txt"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(billTextArea.getText());
                JOptionPane.showMessageDialog(this, "Bill saved to " + file.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Unable to save bill: " + e.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}