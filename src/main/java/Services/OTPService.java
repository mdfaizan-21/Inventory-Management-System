package Services;

import Models.User;
import util.EmailUtil;

import java.util.InputMismatchException;
import java.util.Scanner;

import static Services.UserService.userDAO;

public class OTPService {
    public static int generateOTP(){
        return (int)(Math.random() * 900000) + 100000;
    }
    public static boolean verifyOTP(String email, String name, String password, String role, Scanner scanner){
        int generatedOTP=generateOTP();
        EmailUtil.sendOTP(email,"OTP for registration","OTP:- "+generatedOTP);
        System.out.print("Enter the OTP for verification or Just type '..' if you don't want to get verified:-");
        String otp=scanner.next();
        if(otp.contains("..")){
            User newUser = new User(name, password, role,null,null);
            userDAO.addUserWithoutEmail(newUser);
            return false;
        }
        else{
            try {
                int OtpInt=Integer.parseInt(otp);
                if(OtpInt==generatedOTP) {
                    User newUser = new User(name, password, role, email, "verified");
                    userDAO.addUser(newUser);
                    return true;
                }
                else {
                    System.out.println("You have entered incorrect OTP");
                    User newUser = new User(name, password, role,null,null);
                    userDAO.addUserWithoutEmail(newUser);
                    return false;
                }
            }catch (NumberFormatException | InputMismatchException e){
                System.out.println("Enter the Correct OTP");
                User newUser = new User(name, password, role,null,null);
                userDAO.addUserWithoutEmail(newUser);
                return false;
            }

        }

    }
}
