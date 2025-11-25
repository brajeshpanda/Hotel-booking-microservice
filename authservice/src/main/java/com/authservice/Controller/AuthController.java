package com.authservice.Controller;

import com.authservice.Service.EmailProducer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import com.authservice.Entity.User;
import com.authservice.Payload.APIResponse;
import com.authservice.Payload.LoginDto;
import com.authservice.Payload.UserDto;
import com.authservice.Repository.UserRepository;
import com.authservice.Service.AuthService;
import com.authservice.Service.JwtService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final Map<String, String> loginOtpStore = new HashMap<>();

    private final Map<String, String> forgotPasswordOtpStore = new HashMap<>();


    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private EmailProducer emailProducer;


    @PostMapping("/register")
    public ResponseEntity<APIResponse<String>> register(@Valid @RequestBody UserDto dto) {
        dto.setRole("USER");
        APIResponse<String> response = authService.register(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @PostMapping("/register-owner")
    public ResponseEntity<APIResponse<String>> registerOwner(@Valid @RequestBody UserDto dto) {
        dto.setRole("OWNER");
        APIResponse<String> response = authService.register(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // REGISTER — ADMIN
    @PostMapping("/register-admin")
    public ResponseEntity<APIResponse<String>> registerAdmin(@Valid @RequestBody UserDto dto) {
        dto.setRole("ADMIN");
        APIResponse<String> response = authService.register(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<APIResponse<String>> login(@Valid @RequestBody LoginDto loginDto) {

        APIResponse<String> response = new APIResponse<>();


        if (jwtService.isUserLoggedIn(loginDto.getUsername())) {
            response.setStatus(400);
            response.setMessage("Already Logged In");
            response.setData("Please logout before logging in again.");
            return ResponseEntity.badRequest().body(response);
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                );

        try {
            Authentication authentication = authManager.authenticate(authToken);

            if (!authentication.isAuthenticated()) {
                throw new Exception("Auth failed");
            }

            User user = userRepository.findByUsername(loginDto.getUsername());

            // OTP
            String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
            loginOtpStore.put(user.getEmail(), otp);

            emailProducer.sendLoginOtp(user.getEmail(), otp);

            response.setStatus(200);
            response.setMessage("OTP Sent");
            response.setData("OTP has been sent to your email.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setStatus(401);
            response.setMessage("Login Failed");
            response.setData("Invalid username or password.");
            return ResponseEntity.status(401).body(response);
        }
    }

    // -----------------------------------------------------------------
    // LOGIN STEP 2 → VERIFY OTP + RETURN JWT
    // -----------------------------------------------------------------
    @PostMapping("/login/verify-otp")
    public ResponseEntity<APIResponse<String>> verifyLoginOtp(
            @RequestParam("email") String email,
            @RequestParam("otp") String otp) {

        APIResponse<String> response = new APIResponse<>();

        if (!loginOtpStore.containsKey(email) ||
                !loginOtpStore.get(email).equals(otp)) {

            response.setStatus(401);
            response.setMessage("Invalid OTP");
            response.setData("Wrong or expired OTP.");
            return ResponseEntity.status(401).body(response);
        }

        loginOtpStore.remove(email);

        User user = userRepository.findByEmail(email);

        if (user == null) {
            response.setStatus(404);
            response.setMessage("User Not Found");
            response.setData("Invalid email.");
            return ResponseEntity.status(404).body(response);
        }

        String jwtToken = jwtService.generateToken(user.getUsername(), user.getRole());

        emailProducer.sendLoginEmail(email, user.getName());

        response.setStatus(200);
        response.setMessage("Login Successful via OTP");
        response.setData(jwtToken);

        return ResponseEntity.ok(response);
    }

    // -----------------------------------------------------------------
    // FORGOT PASSWORD → SEND OTP
    // -----------------------------------------------------------------
    @PostMapping("/forgot-password")
    public ResponseEntity<APIResponse<String>> forgotPassword(@RequestParam("email") String email) {

        APIResponse<String> response = new APIResponse<>();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            response.setStatus(404);
            response.setMessage("User Not Found");
            response.setData("Email does not exist.");
            return ResponseEntity.status(404).body(response);
        }

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        // ⭐ STORE OTP HERE
        forgotPasswordOtpStore.put(email, otp);

        // Send OTP via Kafka to email service
        emailProducer.sendForgotPasswordOtp(email, otp);

        response.setStatus(200);
        response.setMessage("OTP Sent");
        response.setData("OTP has been sent to your email.");

        return ResponseEntity.ok(response);
    }


    // -----------------------------------------------------------------
    // RESET PASSWORD
    // -----------------------------------------------------------------
    @PostMapping("/reset-password")
    public ResponseEntity<APIResponse<String>> resetPassword(
            @RequestParam("email") String email,
            @RequestParam("otp") String otp,
            @RequestParam("newPassword") String newPassword) {

        APIResponse<String> response = new APIResponse<>();

        // ⭐ VERIFY OTP FIRST
        if (!forgotPasswordOtpStore.containsKey(email) ||
                !forgotPasswordOtpStore.get(email).equals(otp)) {

            response.setStatus(401);
            response.setMessage("Invalid OTP");
            response.setData("Wrong or expired OTP.");
            return ResponseEntity.status(401).body(response);
        }

        // ⭐ Remove OTP after successful verification
        forgotPasswordOtpStore.remove(email);

        User user = userRepository.findByEmail(email);

        if (user == null) {
            response.setStatus(404);
            response.setMessage("User Not Found");
            response.setData("Invalid email address");
            return ResponseEntity.status(404).body(response);
        }

        // Update password
        String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt(10));
        user.setPassword(hashed);
        userRepository.save(user);

        // Send success email via Kafka
        emailProducer.sendPasswordResetSuccess(email);

        response.setStatus(200);
        response.setMessage("Password Reset Successful");
        response.setData("Password updated successfully.");

        return ResponseEntity.ok(response);
    }



    // -----------------------------------------------------------------
    // LOGOUT
    // -----------------------------------------------------------------
    @PostMapping("/logout")
    public ResponseEntity<APIResponse<String>> logout(
            @RequestHeader("Authorization") String header) {

        APIResponse<String> response = new APIResponse<>();

        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(400);
            response.setMessage("Invalid Token");
            response.setData("Token not provided");
            return ResponseEntity.badRequest().body(response);
        }

        String token = header.substring(7);
        String username = jwtService.validateTokenAndRetrieveSubject(token);

        if (username == null) {
            response.setStatus(400);
            response.setMessage("Invalid or Expired Token");
            response.setData("Cannot logout");
            return ResponseEntity.badRequest().body(response);
        }

        jwtService.invalidateToken(username);

        response.setStatus(200);
        response.setMessage("Logout Successful");
        response.setData("User logged out successfully.");
        return ResponseEntity.ok(response);
    }

    // -----------------------------------------------------------------
    // GET USER
    // -----------------------------------------------------------------
    @GetMapping("/get-user")
    public User getUserByUsername(@RequestParam("username") String username) {
        return userRepository.findByUsername(username);
    }

    // -----------------------------------------------------------------
    // GET ACTIVE TOKEN
    // -----------------------------------------------------------------
    @GetMapping("/my-token/{username}")
    public ResponseEntity<APIResponse<String>> getToken(@PathVariable("username") String username) {

        APIResponse<String> response = new APIResponse<>();
        String token = jwtService.getToken(username);

        if (token == null) {
            response.setStatus(400);
            response.setMessage("No Active Session");
            response.setData("User is logged out or token expired.");
            return ResponseEntity.badRequest().body(response);
        }

        response.setStatus(200);
        response.setMessage("Active Token Retrieved");
        response.setData(token);

        return ResponseEntity.ok(response);
    }
}
