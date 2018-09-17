package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.FeedCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.FeedService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.FeedItem;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.views.FeedItemView;

/**
 * Created by gjmarkov on 16.5.2018.
 */

public class FeedFragment extends Fragment {

  private FeedService feedService;
  private FragmentChangingListener fragmentChangingListener;

  private ProgressBar progressBar;
  private LinearLayout feedItemsLl;

  private List<FeedItem> feedItems;

  public FeedFragment() {
    feedService = FeedService.getInstance();
    feedItems = new ArrayList<>();
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
    View view = inflater.inflate(R.layout.fragment_feed, container, false);

    progressBar = view.findViewById(R.id.progressBar);
    feedItemsLl = view.findViewById(R.id.feedLl);

    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    feedService.getFeed(new FeedCallback() {
      @Override
      public void onSuccess(HashMap<String, FeedItem> feedItemHashMap) {
        Log.d(FeedFragment.class.getSimpleName(), "Feed items from service size: " + feedItemHashMap.size());
        feedItems = new ArrayList<>(feedItemHashMap.values());
        Log.d(FeedFragment.class.getSimpleName(), "isVisible " + FeedFragment.this.isVisible());
        if(FeedFragment.this.isVisible()) {
          showFeedItems();
        }
      }

      @Override
      public void onFailure(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void showFeedItems() {
    sortFeedItems();
    Log.d(FeedFragment.class.getSimpleName(), "Feed items size: " + feedItems.size());
    for(FeedItem current : feedItems) {
      FeedItemView feedItemView = new FeedItemView(getContext(), current, fragmentChangingListener);

      feedItemsLl.addView(feedItemView);
    }

    progressBar.setVisibility(View.GONE);
    feedItemsLl.setVisibility(View.VISIBLE);
  }

  private void sortFeedItems() {
    Collections.sort(feedItems, new Comparator<FeedItem>() {
      @Override
      public int compare(FeedItem o1, FeedItem o2) {
        if (Date.formatFromString(o1.getCourse().getProblems().get(o1.getProblemId()).getDate()).isAfter(
          Date.formatFromString(o2.getCourse().getProblems().get(o2.getProblemId()).getDate()))) {
          return -1;
        } else {
          return 1;
        }
      }
    });
  }

}
