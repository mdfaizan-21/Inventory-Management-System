package util;

import Exceptions.DuplicateProductException;
import Models.Product;
import Services.InventoryManagement;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CSVHelper {

    public static final String REPORT_DIR = "report/";
    InventoryManagement manager = new InventoryManagement();

    public String generateReport() {
        String FILE_NAME = null;

        try {
            File dir = new File(REPORT_DIR);
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    System.out.println("📁 Directory created: " + dir.getAbsolutePath());
                } else {
                    System.out.println("🚫 Failed to create directory: " + dir.getAbsolutePath());
                }
            }

            FILE_NAME = REPORT_DIR + "inventory_report_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv";

            List<Product> productList = manager.Read();

            if (productList.isEmpty()) {
                System.out.println("⚠️ No products available to generate report.");
                return null;
            }

            try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_NAME))) {
                // Header
                String[] header = {"ProductId", "ProductName", "Category", "Quantity", "Price"};
                writer.writeNext(header);

                // Data rows
                for (Product product : productList) {
                    String[] data = {
                            String.valueOf(product.getProductId()),
                            product.getProductName(),
                            product.getProductType(),
                            String.valueOf(product.getAvailableQty()),
                            String.valueOf(product.getPrice())
                    };
                    writer.writeNext(data);
                }
            }

            System.out.println("✅ Report generated at: " + FILE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return FILE_NAME;
    }



    public int readDATFromCSV() {
        return readDATFromCSV("products.csv");
    }

    public int readDATFromCSV(String filePath) {
            int importedCount = 0;
            int skippedCount = 0;
        try {
            CSVReader reader = new CSVReader(new FileReader(filePath));
            String[] lines;

            // remove header
            lines = reader.readNext();


            while ((lines = reader.readNext()) != null) {
                try {
                    Product newProduct = new Product(
                            Integer.valueOf(lines[0]),
                            lines[1],
                            lines[2],
                            Integer.valueOf(lines[3]),
                            Double.valueOf(lines[4])
                    );
                    try {
                        manager.addElementFromCSV(newProduct);
                        System.out.println("✅ Added: " + newProduct);
                        importedCount++;
                    }
                    catch (DuplicateProductException e){
                        System.out.println("Error:- "+e.getMessage());
                        skippedCount++;
                    }

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
        System.out.println(skippedCount+" rows has been skipped due to error or duplication");
        return importedCount;
    }

}
