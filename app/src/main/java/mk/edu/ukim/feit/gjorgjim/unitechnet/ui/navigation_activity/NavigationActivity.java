package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationSet;
import android.widget.TextView;

import com.bluehomestudio.progresswindow.ProgressWindow;
import com.bluehomestudio.progresswindow.ProgressWindowConfiguration;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;

import java.io.File;
import java.io.IOException;
import java.util.List;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.DatabaseCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.DatabaseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.UserService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.helpers.DatePickerDialogIdentifier;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.User;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.WaitingDialog;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.CoursesFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.EditProfileFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.FeedFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.FragmentChangingListener;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.MessagesFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.NotificationsFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.ProfileFragment;

public class NavigationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
  FragmentChangingListener, DatabaseCallback<User> {

  private static final String LOG_TAG = "NavigationActivity";

  private DatabaseService databaseService;
  private AuthenticationService authenticationService;
  private UserService userService;

  private CoursesFragment coursesFragment;
  private FeedFragment feedFragment;
  private NotificationsFragment notificationsFragment;
  private MessagesFragment messagesFragment;
  private ProfileFragment profileFragment;
  private EditProfileFragment editProfileFragment;

  private WaitingDialog waitingDialog;

  private BottomNavigationView navigation;
  private Toolbar toolbar;
  private MaterialSearchView searchView;
  private FloatingActionButton fab;

  private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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

    databaseService = DatabaseService.getInstance();
    authenticationService = AuthenticationService.getInstance();
    userService = UserService.getInstance();

    waitingDialog = new WaitingDialog(NavigationActivity.this);

    navigation = findViewById(R.id.navigation);
    //profileIv = findViewById(R.id.profileIv);
    toolbar = findViewById(R.id.toolbar);
    searchView = findViewById(R.id.searchview);
    fab = findViewById(R.id.fab);

    setSupportActionBar(toolbar);

    showWaitingDialog("Fetching your data...");

    userService.setUserCallback(this);
    userService.isFirstSignIn();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_item, menu);
    MenuItem item = menu.findItem(R.id.action_search);
    searchView.setMenuItem(item);
    return true;
  }

  @Override
  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    if(DatePickerDialogIdentifier.getCurrentDatePicker().equals(DatePickerDialogIdentifier.BIRTHDAY_EDIT_PROFILE)) {
      editProfileFragment.setDateTextView(year, monthOfYear, dayOfMonth);
    } else if(DatePickerDialogIdentifier.getCurrentDatePicker().equals(DatePickerDialogIdentifier.STARTDATE_EXPERIENCE)) {
      profileFragment.setStartDateExperience(year, monthOfYear, dayOfMonth);
    } else if(DatePickerDialogIdentifier.getCurrentDatePicker().equals(DatePickerDialogIdentifier.ENDDATE_EXPERIENCE)) {
      profileFragment.setEndDateExperience(year, monthOfYear, dayOfMonth);
    }
  }

  @Override
  public void changeToUserFragment() {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    profileFragment = new ProfileFragment();
    transaction.replace(R.id.container, profileFragment).commit();

    userService.listenForUserDetailsChanges();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
      Image image = ImagePicker.getFirstImageOrNull(data);
      try {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(image.getPath())));
        editProfileFragment.setNewProfilePicture(bitmap);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onSuccess(CallBackTag tag, User user) {
    if(tag == CallBackTag.FIRST_SIGN_IN) {
      hideWaitingDialog();
      if(user == null){
        Log.d(LOG_TAG, "User is null");
        editProfileFragment = new EditProfileFragment();
        navigation.setSelectedItemId(R.id.navigation_profile);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, editProfileFragment).commit();
        fab.hide();
      } else {
        coursesFragment = CoursesFragment.getInstance();
        feedFragment = FeedFragment.getInstance();
        notificationsFragment = NotificationsFragment.getInstance();
        messagesFragment = MessagesFragment.getInstance();
        profileFragment = new ProfileFragment();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.navigation_feed);

        userService.listenForUserDetailsChanges();
      }
    } else if(tag == CallBackTag.EXPERIENCE_CHANGES){
      profileFragment.updateExperience();
    }
  }

  @Override
  public void onFailure(String message) {
    hideWaitingDialog();
  }

  private void showWaitingDialog(String message){
    waitingDialog.showDialog("Fetching your data...");
  }

  private void hideWaitingDialog(){
    waitingDialog.hideDialog();
  }
}
