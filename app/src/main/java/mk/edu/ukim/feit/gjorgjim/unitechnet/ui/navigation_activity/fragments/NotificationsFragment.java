package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.NotificationCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.NotificationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.Notification;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views.NotificationView;

/**
 * Created by gjmarkov on 16.5.2018.
 */

public class NotificationsFragment extends Fragment {

  private static final String LOG_TAG = NotificationsFragment.class.getSimpleName();

  private LinearLayout notificationsLl;

  NotificationService notificationService;

  public NotificationsFragment() {
    notificationService = NotificationService.getInstance();

    notificationViews = new HashMap<>();
  }

  private HashMap<String, NotificationView> notificationViews;

  private HashMap<String, Notification> allNotifications;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_notifications, container, false);

    notificationsLl = view.findViewById(R.id.notificationsLl);

    getNotificationsFromService();

    return view;
  }

  public void showNotifications() {
    Log.d(LOG_TAG, "showNotifications called");
    for(Map.Entry<String, Notification> notification : allNotifications.entrySet()) {
      Log.d(LOG_TAG, notification.getKey() + notification.getValue().toString());
      NotificationView notificationView = new NotificationView(getContext(), notification.getValue());

      if(!notificationViews.containsKey(notification.getKey())) {
        notificationViews.put(notification.getKey(), notificationView);
        notificationsLl.addView(notificationView);
      }
    }
  }

  private void getNotificationsFromService() {
    if(notificationService.getAllNotifications() != null) {
      allNotifications = notificationService.getAllNotifications();
      showNotifications();
    } else {
      notificationService.getNotifications(new NotificationCallback() {
        @Override
        public void onSuccess(HashMap<String, Notification> notifications) {
          Log.d(LOG_TAG, "onSuccess called");
          allNotifications = notifications;
          showNotifications();
        }

        @Override
        public void onFailure(String message) {
          Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
      });
    }
  }

  public void addNotification(String key, Notification notification) {
    if(!allNotifications.containsKey(key)) {
      allNotifications.put(key, notification);

      NotificationView notificationView = new NotificationView(getContext(), notification);

      notificationViews.put(key, notificationView);

      notificationsLl.addView(notificationView);
    }
  }
}
