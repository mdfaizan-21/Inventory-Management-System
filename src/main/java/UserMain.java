//import DAO.UserDAO;
//import DAO.Impl.UserDAOImpl;
//import Models.User;
//
//import java.util.List;
//import java.util.Scanner;
//
//public class UserMain {
//	public static void main(String[] args) {
//
//		Scanner scanner = new Scanner(System.in);
//		UserDAO userDAO = new UserDAOImpl();
//
//		while (true) {
//			System.out.println("\n============================");
//			System.out.println("📋 USER MANAGEMENT SYSTEM");
//			System.out.println("============================");
//			System.out.println("1️⃣  Add User");
//			System.out.println("2️⃣  View All Users");
//			System.out.println("3️⃣  Get User By ID");
//			System.out.println("4️⃣  Delete User By ID");
//			System.out.println("5️⃣  Exit");
//			System.out.print("👉 Enter your choice: ");
//
//
//			int choice = scanner.nextInt();
//			scanner.nextLine(); // consume newline
//
//			switch (choice) {
//				case 1:
//					System.out.print("Enter Username: ");
//					String name = scanner.nextLine();
//
//					System.out.print("Enter Password: ");
//					String password = scanner.nextLine();
//
//					System.out.print("Enter Role: ");
//					String role = scanner.nextLine();
//
//					User newUser = new User( name, password, role);
//					userDAO.addUser(newUser);
//					break;
//
//				case 2:
//					// Display all users
//					List<User> userList = userDAO.getAllUsers();
//					if (userList.isEmpty()) {
//						System.out.println("⚠️ No users found!");
//					} else {
//						System.out.println("\n👥 All Users:");
//						for (User u : userList) {
//							System.out.println(u);
//						}
//					}
//					break;
//
//				case 3:
//					// Get user by ID
//					System.out.print("Enter User ID to search: ");
//					int searchId = scanner.nextInt();
//					User foundUser = userDAO.getUserById(searchId);
//
//					if (foundUser != null) {
//						System.out.println("✅ User Found: " + foundUser);
//					} else {
//						System.out.println("❌ No user found with ID " + searchId);
//					}
//					break;
//
//				case 4:
//					// Delete user by ID
//					System.out.print("Enter User ID to delete: ");
//					int deleteId = scanner.nextInt();
//					userDAO.deleteUserById(deleteId);
//					break;
//                case 6:
//                    System.out.println("Enter Your Username");
//                    String Username=scanner.next();
//                    User user=userDAO.getUserByUserName(Username);
//                    System.out.println(user);
//                    break;
//
//
//				case 5:
//					// Exit program
//					System.out.println("👋 Exiting User Management System. Goodbye!");
//					System.exit(0);
//					break;
//
//				default:
//					System.out.println("⚠️ Invalid choice! Please try again.");
//			}
//		}
//	}
//}
