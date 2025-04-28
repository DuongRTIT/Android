package com.example.eventmanagementapp.model;

public class Feedback {
    private int id;
    private int event_id;
    private int guest_id;
    private int rating;
    private String comment;
    private String feedback_date;
    private String created_at;

    public Feedback(int id, int event_id, int guest_id, int rating, String comment, String feedback_date, String created_at) {
        this.id = id;
        this.event_id = event_id;
        this.guest_id = guest_id;
        this.rating = rating;
        this.comment = comment;
        this.feedback_date = feedback_date;
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

    public int getGuest_id() {
        return guest_id;
    }

    public void setGuest_id(int guest_id) {
        this.guest_id = guest_id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFeedback_date() {
        return feedback_date;
    }

    public void setFeedback_date(String feedback_date) {
        this.feedback_date = feedback_date;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
