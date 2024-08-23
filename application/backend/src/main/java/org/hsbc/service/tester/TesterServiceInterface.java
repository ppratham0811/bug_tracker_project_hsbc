package org.hsbc.service.tester;

import org.hsbc.exceptions.BugsNotFoundException;
import org.hsbc.exceptions.UserNotFoundException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

import java.util.Collection;

public interface TesterServiceInterface {
    public boolean bugReport(Bug bug, Project project);
    public Collection<Bug> ownBugs(User currentUser, Project project) throws BugsNotFoundException, UserNotFoundException;

}
