package com.example.roofmate_mvp;

import android.content.Intent;
import android.os.Bundle;

public class HomePage extends BaseActivity {
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Get the User object from the Intent
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
    }
}
