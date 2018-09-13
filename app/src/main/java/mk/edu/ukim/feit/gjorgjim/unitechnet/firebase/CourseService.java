package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.ListDatabaseCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.SuccessFailureCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.DataManager;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.ViewDelegate;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Answer;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Problem;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;

/**
 * Created by gjmarkov on 31.7.2018.
 */

public class CourseService {
  private static final String LOG_TAG = CourseService.class.getSimpleName();

  private static final CourseService ourInstance = new CourseService();

  public static CourseService getInstance() {
    return ourInstance;
  }

  private CourseService() {
    allCourses = null;
    databaseService = DatabaseService.getInstance();
    userService = UserService.getInstance();
    viewDelegate = ViewDelegate.getInstance();
    dataManager = DataManager.getInstance();
    authenticationService = AuthenticationService.getInstance();
  }

  private List<Course> allCourses;

  private DatabaseService databaseService;
  private UserService userService;
  private AuthenticationService authenticationService;

  private ViewDelegate viewDelegate;
  private DataManager dataManager;

  ListDatabaseCallback<Course> allCoursesCallback;
  private ValueEventListener courseListener = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      for(DataSnapshot data : dataSnapshot.getChildren()) {
        Course course = data.getValue(Course.class);
        course.setCourseId(data.getKey());
        allCourses.add(course);
      }
      allCoursesCallback.onSuccess(allCourses);
      databaseService.coursesReference().removeEventListener(courseListener);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
      allCoursesCallback.onFailure(databaseError.getMessage());
    }
  };

  public void getAllCourses(ListDatabaseCallback<Course> callback) {
    allCoursesCallback = callback;
    if(allCourses == null) {
      allCourses = new ArrayList<>();
      databaseService.coursesReference().addListenerForSingleValueEvent(courseListener);
    } else {
      allCoursesCallback.onSuccess(allCourses);
    }
  }

  public void removeAllCoursesListener() {
    databaseService.coursesReference().removeEventListener(courseListener);
  }

  public void subscribeUserToCourse(String courseId, String UID, ListDatabaseCallback<Course> callback) {
    addCourseToUser(courseId, UID);
    addUserToCourse(courseId, UID);

    addConnectionsToUserFromCourse(courseId, UID);

    callback.onSuccess(allCourses);
  }

  private void addConnectionsToUserFromCourse(String courseId, String UID) {
    User currentUser = userService.getCurrentUser();

    for (Course course : allCourses) {
      if (course.getCourseId().equals(courseId)) {
        if (course.getSubscribedUsers() != null) {
          for (String key : course.getSubscribedUsers().keySet()) {
            if(!key.equals(UID)) {
              if (currentUser.getConnections() == null) {
                currentUser.setConnections(new HashMap<>());
              }
              if (!currentUser.getConnections().containsKey(key)) {
                currentUser.getConnections().put(key, true);
              }
              databaseService.userReference(UID).child("connections").child(key).setValue(true);
              databaseService.userReference(key).child("connections").child(UID).setValue(true);
            }
          }
        }
      }
    }
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

  public void unsubscribeUserFromCourse(String courseId, String UID, ListDatabaseCallback<Course> callback) {

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

  public void editAnswerDescription(Answer answer, String newDescription, SuccessFailureCallback callback) {
    try {
      Course course = viewDelegate.getCurrentCourse();
      String courseKey = course.getCourseId();
      Problem problem = viewDelegate.getCurrentProblem();
      String problemKey = dataManager.getProblemKey(course.getProblems(), problem);
      String answerKey = dataManager.getAnswerKey(problem.getAnswers(), answer);

      databaseService.courseReference(courseKey).child("problems").child(problemKey).child("answers").child(answerKey).child("description").setValue(newDescription);

      for(Course currentCourse : allCourses) {
        if(currentCourse.getCourseId().equals(courseKey)) {
          currentCourse.getProblems().get(problemKey).getAnswers().get(answerKey).setDescription(newDescription);
          break;
        }
      }

      callback.onSuccess();
    } catch (NullPointerException e) {
      callback.onFailure();
    }
  }

  public void editProblemsAnswerId(Answer answer, SuccessFailureCallback callback) {
    try {
      Course course = viewDelegate.getCurrentCourse();
      String courseKey = course.getCourseId();
      Problem problem = viewDelegate.getCurrentProblem();
      String problemKey = dataManager.getProblemKey(course.getProblems(), problem);
      String answerKey = dataManager.getAnswerKey(problem.getAnswers(), answer);

      databaseService.courseReference(courseKey).child("problems").child(problemKey).child("answerId").setValue(
        answerKey);

      databaseService.courseReference(courseKey).child("problems").child(problemKey).child("answers").child(answerKey)
        .child("answer").setValue(true);

      if(problem.getAnswerid() != null) {
        databaseService.courseReference(courseKey).child("problems").child(problemKey).child("answers").child(problem
          .getAnswerid()).child("answer").setValue(false);
      }

      for (Course currentCourse : allCourses) {
        if (currentCourse.getCourseId().equals(courseKey)) {
          currentCourse.getProblems().get(problemKey).setAnswerid(answerKey);
          currentCourse.getProblems().get(problemKey).getAnswers().get(answerKey).setAnswer(true);
          break;
        }
      }

      callback.onSuccess();
    } catch (NullPointerException e) {
      e.printStackTrace();
      callback.onFailure();
    }
  }

  public void deleteAnswerFromProblem(Answer answer, SuccessFailureCallback callback) {
    try {
      Course course = viewDelegate.getCurrentCourse();
      String courseKey = course.getCourseId();
      Problem problem = viewDelegate.getCurrentProblem();
      String problemKey = dataManager.getProblemKey(course.getProblems(), problem);
      String answerKey = dataManager.getAnswerKey(problem.getAnswers(), answer);

      databaseService.courseReference(courseKey).child("problems").child(problemKey).child("answers").child(answerKey).removeValue();

      if(answer.isAnswer()) {
        databaseService.courseReference(courseKey).child("problems").child(problemKey).child("answerId").setValue("false");

        course.getProblems().get(problemKey).setAnswerid("false");
      }

      callback.onSuccess();

      for(Course currentCourse : allCourses) {
        if(currentCourse.getCourseId().equals(courseKey)) {
          currentCourse.getProblems().get(problemKey).getAnswers().remove(answerKey);
          break;
        }
      }
    } catch (NullPointerException e) {
      callback.onFailure();
    }
  }

  public void postAnswerToProblem(Answer answer, SuccessFailureCallback callback) {
    try {
      String uid = authenticationService.getCurrentUser().getUid();
      User user = new User();
      user.setFirstName(userService.getCurrentUser().getFirstName());
      user.setLastName(userService.getCurrentUser().getLastName());

      Course course = viewDelegate.getCurrentCourse();
      String courseKey = course.getCourseId();
      Problem problem = viewDelegate.getCurrentProblem();
      String problemKey = dataManager.getProblemKey(course.getProblems(), problem);

      String answerKey = databaseService.courseReference(courseKey).child("problems").child(problemKey).child("answers").push().getKey();
      databaseService.courseReference(courseKey).child("problems").child(problemKey).child("answers").child(answerKey).setValue(answer);
      databaseService.courseReference(courseKey).child("problems").child(problemKey).child("answers").child(answerKey).child("author").child(uid).setValue(user);

      for(Course currentCourse : allCourses) {
        if(currentCourse.getCourseId().equals(courseKey)) {
          if(currentCourse.getProblems().get(problemKey).getAnswers() == null) {
            currentCourse.getProblems().get(problemKey).setAnswers(new HashMap<>());
          }
          currentCourse.getProblems().get(problemKey).getAnswers().put(answerKey, answer);
          HashMap<String, User> author = new HashMap<>();
          author.put(uid, user);
          currentCourse.getProblems().get(problemKey).getAnswers().get(answerKey).setAuthor(author);
          break;
        }
      }

      callback.onSuccess();
    } catch (NullPointerException e) {
      e.printStackTrace();
      callback.onFailure();
    }
  }

  public void postProblemToCourse(Problem problem, SuccessFailureCallback callback) {
    try {
      Course course = viewDelegate.getCurrentCourse();
      String courseKey = course.getCourseId();

      String problemKey = databaseService.courseReference(courseKey).child("problems").push().getKey();
      databaseService.courseReference(courseKey).child("problems").child(problemKey).setValue(problem);

      for(Course currentCourse : allCourses) {
        if(currentCourse.getCourseId().equals(courseKey)) {
          currentCourse.getProblems().put(problemKey, problem);
          break;
        }
      }
    } catch (NullPointerException e) {
      callback.onFailure();
    }
  }
}
