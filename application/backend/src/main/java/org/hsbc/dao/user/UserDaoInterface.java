package org.hsbc.dao.user;

import org.hsbc.exceptions.DuplicateUserException;
import org.hsbc.model.User;
import org.hsbc.exceptions.UserNotFoundException;

public interface UserDaoInterface {

    void registerUser(User user) throws DuplicateUserException;

    User loginUser(String username, String password) throws UserNotFoundException;

}
