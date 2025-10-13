package DAO;

import Models.User;

import java.util.List;

public interface UserDAO {
	public void addUser(User user);
	public List<User> getAllUsers();
	public User getUserById(int id);
	public User getUserByUserName(String name);
	public void deleteUserById(int id);


}
