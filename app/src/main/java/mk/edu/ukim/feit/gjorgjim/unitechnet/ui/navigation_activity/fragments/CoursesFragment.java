package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.util.List;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.ListDatabaseCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.CourseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.adapters.CourseAdapter;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs.CourseDialog;

/**
 * Created by gjmarkov on 16.5.2018.
 */

public class CoursesFragment extends Fragment {

  private GridView courseGridView;
  private ProgressBar progressBar;

  private CourseAdapter adapter;

  private CourseService courseService;

  private FragmentChangingListener fragmentChangingListener;

  public CoursesFragment() {
    courseService = CourseService.getInstance();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    fragmentChangingListener = (FragmentChangingListener) context;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_courses, container, false);

    courseGridView = view.findViewById(R.id.courseGv);
    progressBar = view.findViewById(R.id.coursePb);

    Bundle bundle = getArguments();

    courseService.getAllCourses(new ListDatabaseCallback<Course>() {
      @Override
      public void onSuccess(List<Course> list) {
        if(bundle != null && bundle.get("problemId") != null && bundle.get("courseId") != null) {
          Course current = new Course();
          for(Course course : list) {
            if(course.getCourseId().equals(bundle.getString("courseId"))) {
              current = course;
              break;
            }
          }

          fragmentChangingListener.changeToProblemViewFragment(current.getProblems().get(bundle.get("problemId")), current);
        } else {
          adapter = new CourseAdapter(getContext(), list);
          courseGridView.setAdapter(adapter);

          courseGridView.setOnItemClickListener((parent, view1, position, id) -> {
            fragmentChangingListener.changeToCourseViewFragment(list.get(position));
          });

          progressBar.setVisibility(View.GONE);
          courseGridView.setVisibility(View.VISIBLE);
        }

        courseService.removeAllCoursesListener();
      }

      @Override
      public void onFailure(String message) {
        courseService.removeAllCoursesListener();
      }
    });

    return view;
  }

  public void notifyDataSetChanged() {
    adapter.notifyDataSetChanged();
  }
}
