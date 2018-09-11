package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.Map;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.DataManager;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Answer;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Problem;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views.AnswerView;

/**
 * Created by gjmarkov on 09.9.2018.
 */

public class ProblemViewFragment extends Fragment {

  private AuthenticationService authenticationService;

  private DataManager dataManager;

  private AppCompatTextView problemNameTv;
  private AppCompatTextView problemDescription;
  private LinearLayout answersLl;
  private AppCompatTextView noAnswersTv;
  private AppCompatTextView solvedTv;

  private Problem currentProblem;

  private AnswerView currentAnswerView;

  public ProblemViewFragment() {
    authenticationService = AuthenticationService.getInstance();
    dataManager = DataManager.getInstance();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_problem_view, container, false);

    problemNameTv = view.findViewById(R.id.problemNameTv);
    problemDescription = view.findViewById(R.id.problemDescriptionTv);
    answersLl = view.findViewById(R.id.answersLl);
    noAnswersTv = view.findViewById(R.id.noAnswersTv);
    solvedTv = view.findViewById(R.id.solvedTv);

    Bundle bundle = getArguments();
    if(bundle != null) {
      if(bundle.get("currentProblem") != null) {
        currentProblem = (Problem) bundle.get("currentProblem");
      }
    }

    problemNameTv.setText(currentProblem.getName());
    problemDescription.setText(currentProblem.getDescription());

    if(currentProblem.isSolved()) {
        solvedTv.setText(getString(R.string.answered));
        solvedTv.setTextColor(getResources().getColor(R.color.answeredProblemColor));
    } else {
        solvedTv.setText(getString(R.string.not_answered));
        solvedTv.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    if(currentProblem.getAnswers() != null) {
      noAnswersTv.setVisibility(View.GONE);
      showAnswers();
    } else {
      answersLl.setVisibility(View.GONE);
      noAnswersTv.setVisibility(View.VISIBLE);
    }

    return view;
  }

  private void showAnswers() {
    for (Map.Entry<String, Answer> current : currentProblem.getAnswers().entrySet()) {
      AnswerView answerView = null;
      if (currentAnswerView == null && currentProblem.getAnswerid().equals(current.getKey())) {
          answerView = new AnswerView(getContext(), this, current.getValue(), currentProblem.getAuthor(), true);
          currentAnswerView = answerView;
      } else {
        answerView = new AnswerView(getContext(), this, current.getValue(), currentProblem.getAuthor(), false);
      }

      answersLl.addView(answerView);
    }
  }

  public AnswerView getCurrentAnswerView() {
    return currentAnswerView;
  }

}
