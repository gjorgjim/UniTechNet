package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import android.app.Activity;
import android.content.Intent;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.ChatCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.MessageCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Chat;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Message;
import mk.edu.ukim.feit.gjorgjim.unitechnet.services.MessagingBackgroundService;

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

  private MessageCallback<String, Message> messageCallback;
  private MessageCallback<List<String>, List<Message>> lastMessagesCallback;

  private MessagingService() {
    databaseService = DatabaseService.getInstance();
    authenticationService = AuthenticationService.getInstance();
  }

  private ValueEventListener lastMessagesValueEventListener = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      List<Message> messages = new ArrayList<>();
      List<String> keys = new ArrayList<>();
      for(DataSnapshot data : dataSnapshot.getChildren()) {
        keys.add(data.getKey());
        messages.add(data.getValue(Message.class));
      }
      lastMessagesCallback.onMessageReceived(keys, messages);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
  };

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

  private HashMap<String, Chat> chatHashMap;
  private ChatCallback chatCallback;
  private ValueEventListener chatValueEventListener = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
        chatHashMap.put(snapshot.getKey(), snapshot.getValue(Chat.class));
      }
      chatCallback.onSuccess(chatHashMap);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
      chatCallback.onFailure(databaseError.getMessage());
    }
  };

  public void getChat(ChatCallback callback) {
    chatHashMap = new HashMap<>();
    chatCallback = callback;

    databaseService.chatReference(
      authenticationService
        .getCurrentUser()
        .getUid()
    ).addListenerForSingleValueEvent(chatValueEventListener);
  }

  public void removeChatListener() {
    databaseService.chatReference(
      authenticationService
        .getCurrentUser()
        .getUid()
    ).removeEventListener(chatValueEventListener);
  }

  public void getLastMessages(int n, String key, MessageCallback callback) {
    lastMessagesCallback = callback;
    databaseService.chatReference(
      authenticationService.getCurrentUser().getUid()
    ).child(key).child("messages").limitToLast(n).addListenerForSingleValueEvent(lastMessagesValueEventListener);
  }

  public void removeListenerFromLastMessages(int n, String key) {
    databaseService.chatReference(
      authenticationService.getCurrentUser().getUid()
    ).child(key).child("messages").limitToLast(n).removeEventListener(lastMessagesValueEventListener);
  }

  public void listenForNewMessages(String chatKey, MessageCallback messageCallback) {
    this.messageCallback = messageCallback;
    databaseService.chatReference(
      authenticationService.getCurrentUser().getUid()
    ).child(chatKey).child("messages").addChildEventListener(messagesChildEventListener);
  }

  public void stopListeningForNewMessages(String chatKey) {
    databaseService.chatReference(
      authenticationService.getCurrentUser().getUid()
    ).child(chatKey).child("messages").removeEventListener(messagesChildEventListener);
  }

  public void sendMessage(Message message, String chatKey) {
    DatabaseReference ref = databaseService.chatReference(
      authenticationService.getCurrentUser().getUid()
    ).child(chatKey).child("messages");
    String messageKey = ref.push().getKey();
    ref.child(messageKey).setValue(message);

    DatabaseReference lastMsgRef = databaseService.chatReference(
      authenticationService.getCurrentUser().getUid()
    ).child(chatKey).child("lastMessage");

    lastMsgRef.removeValue();
    lastMsgRef.setValue(message);

    databaseService.chatReference(
      chatKey
    ).child(
      authenticationService.getCurrentUser().getUid()
    ).child("messages").child(messageKey).setValue(message);

    lastMsgRef = databaseService.chatReference(
      chatKey
    ).child(
      authenticationService.getCurrentUser().getUid()
    ).child("lastMessage");

    lastMsgRef.removeValue();
    lastMsgRef.setValue(message);
  }

  private Intent messagingIntent;

  public void startBackgroundServiceForMessages(Activity activity) {
    messagingIntent = new Intent(activity, MessagingBackgroundService.class);
    messagingIntent.setAction(MessagingBackgroundService.ACTION_START_SERVICE);

    activity.startService(messagingIntent);
  }

  public void stopBackgroundServiceForMessages(Activity activity) {
    activity.stopService(messagingIntent);
  }
}
