package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.firebase.database.DatabaseReference;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.DatabaseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.DatePickerDialogIdentifier;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.Validator;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Experience;

/**
 * Created by gjmarkov on 11.7.2018.
 */

public class NewExperienceDialog extends Dialog {

  private Activity activity;

  private TextInputLayout titleIl;
  private AppCompatEditText titleEt;
  private TextInputLayout companyIl;
  private AppCompatEditText companyEt;
  private TextInputLayout startDateIl;
  private AppCompatEditText startDateEt;
  private TextInputLayout endDateIl;
  private AppCompatEditText endDateEt;
  private AppCompatCheckBox presentCb;
  private AppCompatButton addExperienceBtn;

  private DatePickerDialog.OnDateSetListener listener;

  private DatabaseService databaseService;
  private AuthenticationService authenticationService;

  public NewExperienceDialog(@NonNull Context context) {
    super(context);
    activity = (Activity) context;
    listener = (DatePickerDialog.OnDateSetListener) context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.dialog_new_experience);

    databaseService = DatabaseService.getInstance();
    authenticationService = AuthenticationService.getInstance();

    titleIl = findViewById(R.id.titleIl);
    titleEt = findViewById(R.id.titleEt);
    companyIl = findViewById(R.id.companyIl);
    companyEt = findViewById(R.id.companyEt);
    startDateIl = findViewById(R.id.startDateIl);
    startDateEt = findViewById(R.id.startDateyEt);
    endDateIl = findViewById(R.id.endDateIl);
    endDateEt = findViewById(R.id.endDateyEt);
    presentCb = findViewById(R.id.endDatePresentCb);
    addExperienceBtn = findViewById(R.id.newExperienceBtn);

    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", new Locale("en"));
    java.util.Date date = new java.util.Date();
    String[] today = dateFormat.format(date).split("/");
    Log.d("Date", dateFormat.format(date));

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
      .minDate(2000, 0, 1)
      .build();

    startDateEt.setOnClickListener(v -> {
      datePicker.setOnShowListener(dialog -> {
        DatePickerDialogIdentifier.setCurrentDatePicker(DatePickerDialogIdentifier.STARTDATE_EXPERIENCE);
      });
      datePicker.show();
    });

    startDateEt.setOnFocusChangeListener( (View v, boolean hasFocus) -> {
      datePicker.setOnShowListener(dialog -> {
        DatePickerDialogIdentifier.setCurrentDatePicker(DatePickerDialogIdentifier.STARTDATE_EXPERIENCE);
      });
      datePicker.show();
    });

    endDateEt.setOnClickListener(v -> {
      datePicker.setOnShowListener(dialog -> {
        DatePickerDialogIdentifier.setCurrentDatePicker(DatePickerDialogIdentifier.ENDDATE_EXPERIENCE);
      });
      datePicker.show();
    });

    endDateEt.setOnFocusChangeListener( (View v, boolean hasFocus) -> {
      datePicker.setOnShowListener(dialog -> {
        DatePickerDialogIdentifier.setCurrentDatePicker(DatePickerDialogIdentifier.ENDDATE_EXPERIENCE);
      });
      datePicker.show();
    });

    presentCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if(isChecked) {
        endDateEt.setText("");
      }
    });

    addExperienceBtn.setOnClickListener(v -> {
      if(validateInput()) {
        String[] startDate = startDateEt.getText().toString().split("/");
        Experience experience = new Experience(
          titleEt.getText().toString().trim(),
          companyEt.getText().toString().trim(),
          new Date(
            Integer.parseInt(startDate[2]),
            Integer.parseInt(startDate[1]),
            Integer.parseInt(startDate[0])
          )
        );
        if(presentCb.isChecked()) {
          experience.setEndDate(null);
        } else {
          String[] endDate = endDateEt.getText().toString().split("/");
          experience.setEndDate(new Date(
            Integer.parseInt(endDate[2]),
            Integer.parseInt(endDate[1]),
            Integer.parseInt(endDate[0])
          ));
        }
        DatabaseReference experienceRef = databaseService.userReference(
          authenticationService
            .getCurrentUser()
            .getUid()
        ).child("experiences");
        String key = experienceRef.push().getKey();
        experienceRef.child(key).setValue(experience);
        dismiss();
      }
    });

  }

  private boolean validateInput() {
    return Validator.validateInput(titleIl, titleEt, activity)
      && Validator.validateInput(companyIl, companyEt, activity)
      && Validator.validateInput(startDateIl, startDateEt, activity)
      && (Validator.validateInput(endDateIl, endDateEt, activity, presentCb));
  }

  public void setStartDate(int year, int month, int day) {
    startDateEt.setText(String.format(new Locale("en"),"%d/%d/%d", day, month + 1, year));
  }

  public void setEndDate(int year, int month, int day) {
    presentCb.setChecked(false);
    endDateEt.setText(String.format(new Locale("en"),"%d/%d/%d", day, month + 1, year));
  }
}
