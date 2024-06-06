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
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakProperties keycloakProperties;
    private final RestTemplate restTemplate;
    private final UserMapper userMapper;

    public boolean createPatient(SignUpRequest signUpRequest) {
        return createUser(userMapper.toUser(signUpRequest), "dental-clinic-patient");
    }

    public boolean createDoctor(CreateUserRequest createUserRequest) {
        return createUser(userMapper.toUser(createUserRequest), "dental-clinic-doctor");
    }

    public boolean createUser(User user, String role) {
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
        return true;
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

        String roleMappingUrl = keycloakProperties.getServerUrl()
                                + "/admin/realms/"
                                + keycloakProperties.getRealm()
                                + "/users/"
                                + userId
                                + "/role-mappings/realm";

        Map<String, String> payload = new HashMap<>();
        payload.put("id", roleId);
        payload.put("name", role);

        restTemplate.postForEntity(roleMappingUrl, new HttpEntity<>(List.of(payload), getAdminHttpHeaders()), Void.class);
    }

    private Map<String, Object> getRole(String role) {
        String getPatientRoleUrl = keycloakProperties.getServerUrl()
                                   + "/admin/realms/"
                                   + keycloakProperties.getRealm()
                                   + "/roles/"
                                   + role;

        return restTemplate.exchange(getPatientRoleUrl, HttpMethod.GET, new HttpEntity<>(getAdminHttpHeaders()), Map.class).getBody();
    }

    private Map<String, Object> getUserByEmail(String email) {
        String url = keycloakProperties.getServerUrl()
                     + "/admin/realms/"
                     + keycloakProperties.getRealm()
                     + "/users?email=" + email;

        List users = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(getAdminHttpHeaders()), List.class).getBody();
        if (users != null && !users.isEmpty()) {
            return (Map<String, Object>) users.getFirst();
        } else {
            throw new UserNotFoundException(email);
        }
    }

    public HttpHeaders getAdminHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());
        return headers;
    }

    public String getAccessToken(LoginRequest loginRequest) {
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
