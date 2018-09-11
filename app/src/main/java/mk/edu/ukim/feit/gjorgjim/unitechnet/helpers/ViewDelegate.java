package mk.edu.ukim.feit.gjorgjim.unitechnet.helpers;

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

  private ViewDelegate() {
  }

  private void viewCurrentCourse(Course course) {
    currentProblem = null;
    if(course != null) {
      currentCourse = course;
    }
  }

  private void viewCurrentProblem(Problem problem) {
    currentProblem = problem;
  }

  public Course getCurrentCourse() {
    return currentCourse;
  }

  public Problem getCurrentProblem() {
    return currentProblem;
  }
}
