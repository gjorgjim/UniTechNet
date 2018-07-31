package mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;

/**
 * Created by gjmarkov on 07.7.2018.
 */

public interface DatabaseCallback<T> {
  void onSuccess(T t);
  void onFailure(String message);
}
