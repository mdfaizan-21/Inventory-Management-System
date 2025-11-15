package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    // DB-URL=jdbc:mysql://localhost:3306/product_tracker;USERNAME=springstudent;PASSWORD=Faizan@1234;MAIL_USER=d18227518@gmail.com;MAIL_PASS=veye
    // vrvc nzgw qduv
    private static final String url = "jdbc:mysql://localhost:3306/product_tracker";
    private static final String username = "springstudent";
    private static final String password = "Faizan@1234";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

}
