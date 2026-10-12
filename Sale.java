package Models;

import java.time.LocalDateTime;

// Sales
public class Sale {

    private int saleId;
    private LocalDateTime saleDate;
    private double totalAmount;
    private int userId;
    private String cashierName; // convenience field: who processed the sale

    public Sale() {
    }

    public Sale(int saleId, LocalDateTime saleDate, double totalAmount, int userId) {
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.totalAmount = totalAmount;
        this.userId = userId;
    }

    // Constructor without ID/date, used when creating a NEW sale
    public Sale(double totalAmount, int userId) {
        this.totalAmount = totalAmount;
        this.userId = userId;
    }

    // Getters and Setters
    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    @Override
    public String toString() {
        return "Sale #" + saleId + " - R" + totalAmount;
    }
}
