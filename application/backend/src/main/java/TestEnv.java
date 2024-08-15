import org.hsbc.dao.manager.ManagerDao;
import org.hsbc.db.JdbcConnector;
import org.hsbc.model.Project;
import org.hsbc.model.enums.ProjectOrBugStatus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;

// example file for jdbc connection

public class TestEnv {
    static Connection con = null;
    public static void main(String[] args) {
        ManagerDao md = new ManagerDao();
//        LocalDate date = LocalDate.parse("2024-08-17");
        md.createNewProject(new Project("BUG TRACKER", 100001, LocalDate.now(), ProjectOrBugStatus.COMPLETED));

//        System.out.println(LocalDateTime.now());

    }
}
