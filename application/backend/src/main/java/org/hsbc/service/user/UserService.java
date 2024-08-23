package org.hsbc.service.user;

import org.hsbc.dao.user.UserDaoInterface;
import org.hsbc.exceptions.*;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

import java.util.List;

public class UserService implements UserServiceInterface{
    private UserDaoInterface dao = null ;

    public UserService(UserDaoInterface dao) {
        this.dao = dao;
    }

    @Override
    public boolean addUser(User user) throws DuplicateUserException {
        try {
            boolean result = dao.registerUser(user);
            return result;
        }catch (IllegalArgumentException e){
            throw new DuplicateUserException(e.getMessage(),e);
        }

    }

    @Override
    public User userLogin(String username, String password) throws UserNotFoundException {
        try {
            User user = dao.loginUser(username, password);
            return user;
        }catch (IllegalArgumentException e){
            throw new UserNotFoundException(e.getMessage(),e);
        }
    }

    @Override
    public List<Project> viewAssignedProjects(User user) throws NoAssignedProjectException {
        try{
            List<Project> projects = dao.readAssignedProjects(user);
            return projects;
        }catch (IllegalArgumentException e){
            throw new NoAssignedProjectException(e.getMessage(),e);
        }
    }

    @Override
    public User viewUserDetails(int userId) throws UserNotFoundException {
        try{
            User user = dao.getUserDetails(userId);
            return user;
        }catch (IllegalArgumentException e){
            throw new UserNotFoundException(e.getMessage(),e);
        }
    }

    @Override
    public List<Integer> viewProjectMembers(Project project) throws ProjectNotFoundException {
        try {
            List<Integer> projectMembers = dao.getProjectMembers(project);
            return projectMembers;
        }catch (IllegalArgumentException e){
            throw new ProjectNotFoundException(e.getMessage(), e);
        }
    }

//    @Override
//    public Bug viewBugDetails(int bugId) throws NoSuchBugException {
//        try {
//            Bug bug = dao.getBugDetails(bugId);
//            return bug;
//        }catch (IllegalArgumentException e){
//            throw new NoSuchBugException(e.getMessage(), e);
//        }
//    }
}
