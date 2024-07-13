package org.ilia.userservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.commons.lang3.tuple.Pair;
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
import java.util.stream.Collectors;

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
    @NonFinal
    Map<Role, RoleRepresentation> allRoles;

    @PostConstruct
    private void init() {
        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
        usersResource = realmResource.users();
        clientsResource = realmResource.clients();
        clientId = clientsResource.findByClientId(keycloakProperties.getClientId()).getFirst().getId();
        allRoles = clientsResource.get(clientId).roles().list().stream()
                .collect(Collectors.toMap(r -> Role.valueOf(r.getName()), r -> r));
    }

    public UserRepresentation createUser(Role role, User user) {
        UserRepresentation userToSave = mapUserToUserRepresentation(user);
        setCredentialsToUserRepresentation(user.getPassword(), userToSave);

        String userId;
        try (Response response = usersResource.create(userToSave)) {
            userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        }
        addRoleToUser(role, userId);
        return usersResource.get(userId).toRepresentation();
    }

    public UserRepresentation updateUser(User user) {
        UserRepresentation userToUpdate = mapUserToUserRepresentation(user);
        usersResource.get(user.getId().toString()).update(userToUpdate);
        return usersResource.get(userToUpdate.getId()).toRepresentation();
    }

    private UserRepresentation mapUserToUserRepresentation(User user) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(user.getId() == null ? null : user.getId().toString());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setAttributes(getAttributes(user));
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);

        return userRepresentation;
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

    private void setCredentialsToUserRepresentation(String password, UserRepresentation userRepresentation) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(PASSWORD);
        credentialRepresentation.setValue(password);

        userRepresentation.setCredentials(List.of(credentialRepresentation));
    }

    private void addRoleToUser(Role role, String userId) {
        usersResource.get(userId).roles().clientLevel(clientId).add(List.of(allRoles.get(role)));
    }

    public void deleteUser(UUID userId) {
        usersResource.delete(userId.toString()).close();
    }

    public Optional<UserRepresentation> getUserByEmail(String email) {
        List<UserRepresentation> userRepresentations = usersResource.searchByEmail(email, true);
        if (userRepresentations.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(userRepresentations.getFirst());
        }
    }

    public Optional<UserRepresentation> getUserById(UUID userId) {
        try {
            return Optional.of(usersResource.get(userId.toString()).toRepresentation());
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    public Role getUserRoleByUserId(UUID userId) {
        return usersResource.get(userId.toString()).roles().clientLevel(clientId).listAll().stream()
                .map(roleRepresentation -> Role.valueOf(roleRepresentation.getName()))
                .findFirst().get();
    }

    public List<UserRepresentation> getUsersByRole(Role role) {
        return clientsResource.get(clientId).roles().get(role.name()).getUserMembers();
    }

    public Pair<String, String> getAccessToken(LoginDto loginDto) {
        Keycloak tempKeycloak = Keycloak.getInstance(
                keycloakProperties.getServerUrl(),
                keycloakProperties.getRealm(),
                loginDto.getEmail(),
                loginDto.getPassword(),
                keycloakProperties.getClientId(),
                keycloakProperties.getClientSecret()
        );
        String token = tempKeycloak.tokenManager().getAccessToken().getToken();
        String refreshToken = tempKeycloak.tokenManager().getAccessToken().getRefreshToken();
        tempKeycloak.close();
        return Pair.of(token, refreshToken);
    }
}
