package com.thedach.messenger_20;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersViewModel extends ViewModel {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userReference;

    private MutableLiveData<FirebaseUser> user = new MutableLiveData<>();
    private MutableLiveData<List<User>> listUsers = new MutableLiveData<>();

    public UsersViewModel() {
        auth = FirebaseAuth.getInstance();
        // Слушатель авторизованности пользователя
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user.setValue(firebaseAuth.getCurrentUser());
            }
        });

        database = FirebaseDatabase.getInstance();
        userReference = database.getReference("Users");
        // Слушатель базы данных со свписком пользователй
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser currentUser = auth.getCurrentUser(); // Для того что бы в списке не показываться пользователя который с этого устрйосвта авторизован
                if (currentUser == null) {
                    return;
                }

                List<User> usersFromDb = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    if (user == null) {
                        return;
                    }
                    if (!user.getId().equals(currentUser.getUid())) {
                        usersFromDb.add(user);
                    }
                }
                listUsers.setValue(usersFromDb);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Нужно когда нибудь обработать ошибку
            }
        });
    }

    public MutableLiveData<FirebaseUser> getUser() {
        return user;
    }
    public MutableLiveData<List<User>> getListUsers() {
        return listUsers;
    }

    public void logout() {
        setUserOnline(false);
        auth.signOut();
    }
    public void setUserOnline(boolean isOnline) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            return;
        }
        userReference.child(firebaseUser.getUid()).child("online").setValue(isOnline);
    }
}
