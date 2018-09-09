package mk.edu.ukim.feit.gjorgjim.unitechnet.models.course;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;

/**
 * Created by gjmarkov on 05.7.2018.
 */

public class Course implements Serializable {
  private String courseId;
  private String name;
  private String description;
  private HashMap<String, Boolean> subscribedUsers;
  private HashMap<String, Problem> problems;

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

  public HashMap<String, Boolean> getSubscribedUsers() {
    return subscribedUsers;
  }

  public void setSubscribedUsers(HashMap<String, Boolean> subscribedUsers) {
    this.subscribedUsers = subscribedUsers;
  }

  public HashMap<String, Problem> getProblems() {
    return problems;
  }

  public void setProblems(HashMap<String, Problem> problems) {
    this.problems = problems;
  }

  @Override
  public String toString() {
    return "Course{" + "courseId='" + courseId + '\'' + ", name='" + name + '\'' + ", description='" + description
      + '\'' + ", subscribedUsers=" + subscribedUsers + ", problems=" + problems + '}';
  }
}
