package org.hsbc.dao.manager;

import org.hsbc.exceptions.ProjectLimitExceededException;
import org.hsbc.exceptions.WrongProjectDateException;
import org.hsbc.model.Project;
import org.hsbc.model.User;

public interface ManagerDaoInterface {
    void createNewProject(Project project) throws ProjectLimitExceededException, WrongProjectDateException;
    void assignProject(Project project, User user);
    void assignBug();
}
