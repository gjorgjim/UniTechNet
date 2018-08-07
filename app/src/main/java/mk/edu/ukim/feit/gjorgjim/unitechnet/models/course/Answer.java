package mk.edu.ukim.feit.gjorgjim.unitechnet.models.course;

import java.util.Date;
import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;

/**
 * Created by gjmarkov on 05.7.2018.
 */

public class Answer {
  private String description;
  private User author;
  private HashMap<String, Comment> comments;
  private Date date;

  public Answer() {
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

  public HashMap<String, Comment> getComments() {
    return comments;
  }

  public void setComments(HashMap<String, Comment> comments) {
    this.comments = comments;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
