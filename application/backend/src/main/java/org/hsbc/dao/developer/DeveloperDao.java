package org.hsbc.dao.developer;

import org.hsbc.db.JdbcConnector;
import org.hsbc.model.Bug;
import org.hsbc.model.User;
import org.hsbc.model.enums.ProjectOrBugStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeveloperDao implements DeveloperDaoInterface { //UserNotFoundException?
    @Override
    public void readAssignedProjects(User user) {
        String getProjectDetailsQuery = "SELECT p.project_name, u_manager.full_name AS project_manager, p.start_date, " +
                "GROUP_CONCAT(u_member.full_name SEPARATOR ', ') AS members " +
                "FROM projects p " +
                "JOIN user_projects up_manager ON p.project_manager = up_manager.user_id " +
                "JOIN users u_manager ON p.project_manager = u_manager.user_id " +
                "JOIN user_projects up_member ON p.project_id = up_member.project_id " +
                "JOIN users u_member ON up_member.user_id = u_member.user_id " +
                "WHERE u_member.user_id = "+ user.getUserId()+
                " GROUP BY p.project_id, u_manager.full_name, p.start_date";
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement insertPS = con.prepareStatement(getProjectDetailsQuery)) {
            try(ResultSet rs = insertPS.executeQuery()){
                if (rs==null)
                    System.out.println("no project assigned"); //we can add exception later, if needed
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
                rs.close();
                insertPS.close();
                con.close();

            }
        } catch (SQLException se) {
            try {
                con.rollback();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            System.out.println(se.getMessage());
        }
    }

    @Override
    public void changeBugStatusToClosed(Bug bug) {
        if(bug.getBugStatus() == ProjectOrBugStatus.COMPLETED)
            System.out.println("bug "+bug.getBugName()+" is already closed");
        if(bug.getBugStatus() == ProjectOrBugStatus.IN_PROGRESS){
            bug.setBugStatus(ProjectOrBugStatus.COMPLETED);
            String bugStatusQuery = "update bugs set bug_status = 'COMPLETED' where bug_name = "+bug.getBugName();
            Connection con = null;
            try {
                con = JdbcConnector.getInstance().getConnectionObject();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try (PreparedStatement insertPS = con.prepareStatement(bugStatusQuery)) {
                try (ResultSet rs = insertPS.executeQuery()) {
                    rs.close();
                    insertPS.close();
                    con.close();
                }
            }catch (SQLException se) {
                try {
                    con.rollback();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(se.getMessage());
            }
        }
    }
}
