package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.DatabaseCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.MessagingService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.NotificationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.UserService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.ViewDelegate;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.Notification;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Course;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.course.Problem;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Chat;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Message;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;
import mk.edu.ukim.feit.gjorgjim.unitechnet.services.MessagingBackgroundService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.services.NotificationBackgroundService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.WaitingDialog;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.delegates.NavigationToolbarDelegate;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.CourseViewFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.CoursesFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.EditProfileFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.FeedFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.FragmentChangingListener;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.MessagesFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.NotificationsFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.ProblemViewFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.ProfileFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.UserMessagingFragment;

public class NavigationActivity extends AppCompatActivity implements FragmentChangingListener {

  private static final String LOG_TAG = "NavigationActivity";

  private UserService userService;
  private MessagingService messagingService;
  private NotificationService notificationService;

  private ViewDelegate viewDelegate;

  private CoursesFragment coursesFragment;
  private FeedFragment feedFragment;
  private NotificationsFragment notificationsFragment;
  private MessagesFragment messagesFragment;
  private ProfileFragment profileFragment = null;
  private EditProfileFragment editProfileFragment;
  private UserMessagingFragment userMessagingFragment;
  private CourseViewFragment courseViewFragment;
  private ProblemViewFragment problemViewFragment;

  private NavigationToolbarDelegate navigationToolbarDelegate;

  private WaitingDialog waitingDialog;

  private BottomNavigationView navigation;
  private Toolbar toolbar;
  private MaterialSearchView searchView;
  private FloatingActionButton fab;

  private boolean isFirstLogin = false;
  private boolean startedFromNotification = false;

  private android.support.v4.app.FragmentManager fragmentManager;

  private List<String> messagesSnackbarShownList;

  private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
    = new BottomNavigationView.OnNavigationItemSelectedListener() {

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
      FragmentTransaction transaction = fragmentManager.beginTransaction();
      transaction.setCustomAnimations(R.animator.enter, R.animator.exit);
      navigationToolbarDelegate.setLogo(NavigationToolbarDelegate.NavigationToolbarLogo.MAIN);
      switch (item.getItemId()) {
        case R.id.navigation_courses:
          transaction.replace(R.id.container, coursesFragment).commit();
          if (fab.isShown()) {
            fab.hide();
          }
          return true;
        case R.id.navigation_messages:
          transaction.replace(R.id.container, messagesFragment).commit();
          if (fab.isShown()) {
            fab.hide();
          }
          return true;
        case R.id.navigation_feed:
          transaction.replace(R.id.container, feedFragment).commit();
          if (!fab.isShown()) {
            fab.show();
          }
          return true;
        case R.id.navigation_notification:
          notificationsFragment = new NotificationsFragment();
          transaction.replace(R.id.container, notificationsFragment).commit();
          if (fab.isShown()) {
            fab.hide();
          }
          return true;
        case R.id.navigation_profile:
          if (isFirstLogin) {
            transaction.replace(R.id.container, editProfileFragment).commit();
            isFirstLogin = false;
          } else {
            transaction.replace(R.id.container, profileFragment).commit();
          }
          if (fab.isShown()) {
            fab.hide();
          }
          return true;
      }
      return false;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_navigation);

    userService = UserService.getInstance();
    messagingService = MessagingService.getInstance();
    notificationService = NotificationService.getInstance();

    viewDelegate = ViewDelegate.getInstance();

    viewDelegate.setCurrentActivity(NavigationActivity.this);

    navigationToolbarDelegate = NavigationToolbarDelegate.getInstance();

    fragmentManager = getSupportFragmentManager();

    waitingDialog = new WaitingDialog(NavigationActivity.this);

    navigation = findViewById(R.id.navigation);
    toolbar = findViewById(R.id.toolbar);
    searchView = findViewById(R.id.searchview);
    fab = findViewById(R.id.fab);

    messagesSnackbarShownList = new ArrayList<>();

    setSupportActionBar(toolbar);
    navigationToolbarDelegate.setToolbar(toolbar, getResources());

    navigationToolbarDelegate.setLogo(NavigationToolbarDelegate.NavigationToolbarLogo.MAIN);

    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    feedFragment = new FeedFragment();
    navigation.setSelectedItemId(R.id.navigation_feed);

    searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
      @Override
      public void onSearchViewShown() {
        toolbar.setLogo(null);
      }

      @Override
      public void onSearchViewClosed() {
        toolbar.setLogo(R.drawable.toolbar_logo);
      }
    });

    Bundle bundle = getIntent().getBundleExtra("info");
    if (bundle != null) {
      startedFromNotification = true;
    }

    showWaitingDialog("Fetching your data...");

    userService.isFirstSignIn(new DatabaseCallback<User>() {
      @Override
      public void onSuccess(User user) {
        hideWaitingDialog();

        coursesFragment = new CoursesFragment();
        feedFragment = new FeedFragment();
        notificationsFragment = new NotificationsFragment();
        messagesFragment = new MessagesFragment();
        profileFragment = new ProfileFragment();

        if (user == null) {
          Log.d(LOG_TAG, "User is null");
          editProfileFragment = new EditProfileFragment();

          isFirstLogin = true;

          navigation.setSelectedItemId(R.id.navigation_profile);

          fab.hide();
        } else {
          if (startedFromNotification) {
            if (bundle.get("key") != null) {
              messagesFragment = new MessagesFragment();

              messagesFragment.setArguments(bundle);

              navigation.setSelectedItemId(R.id.navigation_messages);
            } else if(bundle.getSerializable("notification") != null) {
              coursesFragment = new CoursesFragment();

              coursesFragment.setArguments(bundle);

              navigation.setSelectedItemId(R.id.navigation_courses);
            }
          }
        }
        userService.removeSignInListener();
        messagingService.startBackgroundServiceForMessages(NavigationActivity.this);
        notificationService.startBackgroundServiceForMessages(NavigationActivity.this);
      }

      @Override
      public void onFailure(String message) {
        coursesFragment = new CoursesFragment();
        feedFragment = new FeedFragment();
        notificationsFragment = new NotificationsFragment();
        messagesFragment = new MessagesFragment();
        profileFragment = new ProfileFragment();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.navigation_feed);
        hideWaitingDialog();
        userService.removeSignInListener();
        Toast.makeText(NavigationActivity.this, message, Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_item, menu);
    MenuItem item = menu.findItem(R.id.action_search);
    searchView.setMenuItem(item);
    return true;
  }

  @Override
  public void changeToUserFragment() {
    navigation.setSelectedItemId(R.id.navigation_profile);
  }

  @Override
  public void changeToCoursesFragment() {
    coursesFragment = new CoursesFragment();
    navigation.setSelectedItemId(R.id.navigation_courses);
  }

  @Override
  public void changeToUserMessagingFragment(Chat chat, String key) {
    getSupportActionBar().hide();
    navigation.setVisibility(View.GONE);

    FragmentTransaction transaction = fragmentManager.beginTransaction();
    userMessagingFragment = new UserMessagingFragment();

    Bundle bundle = new Bundle();
    bundle.putSerializable(Chat.TAG, chat);
    bundle.putString("KEY", key);

    userMessagingFragment.setArguments(bundle);

    transaction.replace(R.id.container, userMessagingFragment).commit();
  }

  @Override
  public void changeToMessagesFragment() {
    getSupportActionBar().show();
    navigation.setVisibility(View.VISIBLE);

    messagesFragment = new MessagesFragment();

    navigation.setSelectedItemId(R.id.navigation_messages);
  }

  @Override
  public void changeToCourseViewFragment(Course course, Problem problem) {
    Log.d(LOG_TAG, "Current Course:" + course.toString());
    courseViewFragment = new CourseViewFragment();

    Bundle bundle = new Bundle();
    bundle.putSerializable("currentCourse", course);

    if(problem != null) {
      bundle.putSerializable("currentProblem", problem);
    }

    courseViewFragment.setArguments(bundle);

    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.replace(R.id.container, courseViewFragment).commit();

    viewDelegate.viewCurrentCourse(course);

    //TODO: Change drawable
    navigationToolbarDelegate.setLogo(NavigationToolbarDelegate.NavigationToolbarLogo.COURSE);
  }

  @Override
  public void changeToProblemViewFragment(Problem problem) {
    problemViewFragment = new ProblemViewFragment();

    Bundle bundle = new Bundle();
    bundle.putSerializable("currentProblem", problem);

    problemViewFragment.setArguments(bundle);

    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.replace(R.id.container, problemViewFragment).commit();

    viewDelegate.viewCurrentProblem(problem);

    //TODO: Change drawable
    navigationToolbarDelegate.setLogo(NavigationToolbarDelegate.NavigationToolbarLogo.PROBLEM);
  }

  @Override
  public void changeToNotificationsFragment() {
    notificationsFragment = new NotificationsFragment();

    navigation.setSelectedItemId(R.id.navigation_notification);
  }

  @Override
  public void changeToProblemViewFragment(Problem problem, Course course) {
    problemViewFragment = new ProblemViewFragment();

    Bundle bundle = new Bundle();
    bundle.putSerializable("currentProblem", problem);

    problemViewFragment.setArguments(bundle);

    if(navigation.getSelectedItemId() != R.id.navigation_courses) {
      navigation.setSelectedItemId(R.id.navigation_courses);
    }

    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.replace(R.id.container, problemViewFragment).commit();

    viewDelegate.viewCurrentCourse(course);
    viewDelegate.viewCurrentProblem(problem);

    navigationToolbarDelegate.setLogo(NavigationToolbarDelegate.NavigationToolbarLogo.PROBLEM);
  }

  @Override
  public void changeToCoursesFragment(Notification notification) {
    Bundle bundle = new Bundle();

    bundle.putSerializable("notification", notification);

    coursesFragment = new CoursesFragment();

    coursesFragment.setArguments(bundle);

    navigation.setSelectedItemId(R.id.navigation_courses);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(LOG_TAG, "requestCode: " + requestCode);
    if (resultCode == Activity.RESULT_OK && (requestCode == ProfileFragment.ImagePickerRequestCode
      || requestCode == EditProfileFragment.ImagePickerRequestCode)) {
      Image image = ImagePicker.getFirstImageOrNull(data);
      Log.d(LOG_TAG, "requestCode: " + requestCode);
      try {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
          Uri.fromFile(new File(image.getPath())));
        if (requestCode == EditProfileFragment.ImagePickerRequestCode) {
          editProfileFragment.setNewProfilePicture(bitmap);
        } else if (requestCode == ProfileFragment.ImagePickerRequestCode) {
          profileFragment.changeProfilePicture(bitmap);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void showWaitingDialog(String message) {
    waitingDialog.showDialog(message);
  }

  private void hideWaitingDialog() {
    waitingDialog.hideDialog();
  }

  private BroadcastReceiver messagesReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Log.d(LOG_TAG, "onReceive in messageReceiver called");
      if (intent.getAction().equals(MessagingBackgroundService.ACTION)) {
        Bundle bundle = intent.getBundleExtra("info");

        String key = bundle.getString("key");
        String firstName = bundle.getString("firstName");
        Message lastMessage = (Message) bundle.getSerializable("lastMessage");

        if (messagesFragment != null && messagesFragment.isVisible()) {
          messagesFragment.updateLastMessage(key, lastMessage);
        } else {
          if (!messagesSnackbarShownList.contains(key)) {
            Snackbar.make(findViewById(android.R.id.content),
              String.format("You have a new message from %s", firstName), Snackbar.LENGTH_SHORT).setAction("VIEW",
              v -> {
                messagesFragment = new MessagesFragment();

                Bundle fragmentBundle = new Bundle();
                fragmentBundle.putString("key", key);

                messagesFragment.setArguments(bundle);

                navigation.setSelectedItemId(R.id.navigation_messages);
              }).show();
            messagesSnackbarShownList.add(key);
          }
        }
      }
    }
  };

  private BroadcastReceiver notificationsReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(NotificationBackgroundService.ACTION)) {
        Log.d(LOG_TAG, "Broadcast is from notifications");
        Bundle bundle = intent.getBundleExtra("info");

        String key = bundle.getString("key");

        Notification notification = (Notification) bundle.getSerializable("notification");

        if (notificationsFragment != null && notificationsFragment.isVisible()) {
          Log.d(LOG_TAG, "notificationsFragment is visible");
          notificationsFragment.addNotification(key, notification);
        } else {
          Log.d(LOG_TAG, "notificationsFragment is not visible");
          Snackbar.make(findViewById(android.R.id.content), "You have a new notification", Snackbar.LENGTH_SHORT)
            .setAction(
              "VIEW", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  changeToNotificationsFragment();
                }
              }).show();
        }
      }
    }
  };

  @Override
  protected void onResume() {
    IntentFilter intentFilter = new IntentFilter(MessagingBackgroundService.ACTION);
    LocalBroadcastManager.getInstance(this).registerReceiver(messagesReceiver, intentFilter);
    IntentFilter intentFilter1 = new IntentFilter(NotificationBackgroundService.ACTION);
    LocalBroadcastManager.getInstance(this).registerReceiver(notificationsReceiver, intentFilter1);
    super.onResume();
  }

  @Override
  protected void onDestroy() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(messagesReceiver);
    LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationsReceiver);
    super.onDestroy();
  }

  @Override
  public void onBackPressed() {
    if (userMessagingFragment != null && userMessagingFragment.isVisible()) {
      changeToMessagesFragment();
    } else if (courseViewFragment != null && courseViewFragment.isVisible()) {
      changeToCoursesFragment();
    } else if (problemViewFragment != null && problemViewFragment.isVisible()) {
      changeToCourseViewFragment(viewDelegate.getCurrentCourse(), null);
    } else {
      super.onBackPressed();
    }
  }

  public void clearSnackbarShownList(String key) {
    messagesSnackbarShownList.remove(key);
  }
}
