package com.example.invoiceapp.auth;

public enum Role {
    APPROVER("Approver"),
    MANAGER("Manager"),
    DIRECTOR("Director"),
    ADMIN("Admin");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static Role fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (Role role : values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        return null;
    }
}

