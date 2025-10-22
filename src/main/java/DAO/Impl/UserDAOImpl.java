package DAO.Impl;

import DAO.UserDAO;
import Exceptions.UserNotFoundException;
import Models.User;
import util.DBconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {
	
	// Add a new user to the database
	public void addUserWithoutEmail(User user) {
		String sql = "INSERT INTO users (UserName, Password, Role) VALUES (?, ?, ?)";
		try (Connection connection = DBconnection.getConnection();
		     PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setString(1, user.getUserName());
			preparedStatement.setString(2, user.getPassword());
			preparedStatement.setString(3, user.getRole());

			preparedStatement.executeUpdate();
			System.out.println("✅ User added successfully!");


		} catch (SQLException e) {
			System.out.println("❌ Error while adding user: " + e.getMessage());
		}
	}
	@Override
	public void addUser(User user) {
		String sql = "INSERT INTO users (UserName, Password, Role,status,email) VALUES (?, ?, ?,?,?)";
		try (Connection connection = DBconnection.getConnection();
		     PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setString(1, user.getUserName());
			preparedStatement.setString(2, user.getPassword());
			preparedStatement.setString(3, user.getRole());
			preparedStatement.setString(4,user.getStatus());
			preparedStatement.setString(5,user.getEmail());

			preparedStatement.executeUpdate();
			System.out.println("✅ User verified and added successfully!");


		} catch (SQLException e) {
			System.out.println("❌ Error while adding user: " + e.getMessage());
		}
	}

	// Retrieve all users from the database
	@Override
	public List<User> getAllUsers() {
		List<User> userList = new ArrayList<>();
		String sql = "SELECT * FROM users";

		try (Connection connection = DBconnection.getConnection();
		     Statement stmt = connection.createStatement();
		     ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				User user = new User();
				user.setUserId(rs.getInt("UserId"));
				user.setUserName(rs.getString("UserName"));
				user.setPassword(rs.getString("Password"));
				user.setRole(rs.getString("Role"));
				userList.add(user);
			}

		} catch (SQLException e) {
			System.out.println("❌ Error while retrieving users: " + e.getMessage());
		}

		return userList;
	}

	// Retrieve a user by ID
	@Override
	public User getUserById(int id) {
		User user = null;
		String sql = "SELECT * FROM users WHERE UserId = ?";

		try (Connection connection = DBconnection.getConnection();
		     PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setInt(1, id);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				user = new User();
				user.setUserId(rs.getInt("UserId"));
				user.setUserName(rs.getString("UserName"));
				user.setPassword(rs.getString("Password"));
				user.setRole(rs.getString("Role"));
			}

		} catch (SQLException e) {
			System.out.println("❌ Error while retrieving user: " + e.getMessage());
		}

		return user;
	}
    public User getUserByUserName(String name) {
		User user = null;
		String sql = "SELECT * FROM users WHERE UserName = ?";

		try (Connection connection = DBconnection.getConnection();
		     PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setString(1, name);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				user = new User();
				user.setUserId(rs.getInt("UserId"));
				user.setUserName(rs.getString("UserName"));
				user.setPassword(rs.getString("Password"));
				user.setRole(rs.getString("Role"));
				user.setEmail(rs.getString("email"));
				user.setStatus(rs.getString("status"));
			}
            else {
                throw new UserNotFoundException("User with this username does not exist");
            }

		} catch (SQLException e) {
			System.out.println("❌ Error while retrieving user: " + e.getMessage());
		}
        catch (UserNotFoundException e){}

		return user;
	}

	// Delete user by ID
	@Override
	public void deleteUserById(int id) {
		String sql = "DELETE FROM users WHERE UserId = ?";

		try (Connection connection = DBconnection.getConnection();
		     PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setInt(1, id);
			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("🗑️ User with ID " + id + " deleted successfully!");
			} else {
				System.out.println("⚠️ No user found with ID " + id);
			}

		} catch (SQLException e) {
			System.out.println("❌ Error while deleting user: " + e.getMessage());
		}
	}

	@Override
	public void addVerification(String userName, String status, String email) {
		String sql = "UPDATE users SET status = ?, email = ? WHERE UserName = ?";

		try (Connection connection = DBconnection.getConnection();
		     PreparedStatement stmt = connection.prepareStatement(sql)) {

			// Set values dynamically
			stmt.setString(1, status);
			stmt.setString(2, email);
			stmt.setString(3, userName);

			int rowsUpdated = stmt.executeUpdate();

		} catch (SQLException e) {
			System.err.println("❌ Error updating verification details: " + e.getMessage());
		}
	}

}
