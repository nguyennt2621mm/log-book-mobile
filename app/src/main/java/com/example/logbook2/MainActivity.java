package com.example.logbook2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.logbook2.R;
import com.example.logbook2.TaskAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView taskList;
    TaskAdapter taskAdapter;
    ArrayList<String> tasks;
    ImageButton addTaskButton;
    Button hideCompletedButton;
    boolean isHideCompleted = false;

    TaskDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TaskDatabaseHelper(this);

        taskList = findViewById(R.id.taskList);
        addTaskButton = findViewById(R.id.addTaskButton);
        hideCompletedButton = findViewById(R.id.hideCompletedButton);

        tasks = dbHelper.getAllTasks(); // Lấy nhiệm vụ từ CSDL

        taskAdapter = new TaskAdapter(this, tasks, dbHelper);
        taskList.setAdapter(taskAdapter);

        addTaskButton.setOnClickListener(v -> showAddTaskDialog());

        hideCompletedButton.setOnClickListener(v -> {
            taskAdapter.toggleHideCompleted();
            isHideCompleted = !isHideCompleted;
            hideCompletedButton.setText(isHideCompleted ? "Unhide Completed" : "Hide Completed");
        });
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTask = input.getText().toString();
                if (!newTask.isEmpty()) {
                    taskAdapter.addTask(newTask);
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}