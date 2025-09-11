package Manager;

import Models.Product;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        InventoryManagement myInventoryManager=new InventoryManagement();
        Scanner scanner=new Scanner(System.in);
        while (true){
            System.out.println("Enter 1 for adding the product 2 for watching the list and 3 for search by Id " +
                    "4 for deleting the element and 5 for updating the previous product details 6 for exit" );
            int choice= scanner.nextInt();
            if(choice==1){
                myInventoryManager.addElement(scanner);
                System.out.println("Your Product has been added successfully");
            }
            if(choice==2){
                List<Product>products=myInventoryManager.Read();
                for(Product product:products){
                    System.out.println(product);
                }
            }
            if(choice==3){
                System.out.println("Enter the product Id you want to search");
                int requiredProductId= scanner.nextInt();
                Product requiredProduct=myInventoryManager.ReadById(requiredProductId);
                System.out.println(requiredProduct);
            }
            if(choice==4){
                System.out.println("Enter the product Id you want to delete");
                int requiredProductId= scanner.nextInt();
                myInventoryManager.delete(requiredProductId);
                System.out.println("Your Product has been successfully deleted");
            }
            if (choice==5){
                System.out.println("Enter the product Id you want to update");
                int requiredProductId= scanner.nextInt();
                myInventoryManager.update(requiredProductId,scanner);
            }
            else if(choice==6){
                break;
            }
        }
        scanner.close();
    }
}
