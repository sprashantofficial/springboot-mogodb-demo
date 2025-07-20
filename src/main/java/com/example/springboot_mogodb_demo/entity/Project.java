package com.example.springboot_mogodb_demo.entity;

public class Project {

    private String name;

    private int allocation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAllocation() {
        return allocation;
    }

    public void setAllocation(int allocation) {
        this.allocation = allocation;
    }
}
