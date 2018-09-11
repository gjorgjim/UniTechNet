package mk.edu.ukim.feit.gjorgjim.unitechnet.helpers;

import android.app.Activity;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Problem;

/**
 * Keeps track of current data shown in CoursesFragment
 */
public class ViewDelegate {
  private static final ViewDelegate ourInstance = new ViewDelegate();

  public static ViewDelegate getInstance() {
    return ourInstance;
  }

  private Course currentCourse = null;
  private Problem currentProblem = null;

  private Activity currentActivity;

  private ViewDelegate() {
  }

  public void viewCurrentCourse(Course course) {
    currentProblem = null;
    currentCourse = course;
  }

  public void viewCurrentProblem(Problem problem) {
    currentProblem = problem;
  }

  public Activity getCurrentActivity() {
    return currentActivity;
  }

  public void setCurrentActivity(Activity currentActivity) {
    this.currentActivity = currentActivity;
  }

  public Course getCurrentCourse() {
    return currentCourse;
  }

  public Problem getCurrentProblem() {
    return currentProblem;
  }
}
