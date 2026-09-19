package com.agribridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AgriBridge - Agribusiness Operations and Market Intelligence Platform.
 *
 * Entry point for the Spring Boot application. AgriBridge is built as a
 * modular monolith: each business area (auth, farm, crop, harvest, buyer)
 * lives in its own top-level package with its own entity/repository/service/
 * controller, rather than as separate deployable services. See
 * docs/architecture.md for the full architectural reasoning.
 *
 * Week 2 status: application boots, connects to MySQL, and exposes working
 * Authentication and Farm Management endpoints. Crop, Harvest Lot, Buyer
 * Request and Matching modules currently expose only their JPA entities and
 * repositories (schema is ready; service/controller layers are planned for
 * Week 3 and Week 4 — see docs/roadmap.md).
 *
 * Individual project developed by Karman Kaur Nayyar, Junior Software
 * Developer Intern.
 */
@SpringBootApplication
public class AgribridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgribridgeApplication.class, args);
    }
}
