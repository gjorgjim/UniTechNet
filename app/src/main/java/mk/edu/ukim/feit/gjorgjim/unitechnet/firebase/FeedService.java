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
      for(DataSnapshot data : dataSnapshot.getChildren()) {
        Course course = data.getValue(Course.class);

        if(course.getProblems() != null) {
          for (Map.Entry<String, Problem> problem : course.getProblems().entrySet()) {
            FeedItem feedItem = new FeedItem(course, problem.getKey());

            feed.put(problem.getKey(), feedItem);
          }
        }
      }
      callback.onSuccess(feed);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
  };

  public void getFeed(FeedCallback callback) {
    this.callback = callback;

    feed = new HashMap<>();

    databaseService.userReference(authenticationService.getCurrentUser().getUid())
      .child("courses").addValueEventListener(valueEventListener);
  }
}
