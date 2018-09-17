package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.FeedItem;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Problem;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.FragmentChangingListener;

/**
 * Created by gjmarkov on 14.9.2018.
 */

public class FeedItemView extends RelativeLayout {

  private Context context;

  private AuthenticationService authenticationService;

  private FragmentChangingListener fragmentChangingListener;

  private FeedItem feedItem;

  private Course currentCourse;
  private Problem currentProblem;

  private AppCompatTextView problemNameTv;
  private AppCompatTextView problemDescriptionTv;
  private AppCompatTextView problemAuthorTv;
  private AppCompatTextView problemDateTv;
  private AppCompatTextView courseNameTv;
  private RelativeLayout problemInfoRl;

  public FeedItemView(Context context, FeedItem feedItem, FragmentChangingListener fragmentChangingListener) {
    super(context);
    this.context = context;
    this.feedItem = feedItem;
    currentCourse = feedItem.getCourse();
    currentProblem = feedItem.getCourse().getProblems().get(feedItem.getProblemId());
    authenticationService = AuthenticationService.getInstance();
    this.fragmentChangingListener = fragmentChangingListener;
    init();
  }

  private void init() {
    View view = inflate(context, R.layout.feed_item_view, this);

    view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    setClickable(true);

    problemNameTv = view.findViewById(R.id.problemNameTv);
    problemDescriptionTv = view.findViewById(R.id.problemDescriptionTv);
    problemAuthorTv = view.findViewById(R.id.problemAuthorTv);
    problemDateTv = view.findViewById(R.id.problemDateTv);
    courseNameTv = view.findViewById(R.id.courseNameTv);
    problemInfoRl = view.findViewById(R.id.problemInfoRl);

    problemNameTv.setText(currentProblem.getName());
    problemDescriptionTv.setText(currentProblem.getDescription());
    String author = "";
    if(isProblemAuthor()) {
      author = "You";
    } else {
      List<User> authorUser = new ArrayList<>(currentProblem.getAuthor().values());
      author = String.format("%s %s", authorUser.get(0).getFirstName(), authorUser.get(0).getLastName());
    }
    problemAuthorTv.setText(String.format(
      new Locale("en"),
      "%s",
      author
    ));
    problemDateTv.setText(formatDate(currentProblem.getDate()));
    courseNameTv.setText(currentCourse.getName());

    problemInfoRl.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        fragmentChangingListener.changeToProblemViewFragment(currentProblem, currentCourse);
      }
    });

    problemAuthorTv.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });

    courseNameTv.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        fragmentChangingListener.changeToCourseViewFragment(currentCourse, null);
      }
    });
  }

  private boolean isProblemAuthor() {
    return currentProblem.getAuthor().containsKey(authenticationService.getCurrentUser().getUid());
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
