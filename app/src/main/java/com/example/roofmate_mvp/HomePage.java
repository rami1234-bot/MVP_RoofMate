package com.example.roofmate_mvp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {

//
//    private ListView listView;
//    private ArrayList<User> messages;
//    private MsAdapt messageAdapter;
//    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = findViewById(R.id.tlbr);
        setSupportActionBar(toolbar);

//
//        listView = findViewById(R.id.list_view);
//        messages = new ArrayList<>();
//        mDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(mentalIllness); // Reference the node for the specific mental illness
//
//        messageAdapter = new MsAdapt(this, R.layout.rows, messages);
//        listView.setAdapter(messageAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.tool1) {
            // Handle action for Tool 1
            return true;
        } else if (id == R.id.tool2) {
            Intent intent = new Intent(HomePage.this, Profile.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("uid",currentUser.getUid() );
            startActivity(intent);
            return true;
        } else if (id == R.id.tool3) {
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
}