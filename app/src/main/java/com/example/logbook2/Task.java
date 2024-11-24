package com.example.logbook2;

public class Task {
    private int id;
    private String name;
    private String time;
    private boolean isCompleted;

    public Task(int id, String name, String time, boolean isCompleted) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.isCompleted = isCompleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
