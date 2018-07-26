package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.DatabaseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.UserService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.DatePickerDialogIdentifier;
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

  private DatePickerDialog.OnDateSetListener listener;

  private User currentUser;

  private DatabaseService databaseService;
  private AuthenticationService authenticationService;
  private UserService userService;

  public EditUserDetailsDialog(@NonNull Context context) {
    super(context);
    activity = (Activity) context;
    listener = (DatePickerDialog.OnDateSetListener) context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_edit_user_details);

    databaseService = DatabaseService.getInstance();
    authenticationService = AuthenticationService.getInstance();
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

    currentUser = userService.getCurrentUser();
    setupUi(currentUser);

    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", new Locale("en"));
    java.util.Date date = new java.util.Date();
    String[] today = dateFormat.format(date).split("/");

    DatePickerDialog datePicker = new SpinnerDatePickerDialogBuilder()
      .context(activity)
      .callback(listener)
      .spinnerTheme(R.style.NumberPickerStyle)
      .showTitle(true)
      .defaultDate(2017, 0, 1)
      .maxDate(
        Integer.parseInt(today[0]),
        Integer.parseInt(today[1]) - 1,
        Integer.parseInt(today[2])
      )
      .minDate(1980, 0, 1)
      .build();

    birthdayEt.setOnClickListener(v -> {
      datePicker.setOnShowListener(dialog -> {
        DatePickerDialogIdentifier.setCurrentDatePicker(DatePickerDialogIdentifier.BIRTHDAY_CHANGE_DETAILS);
      });
      datePicker.show();
    });

    birthdayEt.setOnFocusChangeListener( (View v, boolean hasFocus) -> {
      datePicker.setOnShowListener(dialog -> {
        DatePickerDialogIdentifier.setCurrentDatePicker(DatePickerDialogIdentifier.BIRTHDAY_CHANGE_DETAILS);
      });
      datePicker.show();
    });

    saveDetailsBtn.setOnClickListener(v -> {
      DatabaseReference userRef = databaseService.userReference(
        authenticationService
        .getCurrentUser()
        .getUid()
      );

      if(!currentUser.getFirstName().equals(firstNameEt.getText().toString())){
        userRef.child("firstName").removeValue();
        userRef.child("firstName").setValue(firstNameEt.getText().toString());
        currentUser.setFirstName(firstNameEt.getText().toString());
      }


      if(!currentUser.getLastName().equals(lastNameEt.getText().toString())) {
        userRef.child("lastName").removeValue();
        userRef.child("lastName").setValue(lastNameEt.getText().toString());
        currentUser.setLastName(lastNameEt.getText().toString());
      }

      if(!currentUser.getTitle().equals(titleEt.getText().toString())) {
        userRef.child("title").removeValue();
        userRef.child("title").setValue(titleEt.getText().toString());
        currentUser.setTitle(titleEt.getText().toString());
      }

      if(!currentUser.getUsername().equals(usernameEt.getText().toString())) {
        userRef.child("username").removeValue();
        userRef.child("username").setValue(usernameEt.getText().toString());
        currentUser.setUsername(usernameEt.getText().toString());
      }

      String[] birthday = birthdayEt.getText().toString().split("/");
      Date birthDate = new Date(
        Integer.parseInt(birthday[2]),
        Integer.parseInt(birthday[1]),
        Integer.parseInt(birthday[0])
      );
      if(!birthDate.equals(currentUser.getBirthday())) {
        userRef.child("birthday").removeValue();
        userRef.child("birthday").setValue(birthDate);
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
