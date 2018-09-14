package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.FeedCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.FeedService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.FeedItem;

/**
 * Created by gjmarkov on 16.5.2018.
 */

public class FeedFragment extends Fragment {

  private static final FeedFragment instance = new FeedFragment();

  public static FeedFragment getInstance() {
    return instance;
  }

  private FeedService feedService;

  public FeedFragment() {
    feedService = FeedService.getInstance();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_feed, container, false);

    feedService.getFeed(new FeedCallback() {
      @Override
      public void onSuccess(HashMap<String, FeedItem> feedItemHashMap) {
        for(FeedItem feedItem : feedItemHashMap.values()) {
          //TODO: Add feed items in linear layout
        }
      }

      @Override
      public void onFailure(String message) {

      }
    });

    return view;
  }
}
