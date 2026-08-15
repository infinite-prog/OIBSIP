package com.example.to_doapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText editEmail,editPassword;
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        dbHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        int savedUserId = prefs.getInt("user_id", -1);
        if (savedUserId != -1) {
            goToTaskList(savedUserId);
        }
    }


    public void loginUser(View view) {
        String email    = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if(email.isEmpty()||password.isEmpty()){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = dbHelper.loginUser(email, password);

        if(userId != -1){
            SharedPreferences prefs         = getSharedPreferences("session", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putInt("user_id", userId);
            editor.apply();

            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            goToTaskList(userId);
        }
        else{
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToTaskList(int userId) {
        Intent intent = new Intent (this, TaskListActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
        finish();
    }

    public void goToSignUp(View view) {

        startActivity(new Intent(this,SignUpActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        editEmail.setText("");
        editPassword.setText("");
    }

}
