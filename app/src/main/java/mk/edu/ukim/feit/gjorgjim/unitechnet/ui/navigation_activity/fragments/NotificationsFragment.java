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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.NotificationCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.NotificationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.DataManager;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.Notification;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views.NotificationView;

/**
 * Created by gjmarkov on 16.5.2018.
 */

public class NotificationsFragment extends Fragment {

  private static final String LOG_TAG = NotificationsFragment.class.getSimpleName();

  private LinearLayout notificationsLl;

  private NotificationService notificationService;
  private DataManager dataManager;

  public NotificationsFragment() {
    notificationService = NotificationService.getInstance();
    dataManager = DataManager.getInstance();

    notificationViews = new HashMap<>();
  }

  private HashMap<String, NotificationView> notificationViews;

  private HashMap<String, Notification> allNotifications;

  private List<Notification> notifications = null;

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

    List<Notification> list = new ArrayList<>(allNotifications.values());
    Collections.sort(list, new Comparator<Notification>() {
      @Override
      public int compare(Notification o1, Notification o2) {
        if(Date.formatFromString(o1.getDate()).isAfter(Date.formatFromString(o2.getDate()))) {
          return -1;
        } else {
          return 1;
        }
      }
    });

    for(Notification current : list) {
      NotificationView notificationView = new NotificationView(getContext(), current);
      String key = dataManager.getNotificationKey(allNotifications, current);

      if(!notificationViews.containsKey(key)) {
        notificationViews.put(key, notificationView);
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

      notificationsLl.addView(notificationView, 0);
    }
  }

  @Override
  public void onDestroy() {
    notificationService.setNotificationsSeen();
    super.onDestroy();
  }
}
