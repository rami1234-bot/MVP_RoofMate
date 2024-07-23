package com.example.roofmate_mvp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.pushy.sdk.Pushy;
import me.pushy.sdk.util.exceptions.PushyException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textView;
    EditText name;
    Button submit_button;
    Button submit_button1;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Pushy.listen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.name);
        submit_button = findViewById(R.id.submit);
        submit_button1 = findViewById(R.id.submit1);
        name = findViewById(R.id.inputpass);
        submit_button.setOnClickListener(this);
        submit_button1.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void signin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();
                                new RegisterForPushyAndSaveTokenTask(userId).execute();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private class RegisterForPushyAndSaveTokenTask extends AsyncTask<Void, Void, String> {
        private String userId;

        public RegisterForPushyAndSaveTokenTask(String userId) {
            this.userId = userId;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                // Register the device for push notifications
                return Pushy.register(getApplicationContext());
            } catch (PushyException e) {
                // Registration failed
                return null;
            }
        }

        @Override
        protected void onPostExecute(String pushyToken) {
            if (pushyToken != null) {
                // Retrieve user data from Firebase
                mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            // Update the FCM token
                            user.setFcmToken(pushyToken);

                            // Save the updated user data back to the database
                            mDatabase.child("users").child(userId).setValue(user)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(MainActivity.this, HomePage.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(MainActivity.this, "Failed to update FCM token.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(MainActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "Pushy registration failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == submit_button) {
            String email = ((TextView) findViewById(R.id.email)).getText().toString();
            String password = ((TextView) findViewById(R.id.inputpass)).getText().toString();
            signin(email, password);
        }
        if (v == submit_button1) {
            Intent intent = new Intent(this, Signup.class);
            startActivity(intent);
        }
    }
}
