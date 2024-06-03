package com.example.roofmate_mvp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

public class HomeInfo extends AppCompatActivity {

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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle toolbar item clicks
        if (item.getItemId() == android.R.id.home) {



            Intent intent = new Intent(HomeInfo.this, HomeSearch.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
