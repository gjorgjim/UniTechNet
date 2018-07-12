package mk.edu.ukim.feit.gjorgjim.unitechnet.models.user;

/**
 * Created by gjmarkov on 05.7.2018.
 */

public class Experience {
  private String jobTitle;
  private String company;
  private Date startDate;
  private Date endDate;

  public Experience() {
  }

  public Experience(String jobTitle, String company, Date startDate) {
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
