package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Education;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Experience;

/**
 * Created by gjmarkov on 05.7.2018.
 */
public class ProfileListAdapter<T> extends ArrayAdapter<Object> {
  private List<T> list;

  private AppCompatTextView titleTv;
  private AppCompatTextView descriptionTv;
  private AppCompatTextView dateTv;

  public ProfileListAdapter(@NonNull Context context, int resource, List<T> list) {
    super(context, resource);
    this.list = list;
  }


  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View view = convertView;

    if (view == null) {
      view = LayoutInflater.from(getContext()).inflate(R.layout.profile_list_item_layout, null);
    }

    titleTv = view.findViewById(R.id.titleTv);
    descriptionTv = view.findViewById(R.id.descriptionTv);
    dateTv = view.findViewById(R.id.dateTv);

    if(list.size() > 0) {
      if(list.get(position) instanceof Course) {
        Course current = (Course) list.get(position);
        titleTv.setText(current.getName());
        descriptionTv.setText(current.getDescription());
        dateTv.setText("");
      } else if(list.get(position) instanceof Experience) {
        Experience current = (Experience) list.get(position);
        titleTv.setText(current.getJobTitle());
        descriptionTv.setText(current.getCompany());
        String startDate = String.format(new Locale("en"),
          "%d/%d",
          current.getStartDate().getMonth(),
          current.getStartDate().getYear() % 100 );
        String endDate;
        if(current.getEndDate() == null) {
          endDate = "present";
        } else {
          endDate = String.format(new Locale("en"),
            "%d/%d",
            current.getEndDate().getMonth(),
            current.getEndDate().getYear());
        }
        dateTv.setText(String.format(new Locale("en"), "%s - %s", startDate, endDate));
      } else if(list.get(position) instanceof Education) {
        Education current = (Education) list.get(position);
        titleTv.setText(current.getDegree());
        descriptionTv.setText(current.getSchool());
        dateTv.setText("");
      }
    }

    return view;
  }

  @Override
  public int getCount() {
    return list.size();
  }
}
