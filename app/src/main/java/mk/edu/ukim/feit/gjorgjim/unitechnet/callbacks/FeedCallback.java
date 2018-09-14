package mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks;

import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.FeedItem;

/**
 * Created by gjmarkov on 14.9.2018.
 */

public interface FeedCallback {
  void onSuccess(HashMap<String, FeedItem> feedItemHashMap);
  void onFailure(String message);
}
