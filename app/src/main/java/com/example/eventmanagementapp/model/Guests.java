package com.example.eventmanagementapp.model;

public class Guests {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String other_info;
    private String created_at;

    public Guests(int id, String name, String email, String phone, String other_info, String created_at) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.other_info = other_info;
        this.created_at = created_at;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOther_info() {
        return other_info;
    }

    public void setOther_info(String other_info) {
        this.other_info = other_info;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}