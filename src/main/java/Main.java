import DAO.Impl.ReportsTrackerImpl;
import DAO.Impl.UserDAOImpl;
import DAO.ReportsTracker;
import DAO.UserDAO;
import Exceptions.ProductNotFoundException;
import Helpers.PrintHelper;
import Helpers.UpdateHelper;
import Models.Product;
import Models.User;
import Services.InventoryManagement;
import util.CSVHelper;
import util.EmailUtil;

import java.text.SimpleDateFormat;
import java.util.*;


public class Main {
	public static void main(String[] args) {
		InventoryManagement myInventoryManager = new InventoryManagement();
		CSVHelper helper = new CSVHelper();
		UserDAO userDAO = new UserDAOImpl();
		ReportsTracker reportsTracker=new ReportsTrackerImpl();
		Scanner scanner = new Scanner(System.in);
		User user ;
                while ((user=PrintHelper.loginOrRegisterMenu(scanner,userDAO))==null){
                }

		while (true) {
			if (user.getRole().equalsIgnoreCase("USER")) {
				PrintHelper.printUserMenu();
			} else if (user.getRole().equalsIgnoreCase("ADMIN")) {
				PrintHelper.printAdminMenu();
			}
			System.out.print("👉 Enter your choice: ");
			int choice = scanner.nextInt();
            if (user.getRole().equalsIgnoreCase("USER")){
                choice+=20;
            }
			switch (choice) {
				case 1:
					if (myInventoryManager.addElementByInput(scanner))
						System.out.println("✅ Your product has been added successfully!");
					break;

				case 2:
                case 21:
					List<Product> products = myInventoryManager.Read();
					if (!products.isEmpty()) PrintHelper.printTheTable(products);
					else System.out.println("⚠️ No products found in inventory.");
					break;

				case 3:
                case 22:
					System.out.print("🔎 Enter the product ID you want to search: ");
					int requiredProductId1 = scanner.nextInt();
					Product requiredProduct = myInventoryManager.ReadById(requiredProductId1);
					if (requiredProduct != null) {
						PrintHelper.printTheTable(Collections.singletonList(requiredProduct));
					}
					break;

				case 4:
                case 23:
					System.out.print("💰 Enter minimum price: ");
					double minPrice = scanner.nextDouble();
					System.out.print("💰 Enter maximum price: ");
					double maxPrice = scanner.nextDouble();
					List<Product> rangeProducts = myInventoryManager.SearchProductsByPriceRange(minPrice, maxPrice);
					if (!rangeProducts.isEmpty()) PrintHelper.printTheTable(rangeProducts);
					break;

				case 5:
					System.out.print("🗑️ Enter the product ID you want to delete: ");
					int requiredProductId2 = scanner.nextInt();
					try {
						myInventoryManager.delete(requiredProductId2);
						System.out.println("🗑️✅ Product deleted successfully!");
					} catch (ProductNotFoundException e) {
						System.out.println(e.getMessage());
					}
					break;

				case 6:
						System.out.print("✏️ Enter the product ID you want to update: ");
						int productId = scanner.nextInt();
						Product productToUpdate = UpdateHelper.update(scanner, productId);
						if (productToUpdate != null) {
							Product updatedProduct = myInventoryManager.update(productToUpdate);
							PrintHelper.printTheTable(Collections.singletonList(updatedProduct));
							System.out.println("✅ Product updated successfully!");
						}
						break;

				case 7:
                case 24:
					System.out.print("🗂️ Enter the category you want to search: ");
					String category = scanner.next();
					List<Product> productList = myInventoryManager.SearchProductsByCategory(category);
					if (!productList.isEmpty()) PrintHelper.printTheTable(productList);
					else System.out.println("⚠️ No products found in this category.");
					break;

				case 8:
                case 25:
                    System.out.println("📊 Generating report... ");
					String name= helper.generateReport();
	                EmailUtil.sendReport(user.email,
			                "Daily-Reports","The report has been attached in this mail",name);
					reportsTracker.addReports(user.getUserName(), user.getEmail(),new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) );

					break;

				case 9:
					System.out.println("📥 Importing products from CSV...");
					int count =helper.readDATFromCSV();
					System.out.println("✅ "+count+" Products imported successfully!");
					break;

                case 26:
				case 10:
					System.out.println("👋 Exiting Inventory Management System.");
					break;

				default:
					System.out.println("Please Enter a Valid Choice");
					break;
			}
			if (choice == 10 || choice==26) break;

		}
			scanner.close();
	}
}
