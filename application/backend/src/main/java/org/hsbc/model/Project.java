package org.hsbc.model;

import org.hsbc.model.enums.ProjectOrBugStatus;

import java.time.LocalDate;
import java.time.LocalDate;

public class Project {
    static {
        int projectId = 101;
    }

    private String projectName;
    private ProjectOrBugStatus projectStatus;
    private int projectManager;
    private LocalDate startDate;
    private int noOfBugs;

    public Project() {
    }

    public Project(String projectName, int projectManager, LocalDate startDate) {
        this.projectName = projectName;
        this.projectManager = projectManager;
        this.startDate = startDate;
    }

    public Project(String projectName, int projectManager, LocalDate startDate, ProjectOrBugStatus projectStatus) {
        this.projectName = projectName;
        this.projectStatus = projectStatus;
        this.projectManager = projectManager;
        this.startDate = startDate;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public ProjectOrBugStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(ProjectOrBugStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public int getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(int projectManager) {
        this.projectManager = projectManager;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate start_date) {
        this.startDate = start_date;
    }

    public int getNoOfBugs() {
        return noOfBugs;
    }

    public void setNoOfBugs(int noOfBugs) {
        this.noOfBugs = noOfBugs;
    }
}
