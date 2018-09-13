package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Education;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Experience;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.ProfileFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs.EditEducationDialog;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.dialogs.EditExperienceDialog;

/**
 * Created by gjmarkov on 24.7.2018.
 */
@SuppressLint("ViewConstructor")
public class ProfileItemView<T> extends RelativeLayout {
  private Context context;
  private T item;
  private ProfileFragment profileFragment;

  private AppCompatTextView titleTv;
  private AppCompatTextView descriptionTv;
  private AppCompatTextView dateTv;

  private String key;

  private EditEducationDialog educationDialog;
  private EditExperienceDialog experienceDialog;

  public ProfileItemView(Context context, String key, ProfileFragment profileFragment) {
    super(context);
    this.context = context;
    this.key = key;
    this.profileFragment = profileFragment;
  }

  private void init() {
    View view = inflate(context, R.layout.profile_list_item_layout, this);

    view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    setClickable(true);

    setOnLongClickListener(v -> {
      if(item instanceof Education) {
        educationDialog = new EditEducationDialog(context);
        educationDialog.setEducation(key, (Education) item);
        profileFragment.setCurrentEducation((ProfileItemView<Education>) this);
        educationDialog.show();
      } else if(item instanceof Experience) {
        experienceDialog = new EditExperienceDialog(context);
        experienceDialog.setExperience(key, (Experience) item);
        profileFragment.setCurrentExperience((ProfileItemView<Experience>) this);
        experienceDialog.show();
      }
      return true;
    });

    titleTv = view.findViewById(R.id.titleTv);
    descriptionTv = view.findViewById(R.id.descriptionTv);
    dateTv = view.findViewById(R.id.dateTv);

    if(item instanceof Course) {
      Course course = (Course) item;
      titleTv.setText(course.getName());
      descriptionTv.setText(String.format("%s...", course.getDescription().substring(0, 100)));
      dateTv.setVisibility(GONE);
    } else if(item instanceof Experience) {
      Experience current = (Experience) item;

      titleTv.setText(current.getJobTitle());
      descriptionTv.setText(current.getCompany());
      Date startDateString = Date.formatFromString(current.getStartDate());
      String startDate = String.format(new Locale("en"),
        "%s/%d",
        formatMonth(startDateString.getMonth()),
        startDateString.getYear());
      String endDate;
      if(current.getEndDate() == null) {
        endDate = "present";
      } else {
        Date endDateString = Date.formatFromString(current.getEndDate());
        endDate = String.format(new Locale("en"),
          "%s/%d",
          formatMonth(endDateString.getMonth()),
          endDateString.getYear());
      }
      dateTv.setText(String.format(new Locale("en"), "%s - %s", startDate, endDate));
    } else if(item instanceof Education) {
      Education current = (Education) item;
      titleTv.setText(current.getDegree());
      descriptionTv.setText(current.getSchool());
      Date startDateString = Date.formatFromString(current.getStartDate());
      String startDate = String.format(new Locale("en"),
        "%s/%d",
        formatMonth(startDateString.getMonth()),
        startDateString.getYear());
      String endDate;
      if(current.getEndDate() == null) {
        endDate = "present";
      } else {
        Date endDateString = Date.formatFromString(current.getEndDate());
        endDate = String.format(new Locale("en"),
          "%s/%d",
          formatMonth(endDateString.getMonth()),
          endDateString.getYear());
      }
      dateTv.setText(String.format(new Locale("en"), "%s - %s", startDate, endDate));
    }
  }

  public void setItem(T item) {
    this.item = item;
    init();
  }

  private String formatMonth(int month) {
    switch (month) {
      case 1: return "Jan";
      case 2: return "Feb";
      case 3: return "Mar";
      case 4: return "Apr";
      case 5: return "May";
      case 6: return "Jun";
      case 7: return "Jul";
      case 8: return "Aug";
      case 9: return "Sep";
      case 10: return "Oct";
      case 11: return "Nov";
      case 12: return "Dec";
      default: return "";
    }
  }
}
