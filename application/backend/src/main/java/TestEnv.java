import org.hsbc.db.JdbcConnector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// example file for jdbc connection

public class TestEnv {
    static Connection con = null;
    public static void main(String[] args) {
        try {
            con = JdbcConnector.getConnectionObject();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from users");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString(2));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
