package com.example.roofmate_mvp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomePage extends BaseActivity {
    private User user;
    private Button button1, button2, button3, button4, button5, button6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeup);

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

        // Set OnClickListener for buttons
        button1.setOnClickListener(v -> openSwipeActivity("Apartment"));
        button2.setOnClickListener(v -> openSwipeActivity("No apartment"));
        button3.setOnClickListener(v -> openProfileActivity());
        button5.setOnClickListener(v -> openWishlistActivity());
        button6.setOnClickListener(v -> openChatListActivity());
    }

    private void openSwipeActivity(String livingSituation) {
        if (user != null) {
            Intent intent = new Intent(HomePage.this, Swipe.class);
            intent.putExtra("user", user);
            intent.putExtra("livingSituation", livingSituation);
            startActivity(intent);
        } else {
            Toast.makeText(this, "User data not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openChatListActivity() {
        Intent intent = new Intent(HomePage.this, Chatlist.class);
        startActivity(intent);
    }

    private void openProfileActivity() {
        if (user != null) {
            Intent intent = new Intent(HomePage.this, Profile.class);
            intent.putExtra("user", user);
            startActivity(intent);
        } else {
            Toast.makeText(this, "User data not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openWishlistActivity() {
        if (user != null) {
            Intent intent = new Intent(HomePage.this, Wishlist.class);
            intent.putExtra("user", user);
            startActivity(intent);
        } else {
            Toast.makeText(this, "User data not available", Toast.LENGTH_SHORT).show();
        }
    }
}
