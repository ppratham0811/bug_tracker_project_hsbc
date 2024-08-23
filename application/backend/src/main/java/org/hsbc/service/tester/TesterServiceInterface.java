package org.hsbc.service.tester;

import org.hsbc.exceptions.BugsNotFoundException;
import org.hsbc.exceptions.NotAuthorizedException;
import org.hsbc.exceptions.ProjectNotFoundException;
import org.hsbc.exceptions.UserNotFoundException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

import java.util.Collection;

public interface TesterServiceInterface {
    public boolean bugReport(int userId, Bug bug, Project project) throws UserNotFoundException, NotAuthorizedException;
    public Collection<Bug> ownBugs(User currentUser, Project project) throws BugsNotFoundException;

}
