package com.example.logbook2;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TaskDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_IS_TOMORROW = "isTomorrow";
    private static final String COLUMN_IS_COMPLETED = "isCompleted";

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_IS_TOMORROW + " INTEGER, " +
                COLUMN_IS_COMPLETED + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    // Add task to database
    public long addTask(String taskName, boolean isTomorrow) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, taskName);
        values.put(COLUMN_IS_TOMORROW, isTomorrow ? 1 : 0);
        values.put(COLUMN_IS_COMPLETED, 0);  // Default is not completed
        return db.insert(TABLE_TASKS, null, values);
    }

    // Update task in the database
    public void updateTask(String oldTask, String newTask) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newTask);
        db.update(TABLE_TASKS, values, COLUMN_NAME + "=?", new String[]{oldTask});
    }

    // Update task completion status
    public void updateTaskCompletion(int taskId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_COMPLETED, isCompleted ? 1 : 0);
        db.update(TABLE_TASKS, values, COLUMN_ID + "=?", new String[]{String.valueOf(taskId)});
    }

    // Delete task from database
    public void deleteTask(String taskName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_NAME + "=?", new String[]{taskName});
    }

    // Get tasks based on 'Tomorrow' filter
    public ArrayList<Task> getTasks(boolean isTomorrow) {
        ArrayList<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_IS_TOMORROW},
                COLUMN_IS_TOMORROW + "=?", new String[]{isTomorrow ? "1" : "0"}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                taskList.add(new Task(cursor.getInt(0), cursor.getString(1), "12:42 PM", false));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return taskList;
    }
}