package com.agribridge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Allows the static HTML/CSS/JS frontend (opened directly from disk or
 * served by a simple local static server, typically on a different origin
 * than the backend) to call the REST API during local development.
 *
 * For a production/pilot deployment this should be tightened to the actual
 * deployed frontend origin instead of a wildcard - see docs/architecture.md,
 * Section "Security".
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
