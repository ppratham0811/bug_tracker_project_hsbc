package org.hsbc.service.developer;

import org.hsbc.dao.developer.DeveloperDao;
import org.hsbc.exceptions.BugNotAcceptedException;
import org.hsbc.exceptions.NoBugsAssignedException;
import org.hsbc.model.Bug;
import org.hsbc.model.User;

import java.util.List;

public interface DeveloperServiceInterface {
    public Boolean bugStatusChange(Bug bug) throws BugNotAcceptedException;
    public List<Bug> getAssignedBugs(User user)  throws NoBugsAssignedException;



}
