package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views;

import android.content.Context;
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
  private Notification currentNotification;

  public NotificationView(Context context, Notification notification) {
    super(context);
    this.context = context;
    currentNotification = notification;
    init();
  }

  private void init() {
    View view = inflate(context, R.layout.notification_view_layout, this);

    view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    setClickable(true);

  }
}
