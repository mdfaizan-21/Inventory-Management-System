package Services;

import DAO.ProductDAO;
import Models.Product;

import java.util.*;

public class InventoryManagement {

    private static final Map<Integer, Product> productList = new HashMap<>();
    ProductDAO myProductDAO=new ProductDAO();
    public static Product InputHelper(Scanner scanner) {
        int id = 0;
        String name = null;
        String type = null;
        int qty = 0;
        double price = 0;

        try {
            System.out.print("Enter your product Id:- ");
            id = scanner.nextInt();

            if (productList.containsKey(id)) {
                throw new IllegalArgumentException("A product with this ID already exists.");
            }

            System.out.print("Enter your product Name:- ");
            name = scanner.next();

            System.out.print("Enter your product Category:- ");
            type = scanner.next();

            System.out.print("Enter the Quantity of Product:- ");
            qty = scanner.nextInt();
            if (qty < 0) throw new IllegalArgumentException("Quantity cannot be negative.");

            System.out.print("Enter the Price of Product:- ");
            price = scanner.nextDouble();
            if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");

        } catch (InputMismatchException e) {
            System.out.println("Invalid input! Please enter the correct data type.");
            scanner.nextLine(); // clear buffer
            return null;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return new Product(id, name, type, qty, price);
    }

    public void addElement(Scanner scanner) {
        Product newProduct = InputHelper(scanner);
        if (newProduct != null) {
            myProductDAO.AddProduct(newProduct);
            System.out.println("Product added successfully.");
        } else {
            System.out.println("Product could not be added.");
        }
    }

    // Read all products
    public List<Product> Read() {
        return myProductDAO.getAllProducts();
    }

    public Product ReadById(int id) {
        Product requiredProduct = myProductDAO.getProductById(id);
        if (requiredProduct == null) {
            System.out.println("⚠ The product with ID " + id + " does not exist.");
        }
        return requiredProduct;
    }

    public void delete(int id) {
        Product requiredProduct = myProductDAO.getProductById(id);
        if (requiredProduct!=null) {
            myProductDAO.deleteProductById(id);
        } else {
            System.out.println("Cannot delete. Product with ID " + id + " not found.");
        }
    }

    public Product update(Product newProduct) {
        Product oldProduct = myProductDAO.getProductById(newProduct.getProductId());

        if (oldProduct == null) {
            System.out.println("⚠ Cannot update. Product with ID " + newProduct.getProductId() + " not found.");
            return null;
        }

        Product updatedProduct=myProductDAO.updateProduct(newProduct);

        System.out.println("Product updated successfully.");
        return updatedProduct;
    }
}
