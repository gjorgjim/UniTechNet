package mk.edu.ukim.feit.gjorgjim.unitechnet.models.course;

import java.util.List;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;

/**
 * Created by gjmarkov on 05.7.2018.
 */

public class Course {
  private String name;
  private String description;
  private List<User> subscribedUsers;
  private List<Problem> solvedProblems;
  private List<Problem> unsolvedProblems;

  public Course() {
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

  public List<User> getSubscribedUsers() {
    return subscribedUsers;
  }

  public void setSubscribedUsers(List<User> subscribedUsers) {
    this.subscribedUsers = subscribedUsers;
  }

  public List<Problem> getSolvedProblems() {
    return solvedProblems;
  }

  public void setSolvedProblems(List<Problem> solvedProblems) {
    this.solvedProblems = solvedProblems;
  }

  public List<Problem> getUnsolvedProblems() {
    return unsolvedProblems;
  }

  public void setUnsolvedProblems(List<Problem> unsolvedProblems) {
    this.unsolvedProblems = unsolvedProblems;
  }
}
