package util;

import DAO.ProductDAO;
import Models.Product;
import Services.InventoryManagement;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CSVHelper {
    public static final String FILE_NAME="products.csv";
    InventoryManagement manager=new InventoryManagement();

    public void generateReport() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_NAME))) {
            List<Product> productList = manager.Read();

            if (productList.isEmpty()) {
                System.out.println("⚠️ No products available to generate report.");
                return;
            }

            // Header row
            String[] header = {"ProductId", "ProductName", "Category", "Quantity", "Price"};
            writer.writeNext(header);

            // Write each product
            for (Product product : productList) {
                String[] productString = new String[5];
                productString[0] = product.getProductId().toString();
                productString[1] = product.getProductName();
                productString[2] = product.getProductType();
                productString[3] = product.getAvailableQty().toString();
                productString[4] = product.getPrice().toString();

                writer.writeNext(productString);
            }

            System.out.println("✅ Inventory report generated successfully at: " + FILE_NAME);

        } catch (IOException e) {
            System.out.println("🚫 Error writing CSV file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠️ Unexpected error while generating report: " + e.getMessage());
        }
    }

    public void readDATAfromCSV() {
        try {
            CSVReader reader = new CSVReader(new FileReader(FILE_NAME));
            String[] lines;

            // remove header
            lines = reader.readNext();

            int importedCount = 0;
            int skippedCount = 0;

            while ((lines = reader.readNext()) != null) {
                try {
                    Product newProduct = new Product(
                            Integer.valueOf(lines[0]),
                            lines[1],
                            lines[2],
                            Integer.valueOf(lines[3]),
                            Double.valueOf(lines[4])
                    );

                    System.out.println("✅ Added: " + newProduct);
                    manager.addElementFromCSV(newProduct);
                    importedCount++;

                } catch (NumberFormatException e) {
                    System.out.println("⚠️ Skipping row (invalid number format): " + Arrays.toString(lines));
                    skippedCount++;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("⚠️ Skipping row (incomplete data): " + Arrays.toString(lines));
                    skippedCount++;
                } catch (Exception e) {
                    System.out.println("⚠️ Skipping row (unexpected error): " + Arrays.toString(lines));
                    skippedCount++;
                }
            }


        } catch (IOException e) {
            System.out.println("🚫 File not found or cannot be read: " + e.getMessage());
        } catch (CsvValidationException e) {
            System.out.println("🚫 Invalid CSV format: " + e.getMessage());
        }
    }

}
