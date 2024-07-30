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
        button1.setVisibility(View.VISIBLE);
        button1.setBackgroundColor(Color.TRANSPARENT);
        button2.setVisibility(View.VISIBLE);
        button2.setBackgroundColor(Color.TRANSPARENT);
        button3.setVisibility(View.VISIBLE);
        button3.setBackgroundColor(Color.TRANSPARENT);
        button4.setVisibility(View.VISIBLE);
        button4.setBackgroundColor(Color.TRANSPARENT);
        button5.setVisibility(View.VISIBLE);
        button5.setBackgroundColor(Color.TRANSPARENT);
        button6.setVisibility(View.VISIBLE);
        button6.setBackgroundColor(Color.TRANSPARENT);
        // Set OnClickListener for buttons
        button1.setOnClickListener(v -> openSwipeActivity("Apartment"));

        button2.setOnClickListener(v -> openSwipeActivity("No apartment"));

        button3.setOnClickListener(v -> openProfileActivity());

        button4.setOnClickListener(v -> openWishlistActivity());

        button5.setOnClickListener(v -> Toast.makeText(HomePage.this, "Button 5 clicked", Toast.LENGTH_SHORT).show());

        button6.setOnClickListener(v -> Toast.makeText(HomePage.this, "Button 6 clicked", Toast.LENGTH_SHORT).show());
    }

    private void openSwipeActivity(String livingSituation) {
        Intent intent = new Intent(HomePage.this, Swipe.class);
        intent.putExtra("user", user);
        intent.putExtra("livingSituation", livingSituation);
        startActivity(intent);
    }

    private void openProfileActivity() {
        Intent intent = new Intent(HomePage.this, Profile.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void openWishlistActivity() {
        Intent intent = new Intent(HomePage.this, Wishlist.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
