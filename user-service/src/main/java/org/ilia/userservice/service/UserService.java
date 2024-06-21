package org.ilia.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.userservice.controller.request.CreateUserDto;
import org.ilia.userservice.controller.request.LoginDto;
import org.ilia.userservice.controller.request.UpdateUserDto;
import org.ilia.userservice.controller.response.SuccessLoginDto;
import org.ilia.userservice.controller.response.UserDto;
import org.ilia.userservice.enums.Role;
import org.ilia.userservice.mapper.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.userservice.enums.Role.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserService {

    KeycloakService keycloakService;
    UserMapper userMapper;

    public UserDto create(CreateUserDto createUserDto, Role role) {
        UUID userId = keycloakService.createUser(userMapper.toUser(createUserDto), role);
        return findById(userId, role);
    }

    public UserDto update(UpdateUserDto updateUserDto, Role role, UUID userId) {
        if (isAllowedActionOnThisUser(userId, role)) {
            keycloakService.updateUser(userMapper.toUser(updateUserDto, userId));
            return findById(userId, role);
        }
        throw new RuntimeException();
    }

    public SuccessLoginDto login(LoginDto loginDto, Role role) {
        keycloakService.getUserByEmail(loginDto.getEmail());
        return new SuccessLoginDto(keycloakService.getAccessToken(loginDto));
    }

    public UserDto findById(UUID userId, Role role) {
        return userMapper.toUserDto(keycloakService.getUserById(userId), role, userId);
    }

    public List<UserDto> findByRole(Role role) {
        return keycloakService.getUsersByRole(role).stream()
                .map(user -> userMapper.toUserDto(user, role, UUID.fromString(user.getId())))
                .toList();
    }

    public void delete(UUID userId, Role role) {
        if (isAllowedActionOnThisUser(userId, role)) {
            keycloakService.deleteUser(userId);
        }
    }

    private boolean isAllowedActionOnThisUser(UUID userId, Role targetUserRole) {
        UUID currentUserId = getCurrentUserId();
        Role currentUserRole = getCurrentUserRole();

        return ((currentUserRole.equals(OWNER) || currentUserRole.equals(ADMIN)) && (targetUserRole.equals(PATIENT) || targetUserRole.equals(DOCTOR))) ||
               (currentUserRole.equals(PATIENT) && targetUserRole.equals(PATIENT) && currentUserId.equals(userId));
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(((DefaultOAuth2AuthenticatedPrincipal) authentication.getPrincipal()).getName());
    }

    private Role getCurrentUserRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(str -> str.replace("ROLE_", ""))
                .map(Role::valueOf)
                .toList().getFirst();
    }
}
