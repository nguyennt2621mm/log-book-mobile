package com.example.logbook2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TaskDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "taskDatabase.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_STATUS = "status"; // 0 = chưa hoàn thành, 1 = đã hoàn thành

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TASKS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_STATUS + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    // Phương thức thêm nhiệm vụ mới
    public void addTask(String taskName, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, taskName);
        values.put(COLUMN_STATUS, isCompleted ? 1 : 0);
        db.insert(TABLE_TASKS, null, values);
        db.close();
    }

    // Phương thức lấy tất cả nhiệm vụ
    public ArrayList<String> getAllTasks() {
        ArrayList<String> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, new String[]{COLUMN_NAME},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                tasks.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tasks;
    }

    // Phương thức lấy trạng thái hoàn thành của tất cả nhiệm vụ
    public ArrayList<Boolean> getAllTaskStatuses() {
        ArrayList<Boolean> statuses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, new String[]{COLUMN_STATUS},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                statuses.add(cursor.getInt(0) == 1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return statuses;
    }

    // Phương thức cập nhật trạng thái của nhiệm vụ
    public void updateTaskStatus(String taskName, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, isCompleted ? 1 : 0);
        db.update(TABLE_TASKS, values, COLUMN_NAME + "=?", new String[]{taskName});
        db.close();
    }
}
