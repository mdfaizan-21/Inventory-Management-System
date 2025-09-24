package util;

import DAO.ProductDAO;
import Models.Product;
import Services.InventoryManagement;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVHelper {
    public static final String FILE_NAME="products.csv";
    InventoryManagement manager=new InventoryManagement();
    public void generateReport(){
        try(CSVWriter writer=new CSVWriter(new FileWriter(FILE_NAME))){
            List<Product>productList=manager.Read();
            String[] header={"ProductId", "ProductName", "Category", "Quantity", "Price"};
            writer.writeNext(header);
            for (Product product:productList){
                String[] productString = new String[5];
                productString[0]=product.getProductId().toString();
                productString[1]=product.getProductName();
                productString[2]=product.getProductType();
                productString[3]=product.getAvailableQty().toString();
                productString[4]=product.getPrice().toString();

                writer.writeNext(productString);
            }
            System.out.println("written in csv file");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void readDATAfromCSV(){
        try {
            CSVReader reader=new CSVReader(new FileReader(FILE_NAME));
            String[] lines;
            lines=reader.readNext();// removing this because it contains header
            while ((lines=reader.readNext())!=null){
                Product newProduct=new Product(
                        Integer.valueOf(lines[0]),
                        lines[1],
                        lines[2],
                        Integer.valueOf(lines[3]),
                        Double.valueOf(lines[4])
                );
                System.out.println(newProduct);
                manager.addElementFromCSV(newProduct);
            }
            System.out.println("Products Added Successfully");
        }
        catch (IOException e){
            System.out.println("File is not Present");
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }
}
