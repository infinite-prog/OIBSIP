package com.example.to_doapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TaskListActivity extends AppCompatActivity {

    private int userId;
    private DatabaseHelper dbHelper;
    private ListView listTasks;
    private TextView txtEmptyState;
    private TaskCursorAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        dbHelper      = new DatabaseHelper(this);
        listTasks     = findViewById(R.id.listTasks);
        txtEmptyState = findViewById(R.id.txtEmptyState);
        userId = getIntent().getIntExtra("user_id", -1);


        if(userId == -1){
            finish();
            return;

        }

        loadTasks();
    }

    private void loadTasks() {
        Cursor cursor = dbHelper.getTasksForUser(userId);

        if (cursor.getCount() == 0) {
            txtEmptyState.setVisibility(View.VISIBLE);
            listTasks.setVisibility(View.GONE);
        } else {
            txtEmptyState.setVisibility(View.GONE);
            listTasks.setVisibility(View.VISIBLE);
        }

        adapter = new TaskCursorAdapter(this, cursor, new TaskCursorAdapter.TaskActionListener() {
            @Override
            public void onCompleteClicked(int taskId, boolean currentlyCompleted) {
                dbHelper.setTaskCompleted(taskId, !currentlyCompleted);
                loadTasks();
            }

            @Override
            public void onDeleteClicked(int taskId) {
                new AlertDialog.Builder(TaskListActivity.this)
                        .setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            dbHelper.deleteTask(taskId);
                            loadTasks();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        listTasks.setAdapter(adapter);
    }

    public void showAddTaskDialog(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.diag_add_task, null);

        EditText editTaskName = dialogView.findViewById(R.id.editTaskName);
        EditText editTaskNotes = dialogView.findViewById(R.id.editTaskNotes);

        new AlertDialog.Builder(this)
                .setTitle("Add New Task")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String taskName = editTaskName.getText().toString().trim();
                    String notes = editTaskNotes.getText().toString().trim();

                    if (taskName.isEmpty()) {
                        Toast.makeText(this, "Task name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    dbHelper.insertTask(userId, taskName, notes);
                    loadTasks();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void logoutUser(View view) {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        prefs.edit().clear().apply();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}