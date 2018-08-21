package mk.edu.ukim.feit.gjorgjim.unitechnet.helpers;

/**
 * Created by gjmarkov on 11.7.2018.
 */
public class DatePickerDialogIdentifier {
  public static String BIRTHDAY_EDIT_PROFILE = "birthdayEditProfile";
  public static String STARTDATE_EXPERIENCE = "startDateExperience";
  public static String ENDDATE_EXPERIENCE = "endDateExperience";
  public static String STARTDATE_EDIT_EXPERIENCE = "startDateEditExperience";
  public static String ENDDATE_EDIT_EXPERIENCE = "endDateEditExperience";
  public static String STARTDATE_EDUCATION = "statDateEducation";
  public static String ENDDATE_EDUCATION = "endDateEducation";
  public static String STARTDATE_EDIT_EDUCATION = "statDateEditEducation";
  public static String ENDDATE_EDIT_EDUCATION = "endDateEditEducation";
  public static String BIRTHDAY_CHANGE_DETAILS = "birthdayChangeDetails";

  private static String currentDatePicker;

  public static void setCurrentDatePicker(String current) {
    currentDatePicker = current;
  }

  public static String getCurrentDatePicker() {
    return currentDatePicker;
  }
}