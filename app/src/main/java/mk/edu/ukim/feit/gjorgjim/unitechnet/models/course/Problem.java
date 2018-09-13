package mk.edu.ukim.feit.gjorgjim.unitechnet.models.course;


import java.io.Serializable;
import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;

/**
 * Created by gjmarkov on 05.7.2018.
 */

public class Problem implements Serializable{
  private String name;
  private String description;
  private HashMap<String, User> author;
  private HashMap<String, Answer> answers;
  private String date;
  private boolean solved;
  private String answerId;

  public Problem() {
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

  public HashMap<String, User> getAuthor() {
    return author;
  }

  public void setAuthor(HashMap<String, User> author) {
    this.author = author;
  }

  public HashMap<String, Answer> getAnswers() {
    return answers;
  }

  public void setAnswers(HashMap<String, Answer> answers) {
    this.answers = answers;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public boolean isSolved() {
    return solved;
  }

  public void setSolved(boolean solved) {
    this.solved = solved;
  }

  public String getAnswerid() {
    return answerId;
  }

  public void setAnswerid(String answerid) {
    this.answerId = answerid;
  }
}
