package fr.eseo.webcube.api.security;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import fr.eseo.webcube.api.dao.UserDAO;
import fr.eseo.webcube.api.model.CustomUserDetails;
import fr.eseo.webcube.api.model.User;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;


@Configuration
@EnableWebSecurity
public class ApplicationSecurity {

    @Autowired
    UserTokenAzure userDetailsResponse;

    @Autowired
    private UserDAO userRepository;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Value("${spring.security.oauth2.client.registration.azure.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.azure.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.azure.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.azure.token-uri}")
    private String tokenUri;

    //Permet de configurer la sécurité de l'api, ainsi que les urls accéssible par certain role
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers("/user/connexion").permitAll()
                //.antMatchers("/Student").hasRole("TEACHER")
                //.antMatchers("/**").permitAll();
                .anyRequest().authenticated();
        http.oauth2ResourceServer()
                .jwt().decoder(jwtDecoder())
                .and()
                .jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());

        http
                .rememberMe()
                .rememberMeCookieName("monCookie")
                .tokenValiditySeconds(86400);

        http.exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> response.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED,
                                ex.getMessage()
                        )
                );
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

        @Bean
        public UserDetailsService userDetailsService() {
                return username -> {
                        Optional<User> optionalUser = userRepository.findByUniqueName(username);
                        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
                        return new CustomUserDetails(user);
                };
        }

        @Bean
        public JwtDecoder jwtDecoder() {
                NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(this.tokenUri).build();
                jwtDecoder.setClaimSetConverter(new CustomClaimSetConverter());
                return jwtDecoder;
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
                converter.setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter());
                return converter;
        }

}
