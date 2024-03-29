package lk.ijse.dep10.studentRegister.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection dbConnection;
    private final Connection connection;

    private DBConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://dep10.lk/dep10_studentregister", "root", "mysql");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static DBConnection getInstance() {
        return (dbConnection == null) ? dbConnection = new DBConnection() : dbConnection;

    }

    public Connection getConnection() {
        return connection;
    }
}
