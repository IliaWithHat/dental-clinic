package org.ilia.userservice.service;

import lombok.RequiredArgsConstructor;
import org.ilia.userservice.controller.request.CreateUserRequest;
import org.ilia.userservice.controller.request.LoginRequest;
import org.ilia.userservice.controller.request.SignUpRequest;
import org.ilia.userservice.controller.response.CreateUserResponse;
import org.ilia.userservice.controller.response.LoginResponse;
import org.ilia.userservice.controller.response.SignUpResponse;
import org.ilia.userservice.entity.User;
import org.ilia.userservice.mapper.UserMapper;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.ilia.userservice.enums.Role.DOCTOR;
import static org.ilia.userservice.enums.Role.PATIENT;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakService keycloakService;
    private final UserMapper userMapper;

    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        return new SignUpResponse(keycloakService.createUser(userMapper.toUser(signUpRequest), PATIENT));
    }

    public CreateUserResponse create(CreateUserRequest createUserRequest) {
        return new CreateUserResponse(keycloakService.createUser(userMapper.toUser(createUserRequest), DOCTOR));
    }

    public LoginResponse login(LoginRequest loginRequest) {
        keycloakService.getUserByEmail(loginRequest.getEmail());
        return new LoginResponse(keycloakService.getAccessToken(loginRequest));
    }

    public User findById(String id) {
        String role = keycloakService.getUserRolesByUserId(id).getFirst().getName();
        return userMapper.toUser(keycloakService.getUserById(id), role);
    }

    public void delete(String id) {
        String currentUserId = Optional.of(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .map(DefaultOAuth2AuthenticatedPrincipal.class::cast)
                .map(DefaultOAuth2AuthenticatedPrincipal::getName)
                .get();
        List<String> currentUserRoles = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(str -> str.replace("ROLE_", ""))
                .toList();
        List<String> userRolesOnDeletion = keycloakService.getUserRolesByUserId(id).stream()
                .map(RoleRepresentation::getName)
                .toList();

        if ((currentUserRoles.contains("OWNER") && (userRolesOnDeletion.contains("PATIENT") || userRolesOnDeletion.contains("DOCTOR"))) ||
            (currentUserRoles.contains("PATIENT") && userRolesOnDeletion.contains("PATIENT") && currentUserId.equals(id))) {
            keycloakService.deleteUser(id);
        }
    }
}
