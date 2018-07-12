package mk.edu.ukim.feit.gjorgjim.unitechnet.helpers;

/**
 * Created by gjmarkov on 11.7.2018.
 */
public class DatePickerDialogIdentifier {
  public static String BIRTHDAY_EDIT_PROFILE = "birthdayEditProfile";
  public static String STARTDATE_EXPERIENCE = "startDateExperience";
  public static String ENDDATE_EXPERIENCE = "endDateExperience";

  private static String currentDatePicker;

  public static void setCurrentDatePicker(String current) {
    currentDatePicker = current;
  }

  public static String getCurrentDatePicker() {
    return currentDatePicker;
  }
}
