package org.hsbc.dao.tester;

import org.hsbc.db.JdbcConnector;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TesterDao implements TesterDaoInterface{
    @Override
    public void reportNewBug(Bug bug, Project project)  {
        String insertBugQuery = "INSERT INTO bugs (bug_name, bug_description, created_by, created_on, severity_level, bug_status, project_id) VALUES (?,?,?,?,?,?,?)";
        //bug status and project id
        String addBugQuery = "UPDATE projects SET bug_count = ? WHERE user_id = ? and project_id = ?";
        //UPDATE users SET  = ? WHERE user_id = ?
        String getTesterQuery = "SELECT * FROM users WHERE user_id = ?";
        Connection con = null;

        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement insertPS = con.prepareStatement(insertBugQuery); PreparedStatement selectPS = con.prepareStatement(getTesterQuery); PreparedStatement updateBugPS = con.prepareStatement(addBugQuery)){
            insertPS.setString(1,bug.getBugName());
            insertPS.setString(2,bug.getBugDescription());
            insertPS.setInt(3,bug.getCreatedBy());
            insertPS.setTimestamp(4, Timestamp.valueOf(bug.getCreatedOn()) );
            insertPS.setString(5,String.valueOf(bug.getSeverityLevel()));
            insertPS.setString(6,String.valueOf(bug.getBugStatus()));
            insertPS.setInt(7,bug.getProjectId());
            insertPS.executeUpdate();

            selectPS.setInt(1,bug.getCreatedBy());
            selectPS.executeQuery();

            updateBugPS.setInt(1, project.getNoOfBugs() + 1); //but the bugcount will be in project model
            updateBugPS.setInt(2, bug.getCreatedBy());
            updateBugPS.setInt(3,bug.getProjectId());
            updateBugPS.executeUpdate();

            con.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

//    @Override
//    public Collection<Project> viewAssignedProjects(User currentUser) {
//        String getUserQuery = "SELECT * FROM users WHERE username = ?";
//        String getProjectsQuery = "SELECT * FROM user_projects WHERE user_id = ?";
//        Connection con = null;
//
//        try {
//           con = JdbcConnector.getInstance().getConnectionObject();
//           con.setAutoCommit(false);
//        } catch (SQLException se) {
//            se.printStackTrace();
//        }
//
//        try (PreparedStatement getUserPS = con.prepareStatement(getUserQuery); PreparedStatement selectPS = con.prepareStatement(getProjectsQuery)) {
//            getUserPS.setString(1, currentUser.getUsername());
//            ResultSet rs = getUserPS.executeQuery();
//            int userId = 0;
//            if (rs.next()) {
//                userId = rs.getInt("user_id");
//            }
//
//            selectPS.setInt(1, userId);
//            ResultSet allProjects = selectPS.executeQuery();
//
//
//
//            con.commit();
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } finally {
//            try {
//                con.setAutoCommit(true);
//                con.close();
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
////        ResultSet rs = ;
//        return List.of();
//    }

    @Override
    public Collection<Bug> viewOwnBugs(User currentUser, Project project) {
//        String getUserQuery = "SELECT * FROM users WHERE user_id = ?";
        String getUserQuery = "SELECT * FROM users WHERE username = ?";
        List<Bug> allTesterBugs = new ArrayList<>();
        String getOwnBugsQuery = "SELECT * FROM bugs WHERE created_by = ? AND project_id = ?";

        Connection con = null;

        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException se) {
            se.printStackTrace();
        }

        try (PreparedStatement userQueryPS = con.prepareStatement(getUserQuery);PreparedStatement bugQueryPS = con.prepareStatement(getOwnBugsQuery)) {
            userQueryPS.setString(1, currentUser.getUsername());
            ResultSet userRetrieved = userQueryPS.executeQuery();
            int userId = 0;
            if (userRetrieved.next()) {
                userId = userRetrieved.getInt("user_id");
            }



            bugQueryPS.setInt(1, userId);
            bugQueryPS.setInt(2, userId);
            ResultSet rs = bugQueryPS.executeQuery();
    
            while (rs.next()) {


            }

            con.commit();
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

        return List.of();
    }
}
