package com.example.roofmate_mvp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This will be called in child activities to set the content view
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.tlbr);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (id == R.id.tool1) {
            navigateTo(Profile.class, currentUser);
            return true;
        } else if (id == R.id.tool9) {
            navigateTo(HomePage.class, currentUser);
            return true;
        } else if (id == R.id.tool3) {
            navigateTo(AddHome.class, currentUser);
            return true;
        } else if (id == R.id.tool4) {
            navigateTo(HomeSearch.class, currentUser);
            return true;
        } else if (id == R.id.tool5 || id == R.id.tool13) {
            navigateTo(Usersearch.class, currentUser);
            return true;
        } else if (id == R.id.tool10) {
            navigateTo(OwnHomes.class, currentUser);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void navigateTo(Class<?> cls, FirebaseUser currentUser) {
        Intent intent = new Intent(BaseActivity.this, cls);
        if (currentUser != null) {
            intent.putExtra("userid", currentUser.getUid());
        }
        startActivity(intent);
    }
}
