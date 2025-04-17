package com.example.eventmanagementapp.model;

public class Budget {
    private int id;
    private int event_id;
    private float total_budget;
    private float spent_amount;
    private String notes;
    private String created_at;

    public Budget(int id, int event_id, float total_budget, float spent_amount, String notes, String created_at) {
        this.id = id;
        this.event_id = event_id;
        this.total_budget = total_budget;
        this.spent_amount = spent_amount;
        this.notes = notes;
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

    public float getTotal_budget() {
        return total_budget;
    }

    public void setTotal_budget(float total_budget) {
        this.total_budget = total_budget;
    }

    public float getSpent_amount() {
        return spent_amount;
    }

    public void setSpent_amount(float spent_amount) {
        this.spent_amount = spent_amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
