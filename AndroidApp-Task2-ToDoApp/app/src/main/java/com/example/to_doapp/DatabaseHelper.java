package com.example.to_doapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TodoApp.db";
    private static final int DATABASE_VERSION = 2;

    //Users table
    private static final String USERS = "user";
    private static final String USER_ID = "id";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password_hash";

    //Taskas table
    private static final String TASKS = "tasks";
    private static final String TASKS_ID = "id";
    private static final String TASK_USER_ID = "user_id";
    private static final String TASK_NAME = "task_name";
    private static final String TASK_NOTES = "notes";
    private static final String TASK_COMPLETED = "is_completed";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + USERS + "(" +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME + " TEXT NOT NULL, " +
                EMAIL + " TEXT UNIQUE NOT NULL, " +
                PASSWORD + " TEXT NOT NULL)";
        db.execSQL(createUsersTable);

        String createTasksTable = "CREATE TABLE " + TASKS + "(" +
                TASKS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TASK_USER_ID + " INTEGER NOT NULL, " +
                TASK_NAME + " TEXT NOT NULL, " +
                TASK_NOTES + " TEXT, " +
                TASK_COMPLETED + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY (" + TASK_USER_ID + ") REFERENCES " + USERS + "(" + USER_ID + "))";
        db.execSQL(createTasksTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int ytrnewVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + USERS);
        onCreate(db);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean registerUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(EMAIL, email);
        values.put(PASSWORD, hashPassword(password));

        long result = db.insert(USERS, null, values);
        db.close();
        return result != -1;
    }

    public int loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);
        Cursor cursor = db.rawQuery(
                "SELECT " + USER_ID + " FROM " + USERS +
                        " WHERE " + EMAIL + " = ? AND " + PASSWORD + " = ?",
                new String[]{email, hashedPassword});

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return userId;
    }


    public long insertTask(int userId, String taskName, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_USER_ID, userId);
        values.put(TASK_NAME, taskName);
        values.put(TASK_NOTES, notes);
        values.put(TASK_COMPLETED, 0);

        long result = db.insert(TASKS, null, values);
        db.close();
        return result;
    }

    public Cursor getTasksForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT " + TASKS_ID + " AS _id, " + TASK_USER_ID + ", " + TASK_NAME + ", " + TASK_NOTES + ", " + TASK_COMPLETED +
                        " FROM " + TASKS + " WHERE " + TASK_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
    }

    public void setTaskCompleted(int taskId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_COMPLETED, isCompleted ? 1 : 0);
        db.update(TASKS, values, TASKS_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }

    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TASKS, TASKS_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }
}