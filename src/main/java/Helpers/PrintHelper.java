package Helpers;

import DAO.UserDAO;
import Models.Product;
import Models.User;
import Services.UserService;
import util.EmailUtil;
import java.util.List;
import java.util.Scanner;

import static Services.OTPService.generateOTP;
import static Services.OTPService.verifyOTP;

public class PrintHelper {
    public static void printTheTable(List<Product> Products){
        System.out.printf("%-10s %-20s %-20s %-15s %-10s%n",
                "ID", "Name", "Category", "Quantity", "Price");

        System.out.println("----------------------------------------------------------------------------------");

        for (Product product : Products) {
            System.out.printf("%-10d %-20s %-20s %-15d %-10.2f%n",
                    product.getProductId(),
                    product.getProductName(),
                    product.getProductType(),
                    product.getAvailableQty(),
                    product.getPrice());
        }
        System.out.println("-----------------------------------------------------------------------------------");

    }
    public static final String RED = "\u001B[31m";
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String WHITE = "\u001B[37m";


    public static void printAdminMenu() {
        System.out.println(MAGENTA + "╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                 🧑‍💼  ADMIN MENU - INVENTORY SYSTEM            ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣" + RESET);
        System.out.println(GREEN + "║  " + BLUE + "1️⃣ " + GREEN + " ➕   Add a Product                                     ║");
        System.out.println("║  " + BLUE + "2️⃣ " + GREEN + " 📋   Display All Products                              ║");
        System.out.println("║  " + BLUE + "3️⃣ " + GREEN + " 🔍   Search Product by ID                              ║");
        System.out.println("║  " + BLUE + "4️⃣ " + GREEN + " 💰   Search Products by Price Range                    ║");
        System.out.println("║  " + BLUE + "5️⃣ " + GREEN + " 🗑️   Delete a Product                                  ║");
        System.out.println("║  " + BLUE + "6️⃣ " + GREEN + " ✏️   Update a Product                                  ║");
        System.out.println("║  " + BLUE + "7️⃣ " + GREEN + " 🗂️   Search Products by Category                       ║");
        System.out.println("║  " + BLUE + "8️⃣ " + GREEN + " 📊   Generate Report (CSV)                             ║");
        System.out.println("║  " + BLUE + "9️⃣ " + GREEN + " 📥   Import Products from CSV                          ║");
        System.out.println("║  " + BLUE + "🔟 " + GREEN + " 🚪   Exit Menu                                         ║");
        System.out.println(MAGENTA + "╚══════════════════════════════════════════════════════════════╝" + RESET);
        System.out.print(YELLOW + "\n👉 Enter your choice: " + RESET);
    }


    public static void printUserMenu() {
        System.out.println(MAGENTA + "╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                 🙋‍♂️  USER MENU - INVENTORY SYSTEM             ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣" + RESET);
        System.out.println(GREEN + "║  " + BLUE + "1️⃣ " + GREEN + " 📋   Display All Products                              ║");
        System.out.println("║  " + BLUE + "2️⃣ " + GREEN + " 🔍   Search Product by ID                              ║");
        System.out.println("║  " + BLUE + "3️⃣ " + GREEN + " 💰   Search Products by Price Range                    ║");
        System.out.println("║  " + BLUE + "4️⃣ " + GREEN + " 🗂️   Search Products by Category                       ║");
        System.out.println("║  " + BLUE + "5️⃣ " + GREEN + " 📊   Generate Report (CSV)                             ║");
        System.out.println("║  " + BLUE + "6️⃣ " + GREEN + " 🚪   Exit Menu                                         ║");
        System.out.println(MAGENTA + "╚══════════════════════════════════════════════════════════════╝" + RESET);
        System.out.print(YELLOW + "\n👉 Enter your choice: " + RESET);
    }

    public static User loginOrRegisterMenu(Scanner scanner, UserDAO userDAO) {
        User user = null;

        System.out.println(MAGENTA + "╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║          📦  WELCOME TO INVENTORY MANAGEMENT SYSTEM  📦              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════╝" + RESET);

            while (true) {
                System.out.println(CYAN + "\n👋 Are you an existing user?" + RESET);
                System.out.println(GREEN + "1️⃣  Yes, I already have an account");
                System.out.println("2️⃣  No, I want to register" + RESET);
                System.out.print(YELLOW + "\n👉 Enter your choice: " + RESET);
                int choice = scanner.nextInt();
                boolean existing = (choice == 1);

                if (existing) {
                    // 🔐 Login Section
                    System.out.println("\n" + BLUE + "🔐 LOGIN PORTAL" + RESET);
                    System.out.print("👤 Enter Username: ");
                    String userName = scanner.next();
                    System.out.print("🔑 Enter Password: ");
                    String password = scanner.next();
                    String email=null;
                    Integer generatedOTP=null;
                    user = UserService.login(userName, password);

                    if (user != null) {
                        if(user.status==null){
                            System.out.println("You are not verified to access Inventory");
                            System.out.println("Enter your Email to get verified");
                            email=scanner.next();
                            int otp=generateOTP();
                            EmailUtil.sendOTP(email,"OTP For Login","Your OTP for Login is "+otp);
                            System.out.println("Enter the OTP received at your email");
                            generatedOTP=scanner.nextInt();

                            if(generatedOTP!=otp){
                                System.out.println("Incorrect Otp please Try again");
                                return null;
                            }
                            System.out.println("You are now verified to use Inventory");
                            userDAO.addVerification(userName, "verified", email);
                            return null;
                        }
                        System.out.println(GREEN + "\n✅ Login successful! Welcome back, " + userName + "!" + RESET);

                        // 🕐 Smooth transition
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                        break; // Exit loop after successful login
                    }

                } else {
                    // 📝 Registration Section
                    System.out.println(MAGENTA + "\n╔═══════════════════════════════════════════════╗");
                    System.out.println("║            📝  REGISTRATION PORTAL             ║");
                    System.out.println("╚═══════════════════════════════════════════════╝" + RESET);

                    String name = null;
                    String password = null;
                    String role = null;
                    String email=null;
                    System.out.print("👤 Enter Username: ");
                    name = scanner.next();
                    if (userDAO.getUserByUserName(name)!=null)
                    {
                        System.out.println("User with this username already exist");
                        return null;
                    }

                    System.out.print("🔑 Enter Password: ");
                    password = scanner.next();

                    System.out.print("🎭 Enter Role (admin/user): ");
                    role = scanner.next();

                    System.out.print(" Enter Your Email: ");
                    email = scanner.next();

                    boolean verification=verifyOTP(email,name,password,role,scanner);
                    String message=verification?"With verification":"Without Verification";
                    System.out.println(GREEN + "\n✅ Registration successful! "+message+" You can now log in, " + name + "." + RESET);

                    // 🕐 Small transition delay before returning to log in
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            System.out.println(YELLOW + "\n🚀 Redirecting to your dashboard..." + RESET);
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        return user;
    }


}
