package com.example.logbook2;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logbook2.Task;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private ArrayList<Task> tasks;
    private TaskDatabaseHelper dbHelper;
    private boolean isTomorrow;
    private boolean hideCompleted = false;

    public TaskAdapter(Context context, ArrayList<Task> tasks, TaskDatabaseHelper dbHelper, boolean isTomorrow) {
        this.tasks = tasks;
        this.dbHelper = dbHelper;
        this.isTomorrow = isTomorrow;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, taskTime;
        CheckBox checkBoxTask;
        Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskTime = itemView.findViewById(R.id.taskTime);
            checkBoxTask = itemView.findViewById(R.id.checkBoxTask);
            deleteButton = itemView.findViewById(R.id.deleteTaskButton);
        }
    }

    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskAdapter.ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.taskName.setText(task.getName());
        holder.taskTime.setText(task.getTime());
        holder.checkBoxTask.setChecked(task.isCompleted());

        holder.checkBoxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            dbHelper.updateTaskCompletion(task.getId(), isChecked);
            if (hideCompleted) {
                tasks.remove(task);
                notifyDataSetChanged();
            }
        });

        holder.taskName.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Edit Task");

            final EditText input = new EditText(v.getContext());
            input.setText(task.getName());
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String newTask = input.getText().toString();
                if (!newTask.isEmpty()) {
                    updateTask(task.getName(), newTask);
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        holder.deleteButton.setOnClickListener(v -> deleteTask(task.getName()));
    }

    @Override
    public int getItemCount() {
        if (hideCompleted) {
            return (int) tasks.stream().filter(task -> !task.isCompleted()).count();
        }
        return tasks.size();
    }

    public void addTask(Task task) {
        tasks.add(task);
        notifyDataSetChanged();
    }

    public void deleteTask(String taskName) {
        tasks.removeIf(t -> t.getName().equals(taskName));
        dbHelper.deleteTask(taskName);
        notifyDataSetChanged();
    }

    public void updateTask(String oldTask, String newTask) {
        for (Task t : tasks) {
            if (t.getName().equals(oldTask)) {
                t.setName(newTask);
                dbHelper.updateTask(oldTask, newTask);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void setHideCompleted(boolean hideCompleted) {
        this.hideCompleted = hideCompleted;
        notifyDataSetChanged();
    }
}