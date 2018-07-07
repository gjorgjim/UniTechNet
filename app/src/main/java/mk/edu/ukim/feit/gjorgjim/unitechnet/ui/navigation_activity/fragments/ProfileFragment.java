package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by gjmarkov on 16.5.2018.
 */

public class ProfileFragment extends Fragment implements DatabaseCallback<User>{

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

  private ProgressWindow window;

  public ProfileFragment() {
    userService = UserService.getInstance();
    authenticationService = AuthenticationService.getInstance();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);

    showWaitingDialog();

    courseLv = view.findViewById(R.id.coursesLv);
    educationLv = view.findViewById(R.id.educationLv);
    experienceLv = view.findViewById(R.id.experienceLv);
    nameTv = view.findViewById(R.id.nameTv);
    titleTv = view.findViewById(R.id.titleTv);
    usernameTv = view.findViewById(R.id.usernameTv);
    birthdayTv = view.findViewById(R.id.birthdayTv);
    profilePictureIv = view.findViewById(R.id.profilePictureIv);

    profilePictureIv.setOnClickListener(v -> {
      authenticationService.signOut(getActivity());
    });

    userService.setUserCallback(this);
    userService.findCurrentUser();

    return view;
  }

  @Override
  public void onSuccess(User user) {
    setUserUI(user);
  }

  @Override
  public void onFailure(String message) {

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
      }).addOnFailureListener(Throwable::printStackTrace)
      .addOnCompleteListener(task -> hideWaitingDialog());
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

    ProfileListAdapter<Experience> experienceAdapter;
    if(user.getExperiences() != null) {
      experienceAdapter = new ProfileListAdapter<>(
        getContext(),
        R.layout.profile_list_item_layout,
        user.getExperiences()
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

  private void showWaitingDialog(){
    window = ProgressWindow.getInstance(getActivity());
    ProgressWindowConfiguration configuration = new ProgressWindowConfiguration();
    configuration.progressColor = Color.parseColor("#F44336");
    configuration.backgroundColor = R.color.colorPrimary;
    window.setConfiguration(configuration);
    window.showProgress();
  }

  private void hideWaitingDialog(){
    window.hideProgress();
  }
}
