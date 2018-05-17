package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;

import java.io.File;
import java.io.IOException;
import java.util.List;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.UserService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.CoursesFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.EditProfileFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.FeedFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.FragmentChangingListener;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.MessagesFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.NotificationsFragment;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.ProfileFragment;

public class NavigationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
  FragmentChangingListener {

  private static final String TAG = "NavigationActivity";

  public static final int PICK_IMAGE_REQUEST = 1;

  private UserService userService;

  private CoursesFragment coursesFragment;
  private FeedFragment feedFragment;
  private NotificationsFragment notificationsFragment;
  private MessagesFragment messagesFragment;
  private ProfileFragment profileFragment;
  private EditProfileFragment editProfileFragment;

  private BottomNavigationView navigation;
  private Toolbar toolbar;
  private MaterialSearchView searchView;

  private Uri filePath;

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

    userService = UserService.getInstance();

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
    editProfileFragment = EditProfileFragment.getInstance();

    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    if(userService.isFirstSignIn()) {
      Log.d(TAG, "Is First Sign In");
      navigation.setSelectedItemId(R.id.navigation_profile);
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.container, editProfileFragment).commit();
    } else {
      Log.d(TAG, "Is Not First Sign In");
      navigation.setSelectedItemId(R.id.navigation_feed);
    }

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
    editProfileFragment.setDateTextView(year, monthOfYear, dayOfMonth);
  }

  @Override
  public void changeToUserFragment() {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.container, profileFragment).commit();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
      Image image = ImagePicker.getFirstImageOrNull(data);
      try {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(image.getPath())));
        editProfileFragment.setNewProfilePicture(bitmap);
        Log.d(TAG, "Calling setNewProfilePicture");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }
}
