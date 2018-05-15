package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by gjmarkov on 15.5.2018.
 */

public class AuthenticationService {

  private FirebaseAuth mAuth;

  private Activity myActivity;

  private FirebaseUser user;

  private static final AuthenticationService ourInstance = new AuthenticationService();

  public static AuthenticationService getInstance() {
    return ourInstance;
  }

  private AuthenticationService() {
    mAuth = FirebaseAuth.getInstance();
    user = null;
  }

  public void setMyActivity(Activity activity) {
    myActivity = activity;
  }

  public FirebaseUser getCurrentUser() {
    return mAuth.getCurrentUser();
  }

  public FirebaseUser signIn(String email, String password) {
    mAuth.signInWithEmailAndPassword(email, password)
      .addOnCompleteListener(myActivity, (@NonNull Task<AuthResult> task) -> {
        if(task.isSuccessful()) {
          user = mAuth.getCurrentUser();
        }
      });

    return user;
  }

  public void signOut() {
    mAuth.signOut();
  }
}
