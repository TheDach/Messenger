package com.thedach.messenger_20;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String EXTRA_CURRENT_USER_ID = "current_id";
    private static final String EXTRA_OTHER_USER_ID = "other_id";

    private TextView textViewTitle;
    private View viewStatusOnline;
    private RecyclerView recycleViewMessage;
    private EditText editTextMessage;
    private ImageView imageViewSend;

    private MessagesAdapter adapter;
    private ChatViewModel viewModel;
    private ChatViewModelFactory factory;

    private String currentUserId;
    private String otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();

        currentUserId = getIntent().getStringExtra(EXTRA_CURRENT_USER_ID);
        otherUserId = getIntent().getStringExtra(EXTRA_OTHER_USER_ID);

        factory = new ChatViewModelFactory(currentUserId, otherUserId);
        viewModel = new ViewModelProvider(this, factory).get(ChatViewModel.class);

        adapter = new MessagesAdapter(currentUserId);
        recycleViewMessage.setAdapter(adapter);

        observeViewModel();
        setupClickListeners();
    }


    private void observeViewModel() {
        viewModel.getMessages().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                adapter.setMessages(messages);
            }
        });

        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null) {
                    Toast.makeText(ChatActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getIsMessageSent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSent) {
                if (isSent) {
                    editTextMessage.setText("");
                }
            }
        });

        viewModel.getOtherUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                String userInfo = String.format("%s %s", user.getName(), user.getLastName());
                textViewTitle.setText(userInfo);

                int bgResId;
                if (user.isOnline()) {
                    bgResId = R.drawable.circle_green;
                } else {
                    bgResId = R.drawable.circle_red;
                }

                Drawable background = ContextCompat.getDrawable(ChatActivity.this, bgResId);
                viewStatusOnline.setBackground(background);
            }
        });
    }
    private void setupClickListeners() {
        imageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message(
                        otherUserId,
                        currentUserId,
                        editTextMessage.getText().toString().trim()
                );
                viewModel.sendMessage(message);
            }
        });
    }
    private void initViews() {
        textViewTitle = findViewById(R.id.textViewTitle);
        viewStatusOnline = findViewById(R.id.viewStatusOnline);
        recycleViewMessage = findViewById(R.id.recycleViewMyMessage);
        editTextMessage = findViewById(R.id.editTextMessage);
        imageViewSend = findViewById(R.id.imageViewSend);
    }

    public static Intent newIntent(Context context, String currentUserId, String otherUserId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId);
        intent.putExtra(EXTRA_OTHER_USER_ID, otherUserId);
        return intent;
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.setUserOnline(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.setUserOnline(false);
    }
}




