package org.hsbc.dao.user;

import org.hsbc.db.JdbcConnector;
import org.hsbc.exceptions.DuplicateUserException;
import org.hsbc.model.User;
import org.hsbc.exceptions.UserNotFoundException;
import org.hsbc.model.enums.UserRole;

import java.sql.*;

public class UserDao implements UserDaoInterface {

    @Override
    public void registerUser(User user) throws DuplicateUserException{
        String registerQuery = "INSERT INTO users (username, full_name, user_password, user_email, user_role) VALUES (?,?,?,?,?)";
        try (Connection con = JdbcConnector.getInstance().getConnectionObject();
             PreparedStatement ps = con.prepareStatement(registerQuery)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getUserPassword());
            ps.setString(4, user.getUserEmail());
            ps.setString(5, user.getUserRole().name());

            ps.executeUpdate();

            System.out.println("User registered successfully.");
        }
        catch(SQLIntegrityConstraintViolationException e){
            throw new DuplicateUserException("User already exists. ");
        }
        catch (SQLException e2) {
            throw new RuntimeException("Error during user registration", e2);
        }
    }

    @Override
    public User loginUser(String username, String password) throws UserNotFoundException {
        String loginQuery = "SELECT * FROM users WHERE username = ? AND user_password = ?";
        try (Connection con = JdbcConnector.getInstance().getConnectionObject();
             PreparedStatement ps = con.prepareStatement(loginQuery)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUsername(rs.getString("username"));
                    user.setFullName(rs.getString("full_name"));
                    user.setUserPassword(rs.getString("user_password"));
                    user.setUserEmail(rs.getString("user_email"));
                    user.setUserRole(UserRole.valueOf(rs.getString("user_role")));

                    System.out.println("Login successful.");
                    return user;
                } else {
                    throw new UserNotFoundException("Invalid username or password.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error during user login", e);
        }
    }
}
