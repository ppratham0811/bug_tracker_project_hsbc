package org.hsbc.dao.developer;

import org.hsbc.db.JdbcConnector;
import org.hsbc.exceptions.BugNotAcceptedException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeveloperDao implements DeveloperDaoInterface { //UserNotFoundException?
    @Override
    public void viewAllTeamMembers(Project project) {

    }

    @Override
    public void changeBugStatusToMarked(Bug bug) throws BugNotAcceptedException {
        if (bug.isAccepted()) {

            if (bug.getBugStatus() == ProjectOrBugStatus.COMPLETED)
                System.out.println("bug " + bug.getBugName() + " is already closed");
            if (bug.getBugStatus() == ProjectOrBugStatus.IN_PROGRESS) {
                bug.setBugStatus(ProjectOrBugStatus.COMPLETED);
                String bugStatusQuery = "update bugs set bug_status = 'COMPLETED' where bug_name = " + bug.getBugName();
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
                } catch (SQLException se) {
                    try {
                        con.rollback();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(se.getMessage());
                }
            }
        } else {
            throw new BugNotAcceptedException();
        }
    }
}
