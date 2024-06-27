package org.ilia.userservice.mapper;

import org.ilia.userservice.controller.request.CreateUserDto;
import org.ilia.userservice.controller.request.UpdateUserDto;
import org.ilia.userservice.controller.response.UserDto;
import org.ilia.userservice.entity.User;
import org.ilia.userservice.enums.Role;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUser(CreateUserDto createUserDto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "birthDate", expression = "java(getBirthDateFromMap(userRepresentation.getAttributes()))")
    @Mapping(target = "phoneNumber", expression = "java(getPhoneNumberFromMap(userRepresentation.getAttributes()))")
    @Mapping(target = "isWorking", expression = "java(getIsWorkingFromMap(userRepresentation.getAttributes()))")
    @Mapping(target = "role", source = "role")
    UserDto toUserDto(UserRepresentation userRepresentation, Role role, UUID id);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUser(UpdateUserDto updateUserDto, UUID id);

    default LocalDate getBirthDateFromMap(Map<String, List<String>> map) {
        return LocalDate.parse(map.get("birthDate").getFirst());
    }

    default String getPhoneNumberFromMap(Map<String, List<String>> map) {
        return map.get("phoneNumber").getFirst();
    }

    default Boolean getIsWorkingFromMap(Map<String, List<String>> map) {
        List<String> value = map.get("isWorking");
        return value == null ? null : Boolean.parseBoolean(value.getFirst());
    }
}
