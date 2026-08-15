package com.example.to_doapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText editName, editEmail, editPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editName = findViewById(R.id.fullname);
        editEmail = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);
        dbHelper = new DatabaseHelper(this);
    }

    public void registerUser(View view) {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.registerUser(name, email, password);

        if (success) {
            Toast.makeText(this, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();
            finish(); // back to LoginActivity
        } else {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
        }
    }
}
