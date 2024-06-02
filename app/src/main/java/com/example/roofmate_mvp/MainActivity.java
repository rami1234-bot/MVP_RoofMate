package com.example.roofmate_mvp;


import android.annotation.SuppressLint;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textView;
    EditText name;
    Button submit_button;
    Button submit_button1;

    FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.name);
        submit_button = findViewById(R.id.submit);
        submit_button1 = findViewById(R.id.submit1);
        name = findViewById(R.id.inputpass);
        submit_button.setOnClickListener(this);
        submit_button1.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    public void signin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(MainActivity.this, HomePage.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
