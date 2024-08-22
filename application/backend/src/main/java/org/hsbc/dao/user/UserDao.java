package org.hsbc.dao.user;

import org.hsbc.db.JdbcConnector;
import org.hsbc.exceptions.*;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;
import org.hsbc.model.enums.BugStatus;
import org.hsbc.model.enums.ProjectStatus;
import org.hsbc.model.enums.SeverityLevel;
import org.hsbc.model.enums.UserRole;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UserDao implements UserDaoInterface {
    // variables required for password hashing (algorithm used: PKDF5)
    private final int iterations = 100, keyLength = 512;

    // this method will return the hashed password as a bytes array
    private byte[] hashPassword(final char[] password, final int iterations, final int keyLength) {
        Properties props = new Properties();
        Path envFile = Paths.get(".env");

        try (InputStream inputStream = Files.newInputStream(envFile)) {
            props.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] salt = ((String) props.get("HASHSALT")).getBytes();

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return res;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerUser(User user) throws DuplicateUserException {

        String registerQuery = "INSERT INTO users (username, full_name, user_password, user_email, user_role) VALUES (?,?,?,?,?)";

        Connection con = null;

        try {
            con = JdbcConnector.getInstance().getConnectionObject();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement ps = con.prepareStatement(registerQuery)) {

            con.setAutoCommit(false);

            // storing password hash in db
            char[] passwordChars = user.getUserPassword().toCharArray();

            byte[] hashedBytes = hashPassword(passwordChars, iterations, keyLength);
            String hashedString = Hex.encodeHexString(hashedBytes);
            ps.setString(3, hashedString);

            // storing other details
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFullName());

            ps.setString(4, user.getUserEmail());
            ps.setString(5, user.getUserRole().name());

            ps.executeUpdate();
            con.commit();

            System.out.println("User registered successfully.");
        } catch (SQLIntegrityConstraintViolationException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new DuplicateUserException("User already exists.");
        } catch (SQLException e2) {
            throw new RuntimeException("Error during user registration", e2);
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

            char[] passwordChars = password.toCharArray();

            byte[] hashedBytes = hashPassword(passwordChars, iterations, keyLength);
            String hashedString = Hex.encodeHexString(hashedBytes);
            ps.setString(2, hashedString);

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
    public List<Project> readAssignedProjects(User user) throws NoAssignedProjectException {
        // String getProjectDetailsQuery = "SELECT p.project_name, u_manager.full_name
        // AS project_manager, p.start_date, " +
        // "GROUP_CONCAT(u_member.full_name SEPARATOR ', ') AS members " +
        // "FROM projects p " +
        // "JOIN user_projects up_manager ON p.project_manager = up_manager.user_id " +
        // "JOIN users u_manager ON p.project_manager = u_manager.user_id " +
        // "JOIN user_projects up_member ON p.project_id = up_member.project_id " +
        // "JOIN users u_member ON up_member.user_id = u_member.user_id " +
        // "WHERE u_member.user_id = " + user.getUserId() +
        // " GROUP BY p.project_id, u_manager.full_name, p.start_date";
        List<Project> allAssignedProjects = new ArrayList<>();
        String getUserProjectsJoinQuery = "SELECT * FROM user_projects up INNER JOIN projects p ON up.project_id = p.project_id WHERE up.user_id = "
                + user.getUserId();
        Connection con = null;

        Project projectDetails = new Project();
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement selectPS = con.prepareStatement(getUserProjectsJoinQuery);
             ResultSet rs = selectPS.executeQuery();) {
            if (rs == null) {
                throw new NoAssignedProjectException("No Project Assigned");
            }
            while (rs.next()) {
                int projectId = rs.getInt("project_id");
                String projectName = rs.getString("project_name");
                String projectStatus = rs.getString("project_status");
                int projectManager = rs.getInt("project_manager");
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                int noOfBugs = rs.getInt("no_of_bugs");

                projectDetails.setProjectId(projectId);
                projectDetails.setProjectName(projectName);
                projectDetails.setProjectStatus(ProjectStatus.valueOf(projectStatus.toUpperCase()));
                projectDetails.setProjectManager(projectManager);
                projectDetails.setStartDate(startDate);
                projectDetails.setNoOfBugs(noOfBugs);

                allAssignedProjects.add(projectDetails);
            }
            return allAssignedProjects;
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public User getUserDetails(int userId) throws UserNotFoundException {
        String detailsQuery = "select user_id, username, full_name, user_email, user_role, project_count from users where user_id = "
                + userId;
        Connection con = null;
        User user = new User();
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement selectPS = con.prepareStatement(detailsQuery);
             ResultSet rs = selectPS.executeQuery()) {
            if (rs == null) {
                throw new UserNotFoundException("Invalid user id");
            } else if (rs.next()) {
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                String email = rs.getString("user_email");
                String userRole = rs.getString("user_role");
                int projectCount = rs.getInt("project_count");

                user.setUsername(username);
                user.setFullName(fullName);
                user.setUserEmail(email);
                user.setUserRole(UserRole.valueOf(userRole.toUpperCase()));
                user.setProjectCount(projectCount);

                // System.out.println("User Id : " + userId);
                // System.out.println("User Name : " + userName);
                // System.out.println("Full Name : " + fullName);
                // System.out.println("User Email : " + email);
                // System.out.println("User Role : " + userRole);
                // System.out.println("Project Count : " + projectCount);
                return user;
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    // @Override
    // public void readProjectDetails(Project project) throws
    // ProjectNotFoundException {
    // String projectDetailsQuery = "select * from projects where project_id = " +
    // project.getProjectId();
    // Connection con = null;
    // try {
    // con = JdbcConnector.getInstance().getConnectionObject();
    // con.setAutoCommit(false);
    // } catch (SQLException e) {
    // throw new RuntimeException(e);
    // }
    // try (PreparedStatement selectPS = con.prepareStatement(projectDetailsQuery);
    // ResultSet rs = selectPS.executeQuery()) {
    // if (rs == null) {
    // throw new ProjectNotFoundException("Invalid project id");
    // }
    // while (rs.next()) {
    // int projectId = rs.getInt("project_id");
    // String projectName = rs.getString("project_name");
    // String projectStatus = rs.getString("project_status");
    // int projectManagerId = rs.getInt("project_manager");
    // String startDate = rs.getString("start_date");
    // int numberOfBugs = rs.getInt("no_of_bugs");
    //
    // System.out.println("Project Id : " + projectId);
    // System.out.println("Project Name : " + projectName);
    // System.out.println("Project Status : " + projectStatus);
    // System.out.println("Project Manager User Id : " + projectManagerId);
    // System.out.println("Start Date : " + startDate);
    // System.out.println("Number of Bugs : " + numberOfBugs);
    // }
    // } catch (SQLException se) {
    // try {
    // con.rollback();
    // } catch (SQLException e) {
    // throw new RuntimeException(e);
    // }
    // System.out.println(se.getMessage());
    // } finally {
    // try {
    // con.setAutoCommit(true);
    // con.close();
    // } catch (SQLException e) {
    // throw new RuntimeException(e);
    // }
    // }
    // }

    @Override
    public List<Integer> getProjectMembers(Project project) throws ProjectNotFoundException {
        Connection con = null;
        List<Integer> allMembers = new ArrayList<>();
        String getUsersQuery = "SELECT user_id FROM user_projects WHERE project_id = " + project.getProjectId();

        try {
            con = JdbcConnector.getInstance().getConnectionObject();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        try (PreparedStatement getUsersPS = con.prepareStatement(getUsersQuery);
             ResultSet rs = getUsersPS.executeQuery()) {
            if (rs == null) {
                throw new ProjectNotFoundException("Project not found");
            } else {
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    allMembers.add(userId);
                }

                return allMembers;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Bug getBugDetails(int bugId) throws NoSuchBugException {
        String bugDetailsQuery = "select * from bugs where bug_id = " + bugId;
        Connection con = null;
        Bug bug = new Bug();
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement selectPS = con.prepareStatement(bugDetailsQuery);
             ResultSet rs = selectPS.executeQuery()) {
            if (rs == null) {
                throw new NoSuchBugException("You Have No Bugs");
            } else if (rs.next()) {
                String bugName = rs.getString("bug_title");
                String bugDes = rs.getString("bug_description");
                String bugStatus = rs.getString("bug_status");
                int createdBy = rs.getInt("created_by");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                String severityLevel = rs.getString("severity_level");
                int projectId = rs.getInt("project_id");
                boolean acceptStatus = rs.getBoolean("accepted");

                bug.setBugName(bugName);
                bug.setBugDescription(bugDes);
                bug.setBugStatus(BugStatus.valueOf(bugStatus.toUpperCase()));
                bug.setCreatedBy(createdBy);
                bug.setCreatedOn(createdAt);
                bug.setSeverityLevel(SeverityLevel.valueOf(severityLevel.toUpperCase()));
                bug.setProjectId(projectId);
                bug.setAccepted(acceptStatus);

                // System.out.println("Bug Id : " + bugId);
                // System.out.println("Bug Name : " + bugName);
                // System.out.println("Id of Project Present In : " + projectId);
                // System.out.println("Bug Description : " + bugDes);
                // System.out.println("Bug Status : " + bugStatus);
                // System.out.println("Security Level : " + severityLevel);
                // System.out.println("User Id of Creator : " + createdBy);
                // System.out.println("Created At : " + createdAt);
                // System.out.println("Acceptance Status : " + acceptStatus);
                return bug;
            }
        } catch (SQLException se) {
            System.out.println(se.getMessage());
            throw new RuntimeException(se);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return bug;
    }
}
