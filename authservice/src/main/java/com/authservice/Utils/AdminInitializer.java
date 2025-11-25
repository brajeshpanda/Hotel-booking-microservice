package com.authservice.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import com.authservice.Entity.User;
import com.authservice.Repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
public class AdminInitializer {

    @Autowired
    private UserRepository userRepository;

    @Value("${app.admin.name}")
    private String adminName;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @PostConstruct
    public void initAdmin() {


        if (userRepository.existsByRole("ROLE_ADMIN")) {
            System.out.println("Admin already exists. Skipping auto-creation.");
            return;
        }

        System.out.println("No admin found. Creating default admin account...");

        User admin = new User();
        admin.setName(adminName);
        admin.setUsername(adminUsername);
        admin.setEmail(adminEmail);

        admin.setPassword(BCrypt.hashpw(adminPassword, BCrypt.gensalt(10)));
        admin.setRole("ROLE_ADMIN");

        userRepository.save(admin);

        System.out.println("Default admin created successfully.");
    }
}
