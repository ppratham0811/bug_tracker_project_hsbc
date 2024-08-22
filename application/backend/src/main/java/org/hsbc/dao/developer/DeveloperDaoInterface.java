package org.hsbc.dao.developer;

import org.hsbc.exceptions.BugNotAcceptedException;
import org.hsbc.exceptions.NoBugsAssignedException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

public interface DeveloperDaoInterface {
    void changeBugStatusToMarked(Bug bug) throws BugNotAcceptedException;
    public void readAssignedBugs(User user) throws NoBugsAssignedException; //assigned to a user
}
