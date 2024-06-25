package org.ilia.timeservice.feign.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ilia.timeservice.enums.Role;

import java.time.LocalDate;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class UserDto {

    UUID id;
    String email;
    String firstName;
    String lastName;
    LocalDate birthDate;
    String phoneNumber;
    String isWorking;
    String password;
    Role role;
}
