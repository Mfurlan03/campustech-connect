package com.example.campustech.controller; // Ensure correct package

import com.example.campustech.controller.dto.request.LaptopRequestDTO;
// No longer need LoginRequestDTO, UserRegistrationDTO, LoginResponseDTO for setup
import com.example.campustech.controller.dto.response.LaptopResponseDTO;
import com.example.campustech.model.entity.Laptop;
import com.example.campustech.model.entity.Seller;
// No longer need User for setup
import com.example.campustech.model.repository.SellerRepository;
// No longer need UserRepository, AuthService, PasswordEncoder, AuthenticationManager for setup
import com.example.campustech.model.service.LaptopService;
import com.example.campustech.utils.JwtUtils; // Still need for @MockBean
import com.fasterxml.jackson.databind.ObjectMapper; // For converting objects to JSON
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*; // Use JUnit assertions

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // Configure MockMvc
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
// Import @WithMockUser
import org.springframework.security.test.context.support.WithMockUser;
// No longer need Authentication/SecurityContext imports for manual setup
import org.springframework.test.web.servlet.MockMvc;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*; // For jsonPath assertions like is(), hasSize()
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print; // Helpful for debugging
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc // Automatically configure MockMvc
class LaptopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Mocks for Controller Dependencies ---
    @MockBean
    private LaptopService laptopService;
    @MockBean
    private SellerRepository sellerRepository; // Mocked for direct controller calls
    @MockBean // Keep mock even if unused directly, as it's a bean in context
    private JwtUtils jwtUtils;
    // Remove mocks related only to authService setup
    // @MockBean private UserRepository userRepository;
    // @MockBean private PasswordEncoder passwordEncoder;
    // @MockBean private AuthenticationManager authenticationManager;

    // Remove authService Autowired
    // @Autowired private AuthService authService;

    // --- Test Data ---
    private LaptopResponseDTO laptopDto1;
    private LaptopResponseDTO laptopDto2;
    private Seller testSeller;
    // Use a unique email for testing
    private final String testSellerEmail = "seller.laptop.test.final3@univ.ca";
    // Password no longer needed for setup
    // private final String testSellerPassword = "LaptopTestPassFinal3";
    private final Long testSellerId = 113L; // Use a unique ID
    private final Long laptopId1 = 1L; // Example laptop ID
    private final Long laptopId2 = 3L; // Example laptop ID
    // Token no longer needed
    // private String testAuthToken;

    @BeforeEach
    void setUp() {
        System.out.println("=== Setting up Mocks and Test Data ===");

        // 1. Create Seller object
        testSeller = new Seller();
        testSeller.setId(testSellerId); // Set ID directly for mocking consistency
        testSeller.setEmail(testSellerEmail);
        // Password not needed here testSeller.setPassword(testSellerPassword);
        testSeller.setIsAdminVerified(true);
        testSeller.setUniversityId("UNIV_" + testSellerEmail.split("@")[0]);

        // 2. Mock Controller's Seller Lookup
        // This is the key mock needed when @WithMockUser provides the username
        when(sellerRepository.findByEmail(testSellerEmail)).thenReturn(Optional.of(testSeller));
        // Mock for specific failure cases
        when(sellerRepository.findByEmail(eq("unknown.seller@univ.ca"))).thenReturn(Optional.empty());
        // Mock for unverified seller test
        Seller unverifiedSeller = new Seller();
        unverifiedSeller.setId(66L); // Assign ID
        unverifiedSeller.setEmail("unverified.seller6@univ.ca");
        unverifiedSeller.setIsAdminVerified(false);
        when(sellerRepository.findByEmail("unverified.seller6@univ.ca")).thenReturn(Optional.of(unverifiedSeller));


        // 3. Setup Laptop DTOs
        laptopDto1 = new LaptopResponseDTO(laptopId1, "Apple", "MacBook Pro 16\"", 2499.99, "16GB", "1TB SSD", "M2 Pro", "M2 Pro 19-core", true);
        laptopDto2 = new LaptopResponseDTO(laptopId2, "Dell", "XPS 15", 1899.99, "32GB", "1TB SSD", "i7-12700H", "RTX 3050 Ti", true);

        System.out.println("=== Setup Complete. Test Seller ID: " + testSeller.getId() + " ===");
    }


    // --- Test Methods ---

    @Test
    void getAllLaptops_ReturnsListOfLaptops() throws Exception {
        // (No changes needed)
        System.out.println("=== Test: getAllLaptops_ReturnsListOfLaptops ===");
        List<LaptopResponseDTO> expectedLaptops = Arrays.asList(laptopDto1, laptopDto2);
        when(laptopService.getAllLaptops()).thenReturn(expectedLaptops);
        mockMvc.perform(get("/laptops"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(laptopId1.intValue())))
                .andExpect(jsonPath("$[1].id", is(laptopId2.intValue())));
        verify(laptopService).getAllLaptops();
        System.out.println("=== Passed ===");
    }

    @Test
    @WithMockUser(username = testSellerEmail) // <--- Use annotation to set SecurityContext
    void createLaptop_ValidRequest_ReturnsCreatedLaptop() throws Exception {
        System.out.println("=== Test: createLaptop_ValidRequest_ReturnsCreatedLaptop ===");
        // Arrange
        LaptopRequestDTO requestDto = new LaptopRequestDTO(); // Populate DTO...
        requestDto.setBrand("TestCreate");
        requestDto.setModel("Create Model");
        requestDto.setPrice(999.99);
        requestDto.setRam("8GB");
        requestDto.setStorage("256GB SSD");
        requestDto.setCpu("i5");
        requestDto.setSellerId(testSellerId); // DTO requires it

        Laptop createdLaptop = new Laptop(); // Populate...
        createdLaptop.setId(100L);
        createdLaptop.setBrand(requestDto.getBrand());
        createdLaptop.setModel(requestDto.getModel());
        createdLaptop.setPrice(requestDto.getPrice());
        createdLaptop.setRam(requestDto.getRam());
        createdLaptop.setStorage(requestDto.getStorage());
        createdLaptop.setCpu(requestDto.getCpu());
        createdLaptop.setSeller(testSeller); // Associate with the seller object
        createdLaptop.setIsVerified(false);

        LaptopResponseDTO responseDto = new LaptopResponseDTO(100L, "TestCreate", "Create Model", 999.99, "8GB", "256GB SSD", "i5", null, false);

        when(laptopService.createLaptop(eq(testSellerId), any(LaptopRequestDTO.class))).thenReturn(createdLaptop);
        when(laptopService.convertToDTO(createdLaptop)).thenReturn(responseDto);
        // sellerRepository.findByEmail mock is in setup

        // Act & Assert
        mockMvc.perform(post("/laptops")
                        // No header or .with() needed, @WithMockUser handles it
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.brand", is("TestCreate")));

        // Verification
        verify(sellerRepository).findByEmail(testSellerEmail); // Controller verifies seller
        verify(laptopService).createLaptop(eq(testSellerId), any(LaptopRequestDTO.class));
        verify(laptopService).convertToDTO(createdLaptop);
        System.out.println("=== Passed ===");
    }



    @Test
    @WithMockUser(username = testSellerEmail) // Use annotation
    void getSellerLaptops_ReturnsSellerSpecificLaptops() throws Exception {
        System.out.println("=== Test: getSellerLaptops_ReturnsSellerSpecificLaptops ===");
        // Arrange
        List<LaptopResponseDTO> sellerLaptops = Collections.singletonList(laptopDto1);
        when(laptopService.getLaptopsBySeller(testSellerId)).thenReturn(sellerLaptops);
        // sellerRepository.findByEmail mock is in setup

        // Act & Assert
        mockMvc.perform(get("/laptops/seller")
                        // No header or .with() needed
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(laptopId1.intValue())));

        // Verification
        verify(sellerRepository).findByEmail(testSellerEmail);
        verify(laptopService).getLaptopsBySeller(testSellerId);
        System.out.println("=== Passed ===");
    }



    @Test
    @WithMockUser(username = testSellerEmail) // Use annotation
    void deleteLaptop_OwnedLaptop_ReturnsSuccess() throws Exception {
        System.out.println("=== Test: deleteLaptop_OwnedLaptop_ReturnsSuccess ===");
        // Arrange
        Long laptopToDeleteId = laptopId1;
        doNothing().when(laptopService).deleteLaptop(testSellerId, laptopToDeleteId);
        // sellerRepository.findByEmail mock is in setup

        // Act & Assert
        mockMvc.perform(delete("/laptops/{id}", laptopToDeleteId)
                        // No header or .with() needed
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Laptop deleted successfully."));

        // Verification
        verify(sellerRepository).findByEmail(testSellerEmail);
        verify(laptopService).deleteLaptop(testSellerId, laptopToDeleteId);
        System.out.println("=== Passed ===");
    }

}