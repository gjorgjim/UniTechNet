package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class MessagesFragment extends Fragment {

  private MessagingService messagingService;

  private FragmentChangingListener fragmentChangingListener;

  public MessagesFragment() {
    messagingService = MessagingService.getInstance();
  }

  private ListView messagesLv;
  private ProgressBar progressBar;

  private MessagesAdapter adapter;

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

    messagingService.getChat(new ChatCallback() {
      @Override
      public void onSuccess(HashMap<String, Chat> chatHashMap) {
        progressBar.setVisibility(View.GONE);
        adapter = new MessagesAdapter(getContext(), R.layout.adapter_messages, chatHashMap);
        messagesLv.setAdapter(adapter);
        messagesLv.setVisibility(View.VISIBLE);
      }

      @Override
      public void onFailure(String message) {

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
}
