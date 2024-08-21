package org.hsbc.dao.tester;

import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

import java.util.Collection;

public interface TesterDaoInterface {
    void reportNewBug(Bug bug, Project project);
    Collection<Project> viewAssignedProjects(User currentUser);

    Collection<Bug> viewOwnBugs(User currentUser, Project project);
}
