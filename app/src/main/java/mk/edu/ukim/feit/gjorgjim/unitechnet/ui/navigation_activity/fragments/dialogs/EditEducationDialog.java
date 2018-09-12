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

import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.UserService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.Validator;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Education;

/**
 * Created by gjmarkov on 25.7.2018.
 */
public class EditEducationDialog extends Dialog {
  private Activity activity;

  private TextInputLayout schoolIl;
  private AppCompatEditText schoolEt;
  private TextInputLayout degreeIl;
  private AppCompatEditText degreeEt;
  private TextInputLayout gradeIl;
  private AppCompatEditText gradeEt;
  private TextInputLayout startDateIl;
  private AppCompatEditText startDateEt;
  private TextInputLayout endDateIl;
  private AppCompatEditText endDateEt;
  private AppCompatCheckBox presentCb;
  private AppCompatButton saveEducationBtn;
  private AppCompatButton deleteEducationBtn;

  private UserService userService;

  private String key;
  private Education currentEducation;

  public EditEducationDialog(@NonNull Context context) {
    super(context);
    activity = (Activity) context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_edit_education);

    setTitle("Edit Education");

    userService = UserService.getInstance();

    schoolIl = findViewById(R.id.schoolIl);
    schoolEt = findViewById(R.id.schoolEt);
    degreeIl = findViewById(R.id.degreeIl);
    degreeEt = findViewById(R.id.degreeEt);
    gradeIl = findViewById(R.id.gradeIl);
    gradeEt = findViewById(R.id.gradeEt);
    startDateIl = findViewById(R.id.startDateIl);
    startDateEt = findViewById(R.id.startDateEt);
    endDateIl = findViewById(R.id.endDateIl);
    endDateEt = findViewById(R.id.endDateyEt);
    presentCb = findViewById(R.id.endDatePresentCb);
    saveEducationBtn = findViewById(R.id.saveEducationBtn);
    deleteEducationBtn = findViewById(R.id.deleteEducationBtn);

    setupUI();

    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", new Locale("en"));
    java.util.Date date = new java.util.Date();
    String[] today = dateFormat.format(date).split("/");

    SpinnerDatePickerDialogBuilder datePickerBuilder = new SpinnerDatePickerDialogBuilder()
      .context(activity)
      .spinnerTheme(R.style.NumberPickerStyle)
      .showTitle(true)
      .maxDate(
        Integer.parseInt(today[0]),
        Integer.parseInt(today[1]) - 1,
        Integer.parseInt(today[2])
      )
      .minDate(2000, 0, 1);

    startDateEt.setOnClickListener(v -> {
      Date startDate = Date.formatFromString(currentEducation.getStartDate());
      datePickerBuilder
        .defaultDate(
          startDate.getYear(),
          startDate.getMonth() - 1,
          startDate.getDay()
        )
        .callback(new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            setStartDate(year, monthOfYear, dayOfMonth);
          }
        })
        .build()
        .show();
    });

    startDateEt.setOnFocusChangeListener( (View v, boolean hasFocus) -> {
      Date startDate = Date.formatFromString(currentEducation.getStartDate());
      datePickerBuilder
        .defaultDate(
          startDate.getYear(),
          startDate.getMonth() - 1,
          startDate.getDay()
        )
        .callback(new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            setStartDate(year, monthOfYear, dayOfMonth);
          }
        })
        .build()
        .show();
    });

    endDateEt.setOnClickListener(v -> {
      Date endDate = null;
      if(currentEducation.getEndDate() != null) {
        endDate = Date.formatFromString(currentEducation.getEndDate());
      }
      datePickerBuilder
        .defaultDate(
          endDate != null ? endDate.getYear() : 2017,
          endDate != null ? endDate.getMonth() - 1 : 1,
          endDate != null ? endDate.getDay() : 1
        )
        .callback(new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            setEndDate(year, monthOfYear, dayOfMonth);
          }
        })
        .build()
        .show();
    });

    endDateEt.setOnFocusChangeListener( (View v, boolean hasFocus) -> {
      Date endDate = null;
      if(currentEducation.getEndDate() != null) {
        endDate = Date.formatFromString(currentEducation.getEndDate());
      }
      datePickerBuilder
        .defaultDate(
          endDate != null ? endDate.getYear() : 2017,
          endDate != null ? endDate.getMonth() - 1 : 1,
          endDate != null ? endDate.getDay() : 1
        )
        .callback(new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            setEndDate(year, monthOfYear, dayOfMonth);
          }
        })
        .build()
        .show();
    });

    saveEducationBtn.setOnClickListener(v -> {
      if(validateInput()) {
        String[] startDate = startDateEt.getText().toString().split("/");
        Education education = new Education(
          schoolEt.getText().toString().trim(),
          degreeEt.getText().toString().trim(),
          gradeEt.getText().toString().trim(),
          Date.formatToString(new Date(
            Integer.parseInt(startDate[2]),
            Integer.parseInt(startDate[1]),
            Integer.parseInt(startDate[0])
          ))
        );
        if(presentCb.isChecked()) {
          education.setEndDate(null);
        } else {
          String[] endDate = endDateEt.getText().toString().split("/");
          education.setEndDate(Date.formatToString(new Date(
            Integer.parseInt(endDate[2]),
            Integer.parseInt(endDate[1]),
            Integer.parseInt(endDate[0])
          )));
        }

        userService.saveEducation(key, education);

        dismiss();
      }
    });

    deleteEducationBtn.setOnClickListener(v -> {
      userService.deleteEducation(key);

      dismiss();
    });

    presentCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if(isChecked) {
        endDateEt.setText("");
      }
    });
  }

  private boolean validateInput() {
    return Validator.validateInput(schoolIl, schoolEt, activity)
      && Validator.validateInput(degreeIl, degreeEt, activity)
      && Validator.validateInput(gradeIl, gradeEt, activity)
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

  public void setEducation(String key, Education education) {
    currentEducation = education;
    this.key = key;
  }

  private void setupUI() {
    schoolEt.setText(currentEducation.getSchool());
    degreeEt.setText(currentEducation.getDegree());
    gradeEt.setText(currentEducation.getGrade());
    Date startDate = Date.formatFromString(currentEducation.getStartDate());
    startDateEt.setText(String.format(new Locale("en"),
      "%d/%d/%d",
      startDate.getDay(),
      startDate.getMonth(),
      startDate.getYear()));
    if(currentEducation.getEndDate() != null) {
      Date endDate = Date.formatFromString(currentEducation.getEndDate());
      endDateEt.setText(String.format(new Locale("en"),
        "%d/%d/%d",
        endDate.getDay(),
        endDate.getMonth(),
        endDate.getYear()));
    } else {
      presentCb.setChecked(true);
    }
  }
}
