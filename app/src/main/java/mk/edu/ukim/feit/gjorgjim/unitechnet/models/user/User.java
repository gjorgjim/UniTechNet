package mk.edu.ukim.feit.gjorgjim.unitechnet.models.user;

import java.io.Serializable;
import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.enums.UserGender;

/**
 * Created by gjmarkov on 15.5.2018.
 */

public class User implements Serializable {
  private String username;
  private String firstName;
  private String lastName;
  private String title;
  private String email;
  private String birthday;
  private UserGender gender;
  private HashMap<String, Boolean> connections;
  private HashMap<String, Course> courses;
  private HashMap<String, Experience> experiences;
  private HashMap<String, Education> educations;

  public User() {
  }

  public User(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public UserGender getGender() {
    return gender;
  }

  public void setGender(UserGender gender) {
    this.gender = gender;
  }

  public HashMap<String, Experience> getExperiences() {
    return experiences;
  }

  public void setExperiences(HashMap<String, Experience> experiences) {
    this.experiences = experiences;
  }

  public HashMap<String, Education> getEducations() {
    return educations;
  }

  public void setEducations(HashMap<String, Education> educations) {
    this.educations = educations;
  }

  public HashMap<String, Course> getCourses() {
    return courses;
  }

  public void setCourses(HashMap<String, Course> courses) {
    this.courses = courses;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public HashMap<String, Boolean> getConnections() {
    return connections;
  }

  public void setConnections(HashMap<String, Boolean> connections) {
    this.connections = connections;
  }
}
