package mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by gjmarkov on 07.7.2018.
 */

public interface AuthenticationCallback {
  void onSuccess(FirebaseUser user);
  void onFailure(String message);
}
