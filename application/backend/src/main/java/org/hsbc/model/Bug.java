package org.hsbc.model;

import org.hsbc.model.enums.ProjectOrBugStatus;
import org.hsbc.model.enums.SeverityLevel;

import java.time.LocalDate;

public class Bug {
//    bug_name VARCHAR(500),
//    bug_description VARCHAR(1000),
//    created_by INT,
//    created_on DATETIME,
//    severity_level ENUM('low', 'mid', 'high'),
//    bug_status ENUM('in progress', 'closed'),
//    project_id INT

    private String bugName, bugDescription;
    private int createdBy;
    private LocalDate createdOn;
    private SeverityLevel severityLevel;
    private int projectId;
    private ProjectOrBugStatus bugStatus;

    public Bug() {}

    public Bug(String bugName, int createdBy, LocalDate createdOn, SeverityLevel severityLevel, int projectId) {
        this.bugName = bugName;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.severityLevel = severityLevel;
        this.projectId = projectId;
    }

    public String getBugName() {
        return bugName;
    }

    public void setBugName(String bugName) {
        this.bugName = bugName;
    }

    public String getBugDescription() {
        return bugDescription;
    }

    public void setBugDescription(String bugDescription) {
        this.bugDescription = bugDescription;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public SeverityLevel getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(SeverityLevel severityLevel) {
        this.severityLevel = severityLevel;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public ProjectOrBugStatus getBugStatus() {
        return bugStatus;
    }

    public void setBugStatus(ProjectOrBugStatus bugStatus) {
        this.bugStatus = bugStatus;
    }
}
