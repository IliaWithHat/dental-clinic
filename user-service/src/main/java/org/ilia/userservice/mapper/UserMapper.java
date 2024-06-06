package org.ilia.userservice.mapper;

import org.ilia.userservice.controller.request.CreateUserRequest;
import org.ilia.userservice.controller.request.SignUpRequest;
import org.ilia.userservice.entity.User;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", ignore = true)
    User toUser(CreateUserRequest createUserRequest);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isWorking", ignore = true)
    User toUser(SignUpRequest signUpRequest);

    @Mapping(target = "birthDate", expression = "java(getLocalDateFromMap(\"birthDate\", userRepresentation.getAttributes()))")
    @Mapping(target = "isWorking", expression = "java(getValueFromMap(\"isWorking\", userRepresentation.getAttributes()))")
    @Mapping(target = "phoneNumber", expression = "java(getValueFromMap(\"phoneNumber\", userRepresentation.getAttributes()))")
    @Mapping(target = "password", expression = "java(\"<secured>\")")
    @Mapping(target = "role", expression = "java(org.ilia.userservice.enums.Role.valueOf(role))")
    User toUser(UserRepresentation userRepresentation, String role);

    default String getValueFromMap(String param, Map<String, List<String>> map) {
        List<String> value = map.get(param);
        return value == null ? null : value.getFirst();
    }

    default LocalDate getLocalDateFromMap(String param, Map<String, List<String>> map) {
        return LocalDate.parse(map.get(param).getFirst());
    }
}
