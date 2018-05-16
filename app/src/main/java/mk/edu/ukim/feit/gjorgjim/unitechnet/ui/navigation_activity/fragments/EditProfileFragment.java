package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.DatabaseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.UserService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.Validator;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.User;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.enums.UserGender;

/**
 * Created by gjmarkov on 16.5.2018.
 */

public class EditProfileFragment extends Fragment {

  private static final EditProfileFragment instance = new EditProfileFragment();

  public static EditProfileFragment getInstance() {
    return instance;
  }

  public EditProfileFragment() {
    authenticationService = AuthenticationService.getInstance();
    userService = UserService.getInstance();
  }

  private FragmentChangingListener fragmentChangingListener;

  private AuthenticationService authenticationService;
  private UserService userService;

  private TextInputLayout firstNameIl;
  private AppCompatEditText firstNameEt;
  private TextInputLayout lastNameIl;
  private AppCompatEditText lastNameEt;
  private TextInputLayout usernameIl;
  private AppCompatEditText usernameEt;
  private TextInputLayout emailIl;
  private AppCompatEditText emailEt;
  private TextInputLayout birthdayIl;
  private AppCompatEditText birthdayEt;
  private Spinner genderSpinner;
  private CircleImageView profilePictureIv;
  private AppCompatButton saveBtn;

  private User user;

  com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener listener;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    listener = (com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener) context;
    fragmentChangingListener = (FragmentChangingListener) context;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

    user = new User(authenticationService.getCurrentUser().getEmail());

    firstNameIl = view.findViewById(R.id.firstNameIl);
    firstNameEt = view.findViewById(R.id.firstNameEt);
    lastNameIl = view.findViewById(R.id.lastNameIl);
    lastNameEt = view.findViewById(R.id.lastNameEt);
    usernameIl = view.findViewById(R.id.usernameIl);
    usernameEt = view.findViewById(R.id.usernameEt);
    emailIl = view.findViewById(R.id.emailIl);
    emailEt = view.findViewById(R.id.emailEt);
    birthdayIl = view.findViewById(R.id.birthdayIl);
    birthdayEt = view.findViewById(R.id.birthdayEt);
    profilePictureIv = view.findViewById(R.id.profilePictureIv);
    genderSpinner = view.findViewById(R.id.genderSpinner);
    saveBtn = view.findViewById(R.id.saveBtn);

    emailEt.setText(user.getEmail());

    birthdayEt.setInputType(InputType.TYPE_NULL);

    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
      R.array.gender_array, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    genderSpinner.setAdapter(adapter);

    user.setGender(UserGender.NOT_SPECIFIED);
    genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 1) {
          user.setGender(UserGender.MALE);
        } else if(position == 2) {
          user.setGender(UserGender.FEMALE);
        } else {
          user.setGender(UserGender.NOT_SPECIFIED);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    Glide
      .with(getActivity())
      .load(R.drawable.profile)
      .override(250, 250)
      .into(profilePictureIv);

    birthdayEt.setOnFocusChangeListener( (View v, boolean hasFocus) -> {
      birthdayEt.setOnClickListener(v1 -> {
        new SpinnerDatePickerDialogBuilder()
          .context(getActivity())
          .callback(listener)
          .spinnerTheme(R.style.NumberPickerStyle)
          .showTitle(true)
          .defaultDate(2017, 0, 1)
          .maxDate(2020, 0, 1)
          .minDate(2000, 0, 1)
          .build().show();
        });
    });

    birthdayEt.setOnClickListener(v -> {
      new SpinnerDatePickerDialogBuilder()
        .context(getActivity())
        .callback(listener)
        .spinnerTheme(R.style.NumberPickerStyle)
        .showTitle(true)
        .defaultDate(2017, 0, 1)
        .maxDate(2020, 0, 1)
        .minDate(2000, 0, 1)
        .build()
        .show();
    });

    saveBtn.setOnClickListener(v -> {
      if(validateInput()) {
        user.setFirstName(firstNameEt.getText().toString().trim());
        user.setLastName(lastNameEt.getText().toString().trim());
        user.setUsername(usernameEt.getText().toString().trim());
        String[] birthday = birthdayEt.getText().toString().trim().split("/");
        Calendar calendar = Calendar.getInstance();
        calendar.set(
          Integer.parseInt(birthday[2]),
          Integer.parseInt(birthday[1]) - 1,
          Integer.parseInt(birthday[0])
        );
        user.setBirthday(new Date(calendar.getTimeInMillis()));
      }
      userService.saveUser(user);
      fragmentChangingListener.changeToUserFragment();
    });

    return view;
  }

  public void setDateTextView(int year, int month, int day) {
    birthdayEt.setText(day + "/" + month + "/" + year);
  }

  private boolean validateInput() {
    return Validator.validateInput(firstNameIl, firstNameEt, getActivity())
      && Validator.validateInput(lastNameIl, lastNameEt, getActivity())
      && Validator.validateInput(usernameIl, usernameEt, getActivity())
      && Validator.validateEmail(emailIl, emailEt, getActivity())
      && Validator.validateInput(birthdayIl, birthdayEt, getActivity());
  }
}
