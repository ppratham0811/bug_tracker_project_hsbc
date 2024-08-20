package org.hsbc.dao.developer;

import org.hsbc.db.JdbcConnector;
import org.hsbc.exceptions.BugNotAcceptedException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;
import org.hsbc.model.enums.BugStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeveloperDao implements DeveloperDaoInterface {
  @Override
  public void viewAllProjectMembers(int projectId) {

  }

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
}
