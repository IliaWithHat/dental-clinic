package org.ilia.appointmentservice.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    OWNER,
    DOCTOR,
    PATIENT;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
