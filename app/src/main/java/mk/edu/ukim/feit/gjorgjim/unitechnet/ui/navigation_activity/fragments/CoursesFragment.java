package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

  public CoursesFragment() {
    courseService = CourseService.getInstance();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_courses, container, false);

    courseGridView = view.findViewById(R.id.courseGv);
    progressBar = view.findViewById(R.id.coursePb);

    courseService.getAllCourses(new ListDatabaseCallback<Course>() {
      @Override
      public void onSuccess(List<Course> list) {
        adapter = new CourseAdapter(getContext(), list);
        courseGridView.setAdapter(adapter);

        courseGridView.setOnItemClickListener((parent, view1, position, id) -> {
          CourseDialog dialog = new CourseDialog(getContext(), list.get(position), CoursesFragment.this);
          dialog.show();
        });

        progressBar.setVisibility(View.GONE);
        courseGridView.setVisibility(View.VISIBLE);

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
