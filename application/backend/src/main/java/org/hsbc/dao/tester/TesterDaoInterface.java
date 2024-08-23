package org.hsbc.dao.tester;

import org.hsbc.exceptions.BugsNotFoundException;
import org.hsbc.exceptions.NoBugsAssignedException;
import org.hsbc.exceptions.UserNotFoundException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

import java.util.Collection;

public interface TesterDaoInterface {
  boolean reportNewBug(Bug bug, Project project);
  // Collection<Project> viewAssignedProjects(User currentUser);

  Collection<Bug> viewOwnBugs(User currentUser, Project project) throws BugsNotFoundException, UserNotFoundException;
}
