package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

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
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.UserService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Education;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Experience;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;
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

  private HashMap<String, ProfileItemView<Course>> courseViews;
  private HashMap<String, ProfileItemView<Experience>> experienceViews;
  private HashMap<String, ProfileItemView<Education>> educationViews;

  private NewExperienceDialog experienceDialog;


  public ProfileFragment() {
    userService = UserService.getInstance();
    authenticationService = AuthenticationService.getInstance();
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

    if(user.getCourses() != null) {
      for(Course current : user.getCourses().values()) {
        ProfileItemView<Course> profileItemView = new ProfileItemView<>(getContext());
        profileItemView.setItem(current);

        courseViews.put(current.getName(), profileItemView);
        coursesLl.addView(profileItemView);
      }
    }

    if(user.getEducations() != null) {
      for(Education current: user.getEducations().values()) {
        ProfileItemView<Education> profileItemView = new ProfileItemView<>(getContext());
        profileItemView.setItem(current);

        educationViews.put(String.format("%s/%s", current.getDegree(), current.getSchool()), profileItemView);
        educationsLl.addView(profileItemView);
      }
    }

    if(user.getExperiences() != null) {
      for(Experience current: user.getExperiences().values()) {
        ProfileItemView<Experience> profileItemView = new ProfileItemView<>(getContext());
        profileItemView.setItem(current);

        experienceViews.put(String.format("%s/%s", current.getJobTitle(), current.getCompany()), profileItemView);
        experiencesLl.addView(profileItemView);
      }
    }

  }

  public void setStartDateExperience(int year, int month, int day) {
    experienceDialog.setStartDate(year, month, day);
  }

  public void setEndDateExperience(int year, int month, int day) {
    experienceDialog.setEndDate(year, month, day);
  }

  public void addCourse(Course course) {
    if(getContext() != null) {
      ProfileItemView<Course> profileItemView = new ProfileItemView<>(getContext());
      profileItemView.setItem(course);

      courseViews.put(course.getName(), profileItemView);
      coursesLl.addView(profileItemView);
    }
  }

  public void removeCourse(Course course) {
    if(getContext() != null) {
      coursesLl.removeView(courseViews.get(course.getName()));
      courseViews.remove(course.getName());
    }
  }

  public void addExperience(Experience experience) {
    if(getContext() != null) {
      ProfileItemView<Experience> profileItemView = new ProfileItemView<>(getContext());
      profileItemView.setItem(experience);

      experienceViews.put(String.format("%s/%s", experience.getJobTitle(), experience.getCompany()), profileItemView);
      experiencesLl.addView(profileItemView);
    }
  }

  public void removeExperience(Experience experience) {
    if(getContext() != null) {
      experiencesLl.removeView(experienceViews.get(
        String.format("%s/%s", experience.getJobTitle(), experience.getCompany()))
      );
      experienceViews.remove(String.format("%s/%s", experience.getJobTitle(), experience.getCompany()));
    }
  }

  public void addEducation(Education education) {
    if(getContext() != null) {
      ProfileItemView<Education> profileItemView = new ProfileItemView<>(getContext());
      profileItemView.setItem(education);

      educationViews.put(String.format("%s/%s", education.getDegree(), education.getSchool()), profileItemView);
      educationsLl.addView(profileItemView);
    }
  }

  public void removeEducation(Education education) {
    if(getContext() != null) {
      educationsLl.removeView(experienceViews.get(
        String.format("%s/%s", education.getDegree(), education.getSchool()))
      );
      educationViews.remove(String.format("%s/%s", education.getDegree(), education.getSchool()));
    }
  }
}
