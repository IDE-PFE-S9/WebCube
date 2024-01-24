package fr.eseo.webcube.api.security;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import fr.eseo.webcube.api.dao.UserRepository;
import fr.eseo.webcube.api.model.CustomUserDetails;
import fr.eseo.webcube.api.model.User;


@Configuration
@EnableWebSecurity
public class ApplicationSecurity {

    @Autowired
    UserTokenAzure userDetailsResponse;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private UserRepository userRepository;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers("/api/auth").permitAll() // Endpoint public accessible sans authentification
                //.antMatchers("/**").permitAll()
                .antMatchers("/ws/**").permitAll()
                .antMatchers("/api/tp").permitAll()
                .anyRequest().authenticated();

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

}
