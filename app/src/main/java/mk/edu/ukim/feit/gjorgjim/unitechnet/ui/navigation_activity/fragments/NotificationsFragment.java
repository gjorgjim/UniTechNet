package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;

/**
 * Created by gjmarkov on 16.5.2018.
 */

public class NotificationsFragment extends Fragment {

  private static final NotificationsFragment instance = new NotificationsFragment();

  public static NotificationsFragment getInstance() {
    return instance;
  }

  public NotificationsFragment() {
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragnent_notifications, container, false);

    return view;
  }
}