package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.FeedCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.FeedItem;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Problem;

/**
 * Created by gjmarkov on 14.9.2018.
 */

public class FeedService {
  private static final FeedService ourInstance = new FeedService();

  public static FeedService getInstance() {
    return ourInstance;
  }

  private DatabaseService databaseService;
  private AuthenticationService authenticationService;

  private FeedCallback callback;

  private HashMap<String, FeedItem> feed;

  private FeedService() {
    databaseService = DatabaseService.getInstance();
    authenticationService = AuthenticationService.getInstance();
  }

  ValueEventListener valueEventListener = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      Log.d(FeedService.class.getSimpleName(), "onDataChange called");
      feed = new HashMap<>();
      for(DataSnapshot data : dataSnapshot.getChildren()) {
        Course course = data.getValue(Course.class);
        Log.d(FeedService.class.getSimpleName(), "Count: " + data.getChildrenCount());
        if(course.getProblems() != null) {
          Log.d(FeedService.class.getSimpleName(), "Course size problems" + course.getProblems().size());
          for (Map.Entry<String, Problem> problem : course.getProblems().entrySet()) {
            FeedItem feedItem = new FeedItem(course, problem.getKey());

            feed.put(problem.getKey(), feedItem);
          }
        }
      }
      Log.d(FeedService.class.getSimpleName(), "Feed size in service " + feed.size());
      callback.onSuccess(feed);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
  };

  public void getFeed(FeedCallback callback) {
    if(feed == null || feed.size() == 0) {
      Log.d(FeedService.class.getSimpleName(), "in if");

      this.callback = callback;

      databaseService.userReference(authenticationService.getCurrentUser().getUid())
        .child("courses").addValueEventListener(valueEventListener);
    } else {
      Log.d(FeedService.class.getSimpleName(), "in else");
      callback.onSuccess(feed);
    }
  }

  public void removeListenerFromFeed() {
    databaseService.userReference(authenticationService.getCurrentUser().getUid())
      .child("courses").removeEventListener(valueEventListener);
  }
}
