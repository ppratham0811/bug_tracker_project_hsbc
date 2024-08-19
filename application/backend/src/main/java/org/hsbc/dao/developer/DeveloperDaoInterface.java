package org.hsbc.dao.developer;

import org.hsbc.model.Bug;
import org.hsbc.model.User;

public interface DeveloperDaoInterface {
    void readAssignedProjects(User user);
    void changeBugStatusToClosed(Bug bug);

}
