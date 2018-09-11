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

  public void viewCurrentCourse(Course course) {
    currentProblem = null;
    currentCourse = course;
  }

  public void viewCurrentProblem(Problem problem) {
    currentProblem = problem;
  }

  public Course getCurrentCourse() {
    return currentCourse;
  }

  public Problem getCurrentProblem() {
    return currentProblem;
  }
}
