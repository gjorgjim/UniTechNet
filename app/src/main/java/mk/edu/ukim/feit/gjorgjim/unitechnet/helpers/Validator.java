package mk.edu.ukim.feit.gjorgjim.unitechnet.helpers;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.view.WindowManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;

/**
 * Created by gjmarkov on 15.5.2018.
 */

public class Validator {

  public static boolean validateEmail(TextInputLayout emailIl, AppCompatEditText emailEt, Activity activity){
    if(emailEt.getText().toString().trim().isEmpty()) {
      emailIl.setError(activity.getString(R.string.edit_text_empty_error));
      requestFocus(emailEt, activity);
      return false;
    } else {
      if(!isEmailValid(emailEt.getText().toString().trim())) {
        emailIl.setError(activity.getString(R.string.invalid_email_error));
        requestFocus(emailEt, activity);
        return false;
      } else {
        emailIl.setErrorEnabled(false);
      }
    }
    return true;
  }

  private static boolean isEmailValid(String email) {
    String regExpn =
      "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

    CharSequence inputStr = email;

    Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(inputStr);

    return matcher.matches();
  }

  public static boolean validateInput(TextInputLayout inputLayout, AppCompatEditText editText, Activity activity) {
    if(editText.getText().toString().trim().isEmpty()) {
      inputLayout.setError(activity.getString(R.string.edit_text_empty_error));
      requestFocus(editText, activity);
      return false;
    } else {
      inputLayout.setErrorEnabled(false);
    }
    return true;
  }

  public static boolean validateInput(TextInputLayout inputLayout,
    AppCompatEditText editText,
    Activity activity,
    AppCompatCheckBox checkBox) {
    if(editText.getText().toString().trim().isEmpty() && !checkBox.isChecked()) {
      inputLayout.setError(activity.getString(R.string.edit_text_empty_error));
      requestFocus(editText, activity);
      return false;
    } else {
      inputLayout.setErrorEnabled(false);
    }
    return true;
  }

  public static boolean validatePassword(AppCompatEditText pw1, AppCompatEditText pw2, TextInputLayout textInputLayout, Activity activity) {
    if(!pw1.getText().toString().equals(pw2.getText().toString())) {
      textInputLayout.setError(activity.getString(R.string.passwords_not_match));
      requestFocus(pw2, activity);
      return false;
    }
    return true;
  }

  private static void requestFocus(View view, Activity activity) {
    if (view.requestFocus()) {
      activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
  }

}
