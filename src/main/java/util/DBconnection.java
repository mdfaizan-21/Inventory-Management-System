package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {

    private static  final  String url=System.getenv("DB-URL");
    private static  final  String username=System.getenv("USERNAME");
    private static  final  String password=System.getenv("PASSWORD");

    public static Connection getConnection()throws SQLException{
        return DriverManager.getConnection(url,username,password);
    }


}
