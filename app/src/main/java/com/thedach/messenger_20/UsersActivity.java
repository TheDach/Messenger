package com.thedach.messenger_20;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private static final String EXTRA_CURRENT_USER_ID = "current_id";

    private UsersViewModel viewModel;
    private RecyclerView recycleViewUsers;
    private UsersAdapter usersAdapter;

    private String currentUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        initViews();
        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        currentUserId = getIntent().getStringExtra(EXTRA_CURRENT_USER_ID);
        usersAdapter.setOnUserClickListener(new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                startActivity(ChatActivity.newIntent(
                        UsersActivity.this,
                        currentUserId,
                        user.getId()
                ));
            }
        });
        observeViewModel();
    }


    private void initViews() {
        recycleViewUsers = findViewById(R.id.recycleViewUsers);
        usersAdapter = new UsersAdapter();
        recycleViewUsers.setAdapter(usersAdapter);
    }
    private void observeViewModel() {
        // Слушатель авторизации, если не авторизован, то прилетит null
        viewModel.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser user) {
                if (user == null) {
                    startActivity(LoginActivity.newIntent(UsersActivity.this));
                    finish(); // что бы уничтожить это активити как пользователь залогинился и не смог вернуться на него пока не разлогинется
                }
            }
        });
        // Слушатель базы данных с пользователями
        viewModel.getListUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> usersFromDb) {
                usersAdapter.setUsers(usersFromDb);
            }
        });
    }


    public static Intent newIntent(Context context, String currentUserId) {
        Intent intent = new Intent(context, UsersActivity.class);
        intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_logout) {
            viewModel.logout();
        }
        return super.onOptionsItemSelected(item);
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




