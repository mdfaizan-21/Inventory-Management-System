package Services;

import DAO.Impl.UserDAOImpl;
import DAO.UserDAO;
import Exceptions.UserNotFoundException;
import Models.User;

import static Helpers.PrintHelper.RED;
import static Helpers.PrintHelper.RESET;

public class UserService {
	static UserDAO userDAO=new UserDAOImpl();
	public static User login(String userName,String password){
		User user=null;
		try {
			user = userDAO.getUserByUserName(userName);
		} catch (Exception e) {
			// User not found, user will remain null
		}
		
		if(user==null)
		{
            System.out.println(RED + "\n❌ User with this username does not exist. Please try again!" + RESET);		}
		else{
			if(!user.getPassword().equals(password)){
            System.out.println(RED + "\n❌ Incorrect Password. Please try again!" + RESET);
				return null;
			}
		}
		return user;
	}
}
