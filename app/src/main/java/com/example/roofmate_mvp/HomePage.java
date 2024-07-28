package com.example.roofmate_mvp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomePage extends BaseActivity {
    private User user;
    private Button button1, button2, button3, button4, button5, button6, button7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Get the User object from the Intent
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        // Initialize buttons
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);

        // Set OnClickListener for buttons
        button1.setOnClickListener(v -> openSwipeActivity("Has an apartment"));
        button2.setOnClickListener(v -> openSwipeActivity("No apartment"));

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button3 click
                Toast.makeText(HomePage.this, "Button 3 clicked", Toast.LENGTH_SHORT).show();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button4 click
                Toast.makeText(HomePage.this, "Button 4 clicked", Toast.LENGTH_SHORT).show();
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button5 click
                Toast.makeText(HomePage.this, "Button 5 clicked", Toast.LENGTH_SHORT).show();
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button6 click
                Toast.makeText(HomePage.this, "Button 6 clicked", Toast.LENGTH_SHORT).show();
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button7 click
                Toast.makeText(HomePage.this, "Button 7 clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openSwipeActivity(String livingSituation) {
        Intent intent = new Intent(HomePage.this, Swipe.class);
        intent.putExtra("user", user);
        intent.putExtra("livingSituation", livingSituation);
        startActivity(intent);
    }
}
