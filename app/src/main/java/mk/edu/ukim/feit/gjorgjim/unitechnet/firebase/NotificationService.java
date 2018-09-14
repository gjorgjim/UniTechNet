package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.NotificationCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.Notification;
import mk.edu.ukim.feit.gjorgjim.unitechnet.services.NotificationBackgroundService;

/**
 * Created by gjmarkov on 13.9.2018.
 */

public class NotificationService {
  public static final String LOG_TAG = NotificationService.class.getSimpleName();

  private static final NotificationService ourInstance = new NotificationService();

  public static NotificationService getInstance() {
    return ourInstance;
  }

  private DatabaseService databaseService;
  private AuthenticationService authenticationService;

  private HashMap<String, Notification> allNotifications;

  private NotificationCallback callback;

  private Intent notificationsIntent;

  private NotificationService() {
    allNotifications = new HashMap<>();
    databaseService = DatabaseService.getInstance();
    authenticationService = AuthenticationService.getInstance();

    allNotifications = null;
  }

  private ValueEventListener lastNotifications = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      allNotifications = new HashMap<>();
      Log.d(LOG_TAG, "onDataChange called");
      Log.d(LOG_TAG, "dataSnapshot size: " + dataSnapshot.getChildrenCount());
      for(DataSnapshot data : dataSnapshot.getChildren()) {
        Log.d(LOG_TAG, "Notification: " + data.getValue(Notification.class).toString());
        allNotifications.put(data.getKey(), data.getValue(Notification.class));
      }

      callback.onSuccess(allNotifications);
      databaseService.courseReference(authenticationService.getCurrentUser().getUid())
        .child("notifications").removeEventListener(lastNotifications);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
  };

  public void getNotifications(NotificationCallback callback) {
    Log.d(LOG_TAG, "getNotifications called");
    this.callback = callback;

    databaseService.userReference(authenticationService.getCurrentUser().getUid())
      .child("notifications").limitToLast(20).addListenerForSingleValueEvent(lastNotifications);
  }

  public HashMap<String, Notification> getAllNotifications() {
    Log.d(LOG_TAG, "getAllNotifications called");
    return allNotifications;
  }

  public void setNotificationsSeen() {
    for(Map.Entry<String, Notification> current : allNotifications.entrySet()) {
      current.getValue().setSeen(true);
      databaseService.userReference(authenticationService.getCurrentUser().getUid())
        .child("notifications").child(current.getKey()).child("seen").setValue(true);
    }
  }

  public void startBackgroundServiceForMessages(Activity activity) {
    notificationsIntent = new Intent(activity, NotificationBackgroundService.class);
    notificationsIntent.setAction(NotificationBackgroundService.ACTION_START_SERVICE);

    activity.startService(notificationsIntent);
  }

  public void stopBackgroundServiceForMessages(Activity activity) {
    activity.stopService(notificationsIntent);
  }
}
