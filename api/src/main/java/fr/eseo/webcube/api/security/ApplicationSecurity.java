package fr.eseo.webcube.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class ApplicationSecurity {

    @Autowired
    UserTokenAzure userDetailsResponse;

    @Autowired
    private JwtTokenUtil jwtUtil;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers("/**").permitAll() ; // Endpoint public accessible sans authentification
               // .anyRequest().authenticated()
            //.and()
            //.oauth2ResourceServer()
              //  .jwt();
                   //.jwtAuthenticationConverter(jwtAuthenticationConverter());

        return http.build();
    }

    /*@Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        // Ajouter un convertisseur pour extraire des informations spécifiques du token
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            jwt.getClaims().forEach((key, value) -> {
                // Extraitz les informations nécessaires, par exemple, le nom de la personne (given_name, family_name)
                if ("given_name".equals(key)) {
                    // Faites quelque chose avec le prénom
                    String firstName = (String) value;
                    userDetailsResponse.setGiven_name(firstName);

                } else if ("family_name".equals(key)) {
                    // Faites quelque chose avec le nom de famille
                    String lastName = (String) value;
                    userDetailsResponse.setFamily_name(lastName);
                }
            });

            return jwtGrantedAuthoritiesConverter.convert(jwt);
        });

        return converter;
    }*/

}
