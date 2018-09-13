package mk.edu.ukim.feit.gjorgjim.unitechnet.models;

/**
 * Created by gjmarkov on 13.9.2018.
 */

public class Notification {

  public final String NEW_PROBLEM_IN_COURSE = "NEW_PROBLEM_IN_COURSE";
  public final String NEW_ANSWER_IN_PROBLEM = "NEW_ANSWER_IN_PROBLEM";

  private String courseId;
  private String problemId;
  private String type;

  public Notification() {
  }

  public Notification(String courseId, String problemId, String type) {
    this.courseId = courseId;
    this.problemId = problemId;
    this.type = type;
  }

  public String getCourseId() {
    return courseId;
  }

  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }

  public String getProblemId() {
    return problemId;
  }

  public void setProblemId(String problemId) {
    this.problemId = problemId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
