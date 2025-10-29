package Models;

public class Product {
	private Integer productId;
	private String productName;
	private String productType;
	private Integer availableQty;
	private Double price;
    private Integer thresholdLimit;
	public Product() {
	}

	public Product(Integer productId, String productName, String productType, Integer availableQty, Double price) {
		this.productId = productId;
		this.productName = productName;
		this.productType = productType;
		this.availableQty = availableQty;
		this.price = price;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
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

	public Integer getAvailableQty() {
		return availableQty;
	}

	public void setAvailableQty(Integer availableQty) {
		this.availableQty = availableQty;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
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

    public Integer getThresholdLimit() {
        return thresholdLimit;
    }

    public void setThresholdLimit(Integer thresholdLimit) {
        this.thresholdLimit = thresholdLimit;
    }
}
