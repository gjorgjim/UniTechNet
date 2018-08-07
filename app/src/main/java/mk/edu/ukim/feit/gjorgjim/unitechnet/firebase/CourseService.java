package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.ListDatabaseCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;

/**
 * Created by gjmarkov on 31.7.2018.
 */

public class CourseService {
  private static final CourseService ourInstance = new CourseService();

  public static CourseService getInstance() {
    return ourInstance;
  }

  private CourseService() {
    allCourses = null;
    databaseService = DatabaseService.getInstance();
    userService = UserService.getInstance();
  }

  private List<Course> allCourses;

  private DatabaseService databaseService;
  private UserService userService;

  private ListDatabaseCallback<Course> callback;

  private ValueEventListener courseListener;

  public void setCourseCallback(ListDatabaseCallback<Course> callback) {
    this.callback = callback;
  }

  public void getAllCourses() {
    courseListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for(DataSnapshot data : dataSnapshot.getChildren()) {
          Log.d("CourseService", data.getValue().toString());
          Course course = data.getValue(Course.class);
          course.setCourseId(data.getKey());
          allCourses.add(course);
        }
        callback.onSuccess(allCourses);
        databaseService.coursesReference().removeEventListener(courseListener);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        callback.onFailure(databaseError.getMessage());
      }
    };

    if(allCourses == null) {
      allCourses = new ArrayList<>();
      databaseService.coursesReference().addListenerForSingleValueEvent(courseListener);
    } else {
      callback.onSuccess(allCourses);
    }
  }

  public void subscribeUserToCourse(String courseId, String UID) {
    addCourseToUser(courseId, UID);
    addUserToCourse(courseId, UID);

    callback.onSuccess(allCourses);
  }

  private void addUserToCourse(String courseId, String UID) {
    for(Course course : allCourses) {
      if(course.getCourseId().equals(courseId)) {
        if(course.getSubscribedUsers() == null) {
          course.setSubscribedUsers(new HashMap<>());
        }
        course.getSubscribedUsers().put(UID, true);
        break;
      }
    }

    databaseService.courseReference(courseId)
      .child("subscribedUsers")
      .child(UID)
      .setValue(true);
  }

  private void addCourseToUser(String courseId,String UID) {
    Course currentCourse = null;
    for(Course course : allCourses) {
      if(course.getCourseId().equals(courseId)) {
        currentCourse = course;
        break;
      }
    }

    userService.addCourseToCurrentUser(currentCourse);

    databaseService.userReference(UID)
      .child("courses")
      .child(courseId)
      .setValue(currentCourse);
  }

  public void unsubscribeUserFromCourse(String courseId, String UID) {

    removeCourseFromUser(courseId, UID);
    removeUserFromCourse(courseId, UID);

    callback.onSuccess(allCourses);
  }

  private void removeCourseFromUser(String courseId, String UID) {
    userService.removeCourseFromCurrentUser(courseId);

    databaseService.userReference(UID)
      .child("courses")
      .child(courseId)
      .removeValue();
  }

  private void removeUserFromCourse(String courseId, String UID) {
    for(Course course : allCourses) {
      if(course.getCourseId().equals(courseId)) {
        course.getSubscribedUsers().remove(UID);
        break;
      }
    }
    databaseService.courseReference(courseId)
      .child("subscribedUsers")
      .child(UID)
      .removeValue();
  }

  public boolean isUserSubscribed(Course course, String UID) {
    return course.getSubscribedUsers() != null && course.getSubscribedUsers().containsKey(UID);
  }
}
