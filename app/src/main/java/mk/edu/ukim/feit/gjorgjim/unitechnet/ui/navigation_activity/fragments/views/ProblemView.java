package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Problem;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;

/**
 * Created by gjmarkov on 06.9.2018.
 */

public class ProblemView extends RelativeLayout {

  private Problem currentProblem;

  private Context context;

  private AppCompatTextView problemNameTv;
  private AppCompatTextView problemDescriptionTv;
  private AppCompatTextView problemAuthorTv;
  private AppCompatTextView problemDateTv;

  public ProblemView(Context context, Problem problem) {
    super(context);
    this.context = context;
    currentProblem = problem;
    init();
  }

  private void init() {
    View view = inflate(context, R.layout.problem_view_layout, this);

    view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    setClickable(true);

    problemNameTv = view.findViewById(R.id.problemNameTv);
    problemDescriptionTv = view.findViewById(R.id.problemDescriptionTv);
    problemAuthorTv = view.findViewById(R.id.problemAuthorTv);
    problemDateTv = view.findViewById(R.id.problemDateTv);

    problemNameTv.setText(currentProblem.getName());
    problemDescriptionTv.setText(currentProblem.getDescription());
    problemAuthorTv.setText(String.format(
      new Locale("en"),
      "%s %s %s",
      context.getString(R.string.problem_author_placeholder),
      currentProblem.getAuthor().getFirstName(),
      currentProblem.getAuthor().getLastName()
    ));
    problemDateTv.setText(formatDate(currentProblem.getDate()));

  }

  private String formatDate(Date date) {
    return String.format(
      new Locale("en"),
      "%s.%s.%s",
      formatNumber(date.getDay()),
      formatNumber(date.getMonth()),
      date.getYear()
    );
  }

  private String formatNumber(int n) {
    if(n < 10) {
      return String.format(new Locale("en"),"0%d", n);
    } else {
      return String.format(new Locale("en"),"%d", n);
    }
  }
}
