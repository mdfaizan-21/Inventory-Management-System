package DAO;

import DAO.Impl.ProductDAOImpl;
import Models.Product;
import Exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import util.DBconnection;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestMockProduct {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private ProductDAOImpl productDAO;

    @BeforeEach
    void setup() throws Exception {
        MockedStatic<DBconnection> dbMock = mockStatic(DBconnection.class);
        dbMock.when(DBconnection::getConnection).thenReturn(mockConnection);
    }

    @Test
    void testAddProduct() throws Exception {
        Product product = new Product(101, "abc", "def", 123, 1234.0);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        productDAO.AddProduct(product);

        verify(mockPreparedStatement, times(1)).setInt(1, 101);
        verify(mockPreparedStatement, times(1)).setString(2, "abc");
        verify(mockPreparedStatement, times(1)).setString(3, "def");
        verify(mockPreparedStatement, times(1)).setInt(4, 123);
        verify(mockPreparedStatement, times(1)).setDouble(5, 1234.0);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetProductById() throws Exception {

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("productId")).thenReturn(1003);
        when(mockResultSet.getString("productName")).thenReturn("abc");
        when(mockResultSet.getString("productType")).thenReturn("def");
        when(mockResultSet.getInt("availableQty")).thenReturn(123);
        when(mockResultSet.getDouble("price")).thenReturn(1234.0);


        Product gotProduct = productDAO.getProductById(1003, false);

        assertEquals(1003, gotProduct.getProductId());
        assertEquals("abc", gotProduct.getProductName());
        assertEquals("def", gotProduct.getProductType());
        assertEquals(123, gotProduct.getAvailableQty());
        assertEquals(1234.0, gotProduct.getPrice());
    }

    @Test
    void testProductNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        int testId = 1990;
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productDAO.getProductById(testId, false)
        );

        assertEquals("Product with this id:-" + testId + " is not available in the list", exception.getMessage());
    }

    @Test
    void testGetAllProducts() throws Exception {
        when(mockConnection.createStatement()).thenReturn(mock(Statement.class));
        Statement mockStatement = mock(Statement.class);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("productId")).thenReturn(11, 12);
        when(mockResultSet.getString("productName")).thenReturn("Mango", "Apple");
        when(mockResultSet.getString("productType")).thenReturn("Fruits", "Fruits");
        when(mockResultSet.getInt("availableQty")).thenReturn(100, 200);
        when(mockResultSet.getDouble("price")).thenReturn(100.0, 200.0);

        List<Product> products = productDAO.getAllProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("Mango", products.get(0).getProductName());
    }

    @Test
    void testDeleteProductById() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        productDAO.deleteProductById(1009);

        verify(mockPreparedStatement, times(1)).setInt(1, 1009);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
}
