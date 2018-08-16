package mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Message;

/**
 * Created by gjmarkov on 13.8.2018.
 */

public interface MessageCallback<T, K> {
  void onMessageReceived(T key, K message);
}
