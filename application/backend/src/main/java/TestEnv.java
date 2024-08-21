import org.hsbc.dao.user.UserDao;
import org.hsbc.dao.user.UserDaoInterface;
import org.hsbc.exceptions.DuplicateUserException;
import org.hsbc.model.User;
import org.hsbc.model.enums.UserRole;

import java.sql.Connection;

// test file for testing

public class TestEnv {
    static Connection con = null;

    public static void main(String[] args) {
<<<<<<< Updated upstream
//        ManagerDao md = new ManagerDao();
//        LocalDate date = LocalDate.parse("2025-01-10");
//        Project p = new Project("moneymanager", 100002, date, ProjectOrBugStatus.IN_PROGRESS);
=======
        ManagerDao md = new ManagerDao();
        LocalDate date = LocalDate.parse("2025-01-10");
        Project p = new Project("moneymanager", 100002, date);
>>>>>>> Stashed changes
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
