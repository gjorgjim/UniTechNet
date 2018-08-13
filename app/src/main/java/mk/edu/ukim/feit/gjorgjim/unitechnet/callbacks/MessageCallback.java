package mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Message;

/**
 * Created by gjmarkov on 13.8.2018.
 */

public interface MessageCallback {
  void onMessageReceived(String messageKey, Message message);
}
