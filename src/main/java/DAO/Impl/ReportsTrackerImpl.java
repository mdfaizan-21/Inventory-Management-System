package DAO.Impl;

import DAO.ReportsTracker;
import util.DBconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReportsTrackerImpl implements ReportsTracker {
	@Override
	public void addReports(String userName, String email, String DateTime) {
		String sql="INSERT into REPORTS(userName,email,dateAndTime) VALUES(?,?,?)";
		try{
			Connection connection= DBconnection.getConnection();
			PreparedStatement preparedStatement= connection.prepareStatement(sql);
			preparedStatement.setString(1,userName);
			preparedStatement.setString(2,email);
			preparedStatement.setString(3,DateTime);

			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
