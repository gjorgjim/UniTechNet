package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

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

  private mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date lastMessageDate;
  private Message lastMessage;

  private int numberOfMessages;

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
    numberOfMessages = 20;
    lastMessage = null;
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

    messagingService.getLastMessages(numberOfMessages, key, new MessageCallback<List<String>, List<Message>>() {
      @Override
      public void onMessageReceived(List<String> keys, List<Message> messages) {
        Collections.sort(messages, new Comparator<Message>() {
          @Override
          public int compare(Message m1, Message m2) {
            if(m1.getSentDate().isAfter(m2.getSentDate())) {
              return -1;
            } else {
              return 1;
            }
          }
        });

        for(int i = messages.size() - 1; i >= 0; i--) {
          setMessage(messages.get(i));
        }
        lastMessageDate = messages.get(0).getSentDate();
        messagingService.removeListenerFromLastMessages(numberOfMessages, key);
        scrollDown();
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
      }
    });

    messagingService.listenForNewMessages(key, new MessageCallback<String, Message>() {
      @Override
      public void onMessageReceived(String key, Message message) {
        if(message.getSentDate().isAfter(lastMessageDate)) {
          setMessage(message);
          scrollDown();
        }
      }
    });

    userNameTv.setText(
      String.format("%s %s",
        chat.getFirstName(),
        chat.getLastName())
    );

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

    sendMessageEt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        scrollDown();
      }
    });

    sendMessageEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        scrollDown();
      }
    });

    return view;
  }


  private void setMessage(Message message){
    if(lastMessage == null) {
      lastMessage = message;

      addDateView(message.getSentDate(), isToday(message.getSentDate().getDay()));
      addMessageView(message);
    } else {
      if(message.getSentDate().getYear() > lastMessage.getSentDate().getYear()) {
        addDateView(message.getSentDate(), false);
        addMessageView(message);
      } else if(message.getSentDate().getMonth() > lastMessage.getSentDate().getMonth()) {
        addDateView(message.getSentDate(), false);
        addMessageView(message);
      } else if(message.getSentDate().getDay() > lastMessage.getSentDate().getDay()) {
        if(isToday(message.getSentDate().getDay())) {
          addDateView(message.getSentDate(), true);
        } else {
          addDateView(message.getSentDate(), false);
        }
        addMessageView(message);
      } else {
        addMessageView(message);
      }

      lastMessage = message;
    }
  }

  private boolean isToday(int day) {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(new Date());

    return calendar.get(Calendar.DAY_OF_MONTH) == day;
  }

  private void addDateView(mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date date, boolean isToday) {
    TextView dateTv = createDateTv(date, isToday);

    messagesView.addView(dateTv);
  }

  private void addMessageView(Message message) {
    TextView messageTv = createMessageTv(message);

    TextView messageDateTv = createMessageDateTv(message);
    messageDateTv.setVisibility(View.GONE);

    messageTv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(messageDateTv.getVisibility() == View.GONE) {
          messageDateTv.setVisibility(View.VISIBLE);
        } else {
          messageDateTv.setVisibility(View.GONE);
        }
      }
    });

    messagesView.addView(messageTv);
    messagesView.addView(messageDateTv);
  }

  private TextView createMessageTv(Message message) {
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

    return messageTv;
  }

  private TextView createDateTv(mk.edu.ukim.feit.gjorgjim.unitechnet.models.user.Date date, boolean isToday) {
    TextView dateTv = new TextView(getContext());

    if(isToday) {
      dateTv.setText(getString(R.string.today_text_view));
    } else {
      dateTv.setText(
        String.format(
          new Locale("en"),
          "%d.%d.%d",
          date.getDay(),
          date.getMonth(),
          date.getYear()
        )
      );
    }

    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.WRAP_CONTENT,
      LinearLayout.LayoutParams.WRAP_CONTENT
    );

    lp.setMargins(0, 4, 0, 4);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      dateTv.setTextAppearance(R.style.DateTextStyle);
    }

    lp.gravity = Gravity.CENTER_HORIZONTAL;

    dateTv.setLayoutParams(lp);

    return dateTv;
  }

  private TextView createMessageDateTv(Message message) {
    TextView messageDateTv = new TextView(getContext());

    messageDateTv.setText(
      String.format(
        new Locale("en"),
        "%s: %d:%d",
        "Sent",
        message.getSentDate().getHour(),
        message.getSentDate().getMinute()
      )
    );

    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.WRAP_CONTENT,
      LinearLayout.LayoutParams.WRAP_CONTENT
    );

    lp.setMargins(0, 2, 8, 2);

    if(message.getSenderId().equals(UID)) {
      lp.gravity = Gravity.END;
    } else {
      lp.gravity = Gravity.START;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      messageDateTv.setTextAppearance(R.style.DateTextStyle);
    }

    messageDateTv.setLayoutParams(lp);

    return messageDateTv;
  }

  private void sendMessage(String value) {
    Message message = new Message();
    message.setSenderId(UID);
    message.setSentDate(getDate());
    message.setValue(value);

    messagingService.sendMessage(message, key);
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

  private void scrollDown() {
    scrollView.post(new Runnable() {
      @Override
      public void run() {
        scrollView.smoothScrollTo(0, messagesView.getBottom());
      }
    });
  }

  @Override
  public void onDestroy() {
    messagingService.stopListeningForNewMessages(key);
    super.onDestroy();
  }
}
