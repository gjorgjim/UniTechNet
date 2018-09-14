package mk.edu.ukim.feit.gjorgjim.unitechnet.models;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;

/**
 * Created by gjmarkov on 14.9.2018.
 */

public class FeedItem {
  private Course course;
  private String problemId;

  public FeedItem() {
  }

  public FeedItem(Course course, String problemId) {
    this.course = course;
    this.problemId = problemId;
  }

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

  public String getProblemId() {
    return problemId;
  }

  public void setProblemId(String problemId) {
    this.problemId = problemId;
  }

  @Override
  public String toString() {
    return "FeedItem{" + "course=" + course + ", problemId='" + problemId + '\'' + '}';
  }
}
