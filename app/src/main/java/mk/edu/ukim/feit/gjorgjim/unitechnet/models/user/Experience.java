package mk.edu.ukim.feit.gjorgjim.unitechnet.models.user;

/**
 * Created by gjmarkov on 05.7.2018.
 */

public class Experience {
  private String jobTitle;
  private String company;
  private String startDate;
  private String endDate;

  public Experience() {
  }

  public Experience(String jobTitle, String company, String startDate) {
    this.jobTitle = jobTitle;
    this.company = company;
    this.startDate = startDate;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }
}
