package org.hsbc.model;

import org.hsbc.model.enums.UserRole;

import java.time.LocalDate;

public class User {
//    username VARCHAR(100) NOT NULL,
//    full_name VARCHAR(100),
//    user_password VARCHAR(100) NOT NULL,
//    user_email VARCHAR(100),
//    user_role ENUM('manager', 'developer', 'tester') NOT NULL,
//    project_count INT,
//    last_logged_in DATETIME

    private String username, fullName, userPassword, userEmail;
    private UserRole userRole;
    private int projectCount, userId;
    private LocalDate lastLoggedIn;

    public User () {}

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User(String username, String userPassword, UserRole userRole, int userId) {
        this.username = username;
        this.userPassword = userPassword;
        this.userRole = userRole;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public int getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(int projectCount) {
        this.projectCount = projectCount;
    }

    public LocalDate getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(LocalDate lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }
}
