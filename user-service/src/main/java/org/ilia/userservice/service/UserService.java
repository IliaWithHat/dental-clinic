package org.ilia.userservice.service;

import lombok.RequiredArgsConstructor;
import org.ilia.userservice.controller.request.CreateUserRequest;
import org.ilia.userservice.controller.request.LoginRequest;
import org.ilia.userservice.controller.request.SignUpRequest;
import org.ilia.userservice.controller.response.LoginResponse;
import org.ilia.userservice.entity.User;
import org.ilia.userservice.enums.Role;
import org.ilia.userservice.mapper.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import static org.ilia.userservice.enums.Role.DOCTOR;
import static org.ilia.userservice.enums.Role.PATIENT;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakService keycloakService;
    private final UserMapper userMapper;

    public User signUp(SignUpRequest signUpRequest) {
        String userId = keycloakService.createUser(userMapper.toUser(signUpRequest), PATIENT);
        return findById(userId);
    }

    public User create(CreateUserRequest createUserRequest) {
        String userId = keycloakService.createUser(userMapper.toUser(createUserRequest), DOCTOR);
        return findById(userId);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        keycloakService.getUserByEmail(loginRequest.getEmail());
        return new LoginResponse(keycloakService.getAccessToken(loginRequest));
    }

    public User findById(String id) {
        Role role = Role.valueOf(keycloakService.getUserRolesByUserId(id).getName());
        return userMapper.toUser(keycloakService.getUserById(id), role, id);
    }

    public void delete(String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String currentUserId = ((DefaultOAuth2AuthenticatedPrincipal) authentication.getPrincipal()).getName();
        String currentUserRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(str -> str.replace("ROLE_", ""))
                .toList().getFirst();
        String userRolesOnDeletion = keycloakService.getUserRolesByUserId(id).getName();

        if ((currentUserRoles.equals("OWNER") && (userRolesOnDeletion.equals("PATIENT") || userRolesOnDeletion.equals("DOCTOR"))) ||
            (currentUserRoles.equals("PATIENT") && userRolesOnDeletion.equals("PATIENT") && currentUserId.equals(id))) {
            keycloakService.deleteUser(id);
        }
    }
}
