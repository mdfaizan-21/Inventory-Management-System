package Helpers;

import Models.Product;

import java.util.List;

public class PrintHelper {
    public static void printTheTable(List<Product> Products){
        System.out.printf("%-10s %-20s %-20s %-15s %-10s%n",
                "ID", "Name", "Category", "Quantity", "Price");

        System.out.println("----------------------------------------------------------------------------------");

        for (Product product : Products) {
            System.out.printf("%-10d %-20s %-20s %-15d %-10.2f%n",
                    product.getProductId(),
                    product.getProductName(),
                    product.getProductType(),
                    product.getAvailableQty(),
                    product.getPrice());
        }
        System.out.println("-----------------------------------------------------------------------------------");

    }
    public static void printMenu() {
        System.out.println("===== 📦 Inventory Management System =====");
        System.out.println("1️⃣  ➕ Add a Product");
        System.out.println("2️⃣  📋 Display All Products");
        System.out.println("3️⃣  🔍 Search Product by ID");
        System.out.println("4️⃣  🗑️ Delete a Product");
        System.out.println("5️⃣  ✏️ Update a Product");
        System.out.println("6️⃣  🗂️ Search Products by Category");
        System.out.println("7️⃣  📊 Generate Report (CSV)");
        System.out.println("8️⃣  📥 Import Products from CSV");
        System.out.println("9️⃣  🚪 Exit Menu");
    }

}
