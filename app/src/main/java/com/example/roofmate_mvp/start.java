package com.example.roofmate_mvp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import me.pushy.sdk.Pushy;

public class start extends AppCompatActivity implements View.OnClickListener {

    Button submit_button;
    Button submit_button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Pushy.listen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home2);

        submit_button = findViewById(R.id.submit);
        submit_button1 = findViewById(R.id.submit1);

        submit_button.setVisibility(View.VISIBLE);
        submit_button.setBackgroundColor(Color.TRANSPARENT);
        submit_button1.setVisibility(View.VISIBLE);
        submit_button1.setBackgroundColor(Color.TRANSPARENT);

        // Set click listeners
        submit_button.setOnClickListener(this);
        submit_button1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == submit_button) {
            Intent yes = new Intent(this, Signin.class);
            startActivity(yes);
        } else if (v == submit_button1) {
            Intent intent = new Intent(this, Signup.class);
            startActivity(intent);
        }
    }
}
