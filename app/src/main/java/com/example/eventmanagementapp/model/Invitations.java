package com.example.eventmanagementapp.model;

public class Invitations {
    private int id;
    private int event_id;
    private int guest_id;
    private String invitation_method;
    private String status;
    private String sent_date;

    public Invitations(int id, int event_id, int guest_id, String invitation_method, String status, String sent_date) {
        this.id = id;
        this.event_id = event_id;
        this.guest_id = guest_id;
        this.invitation_method = invitation_method;
        this.status = status;
        this.sent_date = sent_date;
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

    public String getInvitation_method() {
        return invitation_method;
    }

    public void setInvitation_method(String invitation_method) {
        this.invitation_method = invitation_method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSent_date() {
        return sent_date;
    }

    public void setSent_date(String sent_date) {
        this.sent_date = sent_date;
    }
}
