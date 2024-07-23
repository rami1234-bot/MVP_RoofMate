package com.example.roofmate_mvp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BaseActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
        FirebaseUser currentUser = mAuth.getCurrentUser();

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
        } else if (id == R.id.tool90) {
            navigateTo(Wishlist.class, currentUser);
            return true;
        } else if (id == R.id.logout) {
            if (currentUser != null) {
                updateFcmTokenAndLogout(currentUser.getUid());
            }
            return true;
        } else if (id == R.id.changeinterests) {
            if (currentUser != null) {
                fetchUserAndNavigate(currentUser.getUid());
            }
            return true;
        }  else if (id == R.id.tinder) {
            if (currentUser != null) {
                Intent intent = new Intent(BaseActivity.this, Swipe.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            }
            return true;
        } else if (id == R.id.friendreq) {
            Intent intent = new Intent(BaseActivity.this, RequestListActivity.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);
            return true;
        } else if (id == R.id.chats) {
            if (currentUser != null) {
                Intent intent = new Intent(BaseActivity.this, Chatlist.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void updateFcmTokenAndLogout(String userId) {
        mDatabase.child("users").child(userId).child("fcmToken").setValue("")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mAuth.signOut();
                        Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(BaseActivity.this, "Failed to update FCM token", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserAndNavigate(String userId) {
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User newUser = dataSnapshot.getValue(User.class);
                if (newUser != null) {
                    Intent intent = new Intent(BaseActivity.this, interests.class);
                    intent.putExtra("user", newUser);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(BaseActivity.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BaseActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateTo(Class<?> cls, FirebaseUser currentUser) {
        Intent intent = new Intent(BaseActivity.this, cls);
        if (currentUser != null) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser1 = mAuth.getCurrentUser();
            intent.putExtra("userid", currentUser1.getUid());
        }
        startActivity(intent);
    }
}
