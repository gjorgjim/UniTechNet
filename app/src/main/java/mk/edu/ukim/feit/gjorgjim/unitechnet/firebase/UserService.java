package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.User;

/**
 * Created by gjmarkov on 15.5.2018.
 */

public class UserService {

  private static final String TAG = "UserService";

  private AuthenticationService authenticationService;

  private DatabaseService databaseService;

  private boolean exists;

  private User currentUser;

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
        exists = dataSnapshot.exists();

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };
    databaseService.usersReference().child(authenticationService
      .getCurrentUser()
      .getUid())
      .addListenerForSingleValueEvent(valueEventListener);
  }

  public User getCurrentUser() {
    if(currentUser != null) {
      return currentUser;
    } else {
      ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          if(dataSnapshot.exists()) {
            currentUser = dataSnapshot.getValue(User.class);
          }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      };
      databaseService.usersReference().child(authenticationService
        .getCurrentUser()
        .getUid())
        .addListenerForSingleValueEvent(valueEventListener);
    }

    return currentUser;
  }

}
