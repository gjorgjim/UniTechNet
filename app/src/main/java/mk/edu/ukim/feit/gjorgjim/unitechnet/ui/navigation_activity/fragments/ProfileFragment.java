package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.DatabaseCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.ProfileChangeCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.ProfilePictureCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.MessagingService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.UserService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Education;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Experience;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.WaitingDialog;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs.EditUserDetailsDialog;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs.NewEducationDialog;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs.NewExperienceDialog;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views.ProfileItemView;

/**
 * Created by gjmarkov on 16.5.2018.
 */

public class ProfileFragment extends Fragment{

  public static final int ImagePickerRequestCode = 0x000123;

  private UserService userService;
  private AuthenticationService authenticationService;
  private MessagingService messagingService;

  private AppCompatTextView nameTv;
  private AppCompatTextView titleTv;
  private AppCompatTextView usernameTv;
  private AppCompatTextView birthdayTv;
  private CircleImageView profilePictureIv;
  private AppCompatImageView plusExperienceIv;
  private AppCompatImageView plusCourseIv;
  private AppCompatImageView plusEducationIv;
  private LinearLayout coursesLl;
  private LinearLayout experiencesLl;
  private LinearLayout educationsLl;
  private AppCompatImageView editDetailsIv;
  private AppCompatTextView changeProfilePictureTv;

  private HashMap<String, ProfileItemView<Course>> courseViews;
  private HashMap<String, ProfileItemView<Experience>> experienceViews;
  private HashMap<String, ProfileItemView<Education>> educationViews;

  private NewExperienceDialog experienceDialog;
  private NewEducationDialog educationDialog;
  private EditUserDetailsDialog userDetailsDialog;

  private FragmentChangingListener listener;

  private ProfileItemView<Education> currentEducation;
  private ProfileItemView<Experience> currentExperience;

  public ProfileFragment() {
    userService = UserService.getInstance();
    authenticationService = AuthenticationService.getInstance();
    messagingService = MessagingService.getInstance();
    listenForUserDetailsChanged();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    listener = (FragmentChangingListener) context;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);

    nameTv = view.findViewById(R.id.nameTv);
    titleTv = view.findViewById(R.id.titleTv);
    usernameTv = view.findViewById(R.id.usernameTv);
    birthdayTv = view.findViewById(R.id.birthdayTv);
    profilePictureIv = view.findViewById(R.id.profilePictureIv);
    plusCourseIv = view.findViewById(R.id.coursePlusIv);
    plusExperienceIv = view.findViewById(R.id.experiencePlusIv);
    plusEducationIv = view.findViewById(R.id.educationPlusIv);
    coursesLl = view.findViewById(R.id.coursesLl);
    experiencesLl = view.findViewById(R.id.experiencesLl);
    educationsLl = view.findViewById(R.id.educationsLl);
    editDetailsIv = view.findViewById(R.id.editIv);
    changeProfilePictureTv = view.findViewById(R.id.changeProfilePictureTv);

    courseViews = new HashMap<>();
    experienceViews = new HashMap<>();
    educationViews = new HashMap<>();

    profilePictureIv.setOnClickListener(v -> {
      messagingService.stopBackgroundServiceForMessages(getActivity());
      authenticationService.signOut(getActivity());
    });

    plusExperienceIv.setOnClickListener(v -> {
      experienceDialog = new NewExperienceDialog(getContext());
      experienceDialog.show();
    });

    plusEducationIv.setOnClickListener(v -> {
      educationDialog = new NewEducationDialog(getContext());
      educationDialog.show();
    });

    plusCourseIv.setOnClickListener(v -> {
      listener.changeToCoursesFragment();
    });

    editDetailsIv.setOnClickListener(v ->  {
      userDetailsDialog = new EditUserDetailsDialog(getContext());
      userDetailsDialog.setProfileFragment(this);

      userDetailsDialog.show();
    });

    changeProfilePictureTv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        chooseImage();
      }
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

    profilePictureIv.setImageBitmap(userService.getBitmapFromMemCache());

    if(user.getCourses() != null) {
      for(Map.Entry<String, Course> current : user.getCourses().entrySet()) {
        ProfileItemView<Course> profileItemView = new ProfileItemView<>(getContext(), current.getKey(), this);
        profileItemView.setItem(current.getValue());

        courseViews.put(current.getKey(), profileItemView);
        coursesLl.addView(profileItemView);
      }
    }

    if(user.getEducations() != null) {
      for(Map.Entry<String, Education> current: user.getEducations().entrySet()) {
        ProfileItemView<Education> profileItemView = new ProfileItemView<>(getContext(), current.getKey(), this);
        profileItemView.setItem(current.getValue());

        educationViews.put(current.getKey(), profileItemView);
        educationsLl.addView(profileItemView);
      }
    }

    if(user.getExperiences() != null) {
      for(Map.Entry<String, Experience> current: user.getExperiences().entrySet()) {
        ProfileItemView<Experience> profileItemView = new ProfileItemView<>(getContext(), current.getKey(), this);
        profileItemView.setItem(current.getValue());

        experienceViews.put(current.getKey(), profileItemView);
        experiencesLl.addView(profileItemView);
      }
    }

  }

  public void setUserDetails(List<String> details, Date birthday){
    nameTv.setText(String.format("%s\n    %s", details.get(0), details.get(1)));
    titleTv.setText(details.get(2));
    usernameTv.setText(details.get(3));
    birthdayTv.setText(String.format(new Locale("en"),
      "%d/%d/%d", birthday.getDay(), birthday.getMonth(), birthday.getYear())
    );
  }

  private void listenForUserDetailsChanged() {
    userService.listenForUserDetailsChanges(new ProfileChangeCallback() {
      @Override
      public void onCourseAdded(String key, Course course) {
        addCourse(key, course);
      }

      @Override
      public void onCourseRemoved(String key) {
        removeCourse(key);
      }

      @Override
      public void onExperienceAdded(String key, Experience experience) {
        addExperience(key, experience);
      }

      @Override
      public void onExperienceRemoved(String key) {
        removeExperience(key);
      }

      @Override
      public void onEducationAdded(String key, Education education) {
        addEducation(key, education);
      }

      @Override
      public void onEducationRemoved(String key) {
        removeEducation(key);
      }
    });
  }

  private void chooseImage() {
    ImagePicker.create(getActivity())
      .folderMode(true)
      .limit(1)
      .theme(R.style.AppTheme)
      .start(ImagePickerRequestCode);
  }

  public void changeProfilePicture(Bitmap bitmap){
    WaitingDialog waitingDialog = new WaitingDialog(getActivity());
    waitingDialog.showDialog("Changing profile picture");

    userService.saveProfilePicture(bitmap, new ProfilePictureCallback() {
      @Override
      public void onSuccess() {
        Glide.with(getActivity())
          .load(bitmap)
          .into(profilePictureIv);
        waitingDialog.hideDialog();
      }

      @Override
      public void onFailure(String message) {
        waitingDialog.hideDialog();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
      }
    });
  }

  public void addCourse(String key, Course course) {
    if(getContext() != null) {
      ProfileItemView<Course> profileItemView = new ProfileItemView<>(getContext(), key, this);
      profileItemView.setItem(course);

      courseViews.put(key, profileItemView);
      coursesLl.addView(profileItemView);
    }
  }

  public void removeCourse(String key) {
    if(getContext() != null) {
      coursesLl.removeView(courseViews.get(key));
      courseViews.remove(key);
    }
  }

  public void addExperience(String key, Experience experience) {
    if(getContext() != null) {
      ProfileItemView<Experience> profileItemView = new ProfileItemView<>(getContext(), key, this);
      profileItemView.setItem(experience);

      experienceViews.put(key, profileItemView);
      experiencesLl.addView(profileItemView);
    }
  }

  public void removeExperience(String key) {
    if(getContext() != null) {
      experiencesLl.removeView(experienceViews.get(key));
      experienceViews.remove(key);
    }
  }

  public void addEducation(String key, Education education) {
    if(getContext() != null) {
      ProfileItemView<Education> profileItemView = new ProfileItemView<>(getContext(), key, this);
      profileItemView.setItem(education);

      educationViews.put(key, profileItemView);
      educationsLl.addView(profileItemView);
    }
  }

  public void removeEducation(String key) {
    if(getContext() != null) {
      educationsLl.removeView(educationViews.get(key));
      educationViews.remove(key);
    }
  }

  public void setCurrentEducation(ProfileItemView<Education> education) {
    currentEducation = education;
  }

  public void setCurrentExperience(ProfileItemView<Experience> experience) {
    currentExperience = experience;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    userService.removeListenersFromUserDetails();
  }
}
