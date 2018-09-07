package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.UserService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.ProfileFragment;

/**
 * Created by gjmarkov on 26.7.2018.
 */

public class EditUserDetailsDialog extends Dialog {

  private Activity activity;

  private ProfileFragment profileFragment;

  private TextInputLayout firstNameIl;
  private AppCompatEditText firstNameEt;
  private TextInputLayout lastNameIl;
  private AppCompatEditText lastNameEt;
  private TextInputLayout titleIl;
  private AppCompatEditText titleEt;
  private TextInputLayout usernameIl;
  private AppCompatEditText usernameEt;
  private TextInputLayout birthdayIl;
  private AppCompatEditText birthdayEt;
  private AppCompatButton saveDetailsBtn;
  private AppCompatTextView changePwTv;

  private User currentUser;

  private UserService userService;

  public EditUserDetailsDialog(@NonNull Context context) {
    super(context);
    activity = (Activity) context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_edit_user_details);

    userService = UserService.getInstance();

    firstNameIl = findViewById(R.id.firstNameIl);
    firstNameEt = findViewById(R.id.firstNameEt);
    lastNameIl = findViewById(R.id.lastNameIl);
    lastNameEt = findViewById(R.id.lastNameEt);
    titleIl = findViewById(R.id.titleIl);
    titleEt = findViewById(R.id.titleEt);
    usernameIl = findViewById(R.id.usernameIl);
    usernameEt = findViewById(R.id.usernameEt);
    birthdayIl = findViewById(R.id.birthdayIl);
    birthdayEt = findViewById(R.id.birthdayEt);
    saveDetailsBtn = findViewById(R.id.saveChangesBtn);
    changePwTv = findViewById(R.id.changePwTv);

    currentUser = userService.getCurrentUser();
    setupUi(currentUser);

    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", new Locale("en"));
    java.util.Date date = new java.util.Date();
    String[] today = dateFormat.format(date).split("/");

    SpinnerDatePickerDialogBuilder datePickerBuilder = new SpinnerDatePickerDialogBuilder()
      .context(activity)
      .spinnerTheme(R.style.NumberPickerStyle)
      .showTitle(true)
      .defaultDate(
        currentUser.getBirthday().getYear(),
        currentUser.getBirthday().getMonth() - 1,
        currentUser.getBirthday().getDay()
      )
      .maxDate(
        Integer.parseInt(today[0]),
        Integer.parseInt(today[1]) - 1,
        Integer.parseInt(today[2])
      )
      .minDate(1980, 0, 1);

    birthdayEt.setOnClickListener(v -> {
      datePickerBuilder
        .callback(new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            setBirthday(year, monthOfYear, dayOfMonth);
          }
        })
        .build()
        .show();
    });

    birthdayEt.setOnFocusChangeListener( (View v, boolean hasFocus) -> {
      datePickerBuilder
        .callback(new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            setBirthday(year, monthOfYear, dayOfMonth);
          }
        })
        .build()
        .show();
    });

    saveDetailsBtn.setOnClickListener(v -> {

      if(!currentUser.getFirstName().equals(firstNameEt.getText().toString())){
        userService.saveUserDetails("firstName", firstNameEt.getText().toString());
        currentUser.setFirstName(firstNameEt.getText().toString());
      }


      if(!currentUser.getLastName().equals(lastNameEt.getText().toString())) {
        userService.saveUserDetails("lastName", lastNameEt.getText().toString());
        currentUser.setLastName(lastNameEt.getText().toString());
      }

      if(!currentUser.getTitle().equals(titleEt.getText().toString())) {
        userService.saveUserDetails("title", titleEt.getText().toString());
        currentUser.setTitle(titleEt.getText().toString());
      }

      if(!currentUser.getUsername().equals(usernameEt.getText().toString())) {
        userService.saveUserDetails("username", usernameEt.getText().toString());
        currentUser.setUsername(usernameEt.getText().toString());
      }

      String[] birthday = birthdayEt.getText().toString().split("/");
      Date birthDate = new Date(
        Integer.parseInt(birthday[2]),
        Integer.parseInt(birthday[1]),
        Integer.parseInt(birthday[0])
      );
      if(!birthDate.equals(currentUser.getBirthday())) {
        userService.saveUserBirthday(birthDate);
        currentUser.setBirthday(birthDate);
      }

      List<String> details = new ArrayList<>();
      details.add(firstNameEt.getText().toString());
      details.add(lastNameEt.getText().toString());
      details.add(usernameEt.getText().toString());
      details.add(titleEt.getText().toString());

      profileFragment.setUserDetails(details, birthDate);
      userService.changeCurrentUserDetails(currentUser);

      dismiss();
    });

    changePwTv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ChangePasswordDialog dialog = new ChangePasswordDialog(activity);
        dialog.show();
      }
    });
  }

  private void setupUi(User user) {
    firstNameEt.setText(user.getFirstName());
    lastNameEt.setText(user.getLastName());
    titleEt.setText(user.getTitle());
    usernameEt.setText(user.getUsername());
    birthdayEt.setText(
      String.format(new Locale("en"),
        "%d/%d/%d",
        user.getBirthday().getDay(),
        user.getBirthday().getMonth(),
        user.getBirthday().getYear())
    );
  }

  public void setProfileFragment(ProfileFragment profileFragment) {
    this.profileFragment = profileFragment;
  }

  public void setBirthday(int year, int month, int day) {
    birthdayEt.setText(
      String.format(new Locale("en"),
        "%d/%d/%d",
        day,
        month + 1,
        year)
    );
  }
}
