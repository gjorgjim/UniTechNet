package mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Education;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Experience;

/**
 * Created by gjmarkov on 24.7.2018.
 */
public interface ProfileChangeCallback {
  void onCourseAdded(String key, Course course);
  void onCourseRemoved(String key);

  void onExperienceAdded(String key, Experience experience);
  void onExperienceRemoved(String key);

  void onEducationAdded(String key, Education education);
  void onEducationRemoved(String key);
}
