package tn.esprit.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.userservice.service.KeycloakUserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final KeycloakUserService userService;

    public UserController(KeycloakUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        var user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail()
        ));
    }


    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    public record CreateUserRequest(
            String username,
            String email,
            String firstName,
            String lastName,
            String password
    ) {
    }

    @PostMapping("/add")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            userService.createUser(
                    request.username(),
                    request.email(),
                    request.firstName(),
                    request.lastName(),
                    request.password()
            );
            return ResponseEntity.status(201).body(Map.of("message", "Utilisateur créé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/me")
    public Object getCurrentUser(@AuthenticationPrincipal Jwt jwt) {

        return Map.of(
                "id", jwt.getSubject(),
                "username", jwt.getClaimAsString("preferred_username"),
                "firstName", jwt.getClaimAsString("given_name"),
                "lastName", jwt.getClaimAsString("family_name"),
                "email", jwt.getClaimAsString("email"),
                "roles", jwt.getClaimAsMap("realm_access").get("roles")
        );
    }

    @GetMapping("/{id}/email")
    public ResponseEntity<String> getUserEmailById(@PathVariable String id) {
        String email = userService.getUserEmailById(id);
        return ResponseEntity.ok(email);
    }

}
