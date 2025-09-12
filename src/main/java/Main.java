import Models.Product;
import Services.InventoryManagement;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        InventoryManagement myInventoryManager=new InventoryManagement();
        Scanner scanner=new Scanner(System.in);
        while (true) {
            System.out.println("===== Inventory Management System ======");
            System.out.println("1.Add the Product");
            System.out.println("2.Display  All the Products");
            System.out.println("3.Search Product by Id");
            System.out.println("4.Delete the Product");
            System.out.println("5.Update the Product");
            System.out.println("6.Exit the Menu");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    myInventoryManager.addElement(scanner);
                    System.out.println("Your Product has been added successfully");
                    break;

                case 2:
                    List<Product> products = myInventoryManager.Read();
                    if(products.isEmpty())
                        System.out.println("Product List is Empty");
                    else {
                        for (Product product : products) {
                            System.out.println(product);
                        }
                    }
                    break;

                case 3:
                    System.out.println("Enter the product Id you want to search");
                    int requiredProductId1 = scanner.nextInt();
                    Product requiredProduct = myInventoryManager.ReadById(requiredProductId1);
                    if (requiredProduct != null) {
                        System.out.println(requiredProduct);
                    }

                    break;

                case 4:
                    System.out.println("Enter the product Id you want to delete");
                    int requiredProductId2 = scanner.nextInt();
                    myInventoryManager.delete(requiredProductId2);
                    System.out.println("Your Product has been successfully deleted");
                    break;

                case 5:
                    System.out.print("Enter the product Id you want to update:- ");
                    int requiredProductId = scanner.nextInt();
                    System.out.print("Enter new Quantity:- ");
                    int newQuantity= scanner.nextInt();
                    Product newProduct=myInventoryManager.update(requiredProductId, newQuantity);
                    if(newProduct!=null) {
                        System.out.println("Your Changed Product is:-");
                        System.out.println(newProduct);
                    }

                    break;

                case 6:
                    break;
            }
            if(choice==6)break;
        }

        scanner.close();
    }
}
