package Models;

public class Product {
    private int productId;
    private String productName;
    private String productType;
    private int availableQty ;


    public Product(int productId, String productName, String productType, int availableQty) {
        this.productId=productId;
        this.productName = productName;
        this.productType = productType;
        this.availableQty = availableQty;
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

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productType='" + productType + '\'' +
                ", availableQty=" + availableQty +
                '}';
    }
}
