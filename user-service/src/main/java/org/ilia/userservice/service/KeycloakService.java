package org.ilia.userservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.ilia.userservice.configuration.KeycloakProperties;
import org.ilia.userservice.controller.request.CreateUserRequest;
import org.ilia.userservice.controller.request.LoginRequest;
import org.ilia.userservice.controller.request.SignUpRequest;
import org.ilia.userservice.entity.User;
import org.ilia.userservice.exception.UserNotFoundException;
import org.ilia.userservice.mapper.UserMapper;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakProperties keycloakProperties;
    private final UserMapper userMapper;
    private final Keycloak keycloak;

    private UsersResource usersResource;
    private ClientsResource clientsResource;

    @PostConstruct
    private void init() {
        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
        usersResource = realmResource.users();
        clientsResource = realmResource.clients();
    }

    public UUID createPatient(SignUpRequest signUpRequest) {
        return createUser(userMapper.toUser(signUpRequest), "PATIENT");
    }

    public UUID createDoctor(CreateUserRequest createUserRequest) {
        return createUser(userMapper.toUser(createUserRequest), "DOCTOR");
    }

    private UUID createUser(User user, String role) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(user.getPassword());

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setAttributes(addAttributes(user));
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);
        userRepresentation.setCredentials(List.of(credentialRepresentation));

        try (Response response = usersResource.create(userRepresentation)) {
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            addRoleToUser(role, userId);
            return UUID.fromString(userId);
        }
    }

    public void deleteUser(String id) {
        usersResource.delete(id).close();
    }

    private Map<String, List<String>> addAttributes(User user) {
        HashMap<String, List<String>> attributes = new HashMap<>();
        attributes.put("birthDate", List.of(user.getBirthDate().toString()));
        attributes.put("phoneNumber", List.of(user.getPhoneNumber()));
        if (user.getIsWorking() != null) {
            attributes.put("isWorking", List.of(user.getIsWorking()));
        }
        return attributes;
    }

    private void addRoleToUser(String role, String userId) {
        String clientUuid = clientsResource.findByClientId(keycloakProperties.getClientId()).getFirst().getId();
        RoleRepresentation roleRepresentation = clientsResource.get(clientUuid).roles().get(role).toRepresentation();
        usersResource.get(userId).roles().clientLevel(clientUuid).add(Collections.singletonList(roleRepresentation));
    }

    public List<RoleRepresentation> getUserRolesByUserId(String id) {
        String clientUuid = clientsResource.findByClientId(keycloakProperties.getClientId()).getFirst().getId();
        return usersResource.get(id).roles().clientLevel(clientUuid).listAll();
    }

    public UserRepresentation getUserByEmail(String email) {
        List<UserRepresentation> userRepresentations = usersResource.searchByEmail(email, true);
        if (userRepresentations.isEmpty()) {
            throw new UserNotFoundException("User not found by this email: " + email);
        } else {
            return userRepresentations.getFirst();
        }
    }

    public String getAccessToken(LoginRequest loginRequest) {
        Keycloak tempKeycloak = Keycloak.getInstance(
                keycloakProperties.getServerUrl(),
                keycloakProperties.getRealm(),
                loginRequest.getEmail(),
                loginRequest.getPassword(),
                keycloakProperties.getClientId(),
                keycloakProperties.getClientSecret()
        );
        String token = tempKeycloak.tokenManager().getAccessToken().getToken();
        tempKeycloak.close();
        return token;
    }
}
