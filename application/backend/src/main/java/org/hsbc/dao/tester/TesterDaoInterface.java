package org.hsbc.dao.tester;

import org.hsbc.exceptions.*;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

import java.util.Collection;

public interface TesterDaoInterface {
  boolean reportNewBug(int userId, Bug bug, Project project) throws UserNotFoundException, NotAuthorizedException;
  // Collection<Project> viewAssignedProjects(User currentUser);

  Collection<Bug> viewOwnBugs(User currentUser, Project project) throws BugsNotFoundException;
}
