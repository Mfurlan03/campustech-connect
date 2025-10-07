package com.example.campustech.model.service;

import com.example.campustech.controller.dto.request.LoginRequestDTO;
import com.example.campustech.controller.dto.request.UserRegistrationDTO;
import com.example.campustech.controller.dto.response.LoginResponseDTO;
import com.example.campustech.model.entity.Buyer;
import com.example.campustech.model.entity.Seller;
import com.example.campustech.model.entity.User;
import com.example.campustech.model.repository.UserRepository;
import com.example.campustech.utils.JwtUtils;
import com.example.campustech.utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtil;  // ✅ Inject JWT utility

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtils jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Register User
    public User registerUser(UserRegistrationDTO dto) {
        System.out.println("🔹 Incoming registration request: " + dto.getEmail());

        if (!dto.getEmail().endsWith("@univ.ca")) {
            throw new IllegalArgumentException("Only university emails (@univ.ca) allowed");
        }

        // Ensure user does not already exist
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        User user;
        if ("BUYER".equalsIgnoreCase(dto.getRole())) {
            Buyer buyer = new Buyer();
            buyer.setMajor("Undeclared");
            buyer.setBudget(1000.00);  // ✅ Set a default budget (or get from request DTO)
            user = buyer;
        } else if ("SELLER".equalsIgnoreCase(dto.getRole())) {
            Seller seller = new Seller();
            seller.setUniversityId("UNIV_" + dto.getEmail().split("@")[0]);
            user = seller;
        } else {
            throw new IllegalArgumentException("Invalid role. Must be 'BUYER' or 'SELLER'.");
        }

        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // 🔥 Storing as plain text (No encoding)

        System.out.println("✅ User registered successfully: " + user.getEmail());
        return userRepository.save(user);
    }


    // Login Method (Returns JWT Token)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElse(null);

        if (user == null) {
            System.out.println("❌ User not found: " + dto.getEmail()); // ✅ Debugging
            return new LoginResponseDTO(null, "User not found");
        }

        System.out.println("🔹 Found user: " + user.getEmail()); // ✅ Debugging

        if (!dto.getPassword().equals(user.getPassword())) {
            System.out.println("❌ Passwords do not match!"); // ✅ Debugging
            return new LoginResponseDTO(null, "Invalid username or password.");
        }

        System.out.println("✅ Passwords match! Generating token..."); // ✅ Debugging
        String token = jwtUtil.generateToken(user);
        return new LoginResponseDTO(token, "Login successful");
    }




}