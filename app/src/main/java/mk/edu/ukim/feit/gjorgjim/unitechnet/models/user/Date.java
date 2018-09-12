package mk.edu.ukim.feit.gjorgjim.unitechnet.models.user;

import android.text.TextUtils;

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

  public static String formatToString(Date date) {
    String dateString = null;
    String bigDate = null;
    String smallDate = null;

    if(date.getYear() != 0 && date.getMonth() != 0 && date.getDay() != 0) {
      String month = String.format(new Locale("en"), "%d", date.getMonth());
      if(date.getMonth() < 10) {
        month = String.format(new Locale("en"), "0%d", date.getMonth());
      }
      String day = String.format(new Locale("en"), "%d", date.getDay());
      if(date.getDay() < 10) {
        day = String.format(new Locale("en"), "0%d", date.getDay());
      }
      bigDate = String.format(new Locale("en"),"%d-%s-%s", date.getYear(), month, day);
    }

    if(date.getHour() != 0 && date.getMinute() != 0 && date.getSecond() != 0) {
      String hour = String.format(new Locale("en"), "%d", date.getHour());
      if(date.getHour() < 10) {
        hour = String.format(new Locale("en"), "0%d", date.getHour());
      }
      String minute = String.format(new Locale("en"), "%d", date.getMinute());
      if(date.getMinute() < 10) {
        minute = String.format(new Locale("en"), "0%d", date.getMinute());
      }
      String second = String.format(new Locale("en"), "%d", date.getSecond());
      if(date.getHour() < 10) {
        second = String.format(new Locale("en"), "0%d", date.getSecond());
      }
      smallDate = String.format(new Locale("en"), "%s:%s:%s", hour, minute, second);
    }

    if(!TextUtils.isEmpty(bigDate) && !TextUtils.isEmpty(smallDate)) {
      dateString = String.format(new Locale("en"), "%sT%s", bigDate, smallDate);
    } else if(!TextUtils.isEmpty(bigDate)) {
      dateString = bigDate;
    } else if(!TextUtils.isEmpty(smallDate)) {
      dateString = smallDate;
    }

    return dateString;
  }

  public static Date formatFromString(String dateString) {
    Date date = new Date();

    String[] dateStringSplited = dateString.split("T");
    if(dateStringSplited.length == 1) {
      String bigSmallDateSplited[] = dateStringSplited[0].split("-");
      if(bigSmallDateSplited.length > 1) {
        date.setYear(Integer.parseInt(bigSmallDateSplited[0]));
        date.setMonth(Integer.parseInt(bigSmallDateSplited[1]));
        date.setDay(Integer.parseInt(bigSmallDateSplited[2]));
      } else {
        bigSmallDateSplited = bigSmallDateSplited[0].split(":");
        date.setHour(Integer.parseInt(bigSmallDateSplited[0]));
        date.setMinute(Integer.parseInt(bigSmallDateSplited[1]));
        date.setSecond(Integer.parseInt(bigSmallDateSplited[2]));
      }
    } else {
      String smalLDate[] = dateStringSplited[0].split("-");
      date.setYear(Integer.parseInt(smalLDate[0]));
      date.setMonth(Integer.parseInt(smalLDate[1]));
      date.setDay(Integer.parseInt(smalLDate[2]));

      String bigDate[] = dateStringSplited[1].split(":");
      date.setHour(Integer.parseInt(bigDate[0]));
      date.setMinute(Integer.parseInt(bigDate[1]));
      date.setSecond(Integer.parseInt(bigDate[2]));
    }

    return date;
  }

  @Override
  public String toString() {
    return "Date{" + "year=" + year + ", month=" + month + ", day=" + day + ", hour=" + hour + ", minute=" + minute
      + ", second=" + second + '}';
  }
}
