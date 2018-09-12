package mk.edu.ukim.feit.gjorgjim.unitechnet.models.course;

import java.util.Date;
import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;

/**
 * Created by gjmarkov on 05.7.2018.
 */

public class Answer {
  private String description;
  private HashMap<String, User> author;
  private Date date;
  private boolean isAnswer;

  public Answer() {
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

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public boolean isIsAnswer() {
    return isAnswer;
  }

  public void setIsAnswer(boolean answer) {
    isAnswer = answer;
  }
}
