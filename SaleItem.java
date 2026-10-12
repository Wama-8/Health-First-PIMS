package Models;

// Sale Item
public class SaleItem {

    private int saleItemId;
    private int saleId;
    private int medicineId;
    private String medicineName; // convenience field for display
    private int quantitySold;
    private double priceAtSale;

    public SaleItem() {
    }

    public SaleItem(int saleItemId, int saleId, int medicineId,
                     int quantitySold, double priceAtSale) {
        this.saleItemId = saleItemId;
        this.saleId = saleId;
        this.medicineId = medicineId;
        this.quantitySold = quantitySold;
        this.priceAtSale = priceAtSale;
    }

    // Constructor
    public SaleItem(int medicineId, int quantitySold, double priceAtSale) {
        this.medicineId = medicineId;
        this.quantitySold = quantitySold;
        this.priceAtSale = priceAtSale;
    }

    //Getters and Setters 

    public int getSaleItemId() {
        return saleItemId;
    }

    public void setSaleItemId(int saleItemId) {
        this.saleItemId = saleItemId;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public double getPriceAtSale() {
        return priceAtSale;
    }

    public void setPriceAtSale(double priceAtSale) {
        this.priceAtSale = priceAtSale;
    }

    //Returns quantity x price
    public double getLineTotal() {
        return quantitySold * priceAtSale;
    }

    @Override
    public String toString() {
        return medicineName + " x" + quantitySold + " = R" + getLineTotal();
    }
}
