package com.thedach.messenger_20;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    private List<User> users = new ArrayList<>();
    private OnUserClickListener onUserClickListener;

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }
    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.user_item,
                parent,
                false
        );
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user = users.get(position);

        String userInfo = String.format("%s %s, %s", user.getName(), user.getLastName(), user.getAge());
        holder.textViewUserInfo.setText(userInfo);

        int bgResId;
        if (user.isOnline()) {
            bgResId = R.drawable.circle_green;
        } else {
            bgResId = R.drawable.circle_red;
        }

        Drawable background = ContextCompat.getDrawable(holder.itemView.getContext(), bgResId);
        holder.viewStatusOnline.setBackground(background);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onUserClickListener != null) {
                    onUserClickListener.onUserClick(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    interface OnUserClickListener {
        void onUserClick(User user);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewUserInfo;
        private View viewStatusOnline;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserInfo = itemView.findViewById(R.id.textViewUserInfo);
            viewStatusOnline = itemView.findViewById(R.id.viewStatusOnline);
        }
    }
}
