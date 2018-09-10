package mk.edu.ukim.feit.gjorgjim.unitechnet.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.cache.ImagesCacher;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.NavigationActivity;

/**
 * Created by gjmarkov on 04.9.2018.
 */

public class NotificationCenter {

  public static final String LOG_TAG  = NotificationCenter.class.getSimpleName();

  private static final String CHANNEL_ID = "MessageNotification";

  private static final NotificationCenter ourInstance = new NotificationCenter();

  public static NotificationCenter getInstance() {
    return ourInstance;
  }

  private Context context;

  private ImagesCacher imagesCacher;

  private NotificationManager notificationManager;

  private int notificationId;

  private NotificationCenter() {
    notificationId = 0;
  }

  public void setContext(Context context) {
    this.context = context;
    createNotificationChannel();
    imagesCacher = ImagesCacher.getInstance();
    imagesCacher.setResources(context.getResources());
  }

  public void sentMessageNotification(String message, String key) {
    if(isNotificationVisible()) {
      return;
    }

    Intent intent = new Intent(context, NavigationActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

    Bundle bundle = new Bundle();
    bundle.putString("key", key);

    intent.putExtra("info", bundle);

    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
      .setLargeIcon(imagesCacher.getSmallLogo())
      .setContentTitle("UniTechNet")
      .setContentText(message)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(pendingIntent)
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
    } else {
      notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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
