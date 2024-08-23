package org.hsbc.service.manager;

import org.hsbc.exceptions.ProjectLimitExceededException;
import org.hsbc.exceptions.ProjectNotFoundException;
import org.hsbc.exceptions.WrongProjectDateException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

public interface ManagerServiceInterface {
    public Project viewProjectDetails(int projectId) throws ProjectNotFoundException;
    boolean addNewProject(Project project) throws ProjectLimitExceededException, WrongProjectDateException;
    boolean projectAssign(Project project, User user) throws ProjectLimitExceededException;
    boolean bugAssignToDeveloper(Bug bug, User user);

    boolean acceptBug(Bug bug, boolean accepted);

    boolean bugClose(Bug bug);

}
