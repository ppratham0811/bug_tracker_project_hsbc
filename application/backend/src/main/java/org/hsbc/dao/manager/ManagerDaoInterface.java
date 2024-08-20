package org.hsbc.dao.manager;

import org.hsbc.exceptions.ProjectLimitExceededException;
import org.hsbc.exceptions.ProjectNotFoundException;
import org.hsbc.exceptions.WrongProjectDateException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

public interface ManagerDaoInterface {
    Project getProjectDetails(int projectId) throws ProjectNotFoundException;

    void createNewProject(Project project) throws ProjectLimitExceededException, WrongProjectDateException;
    void assignProject(Project project, User user);
    void assignBug(Bug bug, int userId);

    void changeBugStatusToClosed(Bug bug);
}
