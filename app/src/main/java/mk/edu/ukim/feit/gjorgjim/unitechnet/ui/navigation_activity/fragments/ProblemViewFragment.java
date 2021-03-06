package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.DataManager;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Answer;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Problem;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs.PostAnswerDialog;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views.AnswerView;

import static android.view.View.GONE;

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
  private FloatingActionButton addAnswerBtn;

  private HashMap<String, AnswerView> answerViews;

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
    addAnswerBtn = view.findViewById(R.id.addAnswerBtn);

    answerViews = new HashMap<>();

    Bundle bundle = getArguments();
    if(bundle != null) {
      if(bundle.get("currentProblem") != null) {
        currentProblem = (Problem) bundle.get("currentProblem");
      }
    }

    problemNameTv.setText(currentProblem.getName());
    problemDescription.setText(currentProblem.getDescription());

    setAnsweredTextView();

    showAnswers();

    addAnswerBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new PostAnswerDialog(getContext(), ProblemViewFragment.this).show();
      }
    });

    return view;
  }

  public void showAnswers() {
    if(currentProblem.getAnswers() != null && currentProblem.getAnswers().size() > 0) {
      noAnswersTv.setVisibility(GONE);
      for (Map.Entry<String, Answer> current : currentProblem.getAnswers().entrySet()) {
        AnswerView answerView = new AnswerView(getContext(), this, current.getValue(), currentProblem.getAuthor());

        if (!answerViews.containsKey(current.getKey())) {
          if (current.getValue().isAnswer()) {
            currentAnswerView = answerView;
          }
          answerViews.put(current.getKey(), answerView);
          answersLl.addView(answerView);
        }
      }
    } else {
      noAnswersTv.setVisibility(View.VISIBLE);
    }
  }

  public void setAnsweredTextView() {
    if(!currentProblem.getAnswerid().equals("false")) {
      solvedTv.setText(getString(R.string.answered));
      solvedTv.setTextColor(getResources().getColor(R.color.answeredProblemColor));
    } else {
      solvedTv.setText(getString(R.string.not_answered));
      solvedTv.setTextColor(getResources().getColor(R.color.colorPrimary));
    }
  }

  public AnswerView getCurrentAnswerView() {
    return currentAnswerView;
  }

  public void setCurrentAnswerView(AnswerView answerView) {
    currentAnswerView = answerView;
  }

  public void deleteAnswerView(Answer answer) {

    String answerKey = dataManager.getAnswerKey(currentProblem.getAnswers(), answer);

    if(answer.isAnswer()) {
      currentProblem.setAnswerid("false");
    }

    setAnsweredTextView();
    answersLl.removeView(answerViews.get(answerKey));
  }

  public void showFloatingActionButton() {
    addAnswerBtn.show();
  }

  public void hideFloatingActionButton() {
    addAnswerBtn.hide();
  }

}
