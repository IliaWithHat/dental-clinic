package org.ilia.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.tuple.Pair;
import org.ilia.userservice.controller.request.CreateUserDto;
import org.ilia.userservice.controller.request.LoginDto;
import org.ilia.userservice.controller.request.UpdateUserDto;
import org.ilia.userservice.controller.response.SuccessLoginDto;
import org.ilia.userservice.controller.response.UserDto;
import org.ilia.userservice.entity.User;
import org.ilia.userservice.enums.Role;
import org.ilia.userservice.exception.UserAlreadyExistException;
import org.ilia.userservice.exception.UserDeletedException;
import org.ilia.userservice.exception.UserNotFoundException;
import org.ilia.userservice.exception.UserNotHavePermissionException;
import org.ilia.userservice.mapper.UserMapper;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.ilia.userservice.constant.ExceptionMessages.*;
import static org.ilia.userservice.enums.Role.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserService {

    KeycloakService keycloakService;
    UserMapper userMapper;
    MailService mailService;

    public UserDto create(Role role, CreateUserDto createUserDto) {
        verifyUserPermissionForCreateAction(role);
        verifyUserNotExistByEmail(createUserDto.getEmail());

        UserRepresentation createdUser = keycloakService.createUser(role, userMapper.toUser(createUserDto));
        UserDto userDto = userMapper.toUserDto(createdUser, role);

        if (role == PATIENT) {
            mailService.sendWelcomeEmail(userDto);
        }
        return userDto;
    }

    public UserDto update(Role role, UUID userId, UpdateUserDto updateUserDto) {
        verifyUserPermissionForUpdateAction(userId, role);
        User user = verifyUserExistByUserIdAndRole(userId, role);
        verifyUserNotDeleted(user);

        UserRepresentation updatedUser = keycloakService.updateUser(userMapper.toUser(updateUserDto, userId));
        return userMapper.toUserDto(updatedUser, role);
    }

    public SuccessLoginDto login(Role role, LoginDto loginDto) {
        User user = verifyUserExistByEmailAndRole(loginDto.getEmail(), role);
        verifyUserNotDeleted(user);

        Pair<String, String> accessToken = keycloakService.getAccessToken(loginDto);
        return new SuccessLoginDto(accessToken.getLeft(), accessToken.getRight());
    }

    public UserDto findById(Role role, UUID userId) {
        User user = verifyUserExistByUserIdAndRole(userId, role);
        verifyUserPermissionForFindByIdAction(userId, role);
        verifyUserNotDeleted(user);

        return userMapper.toUserDto(user);
    }

    public List<UserDto> findByRole(Role role) {
        verifyUserPermissionForFindByRoleAction(role);

        return keycloakService.getUsersByRole(role).stream()
                .map(user -> userMapper.toUser(user, role))
                .filter(user -> !user.getIsDeleted())
                .map(userMapper::toUserDto)
                .toList();
    }

    public void delete(Role role, UUID userId) {
        User user = verifyUserExistByUserIdAndRole(userId, role);
        verifyUserPermissionForDeleteAction(userId, role);
        verifyUserNotDeleted(user);

        user.setIsDeleted(true);
        keycloakService.updateUser(user);
    }

    private void verifyUserPermissionForCreateAction(Role targetUserRole) {
        Role currentUserRole = getCurrentUserRole();
        if (targetUserRole == DOCTOR && (currentUserRole != OWNER && currentUserRole != ADMIN)) {
            throw new UserNotHavePermissionException(USER_NOT_HAVE_PERMISSION);
        }
    }

    private void verifyUserPermissionForUpdateAction(UUID targetUserId, Role targetUserRole) {
        UUID currentUserId = getCurrentUserId();
        Role currentUserRole = getCurrentUserRole();
        boolean isAllowedAction =
                (currentUserRole == OWNER || currentUserRole == ADMIN) && (targetUserRole == OWNER || targetUserRole == DOCTOR) ||
                (currentUserRole == PATIENT && targetUserRole == PATIENT && currentUserId.equals(targetUserId));
        if (!isAllowedAction) {
            throw new UserNotHavePermissionException(USER_NOT_HAVE_PERMISSION);
        }
    }

    private void verifyUserPermissionForFindByIdAction(UUID targetUserId, Role targetUserRole) {
        UUID currentUserId = getCurrentUserId();
        Role currentUserRole = getCurrentUserRole();
        boolean isAllowedAction =
                (currentUserRole == OWNER || currentUserRole == ADMIN || currentUserRole == DOCTOR) ||
                (currentUserRole == PATIENT && targetUserRole == DOCTOR) ||
                (currentUserRole == PATIENT && targetUserRole == PATIENT && currentUserId.equals(targetUserId));
        if (!isAllowedAction) {
            throw new UserNotHavePermissionException(USER_NOT_HAVE_PERMISSION);
        }
    }

    private void verifyUserPermissionForFindByRoleAction(Role targetUserRole) {
        Role currentUserRole = getCurrentUserRole();
        boolean isAllowedAction =
                (currentUserRole == OWNER || currentUserRole == ADMIN || currentUserRole == DOCTOR) ||
                (currentUserRole == PATIENT && targetUserRole == DOCTOR);
        if (!isAllowedAction) {
            throw new UserNotHavePermissionException(USER_NOT_HAVE_PERMISSION);
        }
    }

    private void verifyUserPermissionForDeleteAction(UUID targetUserId, Role targetUserRole) {
        UUID currentUserId = getCurrentUserId();
        Role currentUserRole = getCurrentUserRole();
        boolean isAllowedAction =
                (currentUserRole == OWNER || currentUserRole == ADMIN) &&
                (targetUserRole == OWNER || targetUserRole == DOCTOR || targetUserRole == PATIENT) ||
                (currentUserRole == PATIENT && targetUserRole == PATIENT && currentUserId.equals(targetUserId));
        if (!isAllowedAction) {
            throw new UserNotHavePermissionException(USER_NOT_HAVE_PERMISSION);
        }
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return UUID.fromString(((DefaultOAuth2AuthenticatedPrincipal) authentication.getPrincipal()).getName());
    }

    private Role getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return authentication.getAuthorities().stream()
                .map(Role.class::cast)
                .findFirst().get();
    }

    private void verifyUserNotExistByEmail(String email) {
        if (keycloakService.getUserByEmail(email).isPresent()) {
            throw new UserAlreadyExistException(USER_ALREADY_EXISTS + email);
        }
    }

    private User verifyUserExistByEmailAndRole(String email, Role role) {
        return keycloakService.getUserByEmail(email)
                .filter(user -> isUserRoleValid(UUID.fromString(user.getId()), role))
                .map(ur -> userMapper.toUser(ur, role))
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_BY_EMAIL_AND_ROLE.formatted(email, role)));
    }

    private User verifyUserExistByUserIdAndRole(UUID userId, Role role) {
        return keycloakService.getUserById(userId)
                .filter(ur -> isUserRoleValid(userId, role))
                .map(ur -> userMapper.toUser(ur, role))
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_BY_ID_AND_ROLE.formatted(userId, role)));
    }

    private boolean isUserRoleValid(UUID userId, Role role) {
        return keycloakService.getUserRoleByUserId(userId) == role;
    }

    private void verifyUserNotDeleted(User user) {
        if (user.getIsDeleted()) {
            throw new UserDeletedException(USER_DELETED);
        }
    }
}
