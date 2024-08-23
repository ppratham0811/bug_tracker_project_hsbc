package org.hsbc.dao.user;

import org.hsbc.db.JdbcConnector;
import org.hsbc.exceptions.DuplicateUserException;
import org.hsbc.model.User;
import org.hsbc.exceptions.UserNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hsbc.exceptions.*;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;

import java.util.List;

public interface UserDaoInterface {

  Boolean registerUser(User user) throws DuplicateUserException;

  User loginUser(String username, String password) throws UserNotFoundException;

  public List<Project> readAssignedProjects(User user) throws NoAssignedProjectException;

  User getUserDetails(int userId) throws UserNotFoundException;

  List<Integer> getProjectMembers(Project project) throws ProjectNotFoundException;

  // public void readProjectDetails(Project project) throws
  // ProjectNotFoundException;
//  Bug getBugDetails(int bugId) throws NoSuchBugException;
}
