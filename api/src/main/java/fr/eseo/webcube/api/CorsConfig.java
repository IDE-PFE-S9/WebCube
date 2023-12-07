package fr.eseo.webcube.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${client.host.ip}")
    private String clientHost;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://" + clientHost)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS") // Allow these HTTP methods
                .allowCredentials(true)
                .allowedHeaders("*") // Allow all headers
                .exposedHeaders("Token-Status");
    }
}