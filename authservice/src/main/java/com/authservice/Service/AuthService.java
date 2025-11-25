package com.authservice.Service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.authservice.Entity.User;
import com.authservice.Payload.APIResponse;
import com.authservice.Payload.UserDto;
import com.authservice.Repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public APIResponse<String> register(UserDto dto) {

        APIResponse<String> response = new APIResponse<>();

        // Basic null validations
        if (dto.getName() == null || dto.getName().isBlank() ||
                dto.getUsername() == null || dto.getUsername().isBlank() ||
                dto.getEmail() == null || dto.getEmail().isBlank() ||
                dto.getPassword() == null || dto.getPassword().isBlank()) {

            response.setMessage("Registration Failed");
            response.setStatus(400);
            response.setData("Name, username, email, and password are required!");
            return response;
        }

        // Username exists check
        if (userRepository.existsByUsername(dto.getUsername())) {
            response.setMessage("Registration Failed");
            response.setStatus(500);
            response.setData("User with username exists");
            return response;
        }

        // Email exists check
        if (userRepository.existsByEmail(dto.getEmail())) {
            response.setMessage("Registration Failed");
            response.setStatus(500);
            response.setData("User with Email Id exists");
            return response;
        }

        // Hash password
        String hashed = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt(10));

        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(hashed);

        // ⭐ Role is assigned from controller, not request
        String requestedRole = dto.getRole();          // safe because controller sets

        if (requestedRole == null) {
            response.setMessage("Registration Failed");
            response.setStatus(400);
            response.setData("Role was not assigned internally.");
            return response;
        }

        requestedRole = requestedRole.toUpperCase();

        // Validate allowed roles
        if (!requestedRole.equals("USER") &&
                !requestedRole.equals("OWNER") &&
                !requestedRole.equals("ADMIN")) {

            response.setMessage("Registration Failed");
            response.setStatus(500);
            response.setData("Invalid role! Allowed: USER, OWNER, ADMIN");
            return response;
        }

        // Add ROLE_ prefix
        String role = "ROLE_" + requestedRole;
        user.setRole(role);

        // Save user
        userRepository.save(user);

        response.setMessage("Registration Done");
        response.setStatus(201);
        response.setData("User registered with role: " + role);

        return response;
    }
}
