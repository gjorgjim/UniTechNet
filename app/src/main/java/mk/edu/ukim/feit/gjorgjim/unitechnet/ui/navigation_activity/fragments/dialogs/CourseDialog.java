package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.method.ScrollingMovementMethod;

import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.CourseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.DatabaseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.UserService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.CoursesFragment;

/**
 * Created by gjmarkov on 31.7.2018.
 */

public class CourseDialog extends Dialog {

  private Context context;

  private Course currentCourse;

  private AppCompatTextView courseName;
  private AppCompatTextView courseDescription;
  private AppCompatButton subscribeBtn;
  private AppCompatTextView subscribedUsers;
  private AppCompatTextView solvedProblems;

  private DatabaseService databaseService;
  private AuthenticationService authenticationService;
  private UserService userService;
  private CourseService courseService;

  public CourseDialog(@NonNull Context context, Course course) {
    super(context);
    this.context = context;
    this.currentCourse = course;
    databaseService = DatabaseService.getInstance();
    authenticationService = AuthenticationService.getInstance();
    userService = UserService.getInstance();
    courseService = CourseService.getInstance();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_course);

    courseName = findViewById(R.id.courseNameTv);
    courseDescription = findViewById(R.id.courseDescriptionTv);
    subscribeBtn = findViewById(R.id.subscribeBtn);
    subscribedUsers = findViewById(R.id.subscribedUsersTv);
    solvedProblems = findViewById(R.id.solvedProblemsTv);

    courseDescription.setMovementMethod(new ScrollingMovementMethod());

    courseName.setText(currentCourse.getName());
    courseDescription.setText(currentCourse.getDescription());

    if(currentCourse.getSubscribedUsers() != null) {
      subscribedUsers.setText(String.format(
        new Locale("en"),
        "%s: %d",
        context.getString(R.string.subscribed_users_placeholder),
        currentCourse.getSubscribedUsers().values().size()
      ));
    } else {
      subscribedUsers.setText(String.format(
        new Locale("en"),
        "%s: %d",
        context.getString(R.string.subscribed_users_placeholder),
        0
      ));
    }

    if(currentCourse.getSolvedProblems() != null) {
      solvedProblems.setText(String.format(
        new Locale("en"),
        "%s: %d",
        context.getString(R.string.solved_problems_placeholder),
        currentCourse.getSolvedProblems().values().size()
      ));
    } else {
      solvedProblems.setText(String.format(
        new Locale("en"),
        "%s: %d",
        context.getString(R.string.solved_problems_placeholder),
        0
      ));
    }

    if(courseService.isUserSubscribed(currentCourse, authenticationService.getCurrentUser().getUid())) {
      subscribeBtn.setText(context.getString(R.string.unsubscribe_button));
    }

    subscribeBtn.setOnClickListener(v -> {
      if(subscribeBtn.getText().equals(context.getString(R.string.subscribe_button))) {
        databaseService.courseReference(currentCourse.getCourseId())
          .child("subscribedUsers")
          .child(authenticationService.getCurrentUser().getUid())
          .setValue(userService.getCurrentUser());

        courseService.subscribeUserToCourse(
          currentCourse.getCourseId(),
          userService.getCurrentUser(),
          authenticationService.getCurrentUser().getUid()
        );
      } else {
        databaseService.courseReference(currentCourse.getCourseId())
          .child("subscribedUsers")
          .child(authenticationService.getCurrentUser().getUid())
          .removeValue();

        courseService.unsubscribeUserFromCourse(
          currentCourse.getCourseId(),
          authenticationService.getCurrentUser().getUid()
        );
      }

      dismiss();
    });
  }
}
