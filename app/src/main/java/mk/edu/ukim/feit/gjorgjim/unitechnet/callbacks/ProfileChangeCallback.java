package mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Education;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Experience;

/**
 * Created by gjmarkov on 24.7.2018.
 */
public interface ProfileChangeCallback {
  void onCourseAdded(Course course);
  void onCourseRemoved(Course course);

  void onExperienceAdded(Experience experience);
  void onExperienceRemoved(Experience experience);

  void onEducationAdded(Education education);
  void onEducationRemoved(Education education);
}
