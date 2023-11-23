package fr.eseo.webcube.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;


@SpringBootApplication
@EnableResourceServer
@EnableCaching
public class ApiApplication implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(ApiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("RUNNING ! API");
        // Ajoutez ici le code que vous souhaitez exécuter au démarrage de l'application
    }
}

