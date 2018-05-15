package mk.edu.ukim.feit.gjorgjim.unitechnet.models;

import java.util.Date;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.enums.UserSex;

/**
 * Created by gjmarkov on 15.5.2018.
 */

public class User {
  private String username;
  private String firstName;
  private String lastName;
  private String email;
  private Date birthday;
  private UserSex sex;

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

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  public UserSex getSex() {
    return sex;
  }

  public void setSex(UserSex sex) {
    this.sex = sex;
  }
}
