package com.example.eventmanagementapp.model;

public class Events {
    private int id;
    private int user_id;
    private String event_name;
    private String event_date;
    private String start_time;
    private String end_time;
    private int venue_id;
    private String description;
    private String created_at;

    public Events(int id, int user_id, String event_name, String event_date, String start_time, String end_time, int venue_id, String description, String created_at) {
        this.id = id;
        this.user_id = user_id;
        this.event_name = event_name;
        this.event_date = event_date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.venue_id = venue_id;
        this.description = description;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
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

    public int getVenue_id() {
        return venue_id;
    }

    public void setVenue_id(int venue_id) {
        this.venue_id = venue_id;
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
