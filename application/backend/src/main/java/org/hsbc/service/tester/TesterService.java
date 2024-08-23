package org.hsbc.service.tester;

import org.hsbc.dao.tester.TesterDaoInterface;
import org.hsbc.exceptions.BugsNotFoundException;
import org.hsbc.exceptions.NotAuthorizedException;
import org.hsbc.exceptions.ProjectNotFoundException;
import org.hsbc.exceptions.UserNotFoundException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

import java.util.Collection;
import java.util.List;

public class TesterService implements TesterServiceInterface{
    private TesterDaoInterface dao = null ;

    public TesterService( TesterDaoInterface dao) {
        this.dao = dao;
    }

    @Override
    public boolean bugReport(int userId, Bug bug, Project project) throws UserNotFoundException, NotAuthorizedException {
        try {
            boolean result = dao.reportNewBug(userId, bug, project);
            return result;
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public Collection<Bug> ownBugs(User currentUser, Project project) throws BugsNotFoundException {
        try {
            Collection<Bug> bugList = dao.viewOwnBugs(currentUser,project);
            return bugList;
        }catch (BugsNotFoundException e){
            throw new BugsNotFoundException(e.getMessage(),e);
        }
    }
}
