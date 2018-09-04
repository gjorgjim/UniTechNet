package mk.edu.ukim.feit.gjorgjim.unitechnet.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.DatabaseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.NavigationActivity;

/**
 * Created by gjmarkov on 04.9.2018.
 */

public class NotificationCenter {

  private static final String CHANNEL_ID = "MessageNotification";

  private static final NotificationCenter ourInstance = new NotificationCenter();

  public static NotificationCenter getInstance() {
    return ourInstance;
  }

  private Context context;

  private NotificationManager notificationManager;

  private int notificationId;

  private NotificationCenter() {
    notificationId = 0;
  }

  public void setContext(Context context) {
    this.context = context;
    createNotificationChannel();
  }

  public void sentMessageNotification(String message) {
    if(isNotificationVisible()) {
      return;
    }

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.logo_android_v2)
      .setContentTitle("New Message")
      .setContentText(message)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setAutoCancel(true);

    notificationManager.notify(notificationId, builder.build());
    notificationId++;
  }

  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = context.getString(R.string.channel_name);
      String description = context.getString(R.string.channel_description);
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
      channel.setDescription(description);
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      notificationManager = context.getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
  }

  private boolean isNotificationVisible() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
      StatusBarNotification[] notifications = notificationManager.getActiveNotifications();
      for(StatusBarNotification notification : notifications) {
        if(notification.getId() == notificationId - 1) {
          return true;
        }
      }
    }
    return false;
  }
}
