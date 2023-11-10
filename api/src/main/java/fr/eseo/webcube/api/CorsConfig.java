package fr.eseo.webcube.api;


import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS") // Allow these HTTP methods
                .allowCredentials(true)
                .allowedHeaders("*"); // Allow all headers
    }

    // @Bean
    // public SecurityFilterChain configure(HttpSecurity http) throws Exception {
    //     http.csrf().disable();
    //     http.cors().disable();
    //     http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    //     http.authorizeRequests()
    //             .antMatchers("/**").permitAll();

    //     return http.build();
    // }
}