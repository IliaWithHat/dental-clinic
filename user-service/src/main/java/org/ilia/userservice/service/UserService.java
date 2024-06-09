package org.ilia.userservice.service;

import lombok.RequiredArgsConstructor;
import org.ilia.userservice.controller.request.CreateUserRequest;
import org.ilia.userservice.controller.request.LoginRequest;
import org.ilia.userservice.controller.request.UpdateUserRequest;
import org.ilia.userservice.controller.response.LoginResponse;
import org.ilia.userservice.entity.User;
import org.ilia.userservice.enums.Role;
import org.ilia.userservice.mapper.UserMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.ilia.userservice.enums.Role.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakService keycloakService;
    private final UserMapper userMapper;

    public User create(CreateUserRequest createUserRequest, Role role) {
        UUID userId = keycloakService.createUser(userMapper.toUser(createUserRequest), role);
        return findById(userId, role);
    }

    public User update(UpdateUserRequest updateUserRequest, Role role) {
        if (isAllowedActionOnThisUser(updateUserRequest.getId(), role)) {
            keycloakService.updateUser(userMapper.toUser(updateUserRequest));
            return findById(updateUserRequest.getId(), role);
        }
        throw new RuntimeException();
    }

    public LoginResponse login(LoginRequest loginRequest, Role role) {
        keycloakService.getUserByEmail(loginRequest.getEmail());
        return new LoginResponse(keycloakService.getAccessToken(loginRequest));
    }

    public User findById(UUID userId, Role role) {
        return userMapper.toUser(keycloakService.getUserById(userId), role, userId);
    }

    public List<User> findByRole(Role role) {
        return keycloakService.getUsersByRole(role).stream()
                .map(user -> userMapper.toUser(user, role, UUID.fromString(user.getId())))
                .toList();
    }

    public void delete(UUID userId, Role role) {
        if (isAllowedActionOnThisUser(userId, role)) {
            keycloakService.deleteUser(userId);
        }
    }

    private boolean isAllowedActionOnThisUser(UUID userId, Role targetUserRole) {
        UUID currentUserId = UUID.fromString(((DefaultOAuth2AuthenticatedPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal())
                .getName());
        Role currentUserRole = getCurrentUserRole();

        return (currentUserRole.equals(OWNER) && (targetUserRole.equals(PATIENT) || targetUserRole.equals(DOCTOR))) ||
               (currentUserRole.equals(PATIENT) && targetUserRole.equals(PATIENT) && currentUserId.equals(userId));
    }

    private Role getCurrentUserRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(str -> str.replace("ROLE_", ""))
                .map(Role::valueOf)
                .toList().getFirst();
    }
}
