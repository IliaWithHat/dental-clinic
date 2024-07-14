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

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface UserMapper {

    User toUser(CreateUserDto createUserDto);

    User toUser(UpdateUserDto updateUserDto, UUID id);

    @Mapping(target = "birthDate", expression = "java(getBirthDateFromMap(userRepresentation.getAttributes()))")
    @Mapping(target = "phoneNumber", expression = "java(getPhoneNumberFromMap(userRepresentation.getAttributes()))")
    @Mapping(target = "isWorking", expression = "java(getIsWorkingFromMap(userRepresentation.getAttributes()))")
    @Mapping(target = "isDeleted", expression = "java(getIsDeletedFromMap(userRepresentation.getAttributes()))")
    User toUser(UserRepresentation userRepresentation, Role role);

    UserDto toUserDto(User user);

    @Mapping(target = "birthDate", expression = "java(getBirthDateFromMap(userRepresentation.getAttributes()))")
    @Mapping(target = "phoneNumber", expression = "java(getPhoneNumberFromMap(userRepresentation.getAttributes()))")
    @Mapping(target = "isWorking", expression = "java(getIsWorkingFromMap(userRepresentation.getAttributes()))")
    UserDto toUserDto(UserRepresentation userRepresentation, Role role);

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

    default Boolean getIsDeletedFromMap(Map<String, List<String>> map) {
        List<String> value = map.get("isDeleted");
        return value == null ? null : Boolean.parseBoolean(value.getFirst());
    }
}
