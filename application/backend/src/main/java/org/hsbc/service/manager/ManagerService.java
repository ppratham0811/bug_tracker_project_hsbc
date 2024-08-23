package org.hsbc.service.manager;

import org.hsbc.dao.manager.ManagerDaoInterface;
import org.hsbc.exceptions.ProjectLimitExceededException;
import org.hsbc.exceptions.ProjectNotFoundException;
import org.hsbc.exceptions.WrongProjectDateException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

public class ManagerService implements ManagerServiceInterface{
    ManagerDaoInterface dao = null;

    public ManagerService(ManagerDaoInterface dao) {
        this.dao = dao;
    }

    @Override
    public Project viewProjectDetails(int projectId) throws ProjectNotFoundException {
        try {
            Project project = dao.getProjectDetails(projectId);
            return project;
        }catch (IllegalArgumentException e){
            throw new ProjectNotFoundException(e.getMessage(),e);
        }
    }

    @Override
    public boolean addNewProject(Project project) throws ProjectLimitExceededException, WrongProjectDateException {
        try {
            boolean result = dao.createNewProject(project);
            return result;
        }catch (WrongProjectDateException e){
            throw new WrongProjectDateException(e.getMessage(),e);
        }catch (ProjectLimitExceededException e){
            throw new ProjectLimitExceededException(e.getMessage(), e);
        }
    }

    @Override
    public boolean projectAssign(Project project, User user) throws ProjectLimitExceededException {
        try {
            boolean result = dao.assignProject(project,user);
            return result;
        }catch (IllegalArgumentException e){
            throw new ProjectLimitExceededException(e.getMessage(),e);
        }
    }

    @Override
    public boolean bugAssignToDeveloper(Bug bug, User user) {

            boolean result = dao.assignBugToDeveloper(bug,user);
            return result;



    }

    @Override
    public boolean acceptBug(Bug bug, boolean accepted) {
        boolean result = dao.acceptOrRejectBug(bug,accepted);
        return result;
    }

    @Override
    public boolean bugClose(Bug bug) {
        boolean result = dao.closeBug(bug);
        return result;
    }
}
