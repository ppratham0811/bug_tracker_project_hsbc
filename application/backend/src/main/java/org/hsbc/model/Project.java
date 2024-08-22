package org.hsbc.model;

import org.hsbc.model.enums.ProjectStatus;

import java.time.LocalDate;

public class Project {

  private String projectName;
  private ProjectStatus projectStatus;
  private int projectManager, projectId;
  private LocalDate startDate;
  private int noOfBugs;
  private static int firstProjectId = 100;

  public Project() {
    // when a blank object is created using this constructor, set the values
    // manually
    // this is done to avoid userId increment
  }

  public Project(String projectName, int projectManager, LocalDate startDate) {
    this.projectName = projectName;
    this.projectManager = projectManager;
    this.startDate = startDate;
    projectId = ++firstProjectId;
  }

  @Override
  protected void finalize() throws Throwable {
    projectId = --firstProjectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public int getProjectId() {
    return projectId;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public ProjectStatus getProjectStatus() {
    return projectStatus;
  }

  public void setProjectStatus(ProjectStatus projectStatus) {
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
