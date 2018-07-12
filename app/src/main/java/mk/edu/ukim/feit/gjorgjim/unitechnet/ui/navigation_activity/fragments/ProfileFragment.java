package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;

import com.bluehomestudio.progresswindow.ProgressWindow;
import com.bluehomestudio.progresswindow.ProgressWindowConfiguration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.DatabaseCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.UserService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Education;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Experience;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.adapters.ProfileListAdapter;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs.NewExperienceDialog;

/**
 * Created by gjmarkov on 16.5.2018.
 */

public class ProfileFragment extends Fragment{

  private UserService userService;
  private AuthenticationService authenticationService;

  private ListView courseLv;
  private ListView educationLv;
  private ListView experienceLv;
  private AppCompatTextView nameTv;
  private AppCompatTextView titleTv;
  private AppCompatTextView usernameTv;
  private AppCompatTextView birthdayTv;
  private CircleImageView profilePictureIv;
  private AppCompatImageView plusExperienceIv;
  private AppCompatImageView plusCourseIv;
  private AppCompatImageView plusEducationIv;

  private NewExperienceDialog experienceDialog;

  ProfileListAdapter<Experience> experienceAdapter;
  private ArrayList<Experience> experiences;

  public ProfileFragment() {
    userService = UserService.getInstance();
    authenticationService = AuthenticationService.getInstance();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);

    courseLv = view.findViewById(R.id.coursesLv);
    educationLv = view.findViewById(R.id.educationLv);
    experienceLv = view.findViewById(R.id.experienceLv);
    nameTv = view.findViewById(R.id.nameTv);
    titleTv = view.findViewById(R.id.titleTv);
    usernameTv = view.findViewById(R.id.usernameTv);
    birthdayTv = view.findViewById(R.id.birthdayTv);
    profilePictureIv = view.findViewById(R.id.profilePictureIv);
    plusCourseIv = view.findViewById(R.id.coursePlusIv);
    plusExperienceIv = view.findViewById(R.id.experiencePlusIv);
    plusEducationIv = view.findViewById(R.id.educationPlusIv);

    profilePictureIv.setOnClickListener(v -> {
      authenticationService.signOut(getActivity());
    });

    plusExperienceIv.setOnClickListener(v -> {
      experienceDialog = new NewExperienceDialog(getContext());
      experienceDialog.show();
    });

    setUserUI(userService.getCurrentUser());

    return view;
  }

  private void setUserUI(User user){
    nameTv.setText(String.format("%s\n    %s", user.getFirstName(), user.getLastName()));
    String title = (user.getTitle() != null) ? user.getTitle() : "Title";
    titleTv.setText(String.format("%s", title));
    usernameTv.setText(String.format("%s", user.getUsername()));
    Date birthday = user.getBirthday();
    birthdayTv.setText(String.format(new Locale("en"),
      "%d/%d/%d", birthday.getDay(), birthday.getMonth(), birthday.getYear())
    );

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imageReference = storage
      .getReference()
      .child("images")
      .child(authenticationService.getCurrentUser().getUid())
      .child("pp.jpg");

    try {
      final File localFile = File.createTempFile("images", "jpg");
      imageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
        profilePictureIv.setImageBitmap(bitmap);
      }).addOnFailureListener(Throwable::printStackTrace);
    } catch (IOException e ) {
      e.printStackTrace();
    }

    ProfileListAdapter<Course> courseAdapter;
    if(user.getCourses() != null) {
      courseAdapter = new ProfileListAdapter<>(
        getContext(),
        R.layout.profile_list_item_layout,
        user.getCourses()
      );
    } else {
      courseAdapter = new ProfileListAdapter<>(
        getContext(),
        R.layout.profile_list_item_layout,
        new ArrayList<>()
      );
    }
    courseLv.setAdapter(courseAdapter);

    ProfileListAdapter<Education> educationAdapter;
    if(user.getEducations() != null) {
      educationAdapter = new ProfileListAdapter<>(
        getContext(),
        R.layout.profile_list_item_layout,
        user.getEducations()
      );
    } else {
      educationAdapter = new ProfileListAdapter<>(
        getContext(),
        R.layout.profile_list_item_layout,
        new ArrayList<>()
      );
    }
    educationLv.setAdapter(educationAdapter);

    if(user.getExperiences() != null) {
      experiences = new ArrayList<>(user.getExperiences().values());
      experienceAdapter = new ProfileListAdapter<>(
        getContext(),
        R.layout.profile_list_item_layout,
        experiences
      );
    } else {
      experienceAdapter = new ProfileListAdapter<>(
        getContext(),
        R.layout.profile_list_item_layout,
        new ArrayList<>()
      );
    }
    experienceLv.setAdapter(experienceAdapter);
  }

  public void setStartDateExperience(int year, int month, int day) {
    experienceDialog.setStartDate(year, month, day);
  }

  public void setEndDateExperience(int year, int month, int day) {
    experienceDialog.setEndDate(year, month, day);
  }

  public void updateExperience() {
    if(experienceAdapter != null) {
      experiences = new ArrayList<>(userService.getCurrentUser().getExperiences().values());
      Log.d("ProfileFragment", userService.getCurrentUser().getExperiences().values().toString());
      experienceAdapter.notifyDataSetChanged();
    }
  }
}
