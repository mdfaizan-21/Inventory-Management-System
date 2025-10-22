package Services;

import DAO.Impl.ProductDAOImpl;
import Exceptions.DuplicateProductException;
import Exceptions.ProductNotFoundException;
import Helpers.InputOutputHelper;
import Models.Product;

import java.util.*;

public class InventoryManagement {

	public static ProductDAOImpl myProductDAO = new ProductDAOImpl();

	public boolean addElementByInput(Scanner scanner) {
		Product newProduct = InputOutputHelper.InputHelper(scanner);
		if (newProduct != null) {
			myProductDAO.AddProduct(newProduct);
			return true;
		}
		return false;
	}

	public void addElementFromCSV(Product productToAdd) {
		if (productToAdd != null) {

            try {
                myProductDAO.getProductById(productToAdd.getProductId(),false);
                throw new DuplicateProductException("🚫 A product with this ID already exists.");
            }
            catch (ProductNotFoundException e){
			    myProductDAO.AddProduct(productToAdd);
            }
		} else {
			System.out.println("❌ Product could not be added!");
		}
	}

	// Read all products
	public List<Product> Read() {
		return myProductDAO.getAllProducts();
	}

	public Product ReadById(int id) {
		try {
			return myProductDAO.getProductById(id, false);
		} catch (ProductNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public void delete(int id) throws ProductNotFoundException{
		Product requiredProduct = null;
		try {
			requiredProduct = myProductDAO.getProductById(id, false);
		} catch (Exception e) {
		}
		if (requiredProduct != null) {
			myProductDAO.deleteProductById(id);
		} else {
			throw new ProductNotFoundException("❌ Product with this id:-" + id + " cannot be deleted as this product is not available in list");
		}
	}

	public Product update(Product newProduct) {
        Product oldProduct=null;
        try {
		 oldProduct = myProductDAO.getProductById(newProduct.getProductId(), false);
        }
        catch (ProductNotFoundException e){
            System.out.println("Error:- "+e.getMessage());
        }

		if (oldProduct == null) {
			System.out.println("⚠ Cannot update. Product with ID " + newProduct.getProductId() + " not found.");
			return null;
		}

		return myProductDAO.updateProduct(newProduct);
	}

	public List<Product> SearchProductsByCategory(String productCategory) {
		return myProductDAO.getProductByCategory(productCategory);
	}public List<Product> SearchProductsByPriceRange(double minPrice,double maxPrice) {
		return myProductDAO.searchByPriceRange(minPrice, maxPrice);
	}
}
