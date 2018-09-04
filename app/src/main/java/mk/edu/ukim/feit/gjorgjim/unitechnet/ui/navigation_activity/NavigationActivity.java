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
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.UserService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Chat;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Message;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;
import mk.edu.ukim.feit.gjorgjim.unitechnet.services.MessagingIntentService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.WaitingDialog;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.CoursesFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.EditProfileFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.FeedFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.FragmentChangingListener;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.MessagesFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.NotificationsFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.ProfileFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.UserMessagingFragment;

public class NavigationActivity extends AppCompatActivity implements FragmentChangingListener {

  private static final String LOG_TAG = "NavigationActivity";

  private UserService userService;
  private MessagingService messagingService;

  private CoursesFragment coursesFragment;
  private FeedFragment feedFragment;
  private NotificationsFragment notificationsFragment;
  private MessagesFragment messagesFragment;
  private ProfileFragment profileFragment = null;
  private EditProfileFragment editProfileFragment;
  private UserMessagingFragment userMessagingFragment;

  private WaitingDialog waitingDialog;

  private BottomNavigationView navigation;
  private Toolbar toolbar;
  private MaterialSearchView searchView;
  private FloatingActionButton fab;

  private List<String> messagesSnackbarShownList;

  private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.setCustomAnimations(R.animator.enter, R.animator.exit);
      switch (item.getItemId()) {
        case R.id.navigation_courses:
          transaction.replace(R.id.container, coursesFragment).commitAllowingStateLoss();
          if(fab.isShown()) {
            fab.hide();
          }
          return true;
        case R.id.navigation_messages:
          transaction.replace(R.id.container, messagesFragment).commitAllowingStateLoss();
          if(fab.isShown()) {
            fab.hide();
          }
          return true;
        case R.id.navigation_feed:
          transaction.replace(R.id.container, feedFragment).commitAllowingStateLoss();
          if(!fab.isShown()) {
            fab.show();
          }
          return true;
        case R.id.navigation_notification:
          transaction.replace(R.id.container, notificationsFragment).commitAllowingStateLoss();
          if(fab.isShown()) {
            fab.hide();
          }
          return true;
        case R.id.navigation_profile:
          transaction.replace(R.id.container, profileFragment).commitAllowingStateLoss();
          if(fab.isShown()) {
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

    waitingDialog = new WaitingDialog(NavigationActivity.this);

    navigation = findViewById(R.id.navigation);
    toolbar = findViewById(R.id.toolbar);
    searchView = findViewById(R.id.searchview);
    fab = findViewById(R.id.fab);

    messagesSnackbarShownList = new ArrayList<>();

    setSupportActionBar(toolbar);

    showWaitingDialog("Fetching your data...");

    userService.isFirstSignIn(new DatabaseCallback<User>() {
      @Override
      public void onSuccess(User user) {
        hideWaitingDialog();
        if(user == null){
          Log.d(LOG_TAG, "User is null");
          editProfileFragment = new EditProfileFragment();
          navigation.setSelectedItemId(R.id.navigation_profile);
          FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          transaction.replace(R.id.container, editProfileFragment).commit();
          fab.hide();
        } else {
          coursesFragment = new CoursesFragment();
          feedFragment = FeedFragment.getInstance();
          notificationsFragment = NotificationsFragment.getInstance();
          messagesFragment = new MessagesFragment();
          profileFragment = new ProfileFragment();

          navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

          navigation.setSelectedItemId(R.id.navigation_feed);
        }
        userService.removeSignInListener();
        messagingService.startBackgroundServiceForMessages(NavigationActivity.this);
      }

      @Override
      public void onFailure(String message) {
        coursesFragment = new CoursesFragment();
        feedFragment = FeedFragment.getInstance();
        notificationsFragment = NotificationsFragment.getInstance();
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
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    profileFragment = new ProfileFragment();
    transaction.replace(R.id.container, profileFragment).commit();
  }

  @Override
  public void changeToCoursesFragment() {
    navigation.setSelectedItemId(R.id.navigation_courses);
  }

  @Override
  public void changeToUserMessagingFragment(Chat chat, String key) {
    getSupportActionBar().hide();
    navigation.setVisibility(View.GONE);

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    messagesFragment = new MessagesFragment();
    transaction.replace(R.id.container, messagesFragment).commit();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(LOG_TAG, "requestCode: " + requestCode);
    if(resultCode == Activity.RESULT_OK && (requestCode == ProfileFragment.ImagePickerRequestCode || requestCode == EditProfileFragment.ImagePickerRequestCode)) {
      Image image = ImagePicker.getFirstImageOrNull(data);
      Log.d(LOG_TAG, "requestCode: " + requestCode);
      try {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(image.getPath())));
        if(requestCode == EditProfileFragment.ImagePickerRequestCode) {
          editProfileFragment.setNewProfilePicture(bitmap);
        } else if(requestCode == ProfileFragment.ImagePickerRequestCode) {
          profileFragment.changeProfilePicture(bitmap);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void showWaitingDialog(String message){
    waitingDialog.showDialog(message);
  }

  private void hideWaitingDialog(){
    waitingDialog.hideDialog();
  }

  private BroadcastReceiver messagesReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Bundle bundle = intent.getBundleExtra("info");

      String key = bundle.getString("key");
      String firstName = bundle.getString("firstName");
      Message lastMessage = (Message) bundle.getSerializable("lastMessage");

      if(messagesFragment != null && messagesFragment.isVisible()) {
        messagesFragment.updateLastMessage(key, lastMessage);
      } else {
        if(!messagesSnackbarShownList.contains(key)) {
          Snackbar.make(findViewById(android.R.id.content), String.format("You have a new message from %s", firstName),
            Snackbar.LENGTH_SHORT).setAction("VIEW", v -> {
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
  };

  @Override
  protected void onResume() {
    IntentFilter intentFilter = new IntentFilter(MessagingIntentService.ACTION);
    LocalBroadcastManager.getInstance(this).registerReceiver(messagesReceiver, intentFilter);
    super.onResume();
  }

  @Override
  protected void onDestroy() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(messagesReceiver);
    super.onDestroy();
  }

  @Override
  public void onBackPressed() {
    if(userMessagingFragment != null && userMessagingFragment.isVisible()) {
      changeToMessagesFragment();
    } else {
      super.onBackPressed();
    }
  }

  public void clearSnackbarShownList(String key) {
    messagesSnackbarShownList.remove(key);
  }
}
