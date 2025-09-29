package DAO;

import Exceptions.ProductNotFoundException;
import Models.Product;
import util.DBconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO implements DAO {

    @Override
    public void AddProduct(Product product){
        String sqlQuery="INSERT INTO PRODUCTS VALUES (?, ?, ?, ?, ?)";
        try(Connection myconnection=DBconnection.getConnection();
            PreparedStatement myQuery= myconnection.prepareStatement(sqlQuery)) {
            myQuery.setInt(1, product.getProductId());
            myQuery.setString(2,product.getProductName());
            myQuery.setString(3,product.getProductType());
            myQuery.setDouble(5, product.getPrice());
            myQuery.setInt(4, product.getAvailableQty());
            myQuery.executeUpdate();
        }
        catch (SQLException e){
            System.out.println("There is some error in your Query");
        }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> productslist = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection connection = DBconnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)) {
            if(resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    Product p = new Product();
                    p.setProductId(resultSet.getInt("ProductId"));
                    p.setProductName(resultSet.getString("ProductName"));
                    p.setProductType(resultSet.getString("ProductCategory"));
                    p.setAvailableQty(resultSet.getInt("AvailableQuantity"));
                    p.setPrice(resultSet.getDouble("Price"));
                    productslist.add(p);
                }
            }
            else {
                throw  new ProductNotFoundException("Product List is Empty");
            }
        } catch (SQLException e) {
            System.out.println("Error while Connecting to database");
        }
        catch (ProductNotFoundException e){
            System.out.println(e.getMessage());
        }
        return productslist;
    }

    @Override
    public Product getProductById(int productId,boolean fromInput) {
        Product product = null;
        String query = "SELECT * FROM products WHERE ProductId = ?";

        try (Connection connection = DBconnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                product = new Product();
                product.setProductId(resultSet.getInt("ProductId"));
                product.setProductName(resultSet.getString("ProductName"));
                product.setProductType(resultSet.getString("ProductCategory"));
                product.setAvailableQty(resultSet.getInt("AvailableQuantity"));
                product.setPrice(resultSet.getDouble("Price"));
            }
            else {
                if(!fromInput){
                throw new ProductNotFoundException("Product with this id:-"+productId+" is not available in the list");
                }
            }
        } catch (SQLException | ProductNotFoundException e) {
            System.out.println("Error:- "+e.getMessage());
        }
        return product;
    }

    @Override
    public void deleteProductById(int productId) {
        String query = "DELETE FROM products WHERE ProductId = ?";

        try (Connection connection = DBconnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);

            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Product updateProduct(Product product) {
        StringBuilder query = new StringBuilder("UPDATE products SET ");
        List<Object> params = new ArrayList<>();

        if (product.getProductName() != null) {
            query.append("ProductName = ?, ");
            params.add(product.getProductName());
        }
        if (product.getProductType() != null) {
            query.append("ProductCategory = ?, ");
            params.add(product.getProductType());
        }
        if (product.getAvailableQty() !=null) {
            query.append("AvailableQuantity = ?, ");
            params.add(product.getAvailableQty());
        }
        if (product.getPrice() !=null) {
            query.append("Price = ?, ");
            params.add(product.getPrice());
        }

        query.setLength(query.length() - 2);
        query.append(" WHERE ProductId = ?");
        params.add(product.getProductId());

        try (Connection connection = DBconnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
            preparedStatement.executeUpdate();
            return getProductById(product.getProductId(),false);

        } catch (SQLException e) {
            System.out.println("There are some errors in the query");
        }
        return  null;
    }
    public List<Product> getProductByCategory(String Category) {
        List<Product> productslist = new ArrayList<>();
        String sql = "SELECT * FROM products where ProductCategory= ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement preparedStatement=connection.prepareStatement(sql);) {
            preparedStatement.setString(1,Category);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet!=null) {
                while (resultSet.next()) {
                    Product p = new Product();
                    p.setProductId(resultSet.getInt("ProductId"));
                    p.setProductName(resultSet.getString("ProductName"));
                    p.setProductType(resultSet.getString("ProductCategory"));
                    p.setAvailableQty(resultSet.getInt("AvailableQuantity"));
                    p.setPrice(resultSet.getDouble("Price"));
                    productslist.add(p);
                }
            }
            else {
                throw  new ProductNotFoundException("There are no Products available in this category");
            }
        } catch (SQLException e) {
            System.out.println("Error while Connecting to database");
        }
        catch (ProductNotFoundException e){
            System.out.println(e.getMessage());
        }
        return productslist;
    }

}

