import Models.Product;
import Services.InventoryManagement;
import util.CSVHelper;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        InventoryManagement myInventoryManager=new InventoryManagement();
        CSVHelper helper=new CSVHelper();
        Scanner scanner=new Scanner(System.in);

        while (true) {
            System.out.println("===== Inventory Management System ======");
            System.out.println("1.Add the Product");
            System.out.println("2.Display All the Products");
            System.out.println("3.Search Product by Id");
            System.out.println("4.Delete the Product");
            System.out.println("5.Update the Product");
            System.out.println("6.Generate Report Of Our Inventory into CSV File");
            System.out.println("7.Add the Products from CSV File");
            System.out.println("8.Exit the Menu");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    if(myInventoryManager.addElementByInput(scanner))
                        System.out.println("Your Product has been added successfully");
                    break;

                case 2:
                    List<Product> products = myInventoryManager.Read();
                    myInventoryManager.printTheTable(products);
                    break;

                case 3:
                    System.out.println("Enter the product Id you want to search");
                    int requiredProductId1 = scanner.nextInt();
                    Product requiredProduct = myInventoryManager.ReadById(requiredProductId1);
                    if (requiredProduct != null) {
                        myInventoryManager.printTheTable(Collections.singletonList(requiredProduct));
                    }

                    break;

                case 4:
                    System.out.println("Enter the product Id you want to delete");
                    int requiredProductId2 = scanner.nextInt();
                    myInventoryManager.delete(requiredProductId2);
                    System.out.println("Your Product has been successfully deleted");
                    break;

                case 5:
                    System.out.print("Enter the product Id you want to update: ");
                    int productId = scanner.nextInt();

                    Product productToUpdate = new Product();
                    productToUpdate.setProductId(productId);
//                    System.out.println();
                    System.out.println("Enter the number of things you want to update:");
                    int numberOfUpdation=scanner.nextInt();
                    Set<Integer>selected=new HashSet<>();
                    while (numberOfUpdation-->0) {
                        System.out.println("1. Name");
                        System.out.println("2. Type");
                        System.out.println("3. Quantity");
                        System.out.println("4. Price");

                        int updatechoice = scanner.nextInt();
                        scanner.nextLine();

                        if(selected.contains(updatechoice)){
                            System.out.println("You have already chose this option");
                            numberOfUpdation++;
                            continue;
                        }
                        else {
                            selected.add(updatechoice);
                        }
                        switch (updatechoice) {
                            case 1:
                                System.out.print("Enter new Name: ");
                                String newName = scanner.nextLine();
                                productToUpdate.setProductName(newName);
                                break;
                            case 2:
                                System.out.print("Enter new Type: ");
                                String newType = scanner.nextLine();
                                productToUpdate.setProductType(newType);
                                break;
                            case 3:
                                System.out.print("Enter new Quantity: ");
                                int newQty = scanner.nextInt();
                                productToUpdate.setAvailableQty(newQty);
                                break;
                            case 4:
                                System.out.print("Enter new Price: ");
                                double newPrice = scanner.nextDouble();
                                productToUpdate.setPrice(newPrice);
                                break;
                            default:
                                System.out.println("Invalid choice!");
                                break;
                        }
                    }
                    Product updatedProduct=myInventoryManager.update(productToUpdate);
                    myInventoryManager.printTheTable(Collections.singletonList(productToUpdate));

                    break;

                case 6:
                    helper.generateReport();
                    break;
                case 7:
                    helper.readDATAfromCSV();
                    break;
                case 8:
                    break;
            }
            if(choice==8)break;
        }

        scanner.close();
    }
}
