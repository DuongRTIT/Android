package com.example.eventmanagementapp.model;

public class Venues {
    private int id;
    private String venue_name;
    private String address;
    private String phone;
    private float price;
    private String other_details;
    private String created_at;

    public Venues(int id, String venue_name, String address, String phone, float price, String other_details, String created_at) {
        this.id = id;
        this.venue_name = venue_name;
        this.address = address;
        this.phone = phone;
        this.price = price;
        this.other_details = other_details;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVenue_name() {
        return venue_name;
    }

    public void setVenue_name(String venue_name) {
        this.venue_name = venue_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getOther_details() {
        return other_details;
    }

    public void setOther_details(String other_details) {
        this.other_details = other_details;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
