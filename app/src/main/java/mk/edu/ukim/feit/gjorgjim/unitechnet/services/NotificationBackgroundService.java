package mk.edu.ukim.feit.gjorgjim.unitechnet.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.List;

import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.ServiceCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.DatabaseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.Notification;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.notifications.NotificationCenter;

/**
 * Created by gjmarkov on 13.9.2018.
 */

public class NotificationBackgroundService extends Service {

  private static final String LOG_TAG = NotificationBackgroundService.class.getSimpleName();

  public final static String ACTION_START_SERVICE = "mk.edu.ukim.feit.gjorgjim.unitechnet.services.startBackgroundService";

  public static final String ACTION = "mk.edu.ukim.feit.gjorgjim.unitechnet.services.NotificationBackgroundServiceAction";

  public static boolean isServiceRunning = false;

  public static ServiceCallback callback;

  private DatabaseService databaseService;
  private AuthenticationService authenticationService;

  private NotificationCenter notificationCenter;

  private Date now;

  private ChildEventListener childEventListener = new ChildEventListener() {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//      Notification notification = dataSnapshot.getValue(Notification.class);
//
//      if(Date.formatFromString(notification.getDate()).isAfter(now)) {
//        if(notification.getType().equals(Notification.NEW_ANSWER_IN_PROBLEM) ||
//          notification.getType().equals(Notification.NEW_PROBLEM_IN_COURSE)) {
//          if(!isRunning()) {
//            notificationCenter.sentNotification(notification);
//          } else {
//            Bundle bundle = new Bundle();
//
//            bundle.putString("key", dataSnapshot.getKey());
//            bundle.putSerializable("notification", notification);
//
//            Intent broadcastIntent = new Intent(ACTION);
//            broadcastIntent.putExtra("info", bundle);
//
//            LocalBroadcastManager.getInstance(NotificationBackgroundService.this).sendBroadcast(broadcastIntent);
//          }
//        }
//      }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
      Log.d(LOG_TAG, "onChildChanged called: " + dataSnapshot.toString());
      Notification notification = dataSnapshot.getValue(Notification.class);

      if(Date.formatFromString(notification.getDate()).isAfter(now)) {
        Log.d(LOG_TAG, "Notification is after now");
        if(notification.getType().equals(Notification.NEW_ANSWER_IN_PROBLEM) ||
          notification.getType().equals(Notification.NEW_PROBLEM_IN_COURSE)) {
          if(!isRunning()) {
            Log.d(LOG_TAG, "App is not running");
            notificationCenter.sentNotification(notification);
          } else {
            Bundle bundle = new Bundle();

            bundle.putString("key", dataSnapshot.getKey());
            bundle.putSerializable("notification", notification);

            Intent broadcastIntent = new Intent(ACTION);
            broadcastIntent.putExtra("info", bundle);

            Log.d(LOG_TAG, "App is running, sending broadcast");
            LocalBroadcastManager.getInstance(NotificationBackgroundService.this).sendBroadcast(broadcastIntent);
          }
        }
      }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
  };

  @Override
  public void onCreate() {
    super.onCreate();

    callback = null;

    startServiceWithNotifications();
  }

  @Override
  public void onDestroy() {
    isServiceRunning = false;
    databaseService.userReference(authenticationService.getCurrentUser().getUid())
      .child("notifications").removeEventListener(childEventListener);

    if(callback != null) {
      callback.onDone();
    }
    callback = null;
    super.onDestroy();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null && intent.getAction().equals(ACTION_START_SERVICE)) {
      startServiceWithNotifications();
    }
    return START_STICKY;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  private void startServiceWithNotifications() {
    if (isServiceRunning)
      return;

    isServiceRunning = true;

    databaseService = DatabaseService.getInstance();
    authenticationService = AuthenticationService.getInstance();

    notificationCenter = NotificationCenter.getInstance();
    notificationCenter.setContext(this);

    now = Date.getDate();

    databaseService.userReference(authenticationService.getCurrentUser().getUid())
      .child("notifications").addChildEventListener(childEventListener);
  }

  public boolean isRunning() {
    ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

    for (ActivityManager.RunningTaskInfo task : tasks) {
      if (getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
        return true;
    }

    return false;
  }

  public static void setCallback(ServiceCallback serviceCallback) {
    callback = serviceCallback;
  }
}
