package com.example.roofmate_mvp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.roofmate_mvp.PictureAdapter;
import com.example.roofmate_mvp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeInfo extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView tvHouseName, tvHouseDescription;
    private ListView listViewPictures;
    private Button btnContactOwner;

    private List<String> pictureUrls; // This will store the URLs of the pictures
    private PictureAdapter pictureAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_info);

        // Initialize UI components
        toolbar = findViewById(R.id.toolbar);
        tvHouseName = findViewById(R.id.tv_house_name);
        tvHouseDescription = findViewById(R.id.tv_house_description);
        listViewPictures = findViewById(R.id.listView_pictures);
        btnContactOwner = findViewById(R.id.btn_contact_owner);
        btnContactOwner.setOnClickListener(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Home Information");
        }

        Intent intent = getIntent();
        String homeName = intent.getStringExtra("home_name");
        String homeDescription = intent.getStringExtra("home_description");
        tvHouseName.setText(homeName);
        tvHouseDescription.setText(homeDescription);

        // Sample data for pictures
        pictureUrls = new ArrayList<>();
        pictureUrls.add("https://example.com/pic1.jpg");
        pictureUrls.add("https://example.com/pic2.jpg");
        pictureUrls.add("https://example.com/pic3.jpg");

        // Initialize and set adapter for ListView
        pictureAdapter = new PictureAdapter(this, pictureUrls);
        listViewPictures.setAdapter(pictureAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.tool1) {
            Intent intent = new Intent(HomeInfo.this, HomePage.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.tool3) {
            // Handle action for Tool 3
            return true;
        } else if (id == R.id.tool4) {
            // Handle action for Tool 4
            return true;
        } else if (id == R.id.tool5) {
            // Handle action for Tool 5
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = getIntent();
        String uidoo = intent.getStringExtra("owneruid");

        Intent intent1 = new Intent(HomeInfo.this,Profile.class);
        intent1.putExtra("userId", uidoo);
        startActivity(intent1);
    }


}
