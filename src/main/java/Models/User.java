package Models;

public class User {

	public int UserId;
	public String UserName;
	public String Password;
	public String Role;
	public String email;
	public String status;


	public User( String userName, String password, String role,String email,String status) {
		UserName = userName;
		Password = password;
		Role = role;
		this.email=email;
		this.status=status;
	}

	public User() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
