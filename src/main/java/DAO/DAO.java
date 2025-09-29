package DAO;

import Models.Product;

import java.util.List;

public interface DAO {
    void AddProduct(Product product);
    List<Product> getAllProducts();
    Product getProductById(int id,boolean check);
    void deleteProductById(int id);
    Product updateProduct(Product product);
    List<Product> getProductByCategory(String Category);

}
