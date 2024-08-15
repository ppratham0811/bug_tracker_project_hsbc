package org.hsbc.model;

import org.hsbc.model.enums.ProjectOrBugStatus;

import java.time.LocalDate;

public class Project {
    private String projectName;
    private ProjectOrBugStatus projectStatus;
    private String projectManager;
    private LocalDate startDate;
    private int noOfBugs;

    public Project() {}

    public Project(String projectName, ProjectOrBugStatus projectStatus, String projectManager, LocalDate startDate) {
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

    public String getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(String projectManager) {
        this.projectManager = projectManager;
    }

    public LocalDate getStart_date() {
        return startDate;
    }

    public void setStart_date(LocalDate start_date) {
        this.startDate = start_date;
    }

    public int getNoOfBugs() {
        return noOfBugs;
    }

    public void setNoOfBugs(int noOfBugs) {
        this.noOfBugs = noOfBugs;
    }
}
