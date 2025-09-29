package Helpers;

import Exceptions.DuplicateProductException;
import Exceptions.InvalidProdcutDataException;
import Models.Product;

import java.util.InputMismatchException;
import java.util.Scanner;

import static Services.InventoryManagement.myProductDAO;

public class InputOutputHelper {
    public static Product InputHelper(Scanner scanner) {
        int id = 0;
        String name = null;
        String type = null;
        int qty = 0;
        double price = 0;

        try {
            System.out.print("🆔 Enter Product ID: ");
            id = scanner.nextInt();

            if (myProductDAO.getProductById(id, true) != null) {
                throw new DuplicateProductException("🚫 A product with this ID already exists.");
            }

            System.out.print("🏷️ Enter Product Name: ");
            name = scanner.next();

            System.out.print("📂 Enter Product Category: ");
            type = scanner.next();

            System.out.print("📦 Enter Product Quantity: ");
            qty = scanner.nextInt();
            if (qty < 0) throw new InvalidProdcutDataException("🚫 Quantity cannot be negative.");

            System.out.print("💲 Enter Product Price: ");
            price = scanner.nextDouble();
            if (price < 0) throw new InvalidProdcutDataException("🚫 Price cannot be negative.");

        } catch (InputMismatchException e) {
            System.out.println("⚠️ Invalid input! Please enter the correct data type.");
            scanner.nextLine(); // clear buffer
            return null;
        } catch (InvalidProdcutDataException | DuplicateProductException e) {
            System.out.println("⚠️ " + e.getMessage());
            return null;
        }

        return new Product(id, name, type, qty, price);
    }
}
