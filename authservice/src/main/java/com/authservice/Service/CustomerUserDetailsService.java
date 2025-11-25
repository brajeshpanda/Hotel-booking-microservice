package com.authservice.Service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.authservice.Entity.User;
import com.authservice.Repository.UserRepository;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        System.out.println("🔍 Loading user by username: " + username);

        User user = userRepository.findByUsername(username);


        if (user == null) {
            System.out.println(" User not found in DB: " + username);
            throw new UsernameNotFoundException("User not found: " + username);
        }

        System.out.println(" User found: " + user.getUsername());
        System.out.println(" Stored role = " + user.getRole());
        System.out.println(" Stored password = " + user.getPassword());


        String role = user.getRole();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(role))
        );
    }
}
