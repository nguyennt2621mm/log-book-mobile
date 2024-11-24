package com.example.logbook2;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewToday, recyclerViewTomorrow;
    private TaskAdapter taskAdapterToday, taskAdapterTomorrow;
    private TaskDatabaseHelper dbHelper;
    private boolean hideCompleted = false;
    private ArrayList<Task> tasksToday, tasksTomorrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TaskDatabaseHelper(this);

        recyclerViewToday = findViewById(R.id.recyclerViewToday);
        recyclerViewTomorrow = findViewById(R.id.recyclerViewTomorrow);
        ImageButton fab = findViewById(R.id.fab);

        recyclerViewToday.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTomorrow.setLayoutManager(new LinearLayoutManager(this));

        tasksToday = dbHelper.getTasks(false);
        tasksTomorrow = dbHelper.getTasks(true);

        taskAdapterToday = new TaskAdapter(this, tasksToday, dbHelper, false);
        taskAdapterTomorrow = new TaskAdapter(this, tasksTomorrow, dbHelper, true);

        recyclerViewToday.setAdapter(taskAdapterToday);
        recyclerViewTomorrow.setAdapter(taskAdapterTomorrow);

        TextView hideCompletedButton = findViewById(R.id.hideCompleted);
        hideCompletedButton.setOnClickListener(v -> {
            hideCompleted = !hideCompleted;
            hideCompletedButton.setText(hideCompleted ? "Show completed" : "Hide completed");
            taskAdapterToday.setHideCompleted(hideCompleted);
            taskAdapterTomorrow.setHideCompleted(hideCompleted);
        });

        fab.setOnClickListener(view -> showAddTaskDialog());
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");

        final EditText input = new EditText(this);
        input.setHint("Enter task name");

        final RadioGroup dayGroup = new RadioGroup(this);
        RadioButton todayButton = new RadioButton(this);
        todayButton.setText("Today");
        RadioButton tomorrowButton = new RadioButton(this);
        tomorrowButton.setText("Tomorrow");
        dayGroup.addView(todayButton);
        dayGroup.addView(tomorrowButton);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(input);
        layout.addView(dayGroup);
        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newTask = input.getText().toString();
            if (!newTask.isEmpty()) {
                boolean isTomorrowTask = dayGroup.getCheckedRadioButtonId() == tomorrowButton.getId();
                long taskId = dbHelper.addTask(newTask, isTomorrowTask);  // Get the generated ID
                Task task = new Task((int) taskId, newTask, "12:42 PM", false);  // Create Task with ID
                if (isTomorrowTask) {
                    taskAdapterTomorrow.addTask(task);
                } else {
                    taskAdapterToday.addTask(task);
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}