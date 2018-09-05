package mk.edu.ukim.feit.gjorgjim.unitechnet.models.user;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by gjmarkov on 08.7.2018.
 */

public class Date implements Serializable{
  private int year;
  private int month;
  private int day;
  private int hour;
  private int minute;
  private int second;

  public Date() {
  }

  public Date(int year, int month, int day) {
    this.year = year;
    this.month = month;
    this.day = day;
  }

  public Date(int year, int month, int day, int hour, int minute, int second) {
    this.year = year;
    this.month = month;
    this.day = day;
    this.hour = hour;
    this.minute = minute;
    this.second = second;
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

  public int getHour() {
    return hour;
  }

  public void setHour(int hour) {
    this.hour = hour;
  }

  public int getMinute() {
    return minute;
  }

  public void setMinute(int minute) {
    this.minute = minute;
  }

  public int getSecond() {
    return second;
  }

  public void setSecond(int second) {
    this.second = second;
  }

  public boolean isAfter(Date date) {
    if (year > date.year) {
      return true;
    } else if (year == date.year) {
      if (month > date.month) {
        return true;
      } else if (month == date.month) {
        if (day > date.day) {
          return true;
        } else if (day == date.day) {
          if (hour > date.hour) {
            return true;
          } else if (hour == date.hour) {
            if (minute > date.minute) {
              return true;
            } else if (minute == date.minute) {
              if (second > date.second) {
                return true;
              } else {
                return false;
              }
            }
          }
        }
      }
    }
    return false;
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

  public static Date getDate() {
    Date date = new Date();

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy/hh/mm/ss", new Locale("en"));
    String[] stringDate = simpleDateFormat.format(calendar.getTime()).split("/");
    date.setMonth(Integer.parseInt(stringDate[0]));
    date.setDay(Integer.parseInt(stringDate[1]));
    date.setYear(Integer.parseInt(stringDate[2]));
    date.setHour(Integer.parseInt(stringDate[3]));
    date.setMinute(Integer.parseInt(stringDate[4]));
    date.setSecond(Integer.parseInt(stringDate[5]));

    return date;
  }

  @Override
  public String toString() {
    return "Date{" + "year=" + year + ", month=" + month + ", day=" + day + ", hour=" + hour + ", minute=" + minute
      + ", second=" + second + '}';
  }
}
