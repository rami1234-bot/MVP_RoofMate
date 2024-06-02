package com.example.roofmate_mvp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MessageAdapt extends ArrayAdapter<Message> {
    private Context context;
    private int resource;


    public MessageAdapt(@NonNull Context context, int resource, ArrayList<Message> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(this.context).inflate(this.resource, parent, false);
        }


        Message m = getItem(position);
        if(m != null){

            TextView userAndMessageTextView = convertView.findViewById(R.id.user_and_message);
            String userName = m.getSenderid();
            String messageText = m.getContent();
            String userAndMessage = userName + ": " + messageText;
            userAndMessageTextView.setText(userAndMessage);


        }

        return convertView;

    }
}


