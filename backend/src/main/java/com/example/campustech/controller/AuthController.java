package com.example.campustech.controller;

import com.example.campustech.controller.dto.request.LoginRequestDTO;
import com.example.campustech.controller.dto.request.UserRegistrationDTO;
import com.example.campustech.controller.dto.response.LoginResponseDTO;
import com.example.campustech.model.entity.User;
import com.example.campustech.model.service.AuthService;
import com.example.campustech.model.repository.UserRepository;
import com.example.campustech.utils.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public AuthController(AuthService authService, UserRepository userRepository, JwtUtils jwtUtils) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDTO dto) {
        System.out.println("🔹 Registering new user: " + dto.getEmail());

        authService.registerUser(dto);

        System.out.println("✅ Registration successful for: " + dto.getEmail());
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        System.out.println("🔹 Login request received for: " + loginRequest.getEmail());

        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        if (user == null) {
            System.out.println("❌ User not found: " + loginRequest.getEmail());
            return ResponseEntity.status(400).body(new LoginResponseDTO(null, "Invalid username."));
        }

        System.out.println("👤 User found: " + user.getEmail());

        if (!loginRequest.getPassword().equals(user.getPassword())) {
            System.out.println("❌ Password mismatch!");
            return ResponseEntity.status(401).body(new LoginResponseDTO(null, "Invalid password."));
        }

        System.out.println("✅ Password matched! Generating token...");
        String jwtToken = jwtUtils.generateToken(user);

        return ResponseEntity.ok(new LoginResponseDTO(jwtToken, "Login successful"));
    }

}
