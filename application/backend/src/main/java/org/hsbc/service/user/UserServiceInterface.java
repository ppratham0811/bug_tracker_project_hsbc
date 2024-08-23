package org.hsbc.service.user;

import org.hsbc.exceptions.*;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

import java.util.List;

public interface UserServiceInterface {
    boolean addUser(User user) throws DuplicateUserException;

    User userLogin(String username, String password) throws UserNotFoundException;

    public List<Project> viewAssignedProjects(User user) throws NoAssignedProjectException;

    User viewUserDetails(int userId) throws UserNotFoundException;

    List<Integer> viewProjectMembers(Project project) throws ProjectNotFoundException;

//    Bug viewBugDetails(int bugId) throws NoSuchBugException;

}
