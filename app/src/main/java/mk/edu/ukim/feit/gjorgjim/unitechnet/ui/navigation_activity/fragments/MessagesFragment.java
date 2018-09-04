package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.ChatCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.MessagingService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Chat;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Message;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.adapters.MessagesAdapter;

/**
 * Created by gjmarkov on 16.5.2018.
 */

public class MessagesFragment extends Fragment {

  private MessagingService messagingService;
  private AuthenticationService authenticationService;

  private FragmentChangingListener fragmentChangingListener;

  public MessagesFragment() {
    messagingService = MessagingService.getInstance();
    authenticationService = AuthenticationService.getInstance();
  }

  private ListView messagesLv;
  private ProgressBar progressBar;

  private MessagesAdapter adapter;

  private String key = null;

  private List<String> chatKeys;
  private List<Chat> chatList;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    fragmentChangingListener = (FragmentChangingListener) context;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_messages, container, false);

    messagesLv = view.findViewById(R.id.messagesLv);
    progressBar = view.findViewById(R.id.progressBar);

    Bundle args = getArguments();
    if (args != null) {
      key = args.getString("key");
    }

    messagingService.getChat(new ChatCallback() {
      @Override
      public void onSuccess(HashMap<String, Chat> chatHashMap) {
        MessagesFragment.this.chatKeys = new ArrayList<>(chatHashMap.keySet());
        MessagesFragment.this.chatList = new ArrayList<>(chatHashMap.values());
        if(TextUtils.isEmpty(key)) {
          progressBar.setVisibility(View.GONE);
          adapter = new MessagesAdapter(getContext(), R.layout.adapter_messages, MessagesFragment.this.chatKeys, MessagesFragment.this.chatList);
          messagesLv.setAdapter(adapter);
          messagesLv.setVisibility(View.VISIBLE);
          messagingService.removeChatListener();
        } else {
          fragmentChangingListener.changeToUserMessagingFragment(
            chatHashMap.get(key),
            key
          );
        }
      }

      @Override
      public void onFailure(String message) {
        messagingService.removeChatListener();
      }
    });

    messagesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        fragmentChangingListener.changeToUserMessagingFragment(
          (Chat) adapter.getItem(position),
          adapter.getKey(position)
        );
      }
    });

    return view;
  }

  private Message getMessage(String value) {
    Message message = new Message();
    message.setSenderId(authenticationService.getCurrentUser().getUid());
    message.setSentDate(getDate());
    message.setValue(value);

    return message;
  }

  private mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date getDate() {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(new Date());
    mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date date
      = new mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date(calendar.get(Calendar.YEAR),
      calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
      calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

    return date;
  }

  public void updateLastMessage(String key, Message lastMessage) {
    chatList.get(chatKeys.indexOf(key)).setLastMessage(lastMessage);
    adapter.notifyDataSetChanged();
  }
}
