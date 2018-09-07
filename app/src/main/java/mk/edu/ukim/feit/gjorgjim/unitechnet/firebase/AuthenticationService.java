package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.AuthenticationCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.login_activity.LoginActivity;

/**
 * Created by gjmarkov on 15.5.2018.
 */

public class AuthenticationService {

  private static final String LOG_TAG = AuthenticationService.class.getSimpleName();

  private FirebaseAuth mAuth;

  private static final AuthenticationService ourInstance = new AuthenticationService();

  public static AuthenticationService getInstance() {
    return ourInstance;
  }

  private AuthenticationService() {
    mAuth = FirebaseAuth.getInstance();
  }

  public FirebaseUser getCurrentUser() {
    return mAuth.getCurrentUser();
  }

  public void signIn(String email, String password, Activity activity, AuthenticationCallback callback) {
    mAuth.signInWithEmailAndPassword(email, password)
      .addOnCompleteListener(activity, (@NonNull Task<AuthResult> task) -> {
        if(task.isSuccessful()){
          callback.onSuccess(task.getResult().getUser());
        } else {
          task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
             callback.onFailure(e.getMessage());
            }
          });
        }
      });
  }

  public void signOut(Activity activity) {
    mAuth.signOut();
    activity.startActivity(new Intent(activity, LoginActivity.class));
  }

  public void forgotPassword(String email, AuthenticationCallback callback) {
    mAuth.sendPasswordResetEmail(email)
      .addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
          callback.onSuccess(null);
        }
      })
      .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          callback.onFailure(e.getMessage());
        }
      });
  }
}
