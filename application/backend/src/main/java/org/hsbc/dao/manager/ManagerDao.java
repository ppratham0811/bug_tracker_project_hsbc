package org.hsbc.dao.manager;

import org.hsbc.db.JdbcConnector;
import org.hsbc.exceptions.ProjectLimitExceededException;
import org.hsbc.exceptions.ProjectNotFoundException;
import org.hsbc.exceptions.WrongProjectDateException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;
import org.hsbc.model.enums.BugStatus;
import org.hsbc.model.enums.ProjectStatus;
import org.hsbc.model.enums.UserRole;

import java.sql.*;
import java.time.LocalDate;

public class ManagerDao implements ManagerDaoInterface {
    // private Connection con = null;
    // private Connection con = null;

    // public ManagerDao() {
    // try {
    // this.con = JdbcConnector.getInstance().getConnectionObject();
    // } catch (SQLException e) {
    // throw new RuntimeException("Failed to establish database connection", e);
    // }
    // }
    // public ManagerDao() {
    // try {
    // this.con = JdbcConnector.getInstance().getConnectionObject();
    // } catch (SQLException e) {
    // throw new RuntimeException("Failed to establish database connection", e);
    // }
    // }

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
        String getProjectQuery = "SELECT * FROM projects WHERE project_id=" + projectId;
        Connection con = null;

        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException se) {
            se.printStackTrace();
        }

        try (PreparedStatement getProjectPS = con.prepareStatement(getProjectQuery);
             ResultSet rs = getProjectPS.executeQuery();) {
            Project projectDetails = new Project();
            if (rs.next()) {
                projectDetails.setProjectId(projectId);
                String projectName = rs.getString("project_name");
                projectDetails.setProjectName(projectName);

                String projectStatus = rs.getString("project_status");
                projectDetails.setProjectStatus(ProjectStatus.valueOf(projectStatus.toUpperCase()));

                int projectManager = rs.getInt("project_manager");
                projectDetails.setProjectManager(projectManager);

                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                projectDetails.setStartDate(startDate);

                int noOfBugs = rs.getInt("no_of_bugs");
                projectDetails.setNoOfBugs(noOfBugs);

                return projectDetails;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        throw new ProjectNotFoundException("Project with projectId =" + projectId + " not found");
    }

    @Override
    public boolean createNewProject(Project project) throws ProjectLimitExceededException, WrongProjectDateException {
        String insertProjectQuery = "INSERT INTO projects (project_name, project_manager, start_date, project_status) VALUES (?,?,?,?)";
        String getProjectManagerQuery = "SELECT * FROM users WHERE user_id = ?";
        String addProjectQuery = "UPDATE users SET project_count = ? WHERE user_id = ?";
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement insertPS = con.prepareStatement(insertProjectQuery);
             PreparedStatement selectPS = con.prepareStatement(getProjectManagerQuery);
             PreparedStatement updateManagerProjectsPS = con.prepareStatement(addProjectQuery);) {
            if (getProjectDateDifference(project.getStartDate()) >= 2) {

                insertPS.setString(1, project.getProjectName());
                insertPS.setInt(2, project.getProjectManager());
                insertPS.setDate(3, Date.valueOf(project.getStartDate()));
                insertPS.setString(4, String.valueOf(project.getProjectStatus()));
                insertPS.executeUpdate();

                // need to add +1 to project manager's project count
                selectPS.setInt(1, project.getProjectManager());
                // need to add +1 to project manager's project count
                selectPS.setInt(1, project.getProjectManager());

                try (ResultSet rs = selectPS.executeQuery();) {
                    int projectCount;
                    if (rs.next()) {
                        projectCount = rs.getInt("project_count");
                        System.out.println("This is project count:" + projectCount);
                        if (projectCount < 4) {
                            updateManagerProjectsPS.setInt(1, projectCount + 1);
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
        return true;
    }

    @Override
    public boolean assignProject(Project project, User user) throws ProjectLimitExceededException {
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String insertUserProject = "INSERT INTO user_projects (user_id, project_id) VALUES (" + project.getProjectId() + ","
                + user.getUserId() + ")";
        String updateUserProjectCount = "UPDATE users WHERE user_id=" + user.getUserId() + " SET project_count = ?";
        try (PreparedStatement insertUserProjectPS = con.prepareStatement(insertUserProject);
             PreparedStatement updateUserProjectPS = con.prepareStatement(updateUserProjectCount)) {
            if (user.getUserRole() == UserRole.DEVELOPER && user.getProjectCount() < 2) {
                insertUserProjectPS.executeQuery();
                updateUserProjectPS.setInt(1, user.getProjectCount() + 1);
            } else {
                throw new ProjectLimitExceededException("Project limit exceeded for this user");
            }

            if (user.getUserRole() == UserRole.TESTER && user.getProjectCount() < 1) {
                ResultSet rs1 = insertUserProjectPS.executeQuery();
                updateUserProjectPS.setInt(1, user.getProjectCount() + 1);
                updateUserProjectPS.executeUpdate();

                rs1.close();
            } else {
                throw new ProjectLimitExceededException("Project limit exceeded for this user");
            }
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        } finally {
            try {
                con.setAutoCommit(false);
                con.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean assignBugToDeveloper(Bug bug, User user) {
        String assignQuery = "INSERT INTO user_bugs(user_id, bug_id) VALUES (" + user.getUserId() + ", " + bug.getBugId()
                + ")";
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException se) {
            se.printStackTrace();
        }

        try (PreparedStatement assignPS = con.prepareStatement(assignQuery)) {
            assignPS.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean acceptOrRejectBug(Bug bug, boolean accepted) {
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            String updateBugAcceptedStatusQuery = "UPDATE bugs SET accepted = " + (accepted ? "true" : "false")
                    + " WHERE bug_id = " + bug.getBugId();
            try (PreparedStatement updateBugPS = con.prepareStatement(updateBugAcceptedStatusQuery);) {
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

        } finally {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public boolean closeBug(Bug bug) {
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException se) {
            se.printStackTrace();
        }

        if (bug.getBugStatus() == BugStatus.COMPLETED) {
            System.out.println("Bug already closed");
        } else {
            bug.setBugStatus(BugStatus.COMPLETED);
            String updateBugQuery = "UPDATE bugs WHERE bug_id=" + bug.getBugId() + " SET bug_status='COMPLETED';";
            try (PreparedStatement updateBugPS = con.prepareStatement(updateBugQuery);) {
                ResultSet rs = updateBugPS.executeQuery();
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }
}
