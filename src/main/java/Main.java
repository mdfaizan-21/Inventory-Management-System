import Helpers.PrintHelper;
import Helpers.UpdateHelper;
import Models.Product;
import Services.InventoryManagement;
import util.CSVHelper;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        InventoryManagement myInventoryManager = new InventoryManagement();
        CSVHelper helper = new CSVHelper();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            PrintHelper.printMenu(); // already has emojis
            System.out.print("👉 Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    if (myInventoryManager.addElementByInput(scanner))
                        System.out.println("✅ Your product has been added successfully!");
                    break;

                case 2:
                    List<Product> products = myInventoryManager.Read();
                    if (!products.isEmpty())
                        PrintHelper.printTheTable(products);
                    else
                        System.out.println("⚠️ No products found in inventory.");
                    break;

                case 3:
                    System.out.print("🔎 Enter the product ID you want to search: ");
                    int requiredProductId1 = scanner.nextInt();
                    Product requiredProduct = myInventoryManager.ReadById(requiredProductId1);
                    if (requiredProduct != null) {
                        PrintHelper.printTheTable(Collections.singletonList(requiredProduct));
                    }
                    break;

                case 4:
                    System.out.print("🗑️ Enter the product ID you want to delete: ");
                    int requiredProductId2 = scanner.nextInt();
                    myInventoryManager.delete(requiredProductId2);
                    System.out.println("🗑️✅ Product deleted successfully!");
                    break;

                case 5:
                    System.out.print("✏️ Enter the product ID you want to update: ");
                    int productId = scanner.nextInt();
                    Product productToUpdate = UpdateHelper.update(scanner, productId);
                    if (productToUpdate != null) {
                        Product updatedProduct = myInventoryManager.update(productToUpdate);
                        PrintHelper.printTheTable(Collections.singletonList(updatedProduct));
                        System.out.println("✅ Product updated successfully!");
                    }
                    break;

                case 6:
                    System.out.print("🗂️ Enter the category you want to search: ");
                    String category = scanner.next();
                    List<Product> productList = myInventoryManager.SearchProductsByCategory(category);
                    if (!productList.isEmpty())
                        PrintHelper.printTheTable(productList);
                    else
                        System.out.println("⚠️ No products found in this category.");
                    break;

                case 7:
                    System.out.println("📊 Generating report... ");
                    helper.generateReport();
                    break;

                case 8:
                    System.out.println("📥 Importing products from CSV...");
                    helper.readDATAfromCSV();
                    System.out.println("✅ Products imported successfully!");
                    break;

                case 9:
                    System.out.println("👋 Exiting Inventory Management System.");
                    break;
            }
            if (choice == 9) break;
        }

        scanner.close();
    }
}
