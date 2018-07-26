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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.UserService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Education;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Experience;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs.EditUserDetailsDialog;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs.NewEducationDialog;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs.NewExperienceDialog;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views.ProfileItemView;

/**
 * Created by gjmarkov on 16.5.2018.
 */

public class ProfileFragment extends Fragment{

  private UserService userService;
  private AuthenticationService authenticationService;

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

    courseViews = new HashMap<>();
    experienceViews = new HashMap<>();
    educationViews = new HashMap<>();

    profilePictureIv.setOnClickListener(v -> {
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

    profilePictureIv.setImageBitmap(userService.getBitmapFromMemCache("profilePicture"));

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

  public void setStartDateExperience(int year, int month, int day) {
    experienceDialog.setStartDate(year, month, day);
  }

  public void setEndDateExperience(int year, int month, int day) {
    experienceDialog.setEndDate(year, month, day);
  }

  public void setStartDateEducation(int year, int month, int day) {
    educationDialog.setStartDate(year, month, day);
  }

  public void setEndDateEducation(int year, int month, int day) {
    educationDialog.setEndDate(year, month, day);
  }

  public void setBirthDayDetails(int year, int month, int day) {
    userDetailsDialog.setBirthday(year, month, day);
  }

  public void setStartDateEditEducation(int year, int month, int day) {
    currentEducation.setStartDateEducation(year, month, day);
  }

  public void setEndDateEditEducation(int year, int month, int day) {
    currentEducation.setEndDateEducation(year, month, day);
  }

  public void setStartDateEditExperience(int year, int month, int day) {
    currentExperience.setStartDateExperience(year, month, day);
  }

  public void setEndDateEditExperience(int year, int month, int day) {
    currentExperience.setEndDateExperience(year, month, day);
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
}
