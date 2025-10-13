package Models;

public class User {

	public int UserId;
	public String UserName;
	public String Password;
	public String Role;

	public User( String userName, String password, String role) {
		UserName = userName;
		Password = password;
		Role = role;
	}

	public User() {
	}


	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public int getUserId() {
		return UserId;
	}

	public void setUserId(int userId) {
		UserId = userId;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getRole() {
		return Role;
	}


	public void setRole(String role) {
		Role = role;
	}

	@Override
	public String toString() {
		return "User{" + "UserId=" + UserId + ", UserName='" + UserName + '\'' + ", Password='" + Password + '\'' + ", Role='" + Role + '\'' + '}';
	}


}
