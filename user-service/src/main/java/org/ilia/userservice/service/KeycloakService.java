package org.ilia.userservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.ilia.userservice.configuration.KeycloakProperties;
import org.ilia.userservice.controller.request.LoginDto;
import org.ilia.userservice.entity.User;
import org.ilia.userservice.enums.Role;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.*;

import static lombok.AccessLevel.PRIVATE;
import static org.keycloak.representations.idm.CredentialRepresentation.PASSWORD;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class KeycloakService {

    KeycloakProperties keycloakProperties;
    Keycloak keycloak;

    @NonFinal
    UsersResource usersResource;
    @NonFinal
    ClientsResource clientsResource;
    @NonFinal
    String clientId;

    @PostConstruct
    private void init() {
        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
        usersResource = realmResource.users();
        clientsResource = realmResource.clients();
        clientId = clientsResource.findByClientId(keycloakProperties.getClientId()).getFirst().getId();
    }

    public UUID createUser(Role role, User user) {
        UserRepresentation userRepresentation = mapUserToUserRepresentation(user);
        setCredentialsToUserRepresentation(user.getPassword(), userRepresentation);

        try (Response response = usersResource.create(userRepresentation)) {
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            addRoleToUser(role, userId);
            return UUID.fromString(userId);
        }
    }

    public void updateUser(User user) {
        UserRepresentation userRepresentation = mapUserToUserRepresentation(user);
        usersResource.get(user.getId().toString()).update(userRepresentation);
    }

    public void deleteUser(UUID id) {
        usersResource.delete(id.toString()).close();
    }

    private UserRepresentation mapUserToUserRepresentation(User user) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setAttributes(getAttributes(user));
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);

        return userRepresentation;
    }

    private void setCredentialsToUserRepresentation(String password, UserRepresentation userRepresentation) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(PASSWORD);
        credentialRepresentation.setValue(password);

        userRepresentation.setCredentials(List.of(credentialRepresentation));
    }

    private Map<String, List<String>> getAttributes(User user) {
        HashMap<String, List<String>> attributes = new HashMap<>();
        attributes.put("birthDate", List.of(user.getBirthDate().toString()));
        attributes.put("phoneNumber", List.of(user.getPhoneNumber()));
        if (user.getIsWorking() != null) {
            attributes.put("isWorking", List.of(user.getIsWorking().toString()));
        }
        return attributes;
    }

    private void addRoleToUser(Role role, String userId) {
        RoleRepresentation roleRepresentation = clientsResource.get(clientId).roles().get(role.name()).toRepresentation();
        usersResource.get(userId).roles().clientLevel(clientId).add(List.of(roleRepresentation));
    }

    public Optional<UserRepresentation> getUserByEmail(String email) {
        List<UserRepresentation> userRepresentations = usersResource.searchByEmail(email, true);
        if (!userRepresentations.isEmpty()) {
            return Optional.of(userRepresentations.getFirst());
        } else {
            return Optional.empty();
        }
    }

    public Optional<UserRepresentation> getUserById(UUID id) {
        UserRepresentation userRepresentation;
        try {
            userRepresentation = usersResource.get(id.toString()).toRepresentation();
        } catch (NotFoundException e) {
            return Optional.empty();
        }
        return Optional.of(userRepresentation);
    }

    public Role getUserRoleByUserId(UUID userId) {
        List<RoleRepresentation> roles = usersResource.get(userId.toString()).roles().clientLevel(clientId).listAll();
        return roles.stream()
                .map(roleRepresentation -> Role.valueOf(roleRepresentation.getName()))
                .findAny().orElseThrow();
    }

    public List<UserRepresentation> getUsersByRole(Role role) {
        return clientsResource.get(clientId).roles().get(role.name()).getUserMembers();
    }

    public String getAccessToken(LoginDto loginDto) {
        Keycloak tempKeycloak = Keycloak.getInstance(
                keycloakProperties.getServerUrl(),
                keycloakProperties.getRealm(),
                loginDto.getEmail(),
                loginDto.getPassword(),
                keycloakProperties.getClientId(),
                keycloakProperties.getClientSecret()
        );
        String token = tempKeycloak.tokenManager().getAccessToken().getToken();
        tempKeycloak.close();
        return token;
    }
}
