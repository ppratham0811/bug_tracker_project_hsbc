package org.hsbc.dao.manager;

import org.hsbc.db.JdbcConnector;
import org.hsbc.model.Project;

import java.sql.*;
import java.time.LocalDate;

public class ManagerDao implements ManagerDaoInterface {
//    private Connection con = null;

//    public ManagerDao() {
//        try {
//            this.con = JdbcConnector.getInstance().getConnectionObject();
//        } catch (SQLException e) {
//            throw new RuntimeException("Failed to establish database connection", e);
//        }
//    }

    private static int getProjectDateDifference(LocalDate date) {
        LocalDate currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        int projectDay = date.getDayOfMonth();
        int projectMonth = date.getMonthValue();
        int projectYear = date.getYear();

        if (projectYear > currentYear) {
            return 2;
        } else if (projectYear == currentYear) {
            if (projectMonth == currentMonth) {
                return projectDay - currentDay;
            } else if (projectMonth > currentMonth) {
                return 2;
            }
        }
        return -1;
    }

    @Override
    public void createNewProject(Project project) {
        try (Connection con = JdbcConnector.getInstance().getConnectionObject();) {
            if (getProjectDateDifference(project.getStartDate()) >= 2) {
                String query = "INSERT INTO projects (project_name, project_manager, start_date, project_status) VALUES (?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(query);
                con.setAutoCommit(false);

                ps.setString(1, project.getProjectName());
                ps.setInt(2, project.getProjectManager());
                ps.setDate(3, Date.valueOf(project.getStartDate()));
                ps.setString(4, String.valueOf(project.getProjectStatus()));
                int result = ps.executeUpdate();
                con.commit();
            } else {
                throw new IllegalArgumentException("Project date should be at least 2 days later");
            }
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
    }

    @Override
    public void assignProject() {

    }

    @Override
    public void assignBug() {

    }
}
