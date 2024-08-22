package org.hsbc.dao.developer;

import org.hsbc.db.JdbcConnector;
import org.hsbc.exceptions.BugNotAcceptedException;
import org.hsbc.exceptions.NoBugsAssignedException;
import org.hsbc.model.Bug;
import org.hsbc.model.User;
import org.hsbc.model.enums.BugStatus;
import org.hsbc.model.enums.SeverityLevel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DeveloperDao implements DeveloperDaoInterface {
    @Override
    public void changeBugStatusToMarked(Bug bug) throws BugNotAcceptedException {
        if (bug.isAccepted()) {
            if (bug.getBugStatus() == BugStatus.COMPLETED) {
                System.out.println("bug " + bug.getBugName() + " is already closed");
                return;
            }
            if (bug.getBugStatus() == BugStatus.IN_PROGRESS) {
                bug.setBugStatus(BugStatus.MARKED);
                String bugStatusQuery = "UPDATE bugs SET bug_status = 'MARKED' where bug_id = " + bug.getBugId();
                Connection con = null;
                try {
                    con = JdbcConnector.getInstance().getConnectionObject();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                try (PreparedStatement insertPS = con.prepareStatement(bugStatusQuery)) {

                    ResultSet rs = insertPS.executeQuery();
                    rs.close();
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
                    } catch (SQLException se) {
                        se.printStackTrace();
                    }
                }
            }
        } else {
            throw new BugNotAcceptedException();
        }
    }

    @Override
    public List<Bug> readAssignedBugs(User user) throws NoBugsAssignedException {
        String bugsQuery = "SELECT b.bug_id, b.bug_title, b.bug_description, b.bug_status, b.created_by, b.created_at, b.severity_level, b.project_id"
                +
                "FROM bugs b JOIN users u ON b.assigned_to = u.user_id" +
                "WHERE u.user_id = " + user.getUserId();
        Connection con = null;
        List<Bug> allAssignedBugs = new ArrayList<>();
        Bug bug = new Bug();
        try {
            con = JdbcConnector.getInstance().getConnectionObject();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement selectPS = con.prepareStatement(bugsQuery);
             ResultSet rs = selectPS.executeQuery()) {
            if (rs == null) {
                throw new NoBugsAssignedException("You Have No Bugs");
            }
            while (rs.next()) {
                int bugId = rs.getInt("bug_id");
                String bugName = rs.getString("bug_title");
                String bugDes = rs.getString("bug_description");
                String bugStatus = rs.getString("bug_status");
                int createdBy = rs.getInt("created_by");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                String severityLevel = rs.getString("severity_level");
                int projectId = rs.getInt("project_id");
                boolean acceptStatus = rs.getBoolean("accepted");

                bug.setBugId(bugId);
                bug.setBugName(bugName);
                bug.setBugDescription(bugDes);
                bug.setBugStatus(BugStatus.valueOf(bugStatus.toUpperCase()));
                bug.setCreatedBy(createdBy);
                bug.setCreatedOn(createdAt);
                bug.setSeverityLevel(SeverityLevel.valueOf(severityLevel.toUpperCase()));
                bug.setProjectId(projectId);
                bug.setAccepted(acceptStatus);
                allAssignedBugs.add(bug);
            }
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
}
