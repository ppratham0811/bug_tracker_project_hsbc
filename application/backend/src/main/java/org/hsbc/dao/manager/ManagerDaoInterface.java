package org.hsbc.dao.manager;

import org.hsbc.exceptions.ProjectLimitExceededException;
import org.hsbc.exceptions.ProjectNotFoundException;
import org.hsbc.exceptions.WrongProjectDateException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

public interface ManagerDaoInterface {
  Project getProjectDetails(int projectId) throws ProjectNotFoundException;

  boolean createNewProject(Project project) throws ProjectLimitExceededException, WrongProjectDateException;

  boolean assignProject(Project project, User user) throws ProjectLimitExceededException;

  boolean assignBugToDeveloper(Bug bug, User user);

  boolean acceptOrRejectBug(Bug bug, boolean accepted);

  boolean closeBug(Bug bug);
}
