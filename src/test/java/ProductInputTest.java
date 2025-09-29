import Models.Product;
import Services.InventoryManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

    public class ProductInputTest {

        private InventoryManagement myInventoryManager;

        @BeforeEach
        void setUp() {
            myInventoryManager = new InventoryManagement();
        }

        @Test
        void testValidInput() {
            String input = "130 Phone Electronics 5 10000\n";
            Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

            myInventoryManager.addElementByInput(scanner);
            Product product=myInventoryManager.ReadById(130);

            assertNotNull(product);
            assertEquals(130, product.getProductId());
            assertEquals("Phone", product.getProductName());
            assertEquals("Electronics", product.getProductType());
            assertEquals(5, product.getAvailableQty());
            assertEquals(10000.0, product.getPrice());
        }
    }

