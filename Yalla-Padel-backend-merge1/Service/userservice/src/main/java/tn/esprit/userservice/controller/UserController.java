package tn.esprit.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.userservice.service.KeycloakUserService;

import java.util.LinkedHashMap;
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
        try {
            var user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("firstName", user.getFirstName() != null ? user.getFirstName() : "");
            response.put("lastName", user.getLastName() != null ? user.getLastName() : "");
            response.put("email", user.getEmail() != null ? user.getEmail() : "");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<Map<String, Object>>> getUsersByRole(@PathVariable String roleName) {
        return ResponseEntity.ok(userService.getUsersByRole(roleName));
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
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        Object roles = realmAccess != null ? realmAccess.get("roles") : List.of();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", jwt.getSubject());
        response.put("username", jwt.getClaimAsString("preferred_username"));
        response.put("firstName", jwt.getClaimAsString("given_name"));
        response.put("lastName", jwt.getClaimAsString("family_name"));
        response.put("email", jwt.getClaimAsString("email"));
        response.put("roles", roles);
        return response;
    }

    @GetMapping("/{id}/email")
    public ResponseEntity<String> getUserEmailById(@PathVariable String id) {
        try {
            String email = userService.getUserEmailById(id);
            if (email == null || email.isBlank()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(email);
        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }
}