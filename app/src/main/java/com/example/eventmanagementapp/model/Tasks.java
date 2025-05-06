package com.example.eventmanagementapp.model;

public class Tasks {
    private int id;
    private int event_id;
    private String task_name;
    private String description;
    private String status;
    private String due_date;
    private int assigned_to;
    private String created_at;

    public Tasks(int id, String created_at, int assigned_to, String due_date, String status, String description, String task_name, int event_id) {
        this.id = id;
        this.created_at = created_at;
        this.assigned_to = assigned_to;
        this.due_date = due_date;
        this.status = status;
        this.description = description;
        this.task_name = task_name;
        this.event_id = event_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public int getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(int assigned_to) {
        this.assigned_to = assigned_to;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
