package org.ilia.userservice.entity;

import lombok.Data;
import org.ilia.userservice.enums.Role;

import java.time.LocalDate;

@Data
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