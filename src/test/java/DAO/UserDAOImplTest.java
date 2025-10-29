package DAO;

import DAO.Impl.UserDAOImpl;
import Models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import util.DBconnection;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDAOImplTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private UserDAOImpl userDAO;

    private MockedStatic<DBconnection> dbConnectionMock;

    @BeforeEach
    void setUp() throws SQLException {
        dbConnectionMock = mockStatic(DBconnection.class);
        dbConnectionMock.when(DBconnection::getConnection).thenReturn(mockConnection);
    }

    @AfterEach
    void tearDown() {
        if (dbConnectionMock != null) {
            dbConnectionMock.close();
        }
    }

    // ==================== addUserWithoutEmail Tests ====================

    @Test
    void testAddUserWithoutEmail_Success() throws SQLException {
        
        User user = new User();
        user.setUserName("john_doe");
        user.setPassword("password123");
        user.setRole("user");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        userDAO.addUserWithoutEmail(user);

        // Assert
        verify(mockPreparedStatement).setString(1, "john_doe");
        verify(mockPreparedStatement).setString(2, "password123");
        verify(mockPreparedStatement).setString(3, "user");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testAddUserWithoutEmail_SQLException() throws SQLException {

        User user = new User();
        user.setUserName("jane_doe");
        user.setPassword("pass456");
        user.setRole("admin");

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        // Act & Assert - Should handle exception gracefully
        assertDoesNotThrow(() -> userDAO.addUserWithoutEmail(user));
    }

    @Test
    void testAddUserWithoutEmail_NullConnection() throws SQLException {

        User user = new User();
        user.setUserName("test_user");
        user.setPassword("test123");
        user.setRole("user");

        dbConnectionMock.when(DBconnection::getConnection).thenThrow(new SQLException("Connection failed"));

        // Act & Assert
        assertDoesNotThrow(() -> userDAO.addUserWithoutEmail(user));
    }

    // ==================== addUser Tests ====================

    @Test
    void testAddUser_Success() throws SQLException {

        User user = new User("alice", "alice123", "admin", "alice@example.com", "verified");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        userDAO.addUser(user);

        // Assert
        verify(mockPreparedStatement).setString(1, "alice");
        verify(mockPreparedStatement).setString(2, "alice123");
        verify(mockPreparedStatement).setString(3, "admin");
        verify(mockPreparedStatement).setString(4, "verified");
        verify(mockPreparedStatement).setString(5, "alice@example.com");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testAddUser_WithPendingStatus() throws SQLException {

        User user = new User("bob", "bob456", "user", "bob@example.com", "pending");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        userDAO.addUser(user);

        // Assert
        verify(mockPreparedStatement).setString(4, "pending");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testAddUser_SQLException() throws SQLException {

        User user = new User("error_user", "pass", "user", "error@example.com", "verified");
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Insert failed"));

        // Act & Assert
        assertDoesNotThrow(() -> userDAO.addUser(user));
    }

    // ==================== getAllUsers Tests ====================

    @Test
    void testGetAllUsers_Success() throws SQLException {

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);

        when(mockResultSet.getInt("UserId")).thenReturn(1, 2, 3);
        when(mockResultSet.getString("UserName")).thenReturn("user1", "user2", "user3");
        when(mockResultSet.getString("Password")).thenReturn("pass1", "pass2", "pass3");
        when(mockResultSet.getString("Role")).thenReturn("admin", "user", "user");

        // Act
        List<User> users = userDAO.getAllUsers();

        // Assert
        assertNotNull(users);
        assertEquals(3, users.size());
        assertEquals("user1", users.get(0).getUserName());
        assertEquals("user2", users.get(1).getUserName());
        assertEquals("user3", users.get(2).getUserName());
        assertEquals("admin", users.get(0).getRole());
        assertEquals("user", users.get(1).getRole());
    }

    @Test
    void testGetAllUsers_EmptyList() throws SQLException {

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // Act
        List<User> users = userDAO.getAllUsers();

        // Assert
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void testGetAllUsers_SQLException() throws SQLException {

        when(mockConnection.createStatement()).thenThrow(new SQLException("Query failed"));

        // Act
        List<User> users = userDAO.getAllUsers();

        // Assert
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    // ==================== getUserById Tests ====================

    @Test
    void testGetUserById_Success() throws SQLException {

        int userId = 101;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getInt("UserId")).thenReturn(101);
        when(mockResultSet.getString("UserName")).thenReturn("john_doe");
        when(mockResultSet.getString("Password")).thenReturn("secure123");
        when(mockResultSet.getString("Role")).thenReturn("admin");

        // Act
        User user = userDAO.getUserById(userId);

        // Assert
        assertNotNull(user);
        assertEquals(101, user.getUserId());
        assertEquals("john_doe", user.getUserName());
        assertEquals("secure123", user.getPassword());
        assertEquals("admin", user.getRole());
        verify(mockPreparedStatement).setInt(1, userId);
    }

    @Test
    void testGetUserById_NotFound() throws SQLException {

        int userId = 999;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // Act
        User user = userDAO.getUserById(userId);

        // Assert
        assertNull(user);
    }

    @Test
    void testGetUserById_SQLException() throws SQLException {

        int userId = 101;
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Connection error"));

        // Act
        User user = userDAO.getUserById(userId);

        // Assert
        assertNull(user);
    }

    // ==================== getUserByUserName Tests ====================

    @Test
    void testGetUserByUserName_Success() throws SQLException {

        String userName = "alice";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getInt("UserId")).thenReturn(201);
        when(mockResultSet.getString("UserName")).thenReturn("alice");
        when(mockResultSet.getString("Password")).thenReturn("alice123");
        when(mockResultSet.getString("Role")).thenReturn("user");
        when(mockResultSet.getString("email")).thenReturn("alice@example.com");
        when(mockResultSet.getString("status")).thenReturn("verified");

        // Act
        User user = userDAO.getUserByUserName(userName);

        // Assert
        assertNotNull(user);
        assertEquals(201, user.getUserId());
        assertEquals("alice", user.getUserName());
        assertEquals("alice123", user.getPassword());
        assertEquals("user", user.getRole());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("verified", user.getStatus());
        verify(mockPreparedStatement).setString(1, userName);
    }

    @Test
    void testGetUserByUserName_NotFound() throws SQLException {

        String userName = "nonexistent";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // Act - The implementation catches UserNotFoundException internally
        User user = userDAO.getUserByUserName(userName);

        // Assert - User should be null
        assertNull(user);
    }

    @Test
    void testGetUserByUserName_SQLException() throws SQLException {

        String userName = "alice";
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        // Act
        User user = userDAO.getUserByUserName(userName);

        // Assert
        assertNull(user);
    }

    @Test
    void testGetUserByUserName_WithPendingStatus() throws SQLException {

        String userName = "bob";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getInt("UserId")).thenReturn(202);
        when(mockResultSet.getString("UserName")).thenReturn("bob");
        when(mockResultSet.getString("Password")).thenReturn("bob456");
        when(mockResultSet.getString("Role")).thenReturn("user");
        when(mockResultSet.getString("email")).thenReturn("bob@example.com");
        when(mockResultSet.getString("status")).thenReturn("pending");

        // Act
        User user = userDAO.getUserByUserName(userName);

        // Assert
        assertNotNull(user);
        assertEquals("pending", user.getStatus());
    }

    // ==================== deleteUserById Tests ====================

    @Test
    void testDeleteUserById_Success() throws SQLException {

        int userId = 101;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        userDAO.deleteUserById(userId);

        // Assert
        verify(mockPreparedStatement).setInt(1, userId);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testDeleteUserById_UserNotExists() throws SQLException {

        int userId = 999;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // Act
        userDAO.deleteUserById(userId);

        // Assert
        verify(mockPreparedStatement).setInt(1, userId);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testDeleteUserById_SQLException() throws SQLException {

        int userId = 101;
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Delete failed"));

        // Act & Assert
        assertDoesNotThrow(() -> userDAO.deleteUserById(userId));
    }

    @Test
    void testDeleteUserById_MultipleRowsDeleted() throws SQLException {

        int userId = 101;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(2); // Edge case: multiple rows

        // Act
        userDAO.deleteUserById(userId);

        // Assert
        verify(mockPreparedStatement).setInt(1, userId);
        verify(mockPreparedStatement).executeUpdate();
    }

    // ==================== addVerification Tests ====================

    @Test
    void testAddVerification_Success() throws SQLException {

        String userName = "alice";
        String status = "verified";
        String email = "alice@example.com";

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        userDAO.addVerification(userName, status, email);

        // Assert
        verify(mockPreparedStatement).setString(1, status);
        verify(mockPreparedStatement).setString(2, email);
        verify(mockPreparedStatement).setString(3, userName);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testAddVerification_PendingStatus() throws SQLException {

        String userName = "bob";
        String status = "pending";
        String email = "bob@example.com";

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        userDAO.addVerification(userName, status, email);

        // Assert
        verify(mockPreparedStatement).setString(1, "pending");
        verify(mockPreparedStatement).setString(2, "bob@example.com");
        verify(mockPreparedStatement).setString(3, "bob");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testAddVerification_NoRowsUpdated() throws SQLException {

        String userName = "nonexistent";
        String status = "verified";
        String email = "none@example.com";

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // Act
        userDAO.addVerification(userName, status, email);

        // Assert
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testAddVerification_SQLException() throws SQLException {

        String userName = "alice";
        String status = "verified";
        String email = "alice@example.com";

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Update failed"));

        // Act & Assert
        assertDoesNotThrow(() -> userDAO.addVerification(userName, status, email));
    }

    @Test
    void testAddVerification_NullEmail() throws SQLException {

        String userName = "test_user";
        String status = "verified";
        String email = null;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        userDAO.addVerification(userName, status, email);

        // Assert
        verify(mockPreparedStatement).setString(1, status);
        verify(mockPreparedStatement).setString(2, null);
        verify(mockPreparedStatement).setString(3, userName);
    }

    @Test
    void testAddVerification_EmptyEmail() throws SQLException {

        String userName = "test_user";
        String status = "verified";
        String email = "";

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        userDAO.addVerification(userName, status, email);

        // Assert
        verify(mockPreparedStatement).setString(2, "");
        verify(mockPreparedStatement).executeUpdate();
    }

    // ==================== Edge Cases and Integration Tests ====================

    @Test
    void testGetAllUsers_SingleUser() throws SQLException {

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);

        when(mockResultSet.getInt("UserId")).thenReturn(1);
        when(mockResultSet.getString("UserName")).thenReturn("solo_user");
        when(mockResultSet.getString("Password")).thenReturn("pass");
        when(mockResultSet.getString("Role")).thenReturn("admin");

        // Act
        List<User> users = userDAO.getAllUsers();

        // Assert
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("solo_user", users.get(0).getUserName());
    }

    @Test
    void testAddUser_AllFieldsPopulated() throws SQLException {

        User user = new User("complete_user", "complex@Pass123", "admin", "user@domain.com", "verified");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        userDAO.addUser(user);

        // Assert
        verify(mockPreparedStatement).setString(1, "complete_user");
        verify(mockPreparedStatement).setString(2, "complex@Pass123");
        verify(mockPreparedStatement).setString(3, "admin");
        verify(mockPreparedStatement).setString(4, "verified");
        verify(mockPreparedStatement).setString(5, "user@domain.com");
    }
}
