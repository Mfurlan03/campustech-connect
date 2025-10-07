package com.example.campustech.service;

import com.example.campustech.controller.dto.request.LoginRequestDTO;
import com.example.campustech.controller.dto.request.UserRegistrationDTO;
import com.example.campustech.controller.dto.response.LoginResponseDTO;
import com.example.campustech.model.entity.Buyer;
import com.example.campustech.model.entity.Seller; // Import Seller for role testing
import com.example.campustech.model.entity.User;
import com.example.campustech.model.repository.UserRepository;
import com.example.campustech.model.service.AuthService;
import com.example.campustech.utils.JwtUtils; // Import needed for mocking JwtUtils
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager; // Import needed for mocking AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder; // Import needed for mocking PasswordEncoder

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*; // Import Mockito static methods for verify etc.

@SpringBootTest
class AuthServiceTest {

    @MockBean
    private UserRepository userRepository;

    // --- Mocks needed based on AuthService constructor ---
    @MockBean
    private PasswordEncoder passwordEncoder; // Although not used for encoding in current code, it's injected

    @MockBean
    private AuthenticationManager authenticationManager; // Also injected, needs mocking

    @MockBean
    private JwtUtils jwtUtils;
    // --- End of required mocks ---

    @Autowired
    private AuthService authService;

    @Test
    void registerAndLogin_Successful_Buyer() { // Renamed slightly for clarity
        System.out.println("=== Starting test: registerAndLogin_Successful_Buyer ===");

        // --- 1. Setup test data ---
        String testEmail = "testbuyer@univ.ca"; // Unique email per test
        String testPassword = "password123";
        String testRole = "BUYER";

        // Registration DTO
        UserRegistrationDTO registrationDto = new UserRegistrationDTO();
        registrationDto.setEmail(testEmail);
        registrationDto.setPassword(testPassword);
        registrationDto.setRole(testRole);

        // Login DTO
        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setEmail(testEmail);
        loginDto.setPassword(testPassword);

        // Expected User object after registration (simulating what save would return)
        Buyer expectedRegisteredUser = new Buyer(); // Use Buyer since role is BUYER
        expectedRegisteredUser.setId(1L); // Simulate DB assigning an ID
        expectedRegisteredUser.setEmail(testEmail);
        expectedRegisteredUser.setPassword(testPassword); // Plain text password
        expectedRegisteredUser.setMajor("Undeclared"); // Default set in AuthService
        expectedRegisteredUser.setBudget(1000.00);   // Default set in AuthService

        // Expected Token for Login
        String expectedJwtToken = "mock.jwt.token.buyer";

        System.out.println("Test data created:");
        System.out.println(" - Registration DTO: " + registrationDto);
        System.out.println(" - Login DTO: " + loginDto);
        System.out.println(" - Expected Registered User: " + expectedRegisteredUser);
        System.out.println(" - Expected JWT Token: " + expectedJwtToken);

        // --- 2. Mock repository and utils behavior ---

        // Registration mocks:
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(userRepository.save(any(Buyer.class))).thenAnswer(invocation -> { // Specify Buyer
            Buyer userToSave = invocation.getArgument(0);
            System.out.println("Mock repository saving buyer: " + userToSave);
            assertEquals(testEmail, userToSave.getEmail());
            assertEquals(testPassword, userToSave.getPassword());
            // Simulate saving by returning the object with an ID
            expectedRegisteredUser.setEmail(userToSave.getEmail());
            expectedRegisteredUser.setPassword(userToSave.getPassword());
            expectedRegisteredUser.setMajor(userToSave.getMajor());
            expectedRegisteredUser.setBudget(userToSave.getBudget());
            return expectedRegisteredUser;
        });

        // Login mocks:
        // Chain mocks for findByEmail: first for registration check, second for login
        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.empty()) // For registration check
                .thenReturn(Optional.of(expectedRegisteredUser)); // For login

        // Mock: JWT token generation
        when(jwtUtils.generateToken(expectedRegisteredUser)).thenReturn(expectedJwtToken); // Be specific

        System.out.println("Repository and Utils mocking setup complete");

        // --- 3. Execute the service methods ---
        System.out.println("Calling authService.registerUser()...");
        User actualRegisteredUser = authService.registerUser(registrationDto);
        System.out.println("Service returned registered user: " + actualRegisteredUser);

        System.out.println("Calling authService.login()...");
        LoginResponseDTO loginResponse = authService.login(loginDto);
        System.out.println("Service returned login response: " + loginResponse);

        // --- 4. Verify results ---
        System.out.println("Performing assertions...");
        assertNotNull(actualRegisteredUser, "Registered user should not be null");
        assertTrue(actualRegisteredUser instanceof Buyer, "Registered user should be an instance of Buyer");
        assertEquals(expectedRegisteredUser.getId(), actualRegisteredUser.getId());
        assertEquals(testEmail, actualRegisteredUser.getEmail());
        assertEquals(testPassword, actualRegisteredUser.getPassword());
        assertEquals("Undeclared", ((Buyer)actualRegisteredUser).getMajor());
        assertEquals(1000.00, ((Buyer)actualRegisteredUser).getBudget());

        assertNotNull(loginResponse, "Login response should not be null");
        assertEquals("Login successful", loginResponse.getMessage());
        assertEquals(expectedJwtToken, loginResponse.getToken());

        // Verify interactions
        verify(userRepository, times(2)).findByEmail(testEmail); // Called for reg check and login
        verify(userRepository).save(any(Buyer.class));
        verify(jwtUtils).generateToken(expectedRegisteredUser);
        System.out.println("All assertions passed!");
        System.out.println("=== Test completed successfully: registerAndLogin_Successful_Buyer ===");
    }

    @Test
    void registerAndLogin_Successful_Seller() { // Test for Seller role
        System.out.println("=== Starting test: registerAndLogin_Successful_Seller ===");

        // --- 1. Setup test data ---
        String testEmail = "testseller@univ.ca"; // Unique email
        String testPassword = "securePassword456";
        String testRole = "SELLER";

        UserRegistrationDTO registrationDto = new UserRegistrationDTO();
        registrationDto.setEmail(testEmail);
        registrationDto.setPassword(testPassword);
        registrationDto.setRole(testRole);

        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setEmail(testEmail);
        loginDto.setPassword(testPassword);

        Seller expectedRegisteredUser = new Seller();
        expectedRegisteredUser.setId(2L);
        expectedRegisteredUser.setEmail(testEmail);
        expectedRegisteredUser.setPassword(testPassword); // Plain text password
        expectedRegisteredUser.setUniversityId("UNIV_testseller"); // Default set in AuthService

        String expectedJwtToken = "mock.jwt.token.seller";

        System.out.println("Test data created...");

        // --- 2. Mock repository and utils behavior ---
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(userRepository.save(any(Seller.class))).thenAnswer(invocation -> { // Specify Seller
            Seller userToSave = invocation.getArgument(0);
            System.out.println("Mock repository saving seller: " + userToSave);
            assertEquals(testEmail, userToSave.getEmail());
            assertEquals(testPassword, userToSave.getPassword());
            assertEquals("UNIV_testseller", userToSave.getUniversityId());
            // Simulate saving by returning the object with an ID
            expectedRegisteredUser.setEmail(userToSave.getEmail());
            expectedRegisteredUser.setPassword(userToSave.getPassword());
            expectedRegisteredUser.setUniversityId(userToSave.getUniversityId());
            return expectedRegisteredUser;
        });

        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.empty()) // For registration check
                .thenReturn(Optional.of(expectedRegisteredUser)); // For login

        when(jwtUtils.generateToken(expectedRegisteredUser)).thenReturn(expectedJwtToken); // Be specific

        System.out.println("Repository and Utils mocking setup complete");

        // --- 3. Execute the service methods ---
        System.out.println("Calling authService.registerUser()...");
        User actualRegisteredUser = authService.registerUser(registrationDto);
        System.out.println("Service returned registered user: " + actualRegisteredUser);

        System.out.println("Calling authService.login()...");
        LoginResponseDTO loginResponse = authService.login(loginDto);
        System.out.println("Service returned login response: " + loginResponse);

        // --- 4. Verify results ---
        System.out.println("Performing assertions...");
        assertNotNull(actualRegisteredUser, "Registered user should not be null");
        assertTrue(actualRegisteredUser instanceof Seller, "Registered user should be an instance of Seller");
        assertEquals(expectedRegisteredUser.getId(), actualRegisteredUser.getId());
        assertEquals(testEmail, actualRegisteredUser.getEmail());
        assertEquals(testPassword, actualRegisteredUser.getPassword());
        assertEquals("UNIV_testseller", ((Seller)actualRegisteredUser).getUniversityId());

        assertNotNull(loginResponse, "Login response should not be null");
        assertEquals("Login successful", loginResponse.getMessage());
        assertEquals(expectedJwtToken, loginResponse.getToken());

        // Verify interactions
        verify(userRepository, times(2)).findByEmail(testEmail);
        verify(userRepository).save(any(Seller.class));
        verify(jwtUtils).generateToken(expectedRegisteredUser);
        System.out.println("All assertions passed!");
        System.out.println("=== Test completed successfully: registerAndLogin_Successful_Seller ===");
    }


    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        System.out.println("=== Starting test: registerUser_EmailAlreadyExists_ThrowsException ===");
        // Arrange
        String existingEmail = "existing@univ.ca";
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail(existingEmail);
        dto.setPassword("password");
        dto.setRole("SELLER");

        // Mock repository to find an existing user
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(new User())); // Return any non-empty optional

        System.out.println("Test data created & Mocks setup (existing user)");

        // Act & Assert
        System.out.println("Calling authService.registerUser() expecting exception...");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser(dto);
        });

        assertEquals("Email is already registered.", exception.getMessage());
        System.out.println("Correct exception thrown: " + exception.getMessage());

        // Verify that save was NOT called
        verify(userRepository, never()).save(any(User.class));
        System.out.println("=== Test completed successfully: registerUser_EmailAlreadyExists_ThrowsException ===");
    }

    @Test
    void registerUser_InvalidEmailFormat_ThrowsException() {
        System.out.println("=== Starting test: registerUser_InvalidEmailFormat_ThrowsException ===");
        // Arrange
        String invalidEmail = "test@gmail.com"; // Does not end with @univ.ca
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail(invalidEmail);
        dto.setPassword("password");
        dto.setRole("BUYER");

        // No need to mock findByEmail or save, as the check happens before them.
        System.out.println("Test data created (invalid email format)");

        // Act & Assert
        System.out.println("Calling authService.registerUser() expecting exception...");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser(dto);
        });

        assertEquals("Only university emails (@univ.ca) allowed", exception.getMessage());
        System.out.println("Correct exception thrown: " + exception.getMessage());

        // Verify that findByEmail and save were NOT called
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        System.out.println("=== Test completed successfully: registerUser_InvalidEmailFormat_ThrowsException ===");
    }

    @Test
    void registerUser_InvalidRole_ThrowsException() {
        System.out.println("=== Starting test: registerUser_InvalidRole_ThrowsException ===");
        // Arrange
        String validEmail = "valid-role-test@univ.ca";
        String invalidRole = "ADMIN"; // Not "BUYER" or "SELLER"
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail(validEmail);
        dto.setPassword("password");
        dto.setRole(invalidRole);

        // Mock findByEmail to return empty, as the role check happens after email format/existence check
        when(userRepository.findByEmail(validEmail)).thenReturn(Optional.empty());
        System.out.println("Test data created & Mocks setup (invalid role)");

        // Act & Assert
        System.out.println("Calling authService.registerUser() expecting exception...");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser(dto);
        });

        assertEquals("Invalid role. Must be 'BUYER' or 'SELLER'.", exception.getMessage());
        System.out.println("Correct exception thrown: " + exception.getMessage());

        // Verify that findByEmail WAS called, but save was NOT
        verify(userRepository).findByEmail(validEmail);
        verify(userRepository, never()).save(any(User.class));
        System.out.println("=== Test completed successfully: registerUser_InvalidRole_ThrowsException ===");
    }

    @Test
    void login_UserNotFound_ReturnsErrorResponse() {
        System.out.println("=== Starting test: login_UserNotFound_ReturnsErrorResponse ===");
        // Arrange
        String nonExistentEmail = "nosuchuser@univ.ca";
        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setEmail(nonExistentEmail);
        loginDto.setPassword("password");

        // Mock repository to find no user
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        System.out.println("Test data created & Mocks setup (user not found)");

        // Act
        System.out.println("Calling authService.login()...");
        LoginResponseDTO response = authService.login(loginDto);
        System.out.println("Service returned login response: " + response);


        // Assert
        assertNotNull(response);
        assertNull(response.getToken(), "Token should be null for failed login");
        assertEquals("User not found", response.getMessage(), "Message should indicate user not found");

        // Verify findByEmail was called, but generateToken was not
        verify(userRepository).findByEmail(nonExistentEmail);
        verify(jwtUtils, never()).generateToken(any(User.class));
        System.out.println("Assertions passed!");
        System.out.println("=== Test completed successfully: login_UserNotFound_ReturnsErrorResponse ===");
    }

    @Test
    void login_IncorrectPassword_ReturnsErrorResponse() {
        System.out.println("=== Starting test: login_IncorrectPassword_ReturnsErrorResponse ===");
        // Arrange
        String testEmail = "userexists@univ.ca";
        String correctPassword = "correctPassword";
        String incorrectPassword = "wrongPassword";

        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setEmail(testEmail);
        loginDto.setPassword(incorrectPassword);

        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setEmail(testEmail);
        // Set the *correct* plain text password in the mock user object
        existingUser.setPassword(correctPassword);

        // Mock repository to find the user
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(existingUser));
        // Note: No need to mock JwtUtils as it shouldn't be called if password fails

        System.out.println("Test data created & Mocks setup (user found, wrong password)");

        // Act
        System.out.println("Calling authService.login()...");
        LoginResponseDTO response = authService.login(loginDto);
        System.out.println("Service returned login response: " + response);

        // Assert
        assertNotNull(response);
        assertNull(response.getToken(), "Token should be null for failed login");
        assertEquals("Invalid username or password.", response.getMessage(), "Message should indicate invalid credentials");

        // Verify findByEmail was called, but generateToken was not
        verify(userRepository).findByEmail(testEmail);
        verify(jwtUtils, never()).generateToken(any(User.class));
        System.out.println("Assertions passed!");
        System.out.println("=== Test completed successfully: login_IncorrectPassword_ReturnsErrorResponse ===");
    }
}