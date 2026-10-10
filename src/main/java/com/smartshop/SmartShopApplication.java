package com.smartshop;

import com.smartshop.entity.User;
import com.smartshop.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class SmartShopApplication {

    private static final Logger log = LoggerFactory.getLogger(SmartShopApplication.class);

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        log.info("Initialized application default timezone to Asia/Kolkata (IST)");
    }

    public static void main(String[] args) {
        SpringApplication.run(SmartShopApplication.class, args);
    }

    /**
     * Ensures demo account exists with valid BCrypt hash upon application startup.
     */
    @Bean
    public CommandLineRunner initDefaultUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String demoEmail = "demo@smartshop.com";
            if (!userRepository.existsByEmail(demoEmail)) {
                User demoUser = User.builder()
                        .name("Demo Shop Owner")
                        .email(demoEmail)
                        .password(passwordEncoder.encode("Demo@123"))
                        .shopName("Smart Kirana & General Store")
                        .role("ROLE_USER")
                        .build();
                userRepository.save(demoUser);
                log.info("Initialized default demo user: {} / Demo@123", demoEmail);
            }
        };
    }
}
