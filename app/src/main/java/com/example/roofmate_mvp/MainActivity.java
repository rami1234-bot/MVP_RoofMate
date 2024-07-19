package com.example.roofmate_mvp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;

import me.pushy.sdk.Pushy;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    Button submit_button;
    Button submit_button1;

    FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
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

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (v == submit_button) {
            Intent intent = new Intent(this, Signin.class);
            startActivity(intent);
        }
        if (v == submit_button1) {
            Intent intent = new Intent(this, Signup.class);
            startActivity(intent);
        }
    }
}
