package org.ilia.userservice.mapper;

import org.ilia.userservice.controller.request.CreateUserRequest;
import org.ilia.userservice.controller.request.SignUpRequest;
import org.ilia.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(CreateUserRequest createUserRequest);

    @Mapping(target = "isWorking", ignore = true)
    User toUser(SignUpRequest signUpRequest);
}
