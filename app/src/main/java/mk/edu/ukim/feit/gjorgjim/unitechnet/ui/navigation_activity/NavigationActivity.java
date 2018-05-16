package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.CoursesFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.FeedFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.MessagesFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.NotificationsFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.ProfileFragment;

public class NavigationActivity extends AppCompatActivity {

  private CoursesFragment coursesFragment;
  private FeedFragment feedFragment;
  private NotificationsFragment notificationsFragment;
  private MessagesFragment messagesFragment;
  private ProfileFragment profileFragment;

  private BottomNavigationView navigation;
  private Toolbar toolbar;
  private MaterialSearchView searchView;

  private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      switch (item.getItemId()) {
        case R.id.navigation_courses:
          transaction.replace(R.id.container, coursesFragment).commit();
          return true;
        case R.id.navigation_messages:
          transaction.replace(R.id.container, messagesFragment).commit();
          return true;
        case R.id.navigation_feed:
          transaction.replace(R.id.container, feedFragment).commit();
          return true;
        case R.id.navigation_notification:
          transaction.replace(R.id.container, notificationsFragment).commit();
          return true;
        case R.id.navigation_profile:
          transaction.replace(R.id.container, profileFragment).commit();
          return true;
      }
      return false;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_navigation);

    navigation = findViewById(R.id.navigation);
    //profileIv = findViewById(R.id.profileIv);
    toolbar = findViewById(R.id.toolbar);
    searchView = findViewById(R.id.searchview);

    setSupportActionBar(toolbar);

    coursesFragment = CoursesFragment.getInstance();
    feedFragment = FeedFragment.getInstance();
    notificationsFragment = NotificationsFragment.getInstance();
    messagesFragment = MessagesFragment.getInstance();
    profileFragment = ProfileFragment.getInstance();

    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    navigation.setSelectedItemId(R.id.navigation_feed);

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_item, menu);
    MenuItem item = menu.findItem(R.id.action_search);
    searchView.setMenuItem(item);
    return true;
  }
}
