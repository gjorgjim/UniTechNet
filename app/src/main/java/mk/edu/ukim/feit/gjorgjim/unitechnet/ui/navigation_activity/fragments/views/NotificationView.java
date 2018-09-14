package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.Notification;

/**
 * Created by gjmarkov on 13.9.2018.
 */

public class NotificationView extends RelativeLayout {

  private Context context;

  private RelativeLayout mainLayout;
  private AppCompatTextView notificationTv;
  private View underline;

  private Notification currentNotification;

  public NotificationView(Context context, Notification notification) {
    super(context);
    this.context = context;
    currentNotification = notification;
    init();
  }

  private void init() {
    View view = inflate(context, R.layout.notification_view_layout, this);

    notificationTv = view.findViewById(R.id.notificationTv);
    mainLayout = view.findViewById(R.id.mainLayout);
    underline = view.findViewById(R.id.underline);

    view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    setClickable(true);

    if(!currentNotification.getSeen()) {
      mainLayout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
      underline.setBackground(context.getDrawable(R.drawable.underline_white));
    }

    if(currentNotification.getType().equals(Notification.NEW_PROBLEM_IN_COURSE)) {
      notificationTv.setText(context.getString(R.string.new_problem_notification));
    } else {
      notificationTv.setText(context.getString(R.string.new_answer_notification));
    }

    setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(NotificationView.class.getSimpleName(), "onClick called");
      }
    });
  }
}
