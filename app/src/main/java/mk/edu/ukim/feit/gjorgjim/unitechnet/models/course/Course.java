package mk.edu.ukim.feit.gjorgjim.unitechnet.models.course;

import java.util.HashMap;
import java.util.List;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;

/**
 * Created by gjmarkov on 05.7.2018.
 */

public class Course {
  private String courseId;
  private String name;
  private String description;
  private HashMap<String, User> subscribedUsers;
  private HashMap<String, Problem> solvedProblems;
  private HashMap<String, Problem> unsolvedProblems;

  public Course() {
  }

  public String getCourseId() {
    return courseId;
  }

  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public HashMap<String, User> getSubscribedUsers() {
    return subscribedUsers;
  }

  public void setSubscribedUsers(HashMap<String, User> subscribedUsers) {
    this.subscribedUsers = subscribedUsers;
  }

  public HashMap<String, Problem> getSolvedProblems() {
    return solvedProblems;
  }

  public void setSolvedProblems(HashMap<String, Problem> solvedProblems) {
    this.solvedProblems = solvedProblems;
  }

  public HashMap<String, Problem> getUnsolvedProblems() {
    return unsolvedProblems;
  }

  public void setUnsolvedProblems(HashMap<String, Problem> unsolvedProblems) {
    this.unsolvedProblems = unsolvedProblems;
  }
}
