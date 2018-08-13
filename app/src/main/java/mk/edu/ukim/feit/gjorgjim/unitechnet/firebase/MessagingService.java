package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.ChatCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.MessageCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Chat;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Message;

/**
 * Created by gjmarkov on 08.8.2018.
 */
public class MessagingService {
  public static final String LOG_TAG = "MessagingService";

  private static final MessagingService ourInstance = new MessagingService();

  public static MessagingService getInstance() {
    return ourInstance;
  }

  private DatabaseService databaseService;
  private AuthenticationService authenticationService;

  private ChatCallback callback;
  private MessageCallback messageCallback;

  private MessagingService() {
    databaseService = DatabaseService.getInstance();
    authenticationService = AuthenticationService.getInstance();
  }

  private ChildEventListener messagesChildEventListener =  new ChildEventListener() {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
      Message message = dataSnapshot.getValue(Message.class);
      String key = dataSnapshot.getKey();
      messageCallback.onMessageReceived(key, message);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
  };

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

  public void listenForNewMessages(String chatKey) {
    databaseService.chatReference(
      authenticationService.getCurrentUser().getUid()
    ).child(chatKey).child("messages").addChildEventListener(messagesChildEventListener);
  }

  public void stopListeningForNewMessages(String chatKey) {
    databaseService.chatReference(
      authenticationService.getCurrentUser().getUid()
    ).child(chatKey).child("messages").removeEventListener(messagesChildEventListener);
  }

  public void setMessageCallback(MessageCallback messageCallback) {
    this.messageCallback = messageCallback;
  }

  public void sendMessage(Message message, String chatKey) {
    DatabaseReference ref = databaseService.chatReference(
      authenticationService.getCurrentUser().getUid()
    ).child(chatKey).child("messages");
    String messageKey = ref.push().getKey();
    ref.child(messageKey).setValue(message);

    databaseService.chatReference(
      chatKey
    ).child(
      authenticationService.getCurrentUser().getUid()
    ).child("messages").child(messageKey).setValue(message);
  }
}
