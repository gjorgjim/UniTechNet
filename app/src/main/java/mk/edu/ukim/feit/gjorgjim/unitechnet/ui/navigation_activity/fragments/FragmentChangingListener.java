package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Chat;

/**
 * Created by gjmarkov on 16.5.2018.
 */

public interface FragmentChangingListener {
  void changeToUserFragment();
  void changeToCoursesFragment();
  void changeToUserMessagingFragment(Chat chat, String key);
  void changeToMessagesFragment();
  void changeToCourseViewFragment(Course course);
}
