package org.ilia.userservice.entity;

import lombok.Data;
import org.ilia.userservice.enums.Role;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class User {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String isWorking;
    private String password;
    private Role role;
}
