package org.hsbc.dao.user;

import org.hsbc.db.JdbcConnector;
import org.hsbc.exceptions.*;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;
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
    public void readAssignedProjects(User user) throws NoAssignedProjectException {
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
                throw new NoAssignedProjectException("No Project Assigned");
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

    @Override
    public void readDetails(User user) throws UserNotFoundException {
        String detailsQuery = "select user_id, username, full_name, user_email, user_role, project_count from users where user_id = "+user.getUserId();
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement selectPS = con.prepareStatement(detailsQuery);
             ResultSet rs = selectPS.executeQuery()) {
            if (rs == null) {
                throw new UserNotFoundException("Invalid user id");
            }
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String userName = rs.getString("username");
                String fullName = rs.getString("full_name");
                String email = rs.getString("user_email");
                String userRole = rs.getString("user_role");
                int projectCount = rs.getInt("project_count");

                System.out.println("User Id : "+userId);
                System.out.println("User Name : "+userName);
                System.out.println("Full Name : "+fullName);
                System.out.println("User Email : "+email);
                System.out.println("User Role : "+userRole);
                System.out.println("Project Count : "+projectCount);
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

    @Override
    public void readProjectDetails(Project project) throws ProjectNotFoundException {
        String projectDetailsQuery = "select * from projects where project_id = "+project.getProjectId();
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement selectPS = con.prepareStatement(projectDetailsQuery);
             ResultSet rs = selectPS.executeQuery()) {
            if (rs == null) {
                throw new ProjectNotFoundException("Invalid project id");
            }
            while (rs.next()) {
                int projectId = rs.getInt("project_id");
                String projectName = rs.getString("project_name");
                String projectStatus = rs.getString("project_status");
                int projectManagerId = rs.getInt("project_manager");
                String startDate = rs.getString("start_date");
                int numberOfBugs = rs.getInt("no_of_bugs");

                System.out.println("Project Id : "+projectId);
                System.out.println("Project Name : "+projectName);
                System.out.println("Project Status : "+projectStatus);
                System.out.println("Project Manager User Id : "+projectManagerId);
                System.out.println("Start Date : "+startDate);
                System.out.println("Number of Bugs : "+numberOfBugs);
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

    @Override
    public void readAssignedBugs(User user) throws NoBugsAssignedException {
        String bugsQuery = "SELECT b.bug_id, b.bug_title, b.bug_description, b.bug_status, b.created_by, b.created_at, b.security_level, b.project_id" +
                "FROM bugs b JOIN users u ON b.assigned_to = u.user_id" +
                "WHERE u.user_id = "+user.getUserId();
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement selectPS = con.prepareStatement(bugsQuery);
             ResultSet rs = selectPS.executeQuery()) {
            if (rs == null) {
                throw new NoBugsAssignedException("You Have No Bugs");
            }
            while (rs.next()) {
                int bugId= rs.getInt("bug_id");
                String bugName = rs.getString("bug_title");
                String bugDes = rs.getString("bug_description");
                String bugStatus = rs.getString("bug_status");
                int createdBy = rs.getInt("created_by");
                String createdAt = rs.getString("created_at");
                String securityLevel = rs.getString("security_level");
                int projectId = rs.getInt("project_id");

                System.out.println("Bug Id : "+bugId);
                System.out.println("Bug Name : "+bugName);
                System.out.println("Id of Project Present In : "+projectId);
                System.out.println("Bug Description : "+bugDes);
                System.out.println("Bug Status : "+bugStatus);
                System.out.println("Security Level : "+securityLevel);
                System.out.println("User Id of Creator : "+createdBy);
                System.out.println("Created At : "+createdAt);
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

    @Override
    public void readBugDetails(Bug bug) throws NoSuchBugException {
        String bugDetailsQuery = "select * from bugs where bug_id = "+bug.getBugId();
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement selectPS = con.prepareStatement(bugDetailsQuery);
             ResultSet rs = selectPS.executeQuery()) {
            if (rs == null) {
                throw new NoSuchBugException("You Have No Bugs");
            }
            while (rs.next()) {
                int bugId= rs.getInt("bug_id");
                String bugName = rs.getString("bug_title");
                String bugDes = rs.getString("bug_description");
                String bugStatus = rs.getString("bug_status");
                int createdBy = rs.getInt("created_by");
                String createdAt = rs.getString("created_at");
                String securityLevel = rs.getString("security_level");
                int projectId = rs.getInt("project_id");
                boolean acceptStatus = rs.getBoolean("accepted");

                System.out.println("Bug Id : "+bugId);
                System.out.println("Bug Name : "+bugName);
                System.out.println("Id of Project Present In : "+projectId);
                System.out.println("Bug Description : "+bugDes);
                System.out.println("Bug Status : "+bugStatus);
                System.out.println("Security Level : "+securityLevel);
                System.out.println("User Id of Creator : "+createdBy);
                System.out.println("Created At : "+createdAt);
                System.out.println("Acceptance Status : "+acceptStatus);
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
