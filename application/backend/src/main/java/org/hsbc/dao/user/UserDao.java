package org.hsbc.dao.user;

import org.hsbc.db.JdbcConnector;
import org.hsbc.exceptions.DuplicateUserException;
import org.hsbc.model.User;
import org.hsbc.exceptions.UserNotFoundException;
import org.hsbc.model.enums.UserRole;

import java.sql.*;

public class UserDao implements UserDaoInterface {

    @Override
    public void registerUser(User user) throws DuplicateUserException {
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
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateUserException("User already exists. ");
        } catch (SQLException e2) {
            throw new RuntimeException("Error during user registration", e2);
        }
    }

    @Override
    public User loginUser(String username, String password) throws UserNotFoundException {
        String loginQuery = "SELECT * FROM users WHERE username = ? AND user_password = ?";
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement ps = con.prepareStatement(loginQuery)) {

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
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void readAssignedProjects(User user) {
//        String getProjectDetailsQuery = "SELECT p.project_name, u_manager.full_name AS project_manager, p.start_date, " +
//                "GROUP_CONCAT(u_member.full_name SEPARATOR ', ') AS members " +
//                "FROM projects p " +
//                "JOIN user_projects up_manager ON p.project_manager = up_manager.user_id " +
//                "JOIN users u_manager ON p.project_manager = u_manager.user_id " +
//                "JOIN user_projects up_member ON p.project_id = up_member.project_id " +
//                "JOIN users u_member ON up_member.user_id = u_member.user_id " +
//                "WHERE u_member.user_id = " + user.getUserId() +
//                " GROUP BY p.project_id, u_manager.full_name, p.start_date";

        String getUserProjectsJoinQuery = "SELECT * FROM user_projects up LEFT JOIN projects p ON up.project_id = p.project_id WHERE user_id = <userId>";
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement selectPS = con.prepareStatement(getUserProjectsJoinQuery);
             ResultSet rs = selectPS.executeQuery()) {
            if (rs == null) {
                System.out.println("no project assigned"); //we can add exception later, if needed
                return;
            }
            while (rs.next()) {
                String projectName = rs.getString("project_name");
                String projectManager = rs.getString("project_manager");
                String startDate = rs.getString("start_date");
                String members = rs.getString("members");

                System.out.println("Project Name: " + projectName);
                System.out.println("Project Manager: " + projectManager);
                System.out.println("Start Date: " + startDate);
                System.out.println("Members: " + members);
                System.out.println("-------------------------------");
            }
        } catch (SQLException se) {
            try {
                con.rollback();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            System.out.println(se.getMessage());
        } finally {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
