package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Education;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Experience;

/**
 * Created by gjmarkov on 24.7.2018.
 */
public class ProfileItemView<T> extends RelativeLayout {
  private Context context;
  private T item;

  private AppCompatTextView titleTv;
  private AppCompatTextView descriptionTv;
  private AppCompatTextView dateTv;

  public ProfileItemView(Context context) {
    super(context);
    this.context = context;
  }

  private void init() {
    View view = inflate(context, R.layout.profile_list_item_layout, this);

    view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    titleTv = view.findViewById(R.id.titleTv);
    descriptionTv = view.findViewById(R.id.descriptionTv);
    dateTv = view.findViewById(R.id.dateTv);

    if(item instanceof Course) {
      Course course = (Course) item;
      titleTv.setText(course.getName());
      descriptionTv.setText(course.getDescription());
      dateTv.setVisibility(GONE);
    } else if(item instanceof Experience) {
      Experience current = (Experience) item;

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
    } else if(item instanceof Education) {
      Education current = (Education) item;
      titleTv.setText(current.getDegree());
      descriptionTv.setText(current.getSchool());
      dateTv.setVisibility(GONE);
    }
  }

  public void setItem(T item) {
    this.item = item;
    init();
  }
}
