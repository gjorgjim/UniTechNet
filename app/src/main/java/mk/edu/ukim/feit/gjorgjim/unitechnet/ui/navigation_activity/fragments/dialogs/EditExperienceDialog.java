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
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.DatabaseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.DatePickerDialogIdentifier;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.Validator;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Experience;

/**
 * Created by gjmarkov on 25.7.2018.
 */

public class EditExperienceDialog extends Dialog {

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
  private AppCompatButton saveExperienceBtn;
  private AppCompatButton deleteExperienceBtn;

  private DatePickerDialog.OnDateSetListener listener;

  private DatabaseService databaseService;
  private AuthenticationService authenticationService;

  private String key;
  private Experience currentExperience;

  public EditExperienceDialog(@NonNull Context context) {
    super(context);
    activity = (Activity) context;
    listener = (DatePickerDialog.OnDateSetListener) context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_edit_experience);

    setTitle("Add Experience");

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
    saveExperienceBtn = findViewById(R.id.addExperienceBtn);
    deleteExperienceBtn = findViewById(R.id.deleteExperienceBtn);

    setupUi();

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
      .minDate(2000, 0, 1)
      .build();

    startDateEt.setOnClickListener(v -> {
      datePicker.setOnShowListener(dialog -> {
        DatePickerDialogIdentifier.setCurrentDatePicker(DatePickerDialogIdentifier.STARTDATE_EDIT_EXPERIENCE);
      });
      datePicker.show();
    });

    startDateEt.setOnFocusChangeListener( (View v, boolean hasFocus) -> {
      datePicker.setOnShowListener(dialog -> {
        DatePickerDialogIdentifier.setCurrentDatePicker(DatePickerDialogIdentifier.STARTDATE_EDIT_EXPERIENCE);
      });
      datePicker.show();
    });

    endDateEt.setOnClickListener(v -> {
      datePicker.setOnShowListener(dialog -> {
        DatePickerDialogIdentifier.setCurrentDatePicker(DatePickerDialogIdentifier.ENDDATE_EDIT_EXPERIENCE);
      });
      datePicker.show();
    });

    endDateEt.setOnFocusChangeListener( (View v, boolean hasFocus) -> {
      datePicker.setOnShowListener(dialog -> {
        DatePickerDialogIdentifier.setCurrentDatePicker(DatePickerDialogIdentifier.ENDDATE_EDIT_EXPERIENCE);
      });
      datePicker.show();
    });

    presentCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if(isChecked) {
        endDateEt.setText("");
      }
    });

    saveExperienceBtn.setOnClickListener(v -> {
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
        experienceRef.child(key).removeValue();
        experienceRef.child(key).setValue(experience);
        dismiss();
      }
    });

    deleteExperienceBtn.setOnClickListener(v -> {
      databaseService.userReference(
        authenticationService
          .getCurrentUser()
          .getUid()
      ).child("experiences")
        .child(key)
        .removeValue();
      dismiss();
    });

    presentCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if(isChecked) {
        endDateEt.setText("");
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

  public void setExperience(String key, Experience experience) {
    this.key = key;
    currentExperience = experience;
  }

  private void setupUi() {
    titleEt.setText(currentExperience.getJobTitle());
    companyEt.setText(currentExperience.getCompany());
    startDateEt.setText(String.format(new Locale("en"),
      "%d/%d/%d",
      currentExperience.getStartDate().getDay(),
      currentExperience.getStartDate().getMonth(),
      currentExperience.getStartDate().getYear()));
    if(currentExperience.getEndDate() != null) {
      endDateEt.setText(String.format(new Locale("en"),
        "%d/%d/%d",
        currentExperience.getEndDate().getDay(),
        currentExperience.getEndDate().getMonth(),
        currentExperience.getEndDate().getYear()));
    } else {
      presentCb.setChecked(true);
    }
  }
}
