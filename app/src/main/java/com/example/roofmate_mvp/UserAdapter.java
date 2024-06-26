package com.example.roofmate_mvp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.roofmate_mvp.R;
import com.example.roofmate_mvp.User;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {

    private List<User> users;
    private OnUserClickListener onUserClickListener;

    // Interface for handling click events
    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    // Setter method for setting the click listener
    public void setOnUserClickListener(OnUserClickListener listener) {
        this.onUserClickListener = listener;
    }

    public UserAdapter(Context context, List<User> users) {
        super(context, 0, users);
        this.users = users;
    }

    public void setUsers(List<User> users) {
        this.users.clear();
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        TextView usernameTextView = convertView.findViewById(R.id.usernameTextView);
        TextView emailTextView = convertView.findViewById(R.id.emailTextView);

        usernameTextView.setText(user.getUsername());
        emailTextView.setText(user.getEmail());

        // Set onClickListener for the convertView
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onUserClickListener != null) {
                    onUserClickListener.onUserClick(user);
                }
            }
        });

        return convertView;
    }
}
