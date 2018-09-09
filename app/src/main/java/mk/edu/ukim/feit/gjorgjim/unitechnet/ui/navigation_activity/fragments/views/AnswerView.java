package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Answer;

/**
 * Created by gjmarkov on 09.9.2018.
 */

public class AnswerView extends RelativeLayout {

  private Context context;

  private Answer currentAnswer;

  private AppCompatTextView answerDescriptionTv;
  private AppCompatTextView answerAuthorTv;

  public AnswerView(Context context, Answer answer) {
    super(context);
    this.context = context;
    currentAnswer = answer;
    init();
  }

  private void init() {
    View view = inflate(context, R.layout.answer_view_layout, this);

    answerAuthorTv = view.findViewById(R.id.answerAuthorTv);
    answerDescriptionTv = view.findViewById(R.id.answerDescriptionTv);

    answerDescriptionTv.setText(currentAnswer.getDescription());
    answerAuthorTv.setText(String.format(
      new Locale("en"),
      "%s %s %s",
      context.getString(R.string.problem_author_placeholder),
      new ArrayList<>(currentAnswer.getAuthor().values()).get(0).getFirstName(),
      new ArrayList<>(currentAnswer.getAuthor().values()).get(0).getLastName()
    ));

    view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
  }
}
