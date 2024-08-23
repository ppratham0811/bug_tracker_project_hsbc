package org.hsbc.dao.manager;

import org.hsbc.db.JdbcConnector;
import org.hsbc.exceptions.NotAuthorizedException;
import org.hsbc.exceptions.ProjectLimitExceededException;
import org.hsbc.exceptions.WrongProjectDateException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;
import org.hsbc.model.enums.BugStatus;
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

//    @Override
//    public Project getProjectDetails(int projectId) throws ProjectNotFoundException {
//        String getProjectQuery = "SELECT * FROM projects WHERE project_id=" + projectId;
//        Connection con = null;
//
//        try {
//            con = JdbcConnector.getInstance().getConnectionObject();
//            con.setAutoCommit(false);
//        } catch (SQLException se) {
//            se.printStackTrace();
//        }
//
//        try (PreparedStatement getProjectPS = con.prepareStatement(getProjectQuery);
//             ResultSet rs = getProjectPS.executeQuery();) {
//            Project projectDetails = new Project();
//            if (rs.next()) {
//                projectDetails.setProjectId(projectId);
//                String projectName = rs.getString("project_name");
//                projectDetails.setProjectName(projectName);
//
//                String projectStatus = rs.getString("project_status");
//                projectDetails.setProjectStatus(ProjectStatus.valueOf(projectStatus.toUpperCase()));
//
//                int projectManager = rs.getInt("project_manager");
//                projectDetails.setProjectManager(projectManager);
//
//                LocalDate startDate = rs.getDate("start_date").toLocalDate();
//                projectDetails.setStartDate(startDate);
//
//                int noOfBugs = rs.getInt("no_of_bugs");
//                projectDetails.setNoOfBugs(noOfBugs);
//
//                return projectDetails;
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        throw new ProjectNotFoundException("Project with projectId =" + projectId + " not found");
//    }

    @Override
    public boolean createNewProject(int userId, Project project) throws ProjectLimitExceededException, WrongProjectDateException, NotAuthorizedException {
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String getUser = "SELECT user_role FROM users WHERE user_id = " + userId;

        try (PreparedStatement getUserPS = con.prepareStatement(getUser); ResultSet userRS = getUserPS.executeQuery();) {
            String userRole = userRS.getString("user_role");
            if (UserRole.valueOf(userRole.toUpperCase()) == UserRole.MANAGER) {
                String insertProjectQuery = "INSERT INTO projects (project_name, project_manager, start_date, project_status) VALUES (?,?,?,?)";
                String getProjectManagerQuery = "SELECT * FROM users WHERE user_id = " + userId;
                String addProjectQuery = "UPDATE users SET project_count = ? WHERE user_id = ?";
                String addManagerToProjectQuery = "INSERT INTO user_projects (user_id, project_id) VALUES (" + userId + ", " + project.getProjectId() + ")";
                try (PreparedStatement insertPS = con.prepareStatement(insertProjectQuery);
                     PreparedStatement selectPS = con.prepareStatement(getProjectManagerQuery);
                     PreparedStatement updateManagerProjectsPS = con.prepareStatement(addProjectQuery);
                     PreparedStatement addManagerPS = con.prepareStatement(addManagerToProjectQuery);) {
                    if (getProjectDateDifference(project.getStartDate()) >= 2) {
                        insertPS.setString(1, project.getProjectName());
                        insertPS.setInt(2, project.getProjectManager());
                        insertPS.setDate(3, Date.valueOf(project.getStartDate()));
                        insertPS.setString(4, String.valueOf(project.getProjectStatus()));
                        insertPS.executeUpdate();

                        addManagerPS.executeQuery().close();

                        ResultSet rs = selectPS.executeQuery();
                        int projectCount;
                        if (rs.next()) {
                            projectCount = rs.getInt("project_count");
                            System.out.println("This is project count:" + projectCount);
                            if (projectCount < 4) {
                                updateManagerProjectsPS.setInt(1, projectCount + 1);
                                updateManagerProjectsPS.setInt(2, project.getProjectManager());
                                updateManagerProjectsPS.executeUpdate();
                                System.out.println("Project count updated");
                                rs.close();
                                con.commit();
                                return true;
                            } else {
                                con.rollback();
                                throw new ProjectLimitExceededException("Project limit Exceed");
                            }
                        }
                    } else {
                        con.rollback();
                        throw new WrongProjectDateException("Project date should be at least 2 days later");
                    }
                } catch (SQLException se) {
                    try {
                        con.rollback();
                        se.printStackTrace();
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
            } else {
                throw new NotAuthorizedException("You are not the manager");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean assignProject(int userId, Project project, User user) throws ProjectLimitExceededException, NotAuthorizedException {
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String getCurrentUserQuery = "SELECT user_role FROM users WHERE user_id = " + userId;

        try (PreparedStatement currentUserPS = con.prepareStatement(getCurrentUserQuery); ResultSet currentUserRS = currentUserPS.executeQuery();) {
            String currentUserRole = currentUserRS.getString("user_role");

            if (UserRole.valueOf(currentUserRole.toUpperCase()) == UserRole.MANAGER) {
                String insertUserProject = "INSERT INTO user_projects (user_id, project_id) VALUES (" + project.getProjectId() + "," + user.getUserId() + ")";
                String updateUserProjectCount = "UPDATE users WHERE user_id=" + user.getUserId() + " SET project_count = ?";
                try (PreparedStatement insertUserProjectPS = con.prepareStatement(insertUserProject); PreparedStatement updateUserProjectPS = con.prepareStatement(updateUserProjectCount)) {
                    if (user.getUserRole() == UserRole.DEVELOPER && user.getProjectCount() < 2) {
                        insertUserProjectPS.executeQuery();
                        updateUserProjectPS.setInt(1, user.getProjectCount() + 1);
                        con.commit();
                        return true;
                    } else if (user.getUserRole() == UserRole.TESTER && user.getProjectCount() < 1) {
                        ResultSet rs1 = insertUserProjectPS.executeQuery();
                        updateUserProjectPS.setInt(1, user.getProjectCount() + 1);
                        updateUserProjectPS.executeUpdate();
                        con.commit();
                        rs1.close();
                        return true;
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
            } else {
                throw new NotAuthorizedException("Only manager can create projects");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    public boolean assignBugToDeveloper(int userId, Bug bug, User user) throws NotAuthorizedException {
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException se) {
            se.printStackTrace();
        }

        String getCurrentUserQuery = "SELECT user_role FROM users WHERE user_id = " + userId;

        try (PreparedStatement currentUserPS = con.prepareStatement(getCurrentUserQuery); ResultSet currentUserRS = currentUserPS.executeQuery();) {
            String currentUserRole = currentUserRS.getString("user_role");

            if (UserRole.valueOf(currentUserRole.toUpperCase()) == UserRole.MANAGER) {
                String assignQuery = "INSERT INTO user_bugs(user_id, bug_id) VALUES (" + user.getUserId() + ", " + bug.getBugId() + ")";

                try (PreparedStatement assignPS = con.prepareStatement(assignQuery)) {
                    assignPS.executeUpdate();
                    con.commit();
                    return true;
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
            } else {
                throw new NotAuthorizedException("Only manager can assign bugs");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    public boolean acceptOrRejectBug(int userId, Bug bug, boolean accepted) throws NotAuthorizedException {
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String getCurrentUserQuery = "SELECT user_role FROM users WHERE user_id = " + userId;
        String updateBugAcceptedStatusQuery = "UPDATE bugs SET accepted = " + (accepted ? "true" : "false") + " WHERE bug_id = " + bug.getBugId();

        try (PreparedStatement currentUserPS = con.prepareStatement(getCurrentUserQuery);
             ResultSet currentUserRS = currentUserPS.executeQuery();
             PreparedStatement updateBugPS = con.prepareStatement(updateBugAcceptedStatusQuery);) {
            String currentUserRole = currentUserRS.getString("user_role");

            if (UserRole.valueOf(currentUserRole.toUpperCase()) == UserRole.MANAGER) {
                updateBugPS.executeUpdate();
                con.commit();
                return true;
            } else {
                throw new NotAuthorizedException("Only manager can accept/reject bugs");
            }
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

    @Override
    public boolean closeBug(int userId, Bug bug) throws NotAuthorizedException {
        Connection con = null;
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
            con.setAutoCommit(false);
        } catch (SQLException se) {
            se.printStackTrace();
        }

        String getCurrentUserQuery = "SELECT user_role FROM users WHERE user_id = " + userId;
        String updateBugQuery = "UPDATE bugs WHERE bug_id=" + bug.getBugId() + " SET bug_status='COMPLETED';";

        try (PreparedStatement currentUserPS = con.prepareStatement(getCurrentUserQuery);
             ResultSet currentUserRS = currentUserPS.executeQuery();
             PreparedStatement updateBugPS = con.prepareStatement(updateBugQuery);) {
            String currentUserRole = currentUserRS.getString("user_role");

            if (UserRole.valueOf(currentUserRole.toUpperCase()) == UserRole.MANAGER) {
                if (bug.getBugStatus() == BugStatus.COMPLETED) {
                    System.out.println("Bug already closed");
                } else {
                    bug.setBugStatus(BugStatus.COMPLETED);
                    ResultSet rs = updateBugPS.executeQuery();
                    rs.close();
                    return true;
                }
            } else {
                throw new NotAuthorizedException("Only Manager can close bugs");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
}
