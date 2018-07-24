package mk.edu.ukim.feit.gjorgjim.unitechnet.models.user;

/**
 * Created by gjmarkov on 05.7.2018.
 */

public class Education {
  private String school;
  private String degree;
  private String grade;
  private Date startDate;
  private Date endDate;

  public Education() {
  }

  public Education(String school, String degree, String grade, Date startDate, Date endDate) {
    this.school = school;
    this.degree = degree;
    this.grade = grade;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public Education(String school, String degree, String grade, Date startDate) {
    this.school = school;
    this.degree = degree;
    this.grade = grade;
    this.startDate = startDate;
  }

  public String getSchool() {
    return school;
  }

  public void setSchool(String school) {
    this.school = school;
  }

  public String getDegree() {
    return degree;
  }

  public void setDegree(String degree) {
    this.degree = degree;
  }

  public String getGrade() {
    return grade;
  }

  public void setGrade(String grade) {
    this.grade = grade;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }
}
