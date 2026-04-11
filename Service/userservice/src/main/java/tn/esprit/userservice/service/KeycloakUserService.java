package tn.esprit.userservice.service;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KeycloakUserService {

    private final Keycloak keycloak;

    public KeycloakUserService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public UserRepresentation getUserById(String userId) {
        return keycloak
                .realm("jungleinenglish-realm")
                .users()
                .get(userId)
                .toRepresentation();
    }

    public List<Map<String, Object>> getAllUsers() {
        return keycloak.realm("jungleinenglish-realm")
                .users()
                .list()
                .stream()
                .map(user -> Map.of(
                        "id",        (Object) user.getId(),
                        "username",  user.getUsername(),
                        "email",     user.getEmail() != null ? user.getEmail() : "",
                        "firstName", user.getFirstName() != null ? user.getFirstName() : "",
                        "lastName",  user.getLastName()  != null ? user.getLastName()  : ""
                ))
                .toList();
    }


    public void createUser(String username, String email, String firstName, String lastName, String password) {

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        user.setCredentials(List.of(credential));

        Response response = keycloak
                .realm("jungleinenglish-realm")
                .users()
                .create(user);

        if (response.getStatus() != 201) {
            throw new RuntimeException("Erreur création user : " + response.getStatus());
        }

        // 🔥 récupérer l'ID du user créé
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        // 🔥 récupérer le rôle USER
        RoleRepresentation role = keycloak
                .realm("jungleinenglish-realm")
                .roles()
                .get("USER")
                .toRepresentation();

        // 🔥 assigner le rôle au user
        keycloak.realm("jungleinenglish-realm")
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(List.of(role));
    }

    public String getUserEmailById(String userId) {
        UserRepresentation user = keycloak
                .realm("jungleinenglish-realm")
                .users()
                .get(userId)
                .toRepresentation();

        return user.getEmail();
    }
}