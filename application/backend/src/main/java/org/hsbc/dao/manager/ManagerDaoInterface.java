package org.hsbc.dao.manager;

import org.hsbc.model.Project;

public interface ManagerDaoInterface {
    void createNewProject(Project project);
    void assignProject();
    void assignBug();
}
