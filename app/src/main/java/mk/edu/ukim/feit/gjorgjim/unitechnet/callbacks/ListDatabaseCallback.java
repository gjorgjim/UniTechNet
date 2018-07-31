package mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks;

import java.util.List;

/**
 * Created by gjmarkov on 31.7.2018.
 */
public interface ListDatabaseCallback<T> {
  void onSuccess(List<T> list);
  void onFailure(String message);
}
