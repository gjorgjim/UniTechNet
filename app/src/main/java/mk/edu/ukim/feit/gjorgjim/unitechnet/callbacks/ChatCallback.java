package mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks;

import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Chat;

/**
 * Created by gjmarkov on 08.8.2018.
 */

public interface ChatCallback {
  void onSuccess(HashMap<String, Chat> chatHashMap);
  void onFailure(String message);
}
