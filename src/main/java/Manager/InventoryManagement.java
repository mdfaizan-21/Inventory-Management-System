package Manager;

import Models.Product;

import java.util.*;

public class InventoryManagement {

    private static Map<Integer,Product> productList=new HashMap<>();
    public  static boolean update=false;
    public  static  int updateId;
    public static  Product InputHelper(Scanner scanner){
        int id;
        if(!update) {
            System.out.println("Enter your product Id");
             id = scanner.nextInt();
        }
        else {
            id=updateId;
        }
        System.out.println("Enter your product Name");
        String name= scanner.next();
        System.out.println("Enter your product Category");
        String type= scanner.next();
        System.out.println("Enter the Quantity of Product");
        int qty= scanner.nextInt();
        return  new Product(id,name,type,qty);
    }
    // this method is used to take input from user and store it to the productList
    public void addElement(Scanner scanner){
        Product newProduct=InputHelper(scanner);
        productList.put(newProduct.getProductId(), newProduct);
    }

    public List<Product> Read(){
        List<Product>products=new ArrayList<>(productList.values());
        return products;
    }

    public Product ReadById(int id){
        return productList.get(id);
    }
    public void delete(int id){
        productList.remove(id);
    }

    public Product update(int id,Scanner scanner){
        update=true;
        updateId=id;
        Product newProduct=InputHelper(scanner);
        updateId=0;
        return productList.replace(id,newProduct);
    }

}
