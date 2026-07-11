package com.usdctrading.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    /**
     * Register a new user
     * POST /auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register() {
        // Implementation coming soon
        return ResponseEntity.ok("Register endpoint");
    }

    /**
     * Login user
     * POST /auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login() {
        // Implementation coming soon
        return ResponseEntity.ok("Login endpoint");
    }

    /**
     * Logout user
     * POST /auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Implementation coming soon
        return ResponseEntity.ok("Logout endpoint");
    }
}
