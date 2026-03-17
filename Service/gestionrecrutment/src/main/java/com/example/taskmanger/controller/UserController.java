package com.example.taskmanger.controller;

import com.example.taskmanger.model.User;
import com.example.taskmanger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        // Input validation
        if (user.getName() == null || user.getName().isEmpty() ||
            user.getEmail() == null || user.getEmail().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Name, email, and password are required.");
        }
        
        // Check if user with this email already exists
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("User with email " + user.getEmail() + " already exists. Please use a different email or try logging in.");
        }
        
        // Validate role
        if (user.getRole() == null) {
            user.setRole(com.example.taskmanger.model.Roles.CANDIDATE);
        }
        
        try {
            User created = userService.createUser(user);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            e.printStackTrace(); // Log to server console
            String errorMsg = e.getMessage();
            return ResponseEntity.status(500).body("Signup failed: " + errorMsg);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Optional<User> userOpt = userService.findByEmail(loginRequest.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean passwordMatch = userService.checkPassword(loginRequest.getPassword(), user.getPassword());
            if (passwordMatch) {
                // Return user id, email, name, and role (and dummy token for compatibility)
                return ResponseEntity.ok(Map.of(
                    "token", "dummy-token",
                    "user", Map.of(
                        "id", user.getId(),
                        "email", user.getEmail() != null ? user.getEmail() : "",
                        "name", user.getName() != null ? user.getName() : "",
                        "role", user.getRole() != null ? user.getRole().name() : "CANDIDATE"
                    )
                ));
            }
        }
        return ResponseEntity.status(401).body("Invalid email or password");
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        // No default role override, allow admin to set any role
        try {
            User created = userService.createUser(user);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            return ResponseEntity.status(500).body("User creation failed: " + errorMsg);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        return userService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}