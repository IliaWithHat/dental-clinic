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

import static org.ilia.userservice.enums.Role.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakService keycloakService;
    private final UserMapper userMapper;

    public User create(CreateUserRequest createUserRequest, Role role) {
        String userId = keycloakService.createUser(userMapper.toUser(createUserRequest), role);
        return findById(userId);
    }

    public User update(UpdateUserRequest updateUserRequest) {
        if (isAllowedActionOnThisUser(updateUserRequest.getId())) {
            keycloakService.updateUser(userMapper.toUser(updateUserRequest));
            return findById(updateUserRequest.getId());
        }
        throw new RuntimeException();
    }

    public LoginResponse login(LoginRequest loginRequest) {
        keycloakService.getUserByEmail(loginRequest.getEmail());
        return new LoginResponse(keycloakService.getAccessToken(loginRequest));
    }

    public User findById(String id) {
        Role role = Role.valueOf(keycloakService.getUserRoleByUserId(id).getName());
        return userMapper.toUser(keycloakService.getUserById(id), role, id);
    }

    public List<User> findByRole(Role role) {
        return keycloakService.getUsersByRole(role).stream()
                .map(user -> userMapper.toUser(user, role, user.getId()))
                .toList();
    }

    public void delete(String id) {
        if (isAllowedActionOnThisUser(id)) {
            keycloakService.deleteUser(id);
        }
    }

    private boolean isAllowedActionOnThisUser(String userId) {
        String currentUserId = ((DefaultOAuth2AuthenticatedPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getName();
        Role currentUserRole = getCurrentUserRole();
        Role targetUserRole = Role.valueOf(keycloakService.getUserRoleByUserId(userId).getName());

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
