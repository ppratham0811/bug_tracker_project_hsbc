package org.hsbc.dao.user;

import org.hsbc.exceptions.*;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

public interface UserDaoInterface {

    void registerUser(User user) throws DuplicateUserException;

    User loginUser(String username, String password) throws UserNotFoundException;
    public void readAssignedProjects(User user) throws NoAssignedProjectException;
    public void readDetails(User user) throws UserNotFoundException;
//    public void readProjectDetails(Project project) throws ProjectNotFoundException;
    public void readAssignedBugs(User user) throws NoBugsAssignedException; //assigned to a user
    public void readBugDetails(Bug bug) throws NoSuchBugException;
}
