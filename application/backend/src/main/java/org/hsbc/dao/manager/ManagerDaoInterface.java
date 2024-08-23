package org.hsbc.dao.manager;

import org.hsbc.exceptions.NotAuthorizedException;
import org.hsbc.exceptions.ProjectLimitExceededException;
//import org.hsbc.exceptions.ProjectNotFoundException;
import org.hsbc.exceptions.WrongProjectDateException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

public interface ManagerDaoInterface {
//  Project getProjectDetails(int projectId) throws ProjectNotFoundException;

  boolean createNewProject(int userId, Project project) throws ProjectLimitExceededException, WrongProjectDateException, NotAuthorizedException;

  boolean assignProject(int userId, Project project, User user) throws ProjectLimitExceededException, NotAuthorizedException;

  boolean assignBugToDeveloper(int userId, Bug bug, User user) throws NotAuthorizedException;

  boolean acceptOrRejectBug(int userId, Bug bug, boolean accepted) throws NotAuthorizedException;

  boolean closeBug(int userId, Bug bug) throws NotAuthorizedException;
}
