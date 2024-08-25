# Project Details

## Topic: Bug Tracking System

## Overview

The **Bug Tracking Management System** is designed to help teams efficiently manage projects and track bugs throughout the software development lifecycle. This system facilitates collaboration among managers, developers, and testers, ensuring smooth project execution and prompt resolution of issues.

**Note:** For diagrams, please refer `documents/diagrams.docx`.
To start the frontend application, go to `application/frontend/src/Home/index.html`
As a simple test in the `application/backend/src/main/java/org/hsbc/TestEnv.java`, we have included test code.
The view the sql file, go to `application/bug_tracker.sql`, import this file in the MySQL workbench and run the insert and alter commands to get the tables
## Models

### User

The `User` model represents a user in the system with various attributes that help identify and categorize their role and activity.

**Data Members:**

- `userId` (int): Unique identifier for the user.
- `username` (String): Unique username for login purposes.
- `userEmail` (String): Email address of the user.
- `fullName` (String): Full name of the user.
- `userPassword` (String): Password for user authentication.
- `userRole` (UserRole): Role of the user in the system (e.g., Manager, Developer, Tester).
- `projectCount` (int): Number of projects the user is associated with.
- `lastLoggedIn` (LocalDateTime): Timestamp of the user's last login.

### Project

The `Project` model represents a project in the system, containing details about its management, status, and associated bugs.

**Data Members:**

- `projectName` (String): Name of the project.
- `projectStatus` (ProjectStatus): Current status of the project (e.g., Completed, In Progress).
- `projectManager` (int): User ID of the manager overseeing the project.
- `startDate` (LocalDate): Start date of the project.
- `noOfBugs` (int): Number of bugs currently associated with the project.

### Bug

The `Bug` model tracks individual bugs within projects, including their description, severity, and current status.

**Data Members:**

- `bugName` (String): Name of the bug.
- `bugDescription` (String): Detailed description of the bug.
- `createdBy` (int): User ID of the person who reported the bug.
- `createdOn` (LocalDateTime): Timestamp when the bug was reported.
- `severityLevel` (SeverityLevel): Severity level of the bug.
- `projectId` (int): ID of the project the bug is associated with.
- `bugStatus` (BugStatus): Current status of the bug (e.g., Marked, Completed, In Progress).
- `accepted` (boolean): Indicator of whether the bug has been accepted for resolution.

## Enums Defined in This Project

### UserRole

Represents the different roles a user can have within the system.

- `MANAGER`: User responsible for overseeing projects and managing team members.
- `DEVELOPER`: User involved in the development of software projects.
- `TESTER`: User responsible for testing the software and identifying bugs.

### BugStatus

Defines the various states a bug can be in throughout its lifecycle.

- `MARKED`: Bug has been reported and is awaiting further action.
- `COMPLETED`: Bug has been resolved.
- `IN_PROGRESS`: Bug is currently being worked on.

### ProjectStatus

Indicates the current state of a project.

- `COMPLETED`: Project work has been finished.
- `IN_PROGRESS`: Project is currently ongoing

## Layered Architecture

The **Layered Architecture** in software development divides an application into distinct layers, each with specific responsibilities, promoting separation of concerns and ease of maintenance. In a typical layered architecture for a web application:

- **Controller Layer**: Acts as the entry point for the application. It is typically implemented as a REST API that handles incoming HTTP requests, processes them, and returns appropriate responses. The controller layer calls the service layer to execute business logic.
- **Service Layer**: Contains the core business logic of the application. It orchestrates the application's workflows by managing operations and data transformation. The service layer calls the DAO layer to interact with the data source.
- **DAO (Data Access Object) Layer**: Responsible for direct interaction with the database. It provides methods to perform CRUD (Create, Read, Update, Delete) operations on the data. The DAO layer abstracts the underlying database interactions and is called by the service layer to fetch or persist data.

This structure allows for a clear separation of concerns, where each layer focuses on a specific part of the application, enhancing modularity and making the system easier to develop, test, and maintain

## DAO Layer

### UserDao

The `UserDao` class handles all database operations common to all types of users. Below are the key methods available in the `UserDao` class:

- **`registerUser`**
  - **Parameters:** `User` object
  - **Return Type:** `boolean`
  - **Exception Thrown:** `DuplicateUserException`
  - **Description:** Registers a new user in the system. Returns `true` if registration is successful, otherwise throws a `DuplicateUserException` if the user already exists.
- **`loginUser`**
  - **Parameters:** `String username`, `String password`
  - **Return Type:** `User`
  - **Exception Thrown:** `UserNotFoundException`
  - **Description:** Authenticates a user based on the provided username and password. Returns the `User` object if authentication is successful, otherwise throws a `UserNotFoundException`.
- **`readAssignedProjects`**
  - **Parameters:** `User` object
  - **Return Type:** `List<Project>`
  - **Exception Thrown:** `NoAssignedProjectException`
  - **Description:** Retrieves a list of projects assigned to the specified user. Returns a list of `Project` objects if projects are found, otherwise throws a `NoAssignedProjectException`.
- **`getUserDetails`**
  - **Parameters:** `int userId`
  - **Return Type:** `User`
  - **Exception Thrown:** `UserNotFoundException`
  - **Description:** Fetches detailed information of a user based on the user ID. Returns the `User` object if found, otherwise throws a `UserNotFoundException`.
- **`getProjectMembers`**
  - **Parameters:** `Project` object
  - **Return Type:** `List<Integer>`
  - **Exception Thrown:** `ProjectNotFoundException`
  - **Description:** Retrieves a list of user IDs for all members assigned to a specified project. Returns a list of user IDs if the project is found, otherwise throws a `ProjectNotFoundException`.

### ManagerDao

The `ManagerDao` class handles all database operations related to the manager only. Below are the key methods available in the `ManagerDao` class:

- **`createNewProject`**
  - **Parameters:** `int userId`, `Project` object
  - **Return Type:** `boolean`
  - **Exception Thrown:** `ProjectLimitExceededException`, `WrongProjectDateException`, `NotAuthorizedException`
  - **Description:** Allows a manager to create a new project. Returns `true` if the project is successfully created. Throws `ProjectLimitExceededException` if the manager has exceeded the number of allowable projects, `WrongProjectDateException` if the project dates are incorrect, and `NotAuthorizedException` if the user is not authorized to create projects.
- **`assignProject`**
  - **Parameters:** `int userId`, `Project` object, `User` object
  - **Return Type:** `boolean`
  - **Exception Thrown:** `ProjectLimitExceededException`, `NotAuthorizedException`
  - **Description:** Assigns a project to a user. Returns `true` if the assignment is successful. Throws `ProjectLimitExceededException` if the project limit for the user is exceeded and `NotAuthorizedException` if the user is not authorized to assign projects.
- **`assignBugToDeveloper`**
  - **Parameters:** `int userId`, `Bug` object, `User` object
  - **Return Type:** `boolean`
  - **Exception Thrown:** `NotAuthorizedException`
  - **Description:** Assigns a bug to a developer. Returns `true` if the bug assignment is successful, otherwise throws `NotAuthorizedException` if the user is not authorized to assign bugs.
- **`acceptOrRejectBug`**
  - **Parameters:** `int userId`, `Bug` object, `boolean accepted`
  - **Return Type:** `boolean`
  - **Exception Thrown:** `NotAuthorizedException`
  - **Description:** Allows a manager to accept or reject a bug. Returns `true` if the action is successful, otherwise throws `NotAuthorizedException` if the user is not authorized to perform this action.
- **`closeBug`**
  - **Parameters:** `int userId`, `Bug` object
  - **Return Type:** `boolean`
  - **Exception Thrown:** `NotAuthorizedException`
  - **Description:** Allows a manager to close a bug. Returns `true` if the bug is successfully closed, otherwise throws `NotAuthorizedException` if the user is not authorized to close bugs.

### DeveloperDao

The `DeveloperDao` class handles all database operations related to developers managing bugs assigned to them. Below are the key methods available in the `DeveloperDao` class:

- **`changeBugStatusToMarked`**
  - **Parameters:** `Bug` object
  - **Return Type:** `boolean`
  - **Exception Thrown:** `BugNotAcceptedException`
  - **Description:** Allows a developer to change the status of a bug to "Marked". Returns `true` if the status change is successful. Throws `BugNotAcceptedException` if the bug has not been accepted for work.
- **`readAssignedBugs`**
  - **Parameters:** `User` object
  - **Return Type:** `List<Bug>`
  - **Exception Thrown:** `NoBugsAssignedException`
  - **Description:** Retrieves a list of bugs assigned to the specified developer. Returns a list of `Bug` objects if there are assigned bugs. Throws `NoBugsAssignedException` if no bugs are assigned to the developer.

### TesterDao

The `TesterDao` class manages database operations related to testers, including reporting new bugs and viewing bugs they have reported. Below are the key methods available in the `TesterDao` class:

- **`reportNewBug`**
  - **Parameters:** `int userId`, `Bug` object, `Project` object
  - **Return Type:** `boolean`
  - **Exception Thrown:** `UserNotFoundException`, `NotAuthorizedException`
  - **Description:** Allows a tester to report a new bug within a project. Returns `true` if the bug report is successful. Throws `UserNotFoundException` if the user ID does not exist and `NotAuthorizedException` if the user is not authorized to report bugs.
- **`viewOwnBugs`**
  - **Parameters:** `User` object, `Project` object
  - **Return Type:** `List<Bug>`
  - **Exception Thrown:** `BugsNotFoundException`
  - **Description:** Retrieves a list of bugs reported by the specified tester for a given project. Returns a list of `Bug` objects if bugs are found. Throws `BugsNotFoundException` if no bugs are reported by the tester for the project.

## Service Layer

The **Service Layer** has similar method blueprint to that of the DAO layer, the service layer needs to be called from the **Controller layer**, which is usually the REST API.

## Testing methods

All the testing methods have been mentioned in the documents/Test Cases.xlsx file

