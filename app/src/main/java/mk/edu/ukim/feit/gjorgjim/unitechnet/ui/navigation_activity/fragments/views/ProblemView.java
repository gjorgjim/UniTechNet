package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Problem;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.FragmentChangingListener;

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

  private FragmentChangingListener fragmentChangingListener;

  public ProblemView(Context context, Problem problem) {
    super(context);
    this.context = context;
    currentProblem = problem;
    fragmentChangingListener = (FragmentChangingListener) context;
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
      new ArrayList<>(currentProblem.getAuthor().values()).get(0).getFirstName(),
      new ArrayList<>(currentProblem.getAuthor().values()).get(0).getLastName()
    ));
    problemDateTv.setText(formatDate(currentProblem.getDate()));

    setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        fragmentChangingListener.changeToProblemViewFragment(currentProblem);
      }
    });

  }

  private String formatDate(String date) {
    Date dateString = Date.formatFromString(date);
    return String.format(
      new Locale("en"),
      "%s.%s.%s",
      formatNumber(dateString.getDay()),
      formatNumber(dateString.getMonth()),
      dateString.getYear()
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
