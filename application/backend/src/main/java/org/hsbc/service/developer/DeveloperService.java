package org.hsbc.service.developer;

import org.hsbc.dao.developer.DeveloperDaoInterface;
import org.hsbc.exceptions.BugNotAcceptedException;
import org.hsbc.exceptions.NoBugsAssignedException;
import org.hsbc.model.Bug;
import org.hsbc.model.User;

import java.util.List;

public class DeveloperService implements DeveloperServiceInterface{
    private DeveloperDaoInterface dao;

    public DeveloperService(DeveloperDaoInterface dao) {
        this.dao = dao;
    }

    @Override
    public Boolean bugStatusChange(Bug bug) throws BugNotAcceptedException {
        try {
            boolean result = dao.changeBugStatusToMarked(bug);
            return result;
        }catch (IllegalArgumentException e){
            throw new BugNotAcceptedException(e.getMessage(),e);
        }
    }

    @Override
    public List<Bug> getAssignedBugs(User user) throws NoBugsAssignedException {
        try {
            List<Bug> bugs = dao.readAssignedBugs(user);
            return bugs;
        } catch (IllegalArgumentException e){
            throw new NoBugsAssignedException(e.getMessage(), e);
        }
    }
}
