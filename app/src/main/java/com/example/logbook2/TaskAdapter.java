package com.example.logbook2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.logbook2.R;

import java.util.ArrayList;

public class TaskAdapter extends ArrayAdapter<String> {
    private ArrayList<String> tasks;
    private ArrayList<Boolean> taskStatus;
    private ArrayList<String> filteredTasks;
    private boolean hideCompletedTasks = false;
    private TaskDatabaseHelper dbHelper;

    public TaskAdapter(Context context, ArrayList<String> tasks, TaskDatabaseHelper dbHelper) {
        super(context, 0, tasks);
        this.dbHelper = dbHelper;
        this.tasks = tasks;
        this.taskStatus = dbHelper.getAllTaskStatuses();  // Lấy trạng thái từ cơ sở dữ liệu
        this.filteredTasks = new ArrayList<>(tasks);
    }

    @Override
    public int getCount() {
        return filteredTasks.size();
    }

    @Override
    public String getItem(int position) {
        return filteredTasks.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }

        CheckBox checkBox = convertView.findViewById(R.id.taskCheckBox);
        TextView taskName = convertView.findViewById(R.id.taskName);

        String task = filteredTasks.get(position);
        taskName.setText(task);

        int originalPosition = tasks.indexOf(task);
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(taskStatus.get(originalPosition));

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            taskStatus.set(originalPosition, isChecked);
            dbHelper.updateTaskStatus(task, isChecked);  // Cập nhật trạng thái trong CSDL
            filterTasks();
        });

        return convertView;
    }

    public void addTask(String task) {
        tasks.add(task);
        taskStatus.add(false);
        dbHelper.addTask(task, false);  // Thêm nhiệm vụ vào CSDL
        filterTasks();
    }

    public void toggleHideCompleted() {
        hideCompletedTasks = !hideCompletedTasks;
        filterTasks();
    }

    private void filterTasks() {
        filteredTasks.clear();
        for (int i = 0; i < tasks.size(); i++) {
            if (!hideCompletedTasks || !taskStatus.get(i)) {
                filteredTasks.add(tasks.get(i));
            }
        }
        notifyDataSetChanged();
    }
}

