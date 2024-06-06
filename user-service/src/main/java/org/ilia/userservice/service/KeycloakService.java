package org.ilia.userservice.service;

import lombok.RequiredArgsConstructor;
import org.ilia.userservice.configuration.KeycloakProperties;
import org.ilia.userservice.controller.request.LoginRequest;
import org.ilia.userservice.controller.request.SignUpRequest;
import org.ilia.userservice.exception.UserNotFoundException;
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

    public boolean createUser(SignUpRequest signUpRequest) {
        String createUserUrl = keycloakProperties.getServerUrl()
                               + "/admin/realms/"
                               + keycloakProperties.getRealm()
                               + "/users";

        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("temporary", false);
        credential.put("value", signUpRequest.getPassword());

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", signUpRequest.getEmail());
        payload.put("firstName", signUpRequest.getFirstName());
        payload.put("lastName", signUpRequest.getLastName());
        payload.put("attributes", Map.of(
                "birthDate", signUpRequest.getBirthDate().toString(),
                "phoneNumber", signUpRequest.getPhoneNumber())
        );
        payload.put("enabled", true);
        payload.put("emailVerified", true);
        payload.put("credentials", List.of(credential));

        restTemplate.postForEntity(createUserUrl, new HttpEntity<>(payload, getAdminHttpHeaders()), Void.class);
        addRoleToUser(signUpRequest);
        return true;
    }

    private void addRoleToUser(SignUpRequest signUpRequest) {
        String getPatientRoleUrl = keycloakProperties.getServerUrl()
                                   + "/admin/realms/"
                                   + keycloakProperties.getRealm()
                                   + "/roles/dental-clinic-patient";

        Map roleResponse = restTemplate.exchange(getPatientRoleUrl, HttpMethod.GET, new HttpEntity<>(getAdminHttpHeaders()), Map.class).getBody();
        String roleId = (String) roleResponse.get("id");

        String userId = (String) getUserByEmail(signUpRequest.getEmail()).get("id");

        String roleMappingUrl = keycloakProperties.getServerUrl()
                                + "/admin/realms/"
                                + keycloakProperties.getRealm()
                                + "/users/"
                                + userId
                                + "/role-mappings/realm";

        Map<String, Object> role = new HashMap<>();
        role.put("id", roleId);
        role.put("name", "dental-clinic-patient");

        HttpEntity<List<Map<String, Object>>> roleRequestEntity = new HttpEntity<>(List.of(role), getAdminHttpHeaders());
        restTemplate.postForEntity(roleMappingUrl, roleRequestEntity, Void.class);
    }

    private Map<String, Object> getUserByEmail(String email) {
        String url = keycloakProperties.getServerUrl()
                     + "/admin/realms/"
                     + keycloakProperties.getRealm()
                     + "/users?email=" + email;

        List users = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(getAdminHttpHeaders()), List.class).getBody();
        if (users != null && !users.isEmpty()) {
            return (Map<String, Object>) users;
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
