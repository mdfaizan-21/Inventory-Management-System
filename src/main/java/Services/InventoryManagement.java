package Services;

import DAO.ProductDAO;
import Exceptions.ProductNotFoundException;
import Helpers.InputOutputHelper;
import Models.Product;

import java.util.*;

public class InventoryManagement {

    public  static ProductDAO myProductDAO=new ProductDAO();
    public boolean addElementByInput(Scanner scanner) {
        Product newProduct = InputOutputHelper.InputHelper(scanner);
        if (newProduct != null) {
            myProductDAO.AddProduct(newProduct);
            return true;
        }
        return false;
    }

    public void addElementFromCSV(Product producttoAdd) {
        if (producttoAdd != null) {
            myProductDAO.AddProduct(producttoAdd);
        } else {
            System.out.println("❌ Product could not be added!");
        }
    }

    // Read all products
    public List<Product> Read() {
        return myProductDAO.getAllProducts();
    }

    public Product ReadById(int id) {
        return myProductDAO.getProductById(id,false);
    }

    public void delete(int id) {
        Product requiredProduct = myProductDAO.getProductById(id,false);
        if (requiredProduct!=null) {
            myProductDAO.deleteProductById(id);
        } else {
            throw new ProductNotFoundException("❌ Product with this id:-"+id+" cannot be deleted as this product is not available in list");

        }
    }

    public Product update(Product newProduct) {
        Product oldProduct = myProductDAO.getProductById(newProduct.getProductId(),false);

        if (oldProduct == null) {
            System.out.println("⚠ Cannot update. Product with ID " + newProduct.getProductId() + " not found.");
            return null;
        }

        return myProductDAO.updateProduct(newProduct);
    }
    public List<Product> SearchProductsByCategory(String productCategory){
        return myProductDAO.getProductByCategory(productCategory);
    }
}
