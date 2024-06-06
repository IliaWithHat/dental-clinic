package org.ilia.userservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "keycloak")
@Configuration
@Getter
@Setter
public class KeycloakProperties {

    private String serverUrl;
    private String realm;
    private String clientId;
    private String clientSecret;
    private String email;
    private String password;
}
