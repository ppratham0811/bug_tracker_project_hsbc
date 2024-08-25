package org.hsbc.service.manager;

import org.hsbc.dao.manager.ManagerDaoInterface;
import org.hsbc.exceptions.NotAuthorizedException;
import org.hsbc.exceptions.ProjectLimitExceededException;
import org.hsbc.exceptions.ProjectNotFoundException;
import org.hsbc.exceptions.WrongProjectDateException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

public class ManagerService implements ManagerServiceInterface {
    ManagerDaoInterface dao = null;

    public ManagerService(ManagerDaoInterface dao) {
        this.dao = dao;
    }

    public ManagerService() {

    }

//    @Override
//    public Project viewProjectDetails(int projectId) throws ProjectNotFoundException {
//        try {
//            Project project = dao.getProjectDetails(projectId);
//            return project;
//        }catch (IllegalArgumentException e){
//            throw new ProjectNotFoundException(e.getMessage(),e);
//        }
//    }

    @Override
    public boolean addNewProject(int userId, Project project) throws ProjectLimitExceededException, WrongProjectDateException, NotAuthorizedException {
        try {
            boolean result = dao.createNewProject(userId, project);
            return result;
        } catch (WrongProjectDateException e) {
            throw new WrongProjectDateException(e.getMessage(), e);
        } catch (ProjectLimitExceededException e) {
            throw new ProjectLimitExceededException(e.getMessage(), e);
        }
    }

    @Override
    public boolean projectAssign(int userId, Project project, User user) throws ProjectLimitExceededException, NotAuthorizedException {
        try {
            boolean result = dao.assignProject(userId, project, user);
            return result;
        } catch (ProjectLimitExceededException e) {
            throw new ProjectLimitExceededException(e.getMessage(), e);
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException(e.getMessage());
        }
    }

    @Override
    public boolean bugAssignToDeveloper(int userId, Bug bug, User user) throws NotAuthorizedException {
        try {
            boolean result = dao.assignBugToDeveloper(userId, bug, user);
            return result;
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException(e.getMessage());
        }
    }

    @Override
    public boolean acceptBug(int userId, Bug bug, boolean accepted) throws NotAuthorizedException {
        try {
            boolean result = dao.acceptOrRejectBug(userId, bug, accepted);
            return result;
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException(e.getMessage());
        }
    }

    @Override
    public boolean bugClose(int userId, Bug bug) throws NotAuthorizedException {
        try {
            boolean result = dao.closeBug(userId, bug);
            return result;
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException(e.getMessage());
        }
    }
}
