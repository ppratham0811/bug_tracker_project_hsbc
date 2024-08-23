package org.hsbc.dao.developer;

import org.hsbc.exceptions.BugNotAcceptedException;
import org.hsbc.exceptions.NoBugsAssignedException;
import org.hsbc.model.Bug;
import org.hsbc.model.User;

import java.util.List;

public interface DeveloperDaoInterface {

  Boolean changeBugStatusToMarked(Bug bug) throws BugNotAcceptedException;

  public List<Bug> readAssignedBugs(User user) throws NoBugsAssignedException; // assigned to a user
}
