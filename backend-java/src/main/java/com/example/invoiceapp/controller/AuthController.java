package com.example.invoiceapp.controller;

import com.example.invoiceapp.auth.SessionStore;
import com.example.invoiceapp.auth.UserSession;
import com.example.invoiceapp.config.DemoUsersProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final DemoUsersProperties usersProperties;
    private final SessionStore sessions;

    public AuthController(DemoUsersProperties usersProperties, SessionStore sessions) {
        this.usersProperties = usersProperties;
        this.sessions = sessions;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        DemoUsersProperties.DemoUser user = usersProperties.getUsers().stream()
                .filter(u -> u.getUsername().equals(request.username()) && u.getPassword().equals(request.password()))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password."));
        }

        String token = sessions.create(new UserSession(user.getUsername(), user.getRole()));
        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getRole()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            sessions.remove(authorization.substring("Bearer ".length()));
        }
        return ResponseEntity.noContent().build();
    }

    public record LoginRequest(String username, String password) {
    }

    public record LoginResponse(String token, String username, String role) {
    }
}

