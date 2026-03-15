package com.example.demo.model;

public enum UserRole {
    ADMIN("admin"),
    USER("user");

    @SuppressWarnings("FieldMayBeFinal")
    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}