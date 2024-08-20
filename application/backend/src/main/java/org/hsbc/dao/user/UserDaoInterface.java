package org.hsbc.dao.user;

import org.hsbc.db.JdbcConnector;
import org.hsbc.exceptions.DuplicateUserException;
import org.hsbc.model.User;
import org.hsbc.exceptions.UserNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface UserDaoInterface {

    void registerUser(User user) throws DuplicateUserException;

    User loginUser(String username, String password) throws UserNotFoundException;
    public void readAssignedProjects(User user);
}
