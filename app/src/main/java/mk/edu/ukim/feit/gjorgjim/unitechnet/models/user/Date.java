package mk.edu.ukim.feit.gjorgjim.unitechnet.models.user;

/**
 * Created by gjmarkov on 08.7.2018.
 */

public class Date {
  private int year;
  private int month;
  private int day;

  public Date() {
  }

  public Date(int year, int month, int day) {
    this.year = year;
    this.month = month;
    this.day = day;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  public int getDay() {
    return day;
  }

  public void setDay(int day) {
    this.day = day;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof Date) {
      Date date = (Date) obj;
      if(year == date.getYear() &&
        month == date.getMonth() &&
        day == date.getDay()) {
        return true;
      }
    }
    return false;
  }
}
