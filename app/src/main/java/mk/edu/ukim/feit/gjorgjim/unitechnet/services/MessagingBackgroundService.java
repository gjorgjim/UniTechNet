package mk.edu.ukim.feit.gjorgjim.unitechnet.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.DatabaseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Chat;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.notifications.NotificationCenter;

/**
 * Created by gjmarkov on 05.9.2018.
 */

public class MessagingBackgroundService extends Service {

  private static final String LOG_TAG = MessagingBackgroundService.class.getSimpleName();

  public final static String ACTION_START_SERVICE = "mk.edu.ukim.feit.gjorgjim.unitechnet.services.startBackgroundService";

  public static final String ACTION = "mk.edu.ukim.feit.gjorgjim.unitechnet.services.MessagingBackgroundServiceAction";

  public static boolean isServiceRunning = false;

  private DatabaseService databaseService;
  private AuthenticationService authenticationService;
  private NotificationCenter notificationCenter;

  private Date now;

  ValueEventListener valueEventListener = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      for(DataSnapshot data : dataSnapshot.getChildren()) {
        Chat currentChat = data.getValue(Chat.class);
        if(currentChat.getLastMessage().getSentDate().isAfter(now)) {
          if(isRunning()) {
            Bundle bundle = new Bundle();
            bundle.putString("key", data.getKey());
            bundle.putSerializable("lastMessage", currentChat.getLastMessage());
            bundle.putString("firstName", currentChat.getFirstName());

            Intent broadcastIntent = new Intent(ACTION);
            broadcastIntent.putExtra("info", bundle);

            LocalBroadcastManager.getInstance(MessagingBackgroundService.this).sendBroadcast(broadcastIntent);
          } else {
            notificationCenter.sentMessageNotification(
              String.format("%s %s has sent you a message", currentChat.getFirstName(), currentChat.getLastName()));
          }
          now = Date.getDate();
        }
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
  };

  private void startServiceWithNotifications() {
    if (isServiceRunning)
      return;

    isServiceRunning = true;

    databaseService = DatabaseService.getInstance();
    authenticationService = AuthenticationService.getInstance();

    notificationCenter = NotificationCenter.getInstance();
    notificationCenter.setContext(this);

    now =  Date.getDate();

    databaseService.chatReference(
      authenticationService.getCurrentUser().getUid()
    ).addValueEventListener(valueEventListener);
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

  @Override
  public void onCreate() {
    super.onCreate();
    startServiceWithNotifications();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null && intent.getAction().equals(ACTION_START_SERVICE)) {
      startServiceWithNotifications();
    }
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    isServiceRunning = false;
    databaseService.chatReference(
      authenticationService.getCurrentUser().getUid()
    ).removeEventListener(valueEventListener);
    super.onDestroy();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  void stopMyService() {
    stopForeground(true);
    stopSelf();
    isServiceRunning = false;
  }
}
