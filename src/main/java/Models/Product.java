package Models;

public class Product {
    private int productId;
    private String productName;
    private String productType;
    private int availableQty ;
    private double price;


    public Product(int productId, String productName, String productType, int availableQty, double price) {
        this.productId=productId;
        this.productName = productName;
        this.productType = productType;
        this.availableQty = availableQty;
        this.price=price;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public int getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(int availableQty) {
        this.availableQty = availableQty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productType='" + productType + '\'' +
                ", availableQty=" + availableQty +
                ", price=" + price +
                '}';
    }
}
