package org.hsbc.dao.tester;

import org.hsbc.db.JdbcConnector;
import org.hsbc.exceptions.BugsNotFoundException;
import org.hsbc.exceptions.NotAuthorizedException;
import org.hsbc.exceptions.UserNotFoundException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;
import org.hsbc.model.enums.BugStatus;
import org.hsbc.model.enums.SeverityLevel;
import org.hsbc.model.enums.UserRole;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class TesterDao implements TesterDaoInterface {
    @Override
    public boolean reportNewBug(int userId, Bug bug, Project project) throws UserNotFoundException, NotAuthorizedException {
        Connection con = null;
        boolean isTester = false;

        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String getUser = "SELECT user_role FROM users WHERE user_id = " + userId;
        try (PreparedStatement getUserPS = con.prepareStatement(getUser);
        ResultSet userRS = getUserPS.executeQuery();) {
            if (userRS == null) {
                throw new UserNotFoundException("User not found");
            }
            String userRole = userRS.getString("user_role");
            if (UserRole.valueOf(userRole.toUpperCase()) == UserRole.TESTER) {
                isTester = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (isTester) {
            String insertBugQuery = "INSERT INTO bugs (bug_name, bug_description, created_by, created_on, severity_level, bug_status, project_id) VALUES (?,?,?,?,?,?,?)";
            // bug status and project id
            String addBugQuery = "UPDATE projects SET bug_count = ? WHERE user_id = ? and project_id = ?";
            // UPDATE users SET = ? WHERE user_id = ?
            String getTesterQuery = "SELECT * FROM users WHERE user_id = ?";

            try (PreparedStatement insertPS = con.prepareStatement(insertBugQuery);
                 PreparedStatement selectPS = con.prepareStatement(getTesterQuery);
                 PreparedStatement updateBugPS = con.prepareStatement(addBugQuery)) {
                insertPS.setString(1, bug.getBugName());
                insertPS.setString(2, bug.getBugDescription());
                insertPS.setInt(3, bug.getCreatedBy());
                insertPS.setTimestamp(4, Timestamp.valueOf(bug.getCreatedOn()));
                insertPS.setString(5, String.valueOf(bug.getSeverityLevel()));
                insertPS.setString(6, String.valueOf(bug.getBugStatus()));
                insertPS.setInt(7, bug.getProjectId());
                insertPS.executeUpdate();

                selectPS.setInt(1, bug.getCreatedBy());
                selectPS.executeQuery();

                updateBugPS.setInt(1, project.getNoOfBugs() + 1);
                updateBugPS.setInt(2, bug.getCreatedBy());
                updateBugPS.setInt(3, bug.getProjectId());
                updateBugPS.executeUpdate();

                con.commit();
                return true;
            } catch (SQLException e) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            } finally {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            throw new NotAuthorizedException("Only Tester can raise bugs");
        }
        return false;
    }

    // @Override
    // public Collection<Project> viewAssignedProjects(User currentUser) {
    // String getUserQuery = "SELECT * FROM users WHERE username = ?";
    // String getProjectsQuery = "SELECT * FROM user_projects WHERE user_id = ?";
    // Connection con = null;
    //
    // try {
    // con = JdbcConnector.getInstance().getConnectionObject();
    // con.setAutoCommit(false);
    // } catch (SQLException se) {
    // se.printStackTrace();
    // }
    //
    // try (PreparedStatement getUserPS = con.prepareStatement(getUserQuery);
    // PreparedStatement selectPS = con.prepareStatement(getProjectsQuery)) {
    // getUserPS.setString(1, currentUser.getUsername());
    // ResultSet rs = getUserPS.executeQuery();
    // int userId = 0;
    // if (rs.next()) {
    // userId = rs.getInt("user_id");
    // }
    //
    // selectPS.setInt(1, userId);
    // ResultSet allProjects = selectPS.executeQuery();
    //
    //
    //
    // con.commit();
    //
    // } catch (SQLException e) {
    // throw new RuntimeException(e);
    // } finally {
    // try {
    // con.setAutoCommit(true);
    // con.close();
    // } catch (SQLException e) {
    // throw new RuntimeException(e);
    // }
    // }
    //
    //// ResultSet rs = ;
    // return List.of();
    // }

    @Override
    public Collection<Bug> viewOwnBugs(User currentUser, Project project) throws BugsNotFoundException {
        List<Bug> allTesterBugs = new ArrayList<>();
        String getOwnBugsQuery = "SELECT * FROM bugs WHERE created_by = " + currentUser.getUserId() + " AND project_id = " + project.getProjectId();

        Connection con = null;
        Bug bug = new Bug();
        List<Bug> bugList = new ArrayList<>();
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        try (PreparedStatement bugQueryPS = con.prepareStatement(getOwnBugsQuery);
             ResultSet bugRS = bugQueryPS.executeQuery();) {

            if (bugRS == null) {
                throw new BugsNotFoundException("Bug not found");
            }

            while (bugRS.next()) {
                int bugId = bugRS.getInt("bug_id");
                String bugName = bugRS.getString("bug_name");
                String bugDescription = bugRS.getString("bug_description");
                int createdBy = bugRS.getInt("created_by");
                LocalDateTime createdOn = bugRS.getTimestamp("created_on").toLocalDateTime();
                SeverityLevel severityLevel = SeverityLevel.valueOf(bugRS.getString("severity_level"));
                BugStatus bugStatus = BugStatus.valueOf(bugRS.getString("bug_status"));
                Boolean accepted = bugRS.getBoolean("accepted");
                int projectId = bugRS.getInt("project_id");

                bug.setBugId(bugId);
                bug.setBugName(bugName);
                bug.setBugDescription(bugDescription);
                bug.setCreatedBy(createdBy);
                bug.setCreatedOn(createdOn);
                bug.setSeverityLevel(severityLevel);
                bug.setBugStatus(bugStatus);
                bug.setAccepted(accepted);
                bug.setProjectId(projectId);

                bugList.add(bug);
            }
            return bugList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                con.setAutoCommit(false);
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
