package DAO;

import DAO.Impl.UserDAOImpl;
import Exceptions.UserNotFoundException;
import Models.Product;
import Models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestUserDAO {
    UserDAO userDAO;
    @BeforeEach
    void setUserDAO(){
        userDAO=new UserDAOImpl();
    }
    @Test
    void testAddUser() {
        User user=new User("nitesh","pzb123","user",null,null);
        userDAO.addUser(user);
        User user1=userDAO.getUserByUserName("nitesh");
        assertEquals("nitesh",user1.getUserName());
        assertEquals("pzb123",user1.getPassword());
        assertEquals("user",user1.getRole());

    }
    @Test
    void testGetUser() {
        User user1=userDAO.getUserByUserName("nitesh");
        assertEquals("nitesh",user1.getUserName());
        assertEquals("pzb123",user1.getPassword());
        assertEquals("user",user1.getRole());

    }
    @Test
    void testUserNotFound() {
        UserNotFoundException exception=assertThrows(UserNotFoundException.class,
                ()->userDAO.getUserByUserName("Alex"));
        assertEquals("User with this username does not exist", exception.getMessage());

    }
    @Test
    void testGetAllUser() {
        List<User>list=userDAO.getAllUsers();
        assertEquals(5,list.size());
        User user1=list.get(0);
        assertEquals("faizan",user1.getUserName());
        assertEquals("abc123",user1.getPassword());
        assertEquals("ADMIN",user1.getRole());
    }


}
