package DAO;

import DAO.Impl.ProductDAOImpl;
import Exceptions.ProductNotFoundException;
import Models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestProductDAO {
	ProductDAO productDAO;

	@BeforeEach
	void setup() {
		productDAO = new ProductDAOImpl();
	}

	@Test
	void testAddProduct() {
		Product product = new Product(101, "abc", "def", 123, 1234.0);
		productDAO.AddProduct(product);
		Product gotProduct = productDAO.getProductById(101, false);
		assertEquals(101, gotProduct.getProductId());
		assertEquals("abc", gotProduct.getProductName());
		assertEquals("def", gotProduct.getProductType());
		assertEquals(123, gotProduct.getAvailableQty());
		assertEquals(1234.0, gotProduct.getPrice());

	}

	@Test
	void testGetProduct() {
		Product product = new Product(1003, "abc", "def", 123, 1234.0);
		productDAO.AddProduct(product);
		Product gotProduct = productDAO.getProductById(1003, false);
		assertEquals(1003, gotProduct.getProductId());
		assertEquals("abc", gotProduct.getProductName());
		assertEquals("def", gotProduct.getProductType());
		assertEquals(123, gotProduct.getAvailableQty());
		assertEquals(1234.0, gotProduct.getPrice());

	}

	@Test
	void testProductNotFound() {
		int testId=1990;
		ProductNotFoundException notFoundException = assertThrows(
				ProductNotFoundException.class,
				() -> productDAO.getProductById(testId, false)
		);
		assertEquals(
				"Product with this id:-" + testId + " is not available in the list",
				notFoundException.getMessage()
		);

	}

	@Test
	void testDeleteProduct() {
		int testId = 1003;

		productDAO.deleteProductById(testId);

		ProductNotFoundException notFoundException = assertThrows(
				ProductNotFoundException.class,
				() -> productDAO.getProductById(testId, false)
		);

		assertEquals(
				"Product with this id:-" + testId + " is not available in the list",
				notFoundException.getMessage()
		);
	}
	@Test
	void testUpdateProduct() {
		Product product = new Product(1009, "abc", "def", 123, 1234.0);
		productDAO.AddProduct(product);
		Product forUpdate = new Product(1009, "iPhone", null, 100, 700000.0);
		Product gotProduct = productDAO.updateProduct(forUpdate);
		assertEquals(1009, gotProduct.getProductId());
		assertEquals("iPhone", gotProduct.getProductName());
		assertEquals("def", gotProduct.getProductType());
		assertEquals(100, gotProduct.getAvailableQty());
		assertEquals(700000.0, gotProduct.getPrice());

	}

	@Test
	void testGetAllProduct() {
		List<Product> products = productDAO.getAllProducts();

		assertNotNull(products, "Product list is not empty");
		assertEquals(11, products.size(), "There should be 11 products in the list");

		Product p1 = products.get(0);
		assertEquals(11, p1.getProductId());
		assertEquals("Mango", p1.getProductName());
		assertEquals("Fruits", p1.getProductType());
		assertEquals(100, p1.getAvailableQty());
		assertEquals(100.0, p1.getPrice());

	}
	@Test
	void testGetProductByCategory() {
		List<Product> products = productDAO.getProductByCategory("Electronics");

		assertNotNull(products, "Product list is not empty");
		assertEquals(4, products.size(), "There should be 4 products in the list");

		Product p1 = products.get(0);
		assertEquals(20, p1.getProductId());
		assertEquals("Laptop", p1.getProductName());
		assertEquals("Electronics", p1.getProductType());
		assertEquals(5, p1.getAvailableQty());
		assertEquals(55000.0, p1.getPrice());

	}

	@Test
	void testCategoryNotFound(){

		ProductNotFoundException notFoundException=assertThrows(ProductNotFoundException.class,
				()-> productDAO.getProductByCategory("random"));
		assertEquals(notFoundException.getMessage(),"There are no Products available in this category");
	}




}
