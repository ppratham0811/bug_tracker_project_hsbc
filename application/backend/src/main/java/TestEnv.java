import org.hsbc.dao.user.UserDao;
import org.hsbc.dao.user.UserDaoInterface;
import org.hsbc.exceptions.DuplicateUserException;
import org.hsbc.exceptions.UserNotFoundException;
import org.hsbc.model.User;
import org.hsbc.model.enums.UserRole;

import java.sql.Connection;

// test file for testing

public class TestEnv {
    public static void main(String[] args) throws DuplicateUserException, UserNotFoundException {

        UserDao ud = new UserDao();
        User user = new User("ppratham0811", "Prathmesh", "Pratham", "ppratham0811@gmail.com", UserRole.MANAGER);
        //        ud.registerUser(user);
        User returnedUser = ud.loginUser(user.getUsername(), user.getUserPassword());
        System.out.println(returnedUser.getUserId());
    }
}
