package mk.edu.ukim.feit.gjorgjim.unitechnet.models;

import java.io.Serializable;

/**
 * Created by gjmarkov on 13.9.2018.
 */

public class Notification implements Serializable{

  public static final String NEW_PROBLEM_IN_COURSE = "NEW_PROBLEM_IN_COURSE";
  public static final String NEW_ANSWER_IN_PROBLEM = "NEW_ANSWER_IN_PROBLEM";
  public static final String NEW_MESSAGE = "NEW_MESSAGE";

  private String courseId;
  private String problemId;
  private String senderId;
  private String type;
  private String date;
  private boolean seen;

  public Notification() {
  }

  public Notification(String courseId, String problemId, String type, String date) {
    this.courseId = courseId;
    this.problemId = problemId;
    this.type = type;
    this.date = date;
  }

  public Notification(String senderId, String type, String date) {
    this.senderId = senderId;
    this.type = type;
    this.date = date;
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

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getSenderId() {
    return senderId;
  }

  public void setSenderId(String senderId) {
    this.senderId = senderId;
  }

  public boolean getSeen() {
    return seen;
  }

  public void setSeen(boolean seen) {
    this.seen = seen;
  }

  @Override
  public String toString() {
    return "Notification{" + "courseId='" + courseId + '\'' + ", problemId='" + problemId + '\'' + ", type='" + type
      + '\'' + ", date='" + date + '\'' + '}';
  }
}
