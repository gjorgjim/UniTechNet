package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.DatabaseCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.ProfileChangeCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
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

  private LruCache mMemoryCache;

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

  public void isFirstSignIn(DatabaseCallback<User> userCallback) {
    ValueEventListener valueEventListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(TAG, dataSnapshot.exists() + "");
        if(!dataSnapshot.exists()) {
          userCallback.onSuccess(null);
        } else {
          currentUser = dataSnapshot.getValue(User.class);

          cacheProfilePicture(userCallback);
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

  private void cacheProfilePicture(DatabaseCallback<User> userCallback) {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imageReference = storage
      .getReference()
      .child("images")
      .child(authenticationService.getCurrentUser().getUid())
      .child("pp.jpg");

    try {
      final File localFile = File.createTempFile("images", "jpg");
      imageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
          @Override
          protected int sizeOf(String key, Bitmap bitmap) {
            return bitmap.getByteCount() / 1024;
          }
        };

        addBitmapToMemoryCache("profilePicture", bitmap);

        userCallback.onSuccess(currentUser);
      }).addOnFailureListener(Throwable::printStackTrace);
    } catch (IOException e ) {
      e.printStackTrace();
    }
  }

  public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
    if (getBitmapFromMemCache(key) == null) {
      mMemoryCache.put(key, bitmap);
    }
  }

  public Bitmap getBitmapFromMemCache(String key) {
    return (Bitmap) mMemoryCache.get(key);
  }

  public void listenForUserDetailsChanges(ProfileChangeCallback callback) {
    setCoursesListener(callback);

    setExperiencesListener(callback);

    setEducationListener(callback);
  }

  private void setCoursesListener(ProfileChangeCallback profileChangeCallback) {
    ChildEventListener coursesEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Course course = dataSnapshot.getValue(Course.class);
        if(currentUser.getCourses() == null) {
          currentUser.setCourses(new HashMap<>());
        }
        if(!currentUser.getCourses().containsKey(dataSnapshot.getKey())) {
          currentUser.getCourses().put(dataSnapshot.getKey(), course);
          profileChangeCallback.onCourseAdded(dataSnapshot.getKey(), course);
        }
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        currentUser.getCourses().remove(dataSnapshot.getKey());
        profileChangeCallback.onCourseRemoved(dataSnapshot.getKey());
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

  private void setExperiencesListener(ProfileChangeCallback profileChangeCallback) {
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
          profileChangeCallback.onExperienceAdded(dataSnapshot.getKey(), experience);
        }
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        currentUser.getExperiences().remove(dataSnapshot.getKey());
        profileChangeCallback.onExperienceRemoved(dataSnapshot.getKey());
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

  private void setEducationListener(ProfileChangeCallback profileChangeCallback) {
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
          profileChangeCallback.onEducationAdded(dataSnapshot.getKey(), education);
        }
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d("UserService", "Education removed");
        currentUser.getEducations().remove(dataSnapshot.getKey());
        profileChangeCallback.onEducationRemoved(dataSnapshot.getKey());
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

  public void addCourseToCurrentUser(Course course) {
    if(currentUser.getCourses() == null) {
      currentUser.setCourses(new HashMap<>());
    }

    currentUser.getCourses().put(course.getCourseId(), course);
  }

  public void removeCourseFromCurrentUser(String courseId) {
    if(currentUser.getCourses() != null) {
      currentUser.getCourses().remove(courseId);
    }
  }

  public void saveEducation(String key, Education education) {
    DatabaseReference educationRef = databaseService.userReference(
      authenticationService
        .getCurrentUser()
        .getUid()
    ).child("educations");
    educationRef.child(key).removeValue();
    educationRef.child(key).setValue(education);
  }

  public void deleteEducation(String key) {
    databaseService.userReference(
      authenticationService
        .getCurrentUser()
        .getUid()
    ).child("educations")
      .child(key)
      .removeValue();
  }

  public void saveExperience(String key, Experience experience) {
    DatabaseReference experienceRef = databaseService.userReference(
      authenticationService
        .getCurrentUser()
        .getUid()
    ).child("experiences");
    experienceRef.child(key).removeValue();
    experienceRef.child(key).setValue(experience);
  }

  public void deleteExperience(String key) {
    databaseService.userReference(
      authenticationService
        .getCurrentUser()
        .getUid()
    ).child("experiences")
      .child(key)
      .removeValue();
  }

  public void saveUserDetails(String child, String value) {
    DatabaseReference userRef = databaseService.userReference(
      authenticationService
        .getCurrentUser()
        .getUid()
    );

    userRef.child(child).removeValue();
    userRef.child(child).setValue(value);
  }

  public void saveUserBirthday(Date value) {
    DatabaseReference userRef = databaseService.userReference(
      authenticationService
        .getCurrentUser()
        .getUid()
    );

    userRef.child("birthday").removeValue();
    userRef.child("birthday").setValue(value);
  }

  public void addEducation(Education education) {
    DatabaseReference experienceRef = databaseService.userReference(
      authenticationService
        .getCurrentUser()
        .getUid()
    ).child("educations");
    String key = experienceRef.push().getKey();
    experienceRef.child(key).setValue(education);
  }

  public void addExperience(Experience experience) {
    DatabaseReference experienceRef = databaseService.userReference(
      authenticationService
        .getCurrentUser()
        .getUid()
    ).child("experiences");
    String key = experienceRef.push().getKey();
    experienceRef.child(key).setValue(experience);
  }

  public User getCurrentUser(){
    return currentUser;
  }

  public void changeCurrentUserDetails(User user) {
    this.currentUser = user;
  }
}
