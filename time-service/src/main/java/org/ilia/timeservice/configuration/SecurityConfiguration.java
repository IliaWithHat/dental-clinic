package org.ilia.timeservice.configuration;

import org.ilia.timeservice.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.ilia.timeservice.enums.Role.ADMIN;
import static org.ilia.timeservice.enums.Role.OWNER;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        .requestMatchers(GET,
                                "/v1/{role}/{doctorId}/working-time").authenticated()

                        .requestMatchers(POST,
                                "/v1/{role}/{doctorId}/working-time").hasAnyRole(OWNER.name(), ADMIN.name())
                        .requestMatchers(DELETE,
                                "/v1/{role}/{doctorId}/working-time").hasAnyRole(OWNER.name(), ADMIN.name())

                        .anyRequest().hasRole(ADMIN.name()))
                .oauth2ResourceServer((oauth2) -> oauth2
                        .opaqueToken(Customizer.withDefaults()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(STATELESS));
        return http.build();
    }

    @Component
    public static class AuthoritiesOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

        private final String clientId;
        private final OpaqueTokenIntrospector delegate;

        public AuthoritiesOpaqueTokenIntrospector(@Value("${spring.security.oauth2.resourceserver.opaquetoken.introspection-uri}") String introspectionUri,
                                                  @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}") String clientId,
                                                  @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}") String clientSecret) {
            this.clientId = clientId;
            this.delegate = new NimbusOpaqueTokenIntrospector(introspectionUri, clientId, clientSecret);
        }

        public OAuth2AuthenticatedPrincipal introspect(String token) {
            OAuth2AuthenticatedPrincipal principal = delegate.introspect(token);
            return new DefaultOAuth2AuthenticatedPrincipal(
                    principal.getName(), principal.getAttributes(), extractAuthorities(principal));
        }

        private Collection<GrantedAuthority> extractAuthorities(OAuth2AuthenticatedPrincipal principal) {
            Map<String, Map<String, List<String>>> clientAndRoles = principal.getAttribute("resource_access");
            return clientAndRoles.get(clientId).get("roles").stream()
                    .map(Role::valueOf)
                    .collect(toList());
        }
    }
}
