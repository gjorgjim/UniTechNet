package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.MessageCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.AuthenticationService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.MessagingService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Chat;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Message;

/**
 * Created by gjmarkov on 13.8.2018.
 */

public class UserMessagingFragment extends Fragment {

  public static final String LOG_TAG = UserMessagingFragment.class.getSimpleName();

  private MessagingService messagingService;
  private AuthenticationService authenticationService;

  private FragmentChangingListener fragmentChangingListener;

  private String UID;
  private String key;

  private Chat chat;

  private ScrollView scrollView;
  private LinearLayout messagesView;
  private AppCompatTextView userNameTv;
  private AppCompatImageView arrowBackIv;
  private AppCompatEditText sendMessageEt;
  private AppCompatImageView sendMessageIv;
  private ProgressBar progressBar;

  public UserMessagingFragment() {
    messagingService = MessagingService.getInstance();
    authenticationService = AuthenticationService.getInstance();
    UID = authenticationService.getCurrentUser().getUid();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    fragmentChangingListener = (FragmentChangingListener) context;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_user_message, container, false);

    Bundle args = getArguments();
    if (args != null) {
      chat = (Chat) args.getSerializable(Chat.TAG);
      key = args.getString("KEY");
    }

    messagesView = view.findViewById(R.id.messagesViewLl);
    userNameTv = view.findViewById(R.id.userNameTv);
    arrowBackIv = view.findViewById(R.id.backArrowIv);
    sendMessageEt = view.findViewById(R.id.messageEt);
    sendMessageIv = view.findViewById(R.id.sendIv);
    progressBar = view.findViewById(R.id.progressBar);
    scrollView = view.findViewById(R.id.scrollView);

    messagingService.listenForNewMessages(key, new MessageCallback() {
      @Override
      public void onMessageReceived(String messageKey, Message message) {
        if(!chat.getMessages().containsKey(messageKey)) {
          setMessage(message);
          chat.getMessages().put(messageKey, message);
        }
      }
    });

    userNameTv.setText(
      String.format("%s %s",
        chat.getFirstName(),
        chat.getLastName())
    );

    for(Message message : chat.getMessages().values()) {
      setMessage(message);
    }

    scrollView.fullScroll(View.FOCUS_DOWN);
    progressBar.setVisibility(View.GONE);
    scrollView.setVisibility(View.VISIBLE);

    arrowBackIv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        fragmentChangingListener.changeToMessagesFragment();
      }
    });

    sendMessageIv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(!sendMessageEt.getText().toString().isEmpty()) {
          sendMessage(sendMessageEt.getText().toString());
          sendMessageEt.setText("");
        }
      }
    });

    return view;
  }


  private void setMessage(Message message){
    TextView messageTv = new TextView(getContext());

    messageTv.setText(message.getValue());

    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.WRAP_CONTENT,
      LinearLayout.LayoutParams.WRAP_CONTENT
    );

    lp.setMargins(0, 4, 0, 4);

    if(message.getSenderId().equals(UID)) {
      messageTv.setBackground(getResources().getDrawable(R.drawable.bubble_background_receiver));
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        messageTv.setTextAppearance(R.style.MessageTextStyleReceiver);
      }
      lp.gravity = Gravity.END;
    } else {
      messageTv.setBackground(getResources().getDrawable(R.drawable.bubble_background_sender));
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        messageTv.setTextAppearance(R.style.MessageTextStyleReceiver);
      }
      lp.gravity = Gravity.START;
    }

    messageTv.setLayoutParams(lp);

    messagesView.addView(messageTv);
  }

  private void sendMessage(String value) {
    Message message = new Message();
    message.setSenderId(UID);
    message.setSentDate("ad2123d");
    message.setValue(value);

    messagingService.sendMessage(message, key);
  }

  @Override
  public void onDestroy() {
    messagingService.stopListeningForNewMessages(key);
    super.onDestroy();
  }
}
