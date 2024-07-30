package com.example.roofmate_mvp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signin extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignIn, goBackButton; // Added goBackButton

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin2);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        goBackButton = findViewById(R.id.goBackButton); // Assuming you have a goBackButton in your layout
        goBackButton.setVisibility(View.VISIBLE);
        goBackButton.setBackgroundColor(Color.TRANSPARENT);
        buttonSignIn.setVisibility(View.VISIBLE);
        buttonSignIn.setBackgroundColor(Color.TRANSPARENT);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                signIn(email, password);
            }
        });

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle go back action if needed
                finish(); // Close the activity
            }
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(Signin.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(Signin.this, HomePage.class);
                            startActivity(intent);
                            // You can add further actions here, like opening another activity
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Signin.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}