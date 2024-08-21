package org.hsbc.dao.developer;

import org.hsbc.exceptions.BugNotAcceptedException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

public interface DeveloperDaoInterface {
    void viewAllProjectMembers(int projectId);
    void changeBugStatusToMarked(Bug bug) throws BugNotAcceptedException;
}
