package com.authservice.Controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authservice.Entity.User;

@RestController
@RequestMapping("/api/v1/welcome")
public class WelcomeController {
    
    @GetMapping("/message")
    public String welcome(@AuthenticationPrincipal User user) {
        System.out.println(user.getName());
        return "Welcome! The endpoint is working correctly.";
    }
}
