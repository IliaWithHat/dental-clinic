package org.ilia.userservice.entity;

import lombok.Builder;
import lombok.Data;
import org.ilia.userservice.enums.Role;

import java.time.LocalDate;

@Data
@Builder
public class User {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String isWorking;
    private String password;
    private Role role;
}
