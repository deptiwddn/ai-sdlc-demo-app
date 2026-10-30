package com.example.invoiceapp.auth;

public record UserSession(String username, String role) {
}

class DemoUser {
    private String username;
    private String password;
    private String role;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

class DemoUsersOptions {
    private java.util.List<DemoUser> users = new java.util.ArrayList<>();

    public java.util.List<DemoUser> getUsers() { return users; }
    public void setUsers(java.util.List<DemoUser> users) { this.users = users; }
}

