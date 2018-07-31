package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.CourseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;

/**
 * Created by gjmarkov on 31.7.2018.
 */

public class CourseAdapter extends BaseAdapter {
  private Context context;
  private List<Course> courses;

  private AppCompatTextView courseNameTv;
  private AppCompatTextView courseDescriptionTv;
  private AppCompatTextView statusTv;

  private AuthenticationService authenticationService;
  private CourseService courseService;

  public CourseAdapter(Context context, List<Course> courses) {
    this.context = context;
    this.courses = courses;
    authenticationService = AuthenticationService.getInstance();
    courseService = CourseService.getInstance();
  }

  @Override
  public int getCount() {
    return courses.size();
  }

  @Override
  public Object getItem(int position) {
    return courses.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view;
    if(convertView != null) {
      view = convertView;
    } else {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.course_item_layout, parent, false);
    }

    courseNameTv = view.findViewById(R.id.courseNameTv);
    courseDescriptionTv = view.findViewById(R.id.courseDescriptionTv);
    statusTv = view.findViewById(R.id.courseStatusTv);

    Course current = courses.get(position);

    courseNameTv.setText(current.getName());

    if(current.getDescription().length() > 90) {
      String description = String.format("%s...", current.getDescription().substring(0, 90));
      courseDescriptionTv.setText(description);
    } else {
      courseDescriptionTv.setText(current.getDescription());
    }

    if(courseService.isUserSubscribed(current, authenticationService.getCurrentUser().getUid())) {
      statusTv.setVisibility(View.VISIBLE);
    } else {
      statusTv.setVisibility(View.GONE);
    }

    return view;
  }
}
