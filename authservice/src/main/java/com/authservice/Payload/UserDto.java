package com.authservice.Payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserDto {

    @JsonIgnore
    private long id;

//    @NotBlank(message = "Name cannot be empty")
    private String name;

//    @NotBlank(message = "Username cannot be empty")
//    @Size(min = 2, max = 15, message = "Username must be between 2 to 15 characters")
    private String username;

//    @NotBlank(message = "Email cannot be empty")
//    @Email(message = "Enter a valid email")
    private String email;

    // ⭐ Strong Password Validation
//    @NotBlank(message = "Password cannot be empty")
//    @Size(min = 8, max = 15, message = "Password must be 8 to 15 characters long")
//    @Pattern(
//            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
//            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
//    )
    private String password;

    @JsonIgnore
    private String role;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
