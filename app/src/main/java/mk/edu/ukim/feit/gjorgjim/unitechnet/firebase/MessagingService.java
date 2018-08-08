package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.ChatCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Chat;

/**
 * Created by gjmarkov on 08.8.2018.
 */
public class MessagingService {
  private static final MessagingService ourInstance = new MessagingService();

  public static MessagingService getInstance() {
    return ourInstance;
  }

  private DatabaseService databaseService;
  private AuthenticationService authenticationService;

  private ChatCallback callback;

  private MessagingService() {
    databaseService = DatabaseService.getInstance();
    authenticationService = AuthenticationService.getInstance();
  }

  public void getChat() {
    HashMap<String, Chat> chatHashMap = new HashMap<>();

    ValueEventListener valueEventListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
          chatHashMap.put(snapshot.getKey(), snapshot.getValue(Chat.class));
        }
        callback.onSuccess(chatHashMap);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };

    databaseService.chatReference(
      authenticationService
        .getCurrentUser()
        .getUid()
    ).addListenerForSingleValueEvent(valueEventListener);

  }

  public void setChatCallback(ChatCallback callback) {
    this.callback = callback;
  }
}
