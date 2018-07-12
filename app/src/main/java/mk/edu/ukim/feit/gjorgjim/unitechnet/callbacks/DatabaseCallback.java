package mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;

/**
 * Created by gjmarkov on 07.7.2018.
 */

public interface DatabaseCallback<T> {
  enum CallBackTag{
    FIRST_SIGN_IN,
    EXPERIENCE_CHANGES,
    EDUCATION_CHANGES,
    COURSE_CHANGES
  }

  void onSuccess(CallBackTag tag, T t);
  void onFailure(String message);
}
