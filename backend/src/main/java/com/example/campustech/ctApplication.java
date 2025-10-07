package com.example.campustech;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ctApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ctApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ctApplication.class, args);
        LOG.info("╔═════════════════════════════════════════════════════════════════╗");
        LOG.info("║   CampusTech Connect Successfully Started!                      ║");
        LOG.info("║   API Base URL: http://localhost:8080/api                       ║");
        LOG.info("║   Database: H2 (In-Memory)                                      ║");  // If you're using MySQL, keep original
        LOG.info("║   Swagger UI: http://localhost:8080/api/swagger-ui/index.html#/ ║");  // Swagger is not mapped correctly
        LOG.info("║   H2 Console: http://localhost:8080/h2-console                  ║");  // Since H2 is running
        LOG.info("╚═════════════════════════════════════════════════════════════════╝");

    }
}