package org.hsbc.model;

import org.hsbc.model.enums.BugStatus;
import org.hsbc.model.enums.SeverityLevel;

import java.time.LocalDateTime;

public class Bug {
    // bug_name VARCHAR(500),
    // bug_description VARCHAR(1000),
    // created_by INT,
    // created_on DATETIME,
    // severity_level ENUM('low', 'mid', 'high'),
    // bug_status ENUM('in progress', 'closed'),
    // project_id INT

    private String bugName, bugDescription;
    private int createdBy;
    private LocalDateTime createdOn;
    private SeverityLevel severityLevel;
    private int projectId, bugId;
    private BugStatus bugStatus;
    private boolean accepted;

    private static int firstBugId = 1000;

    public Bug() {
        // when a blank object is created using this constructor, set the values
        // manually
        // this is done to avoid userId increment
    }

    public Bug(String bugName, int createdBy, LocalDateTime createdOn, SeverityLevel severityLevel, int projectId) {
        this.bugName = bugName;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.severityLevel = severityLevel;
        this.projectId = projectId;
        bugId = ++firstBugId;
    }

    @Override
    protected void finalize() throws Throwable {
        bugId = --firstBugId;
    }

    public void setBugId(int i) {
        bugId = i;
    }

    public int getBugId() {
        return bugId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
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

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
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

    public BugStatus getBugStatus() {
        return bugStatus;
    }

    public void setBugStatus(BugStatus bugStatus) {
        this.bugStatus = bugStatus;
    }
}
