package Models;

import java.time.LocalDate;

// Returns medicine 
public class Medicine {

    private int medicineId;
    private String name;
    private String company;
    private String medicineType;
    private double price;
    private int quantityInStock;
    private int reorderLevel;
    private LocalDate expiryDate;
    private int supplierId;
    private String supplierName; // convenience field, not stored directly on this table

    public Medicine() {
    }

    // Constructor
    public Medicine(int medicineId, String name, String company, String medicineType,
                     double price, int quantityInStock, int reorderLevel,
                     LocalDate expiryDate, int supplierId) {
        this.medicineId = medicineId;
        this.name = name;
        this.company = company;
        this.medicineType = medicineType;
        this.price = price;
        this.quantityInStock = quantityInStock;
        this.reorderLevel = reorderLevel;
        this.expiryDate = expiryDate;
        this.supplierId = supplierId;
    }

    // Constructor without ID that will be used when creating a NEW medicine
    public Medicine(String name, String company, String medicineType,
                     double price, int quantityInStock, int reorderLevel,
                     LocalDate expiryDate, int supplierId) {
        this.name = name;
        this.company = company;
        this.medicineType = medicineType;
        this.price = price;
        this.quantityInStock = quantityInStock;
        this.reorderLevel = reorderLevel;
        this.expiryDate = expiryDate;
        this.supplierId = supplierId;
    }

    // Getters and Setters

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    // Returns true if there is low stock
    public boolean isLowStock() {
        return quantityInStock <= reorderLevel;
    }

    // Returns true if medicine is about to expire
    public boolean isExpiringSoon() {
        if (expiryDate == null) {
            return false;
        }
        LocalDate oneMonthFromNow = LocalDate.now().plusMonths(1);
        return !expiryDate.isAfter(oneMonthFromNow) && !expiryDate.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return name + " (Stock: " + quantityInStock + ")";
    }
}
