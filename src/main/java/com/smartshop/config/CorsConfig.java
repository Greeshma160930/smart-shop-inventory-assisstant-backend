package com.smartshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:}")
    private String customAllowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> allowedOriginPatterns = new ArrayList<>(Arrays.asList(
                // Vercel production & preview deployments
                "https://*.vercel.app",
                // Render backend hosting
                "https://*.onrender.com",
                // Local development environments
                "http://localhost:[*]",
                "http://127.0.0.1:[*]"
        ));

        // Support additional custom origins via environment variable/property (comma-separated)
        if (customAllowedOrigins != null && !customAllowedOrigins.isBlank()) {
            Arrays.stream(customAllowedOrigins.split(","))
                    .map(String::trim)
                    .filter(origin -> !origin.isEmpty())
                    .forEach(allowedOriginPatterns::add);
        }

        configuration.setAllowedOriginPatterns(allowedOriginPatterns);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With", "Origin"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
