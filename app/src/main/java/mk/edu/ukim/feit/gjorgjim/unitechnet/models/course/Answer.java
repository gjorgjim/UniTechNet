package mk.edu.ukim.feit.gjorgjim.unitechnet.models.course;

import java.io.Serializable;
import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;

/**
 * Created by gjmarkov on 05.7.2018.
 */

public class Answer implements Serializable{
  private String description;
  private HashMap<String, User> author;
  private String date;
  private boolean answer;

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

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public boolean isAnswer() {
    return answer;
  }

  public void setAnswer(boolean answer) {
    this.answer = answer;
  }
}
