package com.example.eventmanagementapp.model;

public class EventSuppliers {
    private int id;
    private int event_id;
    private int supplier_id;
    private String contract_details;
    private String created_at;

    public EventSuppliers(int id, int event_id, int supplier_id, String contract_details, String created_at) {
        this.id = id;
        this.event_id = event_id;
        this.supplier_id = supplier_id;
        this.contract_details = contract_details;
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

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getContract_details() {
        return contract_details;
    }

    public void setContract_details(String contract_details) {
        this.contract_details = contract_details;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
