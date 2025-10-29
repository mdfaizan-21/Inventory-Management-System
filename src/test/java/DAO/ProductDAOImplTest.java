package DAO;

import DAO.Impl.ProductDAOImpl;
import Exceptions.ProductNotFoundException;
import Models.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import util.DBconnection;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductDAOImplTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private ProductDAOImpl productDAO;

    private MockedStatic<DBconnection> dbConnectionMock;

    @BeforeEach
    void setUp() throws SQLException {
        dbConnectionMock = mockStatic(DBconnection.class);
        dbConnectionMock.when(DBconnection::getConnection).thenReturn(mockConnection);
    }

    @AfterEach
    void tearDown() {
        if (dbConnectionMock != null) {
            dbConnectionMock.close();
        }
    }

    // ==================== AddProduct Tests ====================

    @Test
    void testAddProduct_Success() throws SQLException {
        
        Product product = new Product(101, "Laptop", "Electronics", 50, 999.99);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        
        productDAO.AddProduct(product);

        
        verify(mockPreparedStatement).setInt(1, 101);
        verify(mockPreparedStatement).setString(2, "Laptop");
        verify(mockPreparedStatement).setString(3, "Electronics");
        verify(mockPreparedStatement).setInt(4, 50);
        verify(mockPreparedStatement).setDouble(5, 999.99);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testAddProduct_SQLException() throws SQLException {
        
        Product product = new Product(102, "Mouse", "Electronics", 100, 25.50);
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        
        assertDoesNotThrow(() -> productDAO.AddProduct(product));
    }

    @Test
    void testAddProduct_NullConnection() throws SQLException {
        
        Product product = new Product(103, "Keyboard", "Electronics", 75, 49.99);
        dbConnectionMock.when(DBconnection::getConnection).thenThrow(new SQLException("Connection failed"));

        
        assertDoesNotThrow(() -> productDAO.AddProduct(product));
    }

    // ==================== getAllProducts Tests ====================

    @Test
    void testGetAllProducts_Success() throws SQLException {
        
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.isBeforeFirst()).thenReturn(true);
        when(mockResultSet.next()).thenReturn(true, true, true, false);

        when(mockResultSet.getInt("ProductId")).thenReturn(1, 2, 3);
        when(mockResultSet.getString("ProductName")).thenReturn("Laptop", "Mouse", "Keyboard");
        when(mockResultSet.getString("ProductCategory")).thenReturn("Electronics", "Electronics", "Electronics");
        when(mockResultSet.getInt("AvailableQuantity")).thenReturn(10, 50, 30);
        when(mockResultSet.getDouble("Price")).thenReturn(999.99, 25.50, 49.99);
        when(mockResultSet.getInt("ThresholdLimit")).thenReturn(5, 10, 8);

        
        List<Product> products = productDAO.getAllProducts();

        
        assertNotNull(products);
        assertEquals(3, products.size());
        assertEquals("Laptop", products.get(0).getProductName());
        assertEquals("Mouse", products.get(1).getProductName());
        assertEquals("Keyboard", products.get(2).getProductName());
        assertEquals(999.99, products.get(0).getPrice());
        assertEquals(5, products.get(0).getThresholdLimit());
    }

    @Test
    void testGetAllProducts_EmptyList() throws SQLException {
        
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.isBeforeFirst()).thenReturn(false);

        
        List<Product> products = productDAO.getAllProducts();

        
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void testGetAllProducts_SQLException() throws SQLException {
        
        when(mockConnection.createStatement()).thenThrow(new SQLException("Query error"));

        
        List<Product> products = productDAO.getAllProducts();

        
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    // ==================== getProductById Tests ====================

    @Test
    void testGetProductById_Success() throws SQLException {
        
        int productId = 101;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getInt("ProductId")).thenReturn(101);
        when(mockResultSet.getString("ProductName")).thenReturn("Laptop");
        when(mockResultSet.getString("ProductCategory")).thenReturn("Electronics");
        when(mockResultSet.getInt("AvailableQuantity")).thenReturn(10);
        when(mockResultSet.getDouble("Price")).thenReturn(999.99);

        
        Product product = productDAO.getProductById(productId, false);

        
        assertNotNull(product);
        assertEquals(101, product.getProductId());
        assertEquals("Laptop", product.getProductName());
        assertEquals("Electronics", product.getProductType());
        assertEquals(10, product.getAvailableQty());
        assertEquals(999.99, product.getPrice());
        verify(mockPreparedStatement).setInt(1, productId);
    }

    @Test
    void testGetProductById_NotFound_FromInput() throws SQLException {
        
        int productId = 999;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        
        Product product = productDAO.getProductById(productId, true);

        
        assertNull(product);
    }

    @Test
    void testGetProductById_NotFound_NotFromInput() throws SQLException {
        
        int productId = 999;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        // product not found
        assertThrows(ProductNotFoundException.class, () -> {
            productDAO.getProductById(productId, false);
        });
    }

    @Test
    void testGetProductById_SQLException() throws SQLException {
        
        int productId = 101;
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Connection error"));

        
        Product product = productDAO.getProductById(productId, false);

        
        assertNull(product);
    }

    // ==================== deleteProductById Tests ====================

    @Test
    void testDeleteProductById_Success() throws SQLException {
        
        int productId = 101;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        
        productDAO.deleteProductById(productId);

        
        verify(mockPreparedStatement).setInt(1, productId);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testDeleteProductById_ProductNotExists() throws SQLException {
        
        int productId = 999;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        
        productDAO.deleteProductById(productId);

        
        verify(mockPreparedStatement).setInt(1, productId);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testDeleteProductById_SQLException() throws SQLException {
        
        int productId = 101;
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Delete failed"));

        
        assertDoesNotThrow(() -> productDAO.deleteProductById(productId));
    }

    // ==================== updateProduct Tests ====================

    @Test
    void testUpdateProduct_AllFields() throws SQLException {
        
        Product product = new Product(101, "Updated Laptop", "Electronics", 20, 1299.99);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Mock getProductById call
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("ProductId")).thenReturn(101);
        when(mockResultSet.getString("ProductName")).thenReturn("Updated Laptop");
        when(mockResultSet.getString("ProductCategory")).thenReturn("Electronics");
        when(mockResultSet.getInt("AvailableQuantity")).thenReturn(20);
        when(mockResultSet.getDouble("Price")).thenReturn(1299.99);

        
        Product updatedProduct = productDAO.updateProduct(product);

        
        assertNotNull(updatedProduct);
        assertEquals(101, updatedProduct.getProductId());
        assertEquals("Updated Laptop", updatedProduct.getProductName());
        verify(mockPreparedStatement, atLeastOnce()).executeUpdate();
    }

    @Test
    void testUpdateProduct_PartialFields() throws SQLException {
        Product product = new Product();
        product.setProductId(101);
        product.setProductName("New Name");
        product.setPrice(599.99);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Mock getProductById call
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("ProductId")).thenReturn(101);
        when(mockResultSet.getString("ProductName")).thenReturn("New Name");
        when(mockResultSet.getString("ProductCategory")).thenReturn("Electronics");
        when(mockResultSet.getInt("AvailableQuantity")).thenReturn(10);
        when(mockResultSet.getDouble("Price")).thenReturn(599.99);

        
        Product updatedProduct = productDAO.updateProduct(product);

        
        assertNotNull(updatedProduct);
        assertEquals("New Name", updatedProduct.getProductName());
        assertEquals(599.99, updatedProduct.getPrice());
    }

    @Test
    void testUpdateProduct_SQLException() throws SQLException {
        
        Product product = new Product(101, "Laptop", "Electronics", 10, 999.99);
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Update failed"));

        
        Product result = productDAO.updateProduct(product);

        
        assertNull(result);
    }

    @Test
    void testUpdateProduct_ProductNotFoundAfterUpdate() throws SQLException {
        
        Product product = new Product(101, "Laptop", "Electronics", 10, 999.99);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        
        Product result = productDAO.updateProduct(product);

        
        assertNull(result);
    }

    // ==================== getProductByCategory Tests ====================

    @Test
    void testGetProductByCategory_Success() throws SQLException {
        
        String category = "Electronics";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.isBeforeFirst()).thenReturn(true);
        when(mockResultSet.next()).thenReturn(true, true, false);

        when(mockResultSet.getInt("ProductId")).thenReturn(1, 2);
        when(mockResultSet.getString("ProductName")).thenReturn("Laptop", "Mouse");
        when(mockResultSet.getString("ProductCategory")).thenReturn("Electronics", "Electronics");
        when(mockResultSet.getInt("AvailableQuantity")).thenReturn(10, 50);
        when(mockResultSet.getDouble("Price")).thenReturn(999.99, 25.50);

        
        List<Product> products = productDAO.getProductByCategory(category);

        
        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("Laptop", products.get(0).getProductName());
        assertEquals("Mouse", products.get(1).getProductName());
        verify(mockPreparedStatement).setString(1, category);
    }

    @Test
    void testGetProductByCategory_NoProductsFound() throws SQLException {
        
        String category = "NonExistentCategory";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.isBeforeFirst()).thenReturn(false);

        
        List<Product> products = productDAO.getProductByCategory(category);

        
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void testGetProductByCategory_SQLException() throws SQLException {
        
        String category = "Electronics";
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Query failed"));

        
        List<Product> products = productDAO.getProductByCategory(category);

        
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    // ==================== searchByPriceRange Tests ====================

    @Test
    void testSearchByPriceRange_Success() throws SQLException {
        
        double minPrice = 100.0;
        double maxPrice = 1000.0;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.isBeforeFirst()).thenReturn(true);
        when(mockResultSet.next()).thenReturn(true, true, false);

        when(mockResultSet.getInt("ProductId")).thenReturn(1, 2);
        when(mockResultSet.getString("ProductName")).thenReturn("Tablet", "Phone");
        when(mockResultSet.getString("ProductCategory")).thenReturn("Electronics", "Electronics");
        when(mockResultSet.getInt("AvailableQuantity")).thenReturn(15, 25);
        when(mockResultSet.getDouble("Price")).thenReturn(299.99, 799.99);

        
        List<Product> products = productDAO.searchByPriceRange(minPrice, maxPrice);

        
        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("Tablet", products.get(0).getProductName());
        assertEquals("Phone", products.get(1).getProductName());
        verify(mockPreparedStatement).setDouble(1, minPrice);
        verify(mockPreparedStatement).setDouble(2, maxPrice);
    }

    @Test
    void testSearchByPriceRange_NoProductsFound() throws SQLException {
        
        double minPrice = 5000.0;
        double maxPrice = 10000.0;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.isBeforeFirst()).thenReturn(false);

        
        List<Product> products = productDAO.searchByPriceRange(minPrice, maxPrice);

        
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void testSearchByPriceRange_InvalidRange() throws SQLException {
        double minPrice = 1000.0;
        double maxPrice = 100.0;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.isBeforeFirst()).thenReturn(false);

        
        List<Product> products = productDAO.searchByPriceRange(minPrice, maxPrice);

        
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void testSearchByPriceRange_SQLException() throws SQLException {
        
        double minPrice = 100.0;
        double maxPrice = 1000.0;
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Search failed"));

        
        List<Product> products = productDAO.searchByPriceRange(minPrice, maxPrice);

        
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void testSearchByPriceRange_ZeroPrice() throws SQLException {
        
        double minPrice = 0.0;
        double maxPrice = 50.0;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.isBeforeFirst()).thenReturn(true);
        when(mockResultSet.next()).thenReturn(true, false);

        when(mockResultSet.getInt("ProductId")).thenReturn(1);
        when(mockResultSet.getString("ProductName")).thenReturn("Pen");
        when(mockResultSet.getString("ProductCategory")).thenReturn("Stationery");
        when(mockResultSet.getInt("AvailableQuantity")).thenReturn(100);
        when(mockResultSet.getDouble("Price")).thenReturn(5.99);

        
        List<Product> products = productDAO.searchByPriceRange(minPrice, maxPrice);

        
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Pen", products.get(0).getProductName());
    }
}
