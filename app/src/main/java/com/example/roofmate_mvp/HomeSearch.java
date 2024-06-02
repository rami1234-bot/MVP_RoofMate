package com.example.roofmate_mvp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class HomeSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = findViewById(R.id.tlbr);
        setSupportActionBar(toolbar);






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
            // Handle action for Tool 2
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