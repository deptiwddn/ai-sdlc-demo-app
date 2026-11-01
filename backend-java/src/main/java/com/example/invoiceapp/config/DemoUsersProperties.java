package com.example.invoiceapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DemoUsersProperties {
    private final List<DemoUser> users = new ArrayList<>(List.of(
            new DemoUser("demo.manager", "ChangeMe123!", "Manager"),
            new DemoUser("demo.director", "ChangeMe123!", "Director"),
            new DemoUser("demo.approver", "ChangeMe123!", "Approver"),
            new DemoUser("demo.admin", "ChangeMe123!", "Admin")
    ));

    public List<DemoUser> getUsers() { return users; }

    public static class DemoUser {
        private String username;
        private String password;
        private String role;

        public DemoUser() {
        }

        public DemoUser(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
