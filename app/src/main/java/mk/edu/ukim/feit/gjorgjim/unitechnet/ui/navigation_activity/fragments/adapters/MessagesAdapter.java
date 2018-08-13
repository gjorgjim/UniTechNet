package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Chat;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Message;

/**
 * Created by gjmarkov on 08.8.2018.
 */

public class MessagesAdapter extends ArrayAdapter {

  private List<Chat> chatList;
  private List<String> chatKeys;

  private AppCompatTextView nameTv;
  private CircleImageView profilePictureIv;
  private AppCompatTextView lastMsgTv;
  private AppCompatTextView lastMsgTimeTv;

  public MessagesAdapter(@NonNull Context context, int resource, HashMap<String, Chat> chat) {
    super(context, resource);
    chatList = new ArrayList<>(chat.values());
    chatKeys = new ArrayList<>(chat.keySet());
    Log.d("MessagesAdapter", chatList.size() + "");
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View view;

    if(convertView == null) {
      view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_messages, parent, false);
    } else {
      view = convertView;
    }

    nameTv = view.findViewById(R.id.userNameMsgTv);
    lastMsgTv = view.findViewById(R.id.lastMessageTv);
    lastMsgTimeTv = view.findViewById(R.id.lastMessageTimeTv);
    profilePictureIv = view.findViewById(R.id.profilePictureIv);

    Chat currentChat = chatList.get(position);
    String currentKey = chatKeys.get(position);

    nameTv.setText(String.format(
      new Locale("en"),
      "%s %s",
      currentChat.getFirstName(),
      currentChat.getLastName())
    );

    Message lastMessage = getLastMessage(currentChat);

    lastMsgTv.setText(String.format("%s..", lastMessage.getValue()));
    lastMsgTimeTv.setText(lastMessage.getSentDate());

    //TODO: Get profile picture from Firebase storage using currentKey

    return view;
  }

  @Override
  public int getCount() {
    return chatList.size();
  }

  @Nullable
  @Override
  public Object getItem(int position) {
    return chatList.get(position);
  }

  public String getKey(int position) {
    return chatKeys.get(position);
  }

  private Message getLastMessage(Chat chat) {
    List<Message> messages = new ArrayList<>(chat.getMessages().values());
    return messages.get(messages.size() - 1);
  }
}
