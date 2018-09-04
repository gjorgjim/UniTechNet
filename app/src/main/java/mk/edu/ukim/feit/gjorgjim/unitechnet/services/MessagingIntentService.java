package mk.edu.ukim.feit.gjorgjim.unitechnet.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.DatabaseService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Chat;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Message;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date;
import mk.edu.ukim.feit.gjorgjim.unitechnet.notifications.NotificationCenter;

/**
 * Created by gjmarkov on 04.9.2018.
 */

public class MessagingIntentService extends IntentService {

  public static final String LOG_TAG  = MessagingIntentService.class.getSimpleName();

  private DatabaseService databaseService;
  private AuthenticationService authenticationService;
  private NotificationCenter notificationCenter;

  public MessagingIntentService() {
    super("MessagingIntentService");
  }

  public MessagingIntentService(String name) {
    super(name);
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    databaseService = DatabaseService.getInstance();
    authenticationService = AuthenticationService.getInstance();
    notificationCenter = NotificationCenter.getInstance();
    notificationCenter.setContext(this);
    Date now = Date.getDate();
    DatabaseReference testReference = databaseService.usersReference().child("TestingIntentService");
    int i = 1;
    databaseService.chatReference(
      authenticationService.getCurrentUser().getUid()
    ).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for(DataSnapshot data : dataSnapshot.getChildren()) {
          Chat currentChat = data.getValue(Chat.class);
          if(currentChat.getLastMessage().getSentDate().isAfter(now)) {
            notificationCenter.sentMessageNotification(String.format("%s %s has sent you a message", currentChat.getFirstName(), currentChat.getLastName()));
          }
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

  }
}