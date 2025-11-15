package Services;

import Models.User;
import util.EmailUtil;

import java.util.InputMismatchException;
import java.util.Scanner;

import static Services.UserService.userDAO;

public class OTPService {
    public static int generateOTP() {
        return (int) (Math.random() * 900000) + 100000;
    }

    public static boolean verifyOTP(String email, String name, String password, String role, Scanner scanner) {
        int generatedOTP = generateOTP();
        EmailUtil.sendOTP(email, "OTP for registration", "OTP:- " + generatedOTP);

        // Make email verification mandatory - remove option to skip
        System.out.print("Enter the OTP sent to your email (mandatory): ");
        String otp = scanner.next();

        try {
            int OtpInt = Integer.parseInt(otp);
            if (OtpInt == generatedOTP) {
                User newUser = new User(name, password, role, email, "verified");
                userDAO.addUser(newUser);
                return true;
            } else {
                System.out.println("You have entered incorrect OTP. Registration failed.");
                return false;
            }
        } catch (NumberFormatException | InputMismatchException e) {
            System.out.println("Invalid OTP format. Registration failed.");
            return false;
        }
    }
}