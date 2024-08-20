package org.hsbc.dao.manager;
//gd
import org.hsbc.db.JdbcConnector;
import org.hsbc.exceptions.ProjectLimitExceededException;
import org.hsbc.exceptions.ProjectNotFoundException;
import org.hsbc.exceptions.WrongProjectDateException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;
import org.hsbc.model.enums.BugStatus;
import org.hsbc.model.enums.ProjectStatus;

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
    public Project getProjectDetails(int projectId) throws ProjectNotFoundException {
        String getProjectsQuery = "SELECT * FROM projects WHERE project_id = "+projectId;
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement getProjectsPS = con.prepareStatement(getProjectsQuery)) {
            ResultSet rs = getProjectsPS.executeQuery();
            Project projectDetails = new Project();

            projectDetails.setProjectId(projectId);
            String projectName = rs.getString("project_name");
            projectDetails.setProjectName(projectName);

            String projectStatus = rs.getString("project_status");
            ProjectStatus ps = ProjectStatus.IN_PROGRESS;
            if (projectStatus == "COMPLETED") {
                ps = ProjectStatus.COMPLETED;
            }
            projectDetails.setProjectStatus(ps);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        throw new ProjectNotFoundException();
    }

    @Override
    public void createNewProject(Project project) throws ProjectLimitExceededException, WrongProjectDateException {
        String insertProjectQuery = "INSERT INTO projects (project_name, project_manager, start_date, project_status) VALUES (?,?,?,?)";
        String getProjectManagerQuery = "SELECT * FROM users WHERE user_id = ?";
        String addProjectQuery = "UPDATE users SET project_count = ? WHERE user_id = ?";
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement insertPS = con.prepareStatement(insertProjectQuery); PreparedStatement selectPS = con.prepareStatement(getProjectManagerQuery); PreparedStatement updateManagerProjectsPS = con.prepareStatement(addProjectQuery);) {
            if (getProjectDateDifference(project.getStartDate()) >= 2) {

                con.setAutoCommit(false);

                insertPS.setString(1, project.getProjectName());
                insertPS.setInt(2, project.getProjectManager());
                insertPS.setDate(3, Date.valueOf(project.getStartDate()));
                insertPS.setString(4, String.valueOf(project.getProjectStatus()));
                insertPS.executeUpdate();

//                need to add +1 to project manager's project count
                selectPS.setInt(1, project.getProjectManager());

                try (ResultSet rs = selectPS.executeQuery();) {
                    int projectCount;
                    if (rs.next()) {
                        projectCount = rs.getInt("project_count");
                        System.out.println("This is project count:" + projectCount);
                        if (projectCount < 4) {
                            updateManagerProjectsPS.setInt(1, projectCount+1);
                            updateManagerProjectsPS.setInt(2, project.getProjectManager());
                            updateManagerProjectsPS.executeUpdate();
                            System.out.println("Project count updated");
                            con.commit();
                        } else {
                            con.rollback();
                            throw new ProjectLimitExceededException("Project limit Exceed");
                        }
                    }
                }

            } else {
                con.rollback();
                throw new WrongProjectDateException("Project date should be at least 2 days later");
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
    public void assignProject(Project project, User user) {
        String userRetrieve = "SELECT * FROM users WHERE username = ?";
        try (Connection con = JdbcConnector.getInstance().getConnectionObject(); PreparedStatement ps = con.prepareStatement(userRetrieve);) {
//            table name: user_projects
            ps.setString(1, "prathmesh");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                String password = rs.getString("user_password");
                String email = rs.getString("user_email");
                String role = rs.getString("user_role");
                int projectCount = rs.getInt("project_count");
                Timestamp lastLoggedIn = rs.getTimestamp("last_logged_in");

                System.out.println("User ID: " + userId);
                System.out.println("Username: " + username);
                System.out.println("Full Name: " + fullName);
                System.out.println("Password: " + password);
                System.out.println("Email: " + email);
                System.out.println("Role: " + role);
                System.out.println("Project Count: " + projectCount);
                System.out.println("Last Logged In: " + lastLoggedIn);
            }
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
    }

    @Override
    public void assignBug(Bug bug, int userId) {

    }

    @Override
    public void changeBugStatusToClosed(Bug bug) {
        if(bug.getBugStatus() == BugStatus.COMPLETED)
            System.out.println("bug "+bug.getBugName()+" is already closed");
        if(bug.getBugStatus() == BugStatus.IN_PROGRESS){
            bug.setBugStatus(BugStatus.COMPLETED);
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
