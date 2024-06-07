package org.ilia.userservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.ilia.userservice.configuration.KeycloakProperties;
import org.ilia.userservice.controller.request.LoginRequest;
import org.ilia.userservice.entity.User;
import org.ilia.userservice.enums.Role;
import org.ilia.userservice.exception.UserNotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.keycloak.representations.idm.CredentialRepresentation.PASSWORD;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakProperties keycloakProperties;
    private final Keycloak keycloak;

    private UsersResource usersResource;
    private ClientsResource clientsResource;

    @PostConstruct
    private void init() {
        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
        usersResource = realmResource.users();
        clientsResource = realmResource.clients();
    }

    public String createUser(User user, Role role) {
        UserRepresentation userRepresentation = mapUserToUserRepresentation(user);
        setCredentialsToUserRepresentation(user, userRepresentation);

        try (Response response = usersResource.create(userRepresentation)) {
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            addRoleToUser(role, userId);
            return userId;
        }
    }

    public void updateUser(User user) {
        UserRepresentation userRepresentation = mapUserToUserRepresentation(user);
        usersResource.get(user.getId()).update(userRepresentation);
    }

    public void deleteUser(String id) {
        usersResource.delete(id).close();
    }

    private UserRepresentation mapUserToUserRepresentation(User user) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setAttributes(addAttributes(user));
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);

        return userRepresentation;
    }

    private void setCredentialsToUserRepresentation(User user, UserRepresentation userRepresentation) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(PASSWORD);
        credentialRepresentation.setValue(user.getPassword());

        userRepresentation.setCredentials(List.of(credentialRepresentation));
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

    private void addRoleToUser(Role role, String userId) {
        String clientUuid = clientsResource.findByClientId(keycloakProperties.getClientId()).getFirst().getId();
        RoleRepresentation roleRepresentation = clientsResource.get(clientUuid).roles().get(role.name()).toRepresentation();
        usersResource.get(userId).roles().clientLevel(clientUuid).add(Collections.singletonList(roleRepresentation));
    }

    public RoleRepresentation getUserRoleByUserId(String id) {
        String clientUuid = clientsResource.findByClientId(keycloakProperties.getClientId()).getFirst().getId();
        return usersResource.get(id).roles().clientLevel(clientUuid).listAll().getFirst();
    }

    public UserRepresentation getUserByEmail(String email) {
        List<UserRepresentation> userRepresentations = usersResource.searchByEmail(email, true);
        if (userRepresentations.isEmpty()) {
            throw new UserNotFoundException("User not found by this email: " + email);
        } else {
            return userRepresentations.getFirst();
        }
    }

    public UserRepresentation getUserById(String id) {
        return usersResource.get(id).toRepresentation();
    }

    public List<UserRepresentation> getUsersByRole(Role role) {
        String clientUuid = clientsResource.findByClientId(keycloakProperties.getClientId()).getFirst().getId();
        return clientsResource.get(clientUuid).roles().get(role.name()).getUserMembers();
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
