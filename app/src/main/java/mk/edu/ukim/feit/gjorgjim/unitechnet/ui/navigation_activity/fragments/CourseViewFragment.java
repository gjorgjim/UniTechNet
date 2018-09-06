package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.CourseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Problem;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views.ProblemView;

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
  private LinearLayout solvedProblemsLl;
  private LinearLayout unsolvedProblemsLl;

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
    solvedProblemsLl = view.findViewById(R.id.solvedProblemsLl);
    unsolvedProblemsLl = view.findViewById(R.id.unsolvedProblemsLl);

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
      currentCourse.getSolvedProblems() == null ? 0 : currentCourse.getSolvedProblems().size()
    ));

    if(courseService.isUserSubscribed(currentCourse, authenticationService.getCurrentUser().getUid())) {
      subscribeButton.setText(getString(R.string.unsubscribe_button));
    }

    if(currentCourse.getSolvedProblems() != null) {
      for(Problem problem : currentCourse.getSolvedProblems().values()) {
        ProblemView problemView = new ProblemView(getContext(), problem);

        solvedProblemsLl.addView(problemView);
      }
    } else {
      TextView emptySolvedProblems = new TextView(getContext());

      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
      );

      lp.gravity = Gravity.CENTER;

      emptySolvedProblems.setText(getString(R.string.empty_solved_problems));

      solvedProblemsLl.addView(emptySolvedProblems);
    }

    if(currentCourse.getUnsolvedProblems() != null) {
      for(Problem problem : currentCourse.getUnsolvedProblems().values()) {
        ProblemView problemView = new ProblemView(getContext(), problem);

        unsolvedProblemsLl.addView(problemView);
      }
    } else {
      TextView emptyUnsolvedProblems = new TextView(getContext());

      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
      );

      lp.gravity = Gravity.CENTER;

      emptyUnsolvedProblems.setText(getString(R.string.empty_unsolved_problems));

      unsolvedProblemsLl.addView(emptyUnsolvedProblems);
    }

    return view;
  }
}
