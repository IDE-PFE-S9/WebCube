package fr.eseo.webcube.api.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();

        // Exemple : Extraire le rôle "ROLE_USER" du claim "roles"
        Object rolesClaim = claims.get("roles");
        if (rolesClaim instanceof Collection<?>) {
            // Convertir les rôles en GrantedAuthority
            return ((Collection<?>) rolesClaim).stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        }

        // Si le claim "roles" n'est pas présent ou n'est pas une collection, retourner une liste vide
        return Collections.emptyList();
    }
}
