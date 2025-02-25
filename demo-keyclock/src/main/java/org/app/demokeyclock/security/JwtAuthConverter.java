package org.app.demokeyclock.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    // Extract authorities from the JWT token
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter=new JwtGrantedAuthoritiesConverter();
    /**
     * Converts the provided JWT token into an Authentication Token,
     * which includes the user's authorities and preferred username.
     * @param source the JWT token
     * @return an AbstractAuthenticationToken containing the user's authorities and username
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        // Combining the authorities from the JWT's standard authorities and any roles extracted from "realm_access"
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(source).stream(), // Convert authorities from the JWT itself
                extractResourceRoles(source).stream() // Extract additional roles from realm_access claim
        ).collect(Collectors.toSet());

        // Creating and returning a JwtAuthenticationToken with the extracted authorities and username (preferred_username)
        return new JwtAuthenticationToken(source, authorities, source.getClaim("preferred_username"));
    }

    /**
     * Extracts roles from the "realm_access" claim in the JWT and returns them as granted authorities.
     * @param jwt the JWT token
     * @return a collection of GrantedAuthority objects representing the user's roles
     */
    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
        // Retrieve the "realm_access" claim from the JWT
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        // If the "realm_access" claim is missing or doesn't contain roles, return an empty set
        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            return Set.of(); // Return an empty set if no roles are defined
        }

        // Get the roles and map each role to a SimpleGrantedAuthority (following standard conventions)
        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role)) // Add each role as an authority
                .collect(Collectors.toSet());
    }
}
