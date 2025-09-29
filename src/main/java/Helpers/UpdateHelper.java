package Helpers;

import Models.Product;

import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

public class UpdateHelper {
    public static Product update(Scanner scanner,int productId){
        Product productToUpdate = new Product();
        productToUpdate.setProductId(productId);

        System.out.print("🛠️ Enter the number of fields you want to update (max 4): ");
        try {
            int numberOfUpdation = scanner.nextInt();
            if(numberOfUpdation>4)throw new IllegalArgumentException("⚠️ Number of updates cannot be greater than 4.");
            Set<Integer> selected = new HashSet<>();
            while (numberOfUpdation-- > 0) {
                System.out.println("🔽 Choose a field to update:");
                System.out.println("1️⃣  Name");
                System.out.println("2️⃣  Type");
                System.out.println("3️⃣  Quantity");
                System.out.println("4️⃣  Price");

                int updatechoice = scanner.nextInt();
                scanner.nextLine();

                if (selected.contains(updatechoice)) {
                    System.out.println("⚠️ You already chose this option. Pick another.");
                    numberOfUpdation++;
                    continue;
                } else {
                    selected.add(updatechoice);
                }
                switch (updatechoice) {
                    case 1:
                        System.out.print("✏️ Enter new Name: ");
                        String newName = scanner.nextLine();
                        productToUpdate.setProductName(newName);
                        break;
                    case 2:
                        System.out.print("🏷️ Enter new Type: ");
                        String newType = scanner.nextLine();
                        productToUpdate.setProductType(newType);
                        break;
                    case 3:
                        System.out.print("📦 Enter new Quantity: ");
                        int newQty = scanner.nextInt();
                        productToUpdate.setAvailableQty(newQty);
                        break;
                    case 4:
                        System.out.print("💲 Enter new Price: ");
                        double newPrice = scanner.nextDouble();
                        productToUpdate.setPrice(newPrice);
                        break;
                    default:
                        System.out.println("❌ Invalid choice! Please select a number between 1–4.");
                        numberOfUpdation++;
                        break;
                }
            }
        }
        catch (InputMismatchException e)
        {
            System.out.println("🚫 Invalid input! Please enter the correct data type.");
            return null;
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("🚫 " + e.getMessage());
            return null;
        }

        return productToUpdate;
    }
}
