import org.hsbc.dao.manager.ManagerDao;
import org.hsbc.dao.user.UserDao;
import org.hsbc.dao.user.UserDaoInterface;
import org.hsbc.db.JdbcConnector;
import org.hsbc.exceptions.DuplicateUserException;
import org.hsbc.exceptions.ProjectLimitExceededException;
import org.hsbc.exceptions.UserNotFoundException;
import org.hsbc.exceptions.WrongProjectDateException;
import org.hsbc.model.Project;
import org.hsbc.model.User;
import org.hsbc.model.enums.ProjectOrBugStatus;
import org.hsbc.model.enums.UserRole;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;

// test file for testing

public class TestEnv {
    static Connection con = null;

    public static void main(String[] args) {
//        ManagerDao md = new ManagerDao();
//        LocalDate date = LocalDate.parse("2025-01-10");
//        Project p = new Project("moneymanager", 100002, date, ProjectOrBugStatus.IN_PROGRESS);
//        User u = new User("prathmesh", "root", UserRole.MANAGER);

//        try {
//            md.createNewProject(p);
//        } catch (ProjectLimitExceededException e) {
//            throw new RuntimeException(e);
//        } catch (WrongProjectDateException e) {
//            throw new RuntimeException(e);
//        }

//        md.assignProject(p, u);
//        System.out.println(LocalDateTime.now());
        UserDaoInterface userDao = new UserDao();
        User user = new User("rishita","Rishita Soni","rs123","rishita@gmail.com",UserRole.DEVELOPER);

        try {
            userDao.registerUser(user);
        }catch (DuplicateUserException e){
            System.out.println(e.getMessage());
        }

//        try{
//            userDao.loginUser("rishita", "rs123");
//        }catch (UserNotFoundException e){
//            System.out.println(e.getMessage());
//        }
    }
}
