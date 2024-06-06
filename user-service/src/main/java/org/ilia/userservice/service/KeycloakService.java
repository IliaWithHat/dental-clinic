package org.ilia.userservice.service;

import lombok.RequiredArgsConstructor;
import org.ilia.userservice.configuration.KeycloakProperties;
import org.ilia.userservice.controller.request.CreateUserRequest;
import org.ilia.userservice.controller.request.LoginRequest;
import org.ilia.userservice.controller.request.SignUpRequest;
import org.ilia.userservice.entity.User;
import org.ilia.userservice.exception.UserNotFoundException;
import org.ilia.userservice.mapper.UserMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.springframework.http.HttpMethod.GET;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakProperties keycloakProperties;
    private final RestTemplate restTemplate;
    private final UserMapper userMapper;

    public UUID createPatient(SignUpRequest signUpRequest) {
        return createUser(userMapper.toUser(signUpRequest), "PATIENT");
    }

    public UUID createDoctor(CreateUserRequest createUserRequest) {
        return createUser(userMapper.toUser(createUserRequest), "DOCTOR");
    }

    public UUID createUser(User user, String role) {
        String createUserUrl = keycloakProperties.getServerUrl()
                               + "/admin/realms/"
                               + keycloakProperties.getRealm()
                               + "/users";

        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("temporary", false);
        credential.put("value", user.getPassword());

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", user.getEmail());
        payload.put("firstName", user.getFirstName());
        payload.put("lastName", user.getLastName());
        payload.put("attributes", addAttributes(user));
        payload.put("enabled", true);
        payload.put("emailVerified", true);
        payload.put("credentials", List.of(credential));

        restTemplate.postForEntity(createUserUrl, new HttpEntity<>(payload, getAdminHttpHeaders()), Void.class);
        addRoleToUser(role, user);
        return UUID.fromString((String) getUserByEmail(user.getEmail()).get("id"));
    }

    public void deleteUser(String id) {
        String currentUserId = Optional.of(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .map(DefaultOAuth2AuthenticatedPrincipal.class::cast)
                .map(DefaultOAuth2AuthenticatedPrincipal::getName)
                .get();
        List<String> currentUserRoles = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(str -> str.replace("ROLE_", ""))
                .toList();
        List<String> userRolesOnDeletion = getUserRolesByUserId(id).stream()
                .map(entry -> entry.get("name"))
                .map(String.class::cast)
                .toList();

        if ((currentUserRoles.contains("OWNER") && (userRolesOnDeletion.contains("PATIENT") || userRolesOnDeletion.contains("DOCTOR"))) ||
            (currentUserRoles.contains("PATIENT") && userRolesOnDeletion.contains("PATIENT") && currentUserId.equals(id))) {
            String deleteUserUrl = keycloakProperties.getServerUrl()
                                   + "/admin/realms/"
                                   + keycloakProperties.getRealm()
                                   + "/users/"
                                   + id;

            restTemplate.exchange(deleteUserUrl, HttpMethod.DELETE, new HttpEntity<>(getAdminHttpHeaders()), Void.class);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    private Map<String, String> addAttributes(User user) {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("birthDate", user.getBirthDate().toString());
        attributes.put("phoneNumber", user.getPhoneNumber());
        if (user.getIsWorking() != null) {
            attributes.put("isWorking", user.getIsWorking());
        }
        return attributes;
    }

    private void addRoleToUser(String role, User user) {
        String roleId = (String) getRole(role).get("id");
        String userId = (String) getUserByEmail(user.getEmail()).get("id");
        String clientUuid = (String) getClient().get("id");

        String roleMappingUrl = keycloakProperties.getServerUrl()
                                + "/admin/realms/"
                                + keycloakProperties.getRealm()
                                + "/users/"
                                + userId
                                + "/role-mappings/clients/"
                                + clientUuid;

        Map<String, String> payload = new HashMap<>();
        payload.put("id", roleId);
        payload.put("name", role);

        restTemplate.postForEntity(roleMappingUrl, new HttpEntity<>(List.of(payload), getAdminHttpHeaders()), Void.class);
    }

    private Map<String, Object> getRole(String role) {
        String clientUuid = (String) getClient().get("id");

        String roleUrl = keycloakProperties.getServerUrl()
                         + "/admin/realms/"
                         + keycloakProperties.getRealm()
                         + "/clients/"
                         + clientUuid
                         + "/roles";
        List<Map<String, Object>> roles = restTemplate.exchange(roleUrl, GET, new HttpEntity<>(getAdminHttpHeaders()), List.class).getBody();
        return roles.stream()
                .filter(oneRole -> oneRole.get("name").equals(role))
                .findFirst().get();
    }

    private Map<String, Object> getClient() {
        String clientUrl = keycloakProperties.getServerUrl()
                           + "/admin/realms/"
                           + keycloakProperties.getRealm()
                           + "/clients";
        List<Map<String, Object>> allClients = restTemplate.exchange(clientUrl, GET, new HttpEntity<>(getAdminHttpHeaders()), List.class).getBody();
        return allClients.stream()
                .filter(oneClient -> oneClient.get("clientId").equals(keycloakProperties.getClientId()))
                .toList().getFirst();
    }

    private Map<String, Object> getUserById(String id) {
        String url = keycloakProperties.getServerUrl()
                     + "/admin/realms/"
                     + keycloakProperties.getRealm()
                     + "/users/"
                     + id;

        Map<String, Object> user = restTemplate.exchange(url, GET, new HttpEntity<>(getAdminHttpHeaders()), Map.class).getBody();
        if (user == null) {
            throw new UserNotFoundException("User not found by this id: " + id);
        } else {
            return user;
        }
    }

    private List<Map<String, Object>> getUserRolesByUserId(String id) {
        String clientUuid = (String) getClient().get("id");

        String roleUrl = keycloakProperties.getServerUrl()
                         + "/admin/realms/"
                         + keycloakProperties.getRealm()
                         + "/users/"
                         + id
                         + "/role-mappings/clients/"
                         + clientUuid;
        return (List<Map<String, Object>>) restTemplate.exchange(roleUrl, GET, new HttpEntity<>(getAdminHttpHeaders()), List.class).getBody();
    }

    private Map<String, Object> getUserByEmail(String email) {
        String url = keycloakProperties.getServerUrl()
                     + "/admin/realms/"
                     + keycloakProperties.getRealm()
                     + "/users?email="
                     + email;

        List<Map<String, Object>> users = restTemplate.exchange(url, GET, new HttpEntity<>(getAdminHttpHeaders()), List.class).getBody();
        if (users == null || users.isEmpty()) {
            throw new UserNotFoundException("User not found by this email: " + email);
        } else {
            return users.getFirst();
        }
    }

    private HttpHeaders getAdminHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());
        return headers;
    }

    public String getAccessToken(LoginRequest loginRequest) {
        getUserByEmail(loginRequest.getEmail());
        return getAccessToken(loginRequest.getEmail(), loginRequest.getPassword());
    }

    private String getAccessToken() {
        return getAccessToken(keycloakProperties.getEmail(), keycloakProperties.getPassword());
    }

    private String getAccessToken(String email, String password) {
        String url = keycloakProperties.getServerUrl()
                     + "/realms/"
                     + keycloakProperties.getRealm()
                     + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.ALL));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> payload = new LinkedMultiValueMap<>();
        payload.add("username", email);
        payload.add("password", password);
        payload.add("grant_type", keycloakProperties.getGrantType());
        payload.add("client_id", keycloakProperties.getClientId());
        payload.add("client_secret", keycloakProperties.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(payload, headers);

        return (String) restTemplate.postForEntity(url, requestEntity, Map.class).getBody().get("access_token");
    }
}
