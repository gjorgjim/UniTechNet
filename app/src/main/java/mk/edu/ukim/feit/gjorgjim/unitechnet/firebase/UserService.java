package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Executable;
import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.DatabaseCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Experience;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;

/**
 * Created by gjmarkov on 15.5.2018.
 */

public class UserService {

  private static final String TAG = "UserService";

  private AuthenticationService authenticationService;

  private DatabaseService databaseService;

  private User currentUser;

  private DatabaseCallback<User> userCallback;

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

    currentUser = user;
  }

  public void isFirstSignIn() {
    ValueEventListener valueEventListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(TAG, dataSnapshot.exists() + "");
        if(!dataSnapshot.exists()) {
          userCallback.onSuccess(DatabaseCallback.CallBackTag.FIRST_SIGN_IN, null);
        } else {
          currentUser = dataSnapshot.getValue(User.class);
          userCallback.onSuccess(DatabaseCallback.CallBackTag.FIRST_SIGN_IN, currentUser);
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
  }

  public void listenForUserDetailsChanges() {
    ChildEventListener childEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d("User Service Added", dataSnapshot.getKey());
        Log.d("User Service Added", dataSnapshot.getValue().toString());
        if(dataSnapshot.getKey().equals("experiences")) {
          for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Experience experience = snapshot.getValue(Experience.class);
            if(currentUser.getExperiences() == null) {
              currentUser.setExperiences(new HashMap<>());
            }
            HashMap<String, Experience> hashMap = currentUser.getExperiences();
            hashMap.put(snapshot.getKey(), snapshot.getValue(Experience.class));
            currentUser.setExperiences(hashMap);
            userCallback.onSuccess(DatabaseCallback.CallBackTag.EXPERIENCE_CHANGES, null);
          }
        }
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Log.d("User Service Changed", dataSnapshot.getValue().toString());
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {

      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };
    databaseService.usersReference().child(authenticationService
    .getCurrentUser()
    .getUid())
      .addChildEventListener(childEventListener);
  }

  public void setUserCallback(DatabaseCallback<User> userCallback){
    this.userCallback = userCallback;
  }

  public User getCurrentUser(){
    return currentUser;
  }
}
