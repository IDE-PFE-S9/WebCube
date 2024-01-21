package fr.eseo.webcube.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(ApiApplication.class);

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        SpringApplication.run(ApiApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("RUNNING ! API");
        // Ajoutez ici le code que vous souhaitez exécuter au démarrage de l'application
    }
}

