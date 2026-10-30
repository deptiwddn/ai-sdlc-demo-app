package com.example.invoiceapp.auth;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionStore {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final Map<String, UserSession> sessions = new ConcurrentHashMap<>();

    public String create(UserSession session) {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        String token = java.util.HexFormat.of().formatHex(bytes);
        sessions.put(token, session);
        return token;
    }

    public UserSession get(String token) {
        return token != null ? sessions.get(token) : null;
    }

    public void remove(String token) {
        if (token != null) {
            sessions.remove(token);
        }
    }
}

