package mk.edu.ukim.feit.gjorgjim.unitechnet.helpers;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by gjmarkov on 07.9.2018.
 */

public class KeyboardDelegate {

  public static void hideSoftKeyboard(Activity activity, View view) {
    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

    if (view == null) {
      view = new View(activity);
    }
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  public static void showSoftKeyboard(Activity activity, View view) {
    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

    if(view == null) {
      view = new View(activity);
    }

    imm.showSoftInput(view, 0);
  }

}
