package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.DatabaseCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;

/**
 * Created by gjmarkov on 15.5.2018.
 */

public class UserService {

  private static final String TAG = "UserService";

  private AuthenticationService authenticationService;

  private DatabaseService databaseService;

  private boolean exists;

  private User currentUser;

  private DatabaseCallback<User> userCallback;

  private DatabaseCallback<Boolean> firstSignInCallback;

  private static final UserService ourInstance = new UserService();

  public static UserService getInstance() {
    return ourInstance;
  }

  private UserService() {
    databaseService = DatabaseService.getInstance();
    authenticationService = AuthenticationService.getInstance();
    currentUser = null;
  }

  public void saveUser(User user) {
    DatabaseReference userReference = databaseService.userReference(authenticationService.getCurrentUser().getUid());
    userReference.setValue(user);
  }

  public void isFirstSignIn() {
    ValueEventListener valueEventListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(TAG, dataSnapshot.exists() + "");
        if(!dataSnapshot.exists()) {
          firstSignInCallback.onSuccess(true);
        } else {
          firstSignInCallback.onSuccess(false);
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        firstSignInCallback.onFailure(databaseError.getMessage());
      }
    };
    databaseService.usersReference().child(authenticationService
      .getCurrentUser()
      .getUid())
      .addListenerForSingleValueEvent(valueEventListener);
  }

  public void findCurrentUser() {
    if(currentUser == null) {
      ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          if (dataSnapshot.exists()) {
            currentUser = dataSnapshot.getValue(User.class);
            userCallback.onSuccess(currentUser);
          }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            userCallback.onFailure(databaseError.getMessage());
        }
      };
      databaseService.usersReference().child(authenticationService
        .getCurrentUser()
        .getUid())
        .addListenerForSingleValueEvent(valueEventListener);
    } else {
      userCallback.onSuccess(currentUser);
    }
  }

  public void setUserCallback(DatabaseCallback<User> userCallback){
    this.userCallback = userCallback;
  }

  public void setFirstSignInCallback(DatabaseCallback<Boolean> firstSignInCallback) {
    this.firstSignInCallback = firstSignInCallback;
  }
}
