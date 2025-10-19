package com.thedach.messenger_20;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<List<Message>> messages = new MutableLiveData<>();
    private MutableLiveData<User> otherUser = new MutableLiveData<>();
    private MutableLiveData<Boolean> isMessageSent = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersReference = database.getReference("Users");
    private DatabaseReference messagesReference = database.getReference("Messages");

    private String currentUserId;
    private String otherUserId;

    public ChatViewModel(String currentUserId, String otherUserId) {
        this.currentUserId = currentUserId;
        this.otherUserId = otherUserId;

        usersReference.child(otherUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                otherUser.setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messagesReference.child(currentUserId).child(otherUserId) // так как нам нужна только переписка между пользователм currenUserId и otherUserId
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) { // Сюда прилетает весь список сообщенией по пути который указали выше child(currentUserId).child(otherUserId)
                         List<Message> messageList = new ArrayList<>();
                         for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                             Message message = dataSnapshot.getValue(Message.class);
                             messageList.add(message);
                         }
                         messages.setValue(messageList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public LiveData<String> getError() {
        return error;
    }
    public LiveData<Boolean> getIsMessageSent() {
        return isMessageSent;
    }
    public LiveData<List<Message>> getMessages() {
        return messages;
    }
    public LiveData<User> getOtherUser() {
        return otherUser;
    }

    public void sendMessage(Message message) {
        messagesReference
                .child(message.getSenderId()) // В бд создается запись и внего вкладывается через child
                .child((message.getReceiverId())) // Получается вложеным в таблице для sender'a
                .push()
                .setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        messagesReference
                                .child(message.getReceiverId())
                                .child(message.getSenderId())
                                .push()
                                .setValue(message)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        isMessageSent.setValue(true);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        error.setValue(e.getMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        error.setValue(e.getMessage());
                    }
                });
    }
    public void setUserOnline(boolean isOnline) {
        usersReference.child(currentUserId).child("online").setValue(isOnline);
    }
}
