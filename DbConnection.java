import java.sql.*;

public class DbConnection {

    DbConnection() throws SQLException {
        connection = DriverManager.getConnection(
                Data.url,
                Data.user,
                Data.password
        );
    }

    static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            DbConnection con = new DbConnection();
        }
        return connection;
    }
}
