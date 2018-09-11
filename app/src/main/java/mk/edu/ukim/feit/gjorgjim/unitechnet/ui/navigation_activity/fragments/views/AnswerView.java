package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Answer;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;

/**
 * Created by gjmarkov on 09.9.2018.
 */

public class AnswerView extends RelativeLayout {

  private Context context;

  private AuthenticationService authenticationService;

  private Answer currentAnswer;

  private HashMap<String, User> problemAuthor;

  private AppCompatTextView answerDescriptionTv;
  private AppCompatTextView answerAuthorTv;
  private RelativeLayout answerAuthorRl;
  private RelativeLayout problemAuthorRl;
  private AppCompatImageView editAnswerIv;
  private AppCompatImageView deleteAnswerIv;
  private AppCompatTextView markAsAnswerTv;

  public AnswerView(Context context, Answer answer, HashMap<String, User> problemAuthor) {
    super(context);
    this.context = context;
    currentAnswer = answer;
    this.problemAuthor = problemAuthor;
    authenticationService = AuthenticationService.getInstance();
    init();
  }

  private void init() {
    View view = inflate(context, R.layout.answer_view_layout, this);

    answerAuthorTv = view.findViewById(R.id.answerAuthorTv);
    answerDescriptionTv = view.findViewById(R.id.answerDescriptionTv);
    answerAuthorRl = view.findViewById(R.id.answerAuthorRl);
    problemAuthorRl = view.findViewById(R.id.problemAuthorRl);
    editAnswerIv = view.findViewById(R.id.editAnswerIv);
    deleteAnswerIv = view.findViewById(R.id.deleteAnswerIv);
    markAsAnswerTv = view.findViewById(R.id.markAsAnswerTv);

    answerDescriptionTv.setText(currentAnswer.getDescription());
    answerAuthorTv.setText(String.format(
      new Locale("en"),
      "%s %s %s",
      context.getString(R.string.problem_author_placeholder),
      new ArrayList<>(currentAnswer.getAuthor().values()).get(0).getFirstName(),
      new ArrayList<>(currentAnswer.getAuthor().values()).get(0).getLastName()
    ));

    if(isSameAuthor()) {
      showBothViews();
    } else if(isAnswerAuthor()) {
      showAnswerAuthorView();
    } else if(isProblemAuthor()) {
      showProblemAuthorView();
    } else {
      throw new UnsupportedOperationException();
    }

    view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
  }

  private boolean isSameAuthor() {
    return problemAuthor.containsKey(authenticationService.getCurrentUser().getUid()) &&
      currentAnswer.getAuthor().containsKey(authenticationService.getCurrentUser().getUid());
  }

  private boolean isAnswerAuthor() {
    return currentAnswer.getAuthor().containsKey(authenticationService.getCurrentUser().getUid());
  }

  private boolean isProblemAuthor() {
    return problemAuthor.containsKey(authenticationService.getCurrentUser().getUid());
  }

  private void showAnswerAuthorView() {
    answerAuthorRl.setVisibility(VISIBLE);
    problemAuthorRl.setVisibility(GONE);
  }

  private void showProblemAuthorView() {
    answerAuthorRl.setVisibility(GONE);
    problemAuthorRl.setVisibility(VISIBLE);
  }

  private void showBothViews() {
    answerAuthorRl.setVisibility(VISIBLE);
    problemAuthorRl.setVisibility(VISIBLE);
  }
}
