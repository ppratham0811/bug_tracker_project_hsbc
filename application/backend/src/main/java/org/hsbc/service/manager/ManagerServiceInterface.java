package org.hsbc.service.manager;

import org.hsbc.exceptions.NotAuthorizedException;
import org.hsbc.exceptions.ProjectLimitExceededException;
import org.hsbc.exceptions.WrongProjectDateException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

public interface ManagerServiceInterface {
//    public Project viewProjectDetails(int projectId) throws ProjectNotFoundException;
    boolean addNewProject(int userId, Project project) throws ProjectLimitExceededException, WrongProjectDateException, NotAuthorizedException;
    boolean projectAssign(int userId, Project project, User user) throws ProjectLimitExceededException, NotAuthorizedException;
    boolean bugAssignToDeveloper(int userId, Bug bug, User user) throws NotAuthorizedException;

    boolean acceptBug(int userId, Bug bug, boolean accepted) throws NotAuthorizedException;

    boolean bugClose(int userId, Bug bug) throws NotAuthorizedException;

}
