package mk.edu.ukim.feit.gjorgjim.unitechnet.models.course;

import java.util.Date;
import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;

/**
 * Created by gjmarkov on 05.7.2018.
 */

public class Problem {
  private String name;
  private String description;
  private User author;
  private HashMap<String, Answer> answers;
  private Date date;
  private boolean solved;

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

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
  }

  public HashMap<String, Answer> getAnswers() {
    return answers;
  }

  public void setAnswers(HashMap<String, Answer> answers) {
    this.answers = answers;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public boolean isSolved() {
    return solved;
  }

  public void setSolved(boolean solved) {
    this.solved = solved;
  }
}
