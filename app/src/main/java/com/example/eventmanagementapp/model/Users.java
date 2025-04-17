package com.example.eventmanagementapp.model;

public class Users {
    private int id;
    private String username;
    private String email;
    private String password;
    private String other_info;
    private String created_at;

    public Users(int id, String username, String email, String password, String other_info, String created_at) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.other_info = other_info;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
