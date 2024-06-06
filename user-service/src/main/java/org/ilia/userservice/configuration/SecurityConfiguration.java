package org.ilia.userservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "v1/user/signUp", "/v1/user/login").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "v1/user").hasRole("OWNER")
                        .anyRequest().denyAll())
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
            List<String> roles = clientAndRoles.get(clientId).get("roles");
            return roles.stream()
                    .map(str -> "ROLE_" + str)
                    .map(SimpleGrantedAuthority::new)
                    .collect(toList());
        }
    }
}
