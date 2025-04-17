package com.example.eventmanagementapp.model;

public class Schedule {
    private int id;
    private int event_id;
    private String activity_name;
    private String activity_date;
    private String start_time;
    private String end_time;
    private String description;
    private String created_at;

    public Schedule(int event_id, int id, String activity_name, String activity_date, String start_time, String description, String end_time, String created_at) {
        this.event_id = event_id;
        this.id = id;
        this.activity_name = activity_name;
        this.activity_date = activity_date;
        this.start_time = start_time;
        this.description = description;
        this.end_time = end_time;
        this.created_at = created_at;
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

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getActivity_date() {
        return activity_date;
    }

    public void setActivity_date(String activity_date) {
        this.activity_date = activity_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
