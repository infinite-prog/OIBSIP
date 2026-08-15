package com.example.to_doapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class TaskCursorAdapter extends CursorAdapter {

    // Interface so TaskListActivity can react to button taps
    public interface TaskActionListener {
        void onCompleteClicked(int taskId, boolean currentlyCompleted);
        void onDeleteClicked(int taskId);
    }

    private final TaskActionListener listener;

    public TaskCursorAdapter(Context context, Cursor cursor, TaskActionListener listener) {
        super(context, cursor, 0);
        this.listener = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView txtTaskName = view.findViewById(R.id.txtTaskName);
        TextView txtTaskNotes = view.findViewById(R.id.txtTaskNotes);
        Button btnComplete = view.findViewById(R.id.btnComplete);
        Button btnDelete = view.findViewById(R.id.btnDelete);

        int taskId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String taskName = cursor.getString(cursor.getColumnIndexOrThrow("task_name"));
        String notes = cursor.getString(cursor.getColumnIndexOrThrow("notes"));
        int isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed"));

        txtTaskName.setText(taskName);
        txtTaskNotes.setText(notes);

        if (isCompleted == 1) {
            txtTaskName.setPaintFlags(txtTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            btnComplete.setBackgroundColor(Color.parseColor("#4CAF50")); // green
            btnComplete.setText("Done");
        } else {
            txtTaskName.setPaintFlags(txtTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            btnComplete.setBackgroundColor(Color.parseColor("#E0E0E0")); // default grey
            btnComplete.setText("Complete");
        }

        boolean finalIsCompleted = isCompleted == 1;
        btnComplete.setOnClickListener(v -> listener.onCompleteClicked(taskId, finalIsCompleted));
        btnDelete.setOnClickListener(v -> listener.onDeleteClicked(taskId));
    }
}