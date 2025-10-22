package DAO;
import Models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import util.DBconnection;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestMockUser {

    @InjectMocks
    private UserDAO userDao;

    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private MockedStatic<DBconnection> mockedDbConnection;

    @BeforeEach
    void setUp() throws SQLException {
        mockedDbConnection = Mockito.mockStatic(DBconnection.class);
        mockedDbConnection.when(DBconnection::getConnection).thenReturn(mockConnection);
    }

    @AfterEach
    void tearDown() {
        mockedDbConnection.close();
    }

    @Test
    void testGetAllUsers_Success() throws SQLException {
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, true, false);

        when(mockResultSet.getInt("UserId")).thenReturn(1, 2);
        when(mockResultSet.getString("UserName")).thenReturn("alice", "bob");
        when(mockResultSet.getString("Password")).thenReturn("pass1", "pass2");
        when(mockResultSet.getString("Role")).thenReturn("admin", "user");

        List<User> users = userDao.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());

        assertEquals(1, users.get(0).getUserId());
        assertEquals("alice", users.get(0).getUserName());

        assertEquals(2, users.get(1).getUserId());
        assertEquals("bob", users.get(1).getUserName());

        verify(mockResultSet).close();
        verify(mockStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void testGetAllUsers_SQLException() throws SQLException {
        when(mockConnection.createStatement()).thenThrow(new SQLException("Test DB Error"));

        List<User> users = userDao.getAllUsers();

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void testGetUserById_Success() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("UserId")).thenReturn(10);
        when(mockResultSet.getString("UserName")).thenReturn("charlie");
        when(mockResultSet.getString("Password")).thenReturn("pass3");
        when(mockResultSet.getString("Role")).thenReturn("user");

        User user = userDao.getUserById(10);

        assertNotNull(user);
        assertEquals(10, user.getUserId());
        assertEquals("charlie", user.getUserName());

        verify(mockPreparedStatement).setInt(1, 10);

        verify(mockResultSet).close();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void testGetUserById_NotFound() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(false);

        User user = userDao.getUserById(99);

        assertNull(user);
        verify(mockPreparedStatement).setInt(1, 99);
    }

    @Test
    void testGetUserByUserName_Success() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("UserId")).thenReturn(11);
        when(mockResultSet.getString("UserName")).thenReturn("rohan");
        when(mockResultSet.getString("Password")).thenReturn("pass4");
        when(mockResultSet.getString("Role")).thenReturn("guest");
        when(mockResultSet.getString("email")).thenReturn("rohan@example.com");
        when(mockResultSet.getString("status")).thenReturn("active");

        User user = userDao.getUserByUserName("rohan");

        assertNotNull(user);
        assertEquals(11, user.getUserId());
        assertEquals("rohan", user.getUserName());
        assertEquals("rohan@example.com", user.getEmail());
        assertEquals("active", user.getStatus());

        verify(mockPreparedStatement).setString(1, "rohan");
    }

    @Test
    void testGetUserByUserName_NotFound() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(false);

        User user = userDao.getUserByUserName("randomUser");

        assertNull(user);

        verify(mockPreparedStatement).setString(1, "randomUser");
    }
}