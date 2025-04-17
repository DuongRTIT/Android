package com.example.eventmanagementapp.model;

public class Suppliers {
    private int id;
    private String supplier_name;
    private String service_type;
    private String contact_info;
    private String other_details;
    private String created_at;

    public Suppliers(int id, String supplier_name, String service_type, String contact_info, String other_details, String created_at) {
        this.id = id;
        this.supplier_name = supplier_name;
        this.service_type = service_type;
        this.contact_info = contact_info;
        this.other_details = other_details;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public String getContact_info() {
        return contact_info;
    }

    public void setContact_info(String contact_info) {
        this.contact_info = contact_info;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
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
