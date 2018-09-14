package mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks;

import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.Notification;

/**
 * Created by gjmarkov on 14.9.2018.
 */

public interface NotificationCallback {
  void onSuccess(HashMap<String, Notification> notifications);
  void onFailure(String message);
}
