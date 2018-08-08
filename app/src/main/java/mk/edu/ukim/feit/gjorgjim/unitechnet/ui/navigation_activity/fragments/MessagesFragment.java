package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.HashMap;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.callbacks.ChatCallback;
import mk.edu.ukim.feit.gjorgjim.unitechnet.firebase.MessagingService;
import mk.edu.ukim.feit.gjorgjim.unitechnet.models.messaging.Chat;
import mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments.adapters.MessagesAdapter;

/**
 * Created by gjmarkov on 16.5.2018.
 */

public class MessagesFragment extends Fragment implements ChatCallback {

  private MessagingService messagingService;

  public MessagesFragment() {
    messagingService = MessagingService.getInstance();
    messagingService.setChatCallback(this);
  }

  private ListView messagesLv;
  private ProgressBar progressBar;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_messages, container, false);

    messagesLv = view.findViewById(R.id.messagesLv);
    progressBar = view.findViewById(R.id.progressBar);

    messagingService.getChat();

    return view;
  }

  @Override
  public void onSuccess(HashMap<String, Chat> chatHashMap) {
    progressBar.setVisibility(View.GONE);
    MessagesAdapter adapter = new MessagesAdapter(getContext(), R.layout.adapter_messages, chatHashMap);
    messagesLv.setAdapter(adapter);
    messagesLv.setVisibility(View.VISIBLE);
  }

  @Override
  public void onFailure(String message) {

  }
}
