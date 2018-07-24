package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.DatabaseCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.ProfileChangeCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Education;
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

  private ProfileChangeCallback profileChangeCallback;

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
          userCallback.onSuccess(null);
        } else {
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
  }

  public void listenForUserDetailsChanges() {
    setCoursesListener();

    setExperiencesListener();

    setEducationListener();
  }

  private void setCoursesListener() {
    ChildEventListener coursesEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Course course = dataSnapshot.getValue(Course.class);
        if(currentUser.getCourses() == null) {
          currentUser.setCourses(new HashMap<>());
        }
        if(!currentUser.getCourses().containsKey(dataSnapshot.getKey())) {
          HashMap<String, Course> hashMap = currentUser.getCourses();
          hashMap.put(dataSnapshot.getKey(), course);
          currentUser.setCourses(hashMap);
          profileChangeCallback.onCourseAdded(course);
        }
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        currentUser.getCourses().remove(dataSnapshot.getKey());
        profileChangeCallback.onCourseRemoved(dataSnapshot.getValue(Course.class));
      }
      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
      @Override
      public void onCancelled(DatabaseError databaseError) {}
    };
    databaseService.usersReference().child(authenticationService
      .getCurrentUser()
      .getUid())
      .child("courses")
      .addChildEventListener(coursesEventListener);
  }

  private void setExperiencesListener() {
    ChildEventListener experiencesEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Experience experience = dataSnapshot.getValue(Experience.class);
        if(currentUser.getExperiences() == null) {
          currentUser.setExperiences(new HashMap<>());
        }
        if(!currentUser.getExperiences().containsKey(dataSnapshot.getKey())) {
          HashMap<String, Experience> hashMap = currentUser.getExperiences();
          hashMap.put(dataSnapshot.getKey(), experience);
          currentUser.setExperiences(hashMap);
          profileChangeCallback.onExperienceAdded(experience);
        }
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        currentUser.getExperiences().remove(dataSnapshot.getKey());
        profileChangeCallback.onExperienceRemoved(dataSnapshot.getValue(Experience.class));
      }
      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
      @Override
      public void onCancelled(DatabaseError databaseError) {}
    };
    databaseService.usersReference().child(authenticationService
      .getCurrentUser()
      .getUid())
      .child("experiences")
      .addChildEventListener(experiencesEventListener);
  }

  private void setEducationListener() {
    ChildEventListener educationsEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Education education = dataSnapshot.getValue(Education.class);
        if(currentUser.getEducations() == null) {
          currentUser.setEducations(new HashMap<>());
        }
        if(!currentUser.getEducations().containsKey(dataSnapshot.getKey())) {
          HashMap<String, Education> hashMap = currentUser.getEducations();
          hashMap.put(dataSnapshot.getKey(), education);
          currentUser.setEducations(hashMap);
          profileChangeCallback.onEducationAdded(education);
        }
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d("UserService", "Education removed");
        currentUser.getEducations().remove(dataSnapshot.getKey());
        profileChangeCallback.onEducationRemoved(dataSnapshot.getValue(Education.class));
      }
      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
      @Override
      public void onCancelled(DatabaseError databaseError) {}
    };
    databaseService.usersReference().child(authenticationService
      .getCurrentUser()
      .getUid())
      .child("educations")
      .addChildEventListener(educationsEventListener);
  }

  public void setUserCallback(DatabaseCallback<User> userCallback){
    this.userCallback = userCallback;
  }

  public void setProfileChangeCallback(ProfileChangeCallback profileChangeCallback) {
    this.profileChangeCallback = profileChangeCallback;
  }

  public User getCurrentUser(){
    return currentUser;
  }
}
