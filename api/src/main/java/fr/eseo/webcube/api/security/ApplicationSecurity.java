package fr.eseo.webcube.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class ApplicationSecurity {


    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Configurer les autorisations pour l'endpoint de connexion Azure AD
        http.authorizeRequests()
                .antMatchers("/user").permitAll() // Votre endpoint de connexion
                .anyRequest().permitAll();
                //.and().oauth2Login();

        http.exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> response.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED,
                                ex.getMessage()));

        return http.build();
    }

    /*@Bean
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // Allow this origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS") // Allow these HTTP methods
                .allowCredentials(true)
                .allowedHeaders("*"); // Allow all headers
    }*/
    
    /*@Autowired
    private JwtTokenFilter jwtTokenFilter;

    // ... Autres configurations

    @Bean
    public OidcUserService oidcUserService() {
        return new OidcUserService();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Configurer les autorisations pour l'endpoint de connexion Azure AD
        http.authorizeRequests()
                .antMatchers("/auth/login").permitAll() // Votre endpoint de connexion
                .anyRequest().authenticated()
                .and().oauth2Login();

        http.rememberMe()
                .rememberMeCookieName("monCookie")
                .tokenValiditySeconds(86400);

        http.exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> response.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED,
                                ex.getMessage()));

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /*@Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver() {
        DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(
                oauth2ClientRegistrationRepository, "/auth/login");
        resolver.setAuthorizationRequestBaseUri("/oauth2/authorize");
        return resolver;
    }*/

    /*@Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        accessTokenResponseClient.setRequestEntityConverter(new CustomAuthorizationCodeRequestEntityConverter());
        return accessTokenResponseClient;
    }*/

    /*@Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> userService() {
        OidcUserService oidcUserService = new OidcUserService();
        // Personnalisez la récupération des informations de l'utilisateur à partir du
        // jeton Azure AD ici
        // Utilisez le jeton pour appeler l'API Microsoft Graph pour obtenir les
        // informations de l'utilisateur
        // Créez un objet UserDetails ou un objet personnalisé pour représenter
        // l'utilisateur
        return oidcUserService;
    }*/

    //code afin de se connecter à gitHub
    /*@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorizeRequests -> 
                authorizeRequests
                    .antMatchers("/user").permitAll() // Ajoutez ici les autorisations spécifiques pour "/user"
                    .anyRequest().authenticated()
            )
            .oauth2Login(Customizer.withDefaults())
            .logout(Customizer.withDefaults());

        return http.build();
    }

	
	@Bean
    public UserDetailsService users() {
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("user"))
            .authorities("ROLE_STUDENT")
            //.roles("ROLE_USER")  // Ajoutez le rôle ici
            .build();
        return new InMemoryUserDetailsManager(user);
    }

	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}*/

    /*@Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorizeRequests ->
                authorizeRequests
                    .antMatchers("/login").permitAll()
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer()
                .jwt();

        http.cors();
        http.csrf().disable();

        http.rememberMe()
            .rememberMeCookieName("monCookie")
            .tokenValiditySeconds(86400);

        http.exceptionHandling()
            .authenticationEntryPoint((request, response, ex) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage()));

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService users() {
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("user"))
            .authorities("ROLE_STUDENT")
            //.roles("ROLE_USER")  // Ajoutez le rôle ici
            .build();
        return new InMemoryUserDetailsManager(user);
    }*/
}