package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.ListDatabaseCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.CourseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Problem;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views.ProblemView;

import static android.view.View.GONE;

/**
 * Created by gjmarkov on 06.9.2018.
 */

public class CourseViewFragment extends Fragment {

  private CourseService courseService;
  private AuthenticationService authenticationService;

  private AppCompatTextView courseNameTv;
  private AppCompatTextView courseDescriptionTv;
  private AppCompatTextView subscribedUsersTv;
  private AppCompatTextView solvedProblemsTv;
  private AppCompatButton subscribeButton;
  private LinearLayout problemsLl;
  private AppCompatTextView hideProblemsTv;
  private ContentLoadingProgressBar progressBar;

  private Course currentCourse;

  public CourseViewFragment() {
    courseService = CourseService.getInstance();
    authenticationService = AuthenticationService.getInstance();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_course_view, container, false);

    courseNameTv = view.findViewById(R.id.courseNameTv);
    courseDescriptionTv = view.findViewById(R.id.courseDescriptionTv);
    subscribedUsersTv = view.findViewById(R.id.subscribedUsersTv);
    solvedProblemsTv = view.findViewById(R.id.solvedProblemsTv);
    subscribeButton = view.findViewById(R.id.subscribeBtn);
    problemsLl = view.findViewById(R.id.problemsLl);
    hideProblemsTv = view.findViewById(R.id.hideProblemsTv);
    progressBar = view.findViewById(R.id.waitingPb);

    Bundle bundle = getArguments();
    if(bundle != null) {
      if(bundle.get("currentCourse") != null) {
        currentCourse = (Course) bundle.get("currentCourse");
      }
    }

    courseNameTv.setText(currentCourse.getName());
    courseDescriptionTv.setText(currentCourse.getDescription());
    subscribedUsersTv.setText(String.format(
      new Locale("en"),
      "%s %d",
      getString(R.string.subscribed_users_placeholder),
      currentCourse.getSubscribedUsers() == null ? 0 : currentCourse.getSubscribedUsers().size()
    ));
    solvedProblemsTv.setText(String.format(
      new Locale("en"),
      "%s %d",
      getString(R.string.solved_problems_placeholder),
      currentCourse.getProblems() == null ? 0 : currentCourse.getProblems().size()
    ));

    if(courseService.isUserSubscribed(currentCourse, authenticationService.getCurrentUser().getUid())) {
      setupSubscribedUser();
    } else {
      setupUnsubscribedUser();
    }

    subscribeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        subscribeButton.setVisibility(View.GONE);
        if(subscribeButton.getText().equals(getString(R.string.subscribe_button))) {

          courseService.subscribeUserToCourse(currentCourse.getCourseId(),
            authenticationService.getCurrentUser().getUid(), new ListDatabaseCallback<Course>() {
              @Override
              public void onSuccess(List<Course> list) {
                setupSubscribedUser();
                subscribeButton.setVisibility(View.VISIBLE);
              }

              @Override
              public void onFailure(String message) {
                Toast.makeText(getContext(), message , Toast.LENGTH_SHORT).show();
                subscribeButton.setVisibility(View.VISIBLE);
              }
            });

        } else {

          courseService.unsubscribeUserFromCourse(currentCourse.getCourseId(),
            authenticationService.getCurrentUser().getUid(), new ListDatabaseCallback<Course>() {
              @Override
              public void onSuccess(List<Course> list) {
                setupUnsubscribedUser();
                subscribeButton.setVisibility(View.VISIBLE);
              }

              @Override
              public void onFailure(String message) {
                Toast.makeText(getContext(), message , Toast.LENGTH_SHORT).show();
                subscribeButton.setVisibility(View.VISIBLE);
              }
            });

        }
      }
    });

    return view;
  }

  private void setupSubscribedUser() {
    subscribeButton.setText(getString(R.string.unsubscribe_button));
    subscribedUsersTv.setText(String.format(
      new Locale("en"),
      "%s %d",
      getString(R.string.subscribed_users_placeholder),
      currentCourse.getSubscribedUsers() == null ? 0 : currentCourse.getSubscribedUsers().size()
    ));
    hideProblemsTv.setVisibility(GONE);
    problemsLl.setVisibility(View.VISIBLE);
    showProblems();
  }

  private void setupUnsubscribedUser() {
    subscribeButton.setText(getString(R.string.subscribe_button));
    subscribedUsersTv.setText(String.format(
      new Locale("en"),
      "%s %d",
      getString(R.string.subscribed_users_placeholder),
      currentCourse.getSubscribedUsers() == null ? 0 : currentCourse.getSubscribedUsers().size()
    ));
    problemsLl.removeAllViews();
    problemsLl.setVisibility(GONE);
    hideProblemsTv.setVisibility(View.VISIBLE);
  }

  private void showProblems() {
    if(currentCourse.getProblems() != null) {
      for(Problem problem : currentCourse.getProblems().values()) {
        ProblemView problemView = new ProblemView(getContext(), problem);

        problemsLl.addView(problemView);
      }
    } else {
      TextView emptyProblems = new TextView(getContext());

      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
      );

      lp.gravity = Gravity.CENTER;

      emptyProblems.setText(getString(R.string.empty_solved_problems));

      problemsLl.addView(emptyProblems);
    }
  }
}
