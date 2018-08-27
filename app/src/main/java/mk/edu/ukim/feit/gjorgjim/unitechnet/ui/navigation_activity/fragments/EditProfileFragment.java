package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.UserService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.Validator;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.enums.UserGender;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.WaitingDialog;

/**
 * Created by gjmarkov on 16.5.2018.
 */

public class EditProfileFragment extends Fragment {

  private static final String TAG = "EditProfileFragment";

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
  private TextInputLayout titleIl;
  private AppCompatEditText titleEt;
  private TextInputLayout usernameIl;
  private AppCompatEditText usernameEt;
  private TextInputLayout emailIl;
  private AppCompatEditText emailEt;
  private TextInputLayout birthdayIl;
  private AppCompatEditText birthdayEt;
  private Spinner genderSpinner;
  private CircleImageView profilePictureIv;
  private AppCompatTextView changeProfilePictureTv;
  private AppCompatButton saveBtn;

  private User user;

  private DatePickerDialog.OnDateSetListener listener;
  private WaitingDialog waitingDialog;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    listener = (DatePickerDialog.OnDateSetListener) context;
    fragmentChangingListener = (FragmentChangingListener) context;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

    user = new User(authenticationService.getCurrentUser().getEmail());

    waitingDialog = new WaitingDialog(getActivity());

    firstNameIl = view.findViewById(R.id.firstNameIl);
    firstNameEt = view.findViewById(R.id.firstNameEt);
    lastNameIl = view.findViewById(R.id.lastNameIl);
    lastNameEt = view.findViewById(R.id.lastNameEt);
    titleIl = view.findViewById(R.id.titleIl);
    titleEt = view.findViewById(R.id.titleEt);
    usernameIl = view.findViewById(R.id.usernameIl);
    usernameEt = view.findViewById(R.id.usernameEt);
    emailIl = view.findViewById(R.id.emailIl);
    emailEt = view.findViewById(R.id.emailEt);
    birthdayIl = view.findViewById(R.id.birthdayIl);
    birthdayEt = view.findViewById(R.id.birthdayEt);
    profilePictureIv = view.findViewById(R.id.profilePictureIv);
    changeProfilePictureTv = view.findViewById(R.id.changeProfilePictureTv);
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
      .into(profilePictureIv);

    Snackbar
      .make(getActivity().findViewById(android.R.id.content), "Set up your profile", Snackbar.LENGTH_INDEFINITE)
      .setAction("GOT IT", v -> {
      })
      .show();

    changeProfilePictureTv.setOnClickListener(v -> {
      chooseImage();
    });

    DatePickerDialog datePicker = new SpinnerDatePickerDialogBuilder()
      .context(getActivity())
      .callback(new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
          setDateTextView(year, monthOfYear, dayOfMonth);
        }
      })
      .spinnerTheme(R.style.NumberPickerStyle)
      .showTitle(true)
      .maxDate(2005, 11, 31)
      .minDate(1980, 0, 1)
      .defaultDate(2017, 0, 1)
      .build();

    birthdayEt.setOnFocusChangeListener( (View v, boolean hasFocus) -> {
      birthdayEt.setOnClickListener(v1 -> {
          datePicker.show();
        });
    });

    birthdayEt.setOnClickListener(v -> {
      datePicker.show();
    });

    saveBtn.setOnClickListener(v -> {
      waitingDialog.showDialog("Saving user details...");
      if(validateInput()) {
        user.setFirstName(firstNameEt.getText().toString().trim());
        user.setLastName(lastNameEt.getText().toString().trim());
        user.setUsername(usernameEt.getText().toString().trim());
        user.setTitle(titleEt.getText().toString().trim());
        String[] birthday = birthdayEt.getText().toString().trim().split("/");
        user.setBirthday(new Date(Integer.parseInt(birthday[2]),
          Integer.parseInt(birthday[1]),
          Integer.parseInt(birthday[0])));

        userService.saveUser(user);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageReference = storage
          .getReference()
          .child("images")
          .child(authenticationService.getCurrentUser().getUid())
          .child("pp.jpg");

        profilePictureIv.setDrawingCacheEnabled(true);
        profilePictureIv.buildDrawingCache();
        Bitmap bitmap = profilePictureIv.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageReference.putBytes(data);
        uploadTask.addOnSuccessListener((UploadTask.TaskSnapshot taskSnapshot) -> {
          waitingDialog.hideDialog();
          fragmentChangingListener.changeToUserFragment();
        });
      }
    });

    return view;
  }

  public void setDateTextView(int year, int month, int day) {
    birthdayEt.setText(String.format(new Locale("en"), "%d/%d/%d", day, month + 1, year));
  }

  public void setNewProfilePicture(Bitmap image) {
    Log.d(TAG, "setNewProfilePicture called");
    Glide
      .with(getActivity())
      .load(image)
      .into(profilePictureIv);
  }

  private boolean validateInput() {
    return Validator.validateInput(firstNameIl, firstNameEt, getActivity())
      && Validator.validateInput(lastNameIl, lastNameEt, getActivity())
      && Validator.validateInput(usernameIl, usernameEt, getActivity())
      && Validator.validateEmail(emailIl, emailEt, getActivity())
      && Validator.validateInput(birthdayIl, birthdayEt, getActivity())
      && Validator.validateInput(titleIl, titleEt, getActivity());
  }

  private void chooseImage() {
    ImagePicker.create(getActivity())
      .folderMode(true)
      .limit(1)
      .start();
  }

}
