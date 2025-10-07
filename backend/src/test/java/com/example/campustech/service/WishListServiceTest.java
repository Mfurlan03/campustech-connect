// Working WishlistController Test:
package com.example.campustech.service;

// --- Authentication related imports ---
import com.example.campustech.controller.dto.request.LoginRequestDTO;
import com.example.campustech.controller.dto.request.UserRegistrationDTO;
import com.example.campustech.model.entity.Buyer;
import com.example.campustech.model.entity.User;
import com.example.campustech.model.repository.UserRepository;
import com.example.campustech.model.service.AuthService;
import com.example.campustech.utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
// --- Wishlist related imports ---
import com.example.campustech.controller.dto.request.WishlistRequestDTO;
import com.example.campustech.controller.dto.response.WishlistResponseDTO;
import com.example.campustech.exceptions.DuplicateWishlistItemException;
import com.example.campustech.model.entity.Laptop;
import com.example.campustech.model.entity.WishlistItem;
import com.example.campustech.model.entity.WishlistKey;
import com.example.campustech.model.repository.BuyerRepository;
import com.example.campustech.model.repository.LaptopRepository;
import com.example.campustech.model.repository.WishlistRepository;
import com.example.campustech.model.service.WishlistService;

// --- JUnit & Mockito imports ---
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq; // Use eq() for specific values
import static org.mockito.Mockito.*;

@SpringBootTest
class WishlistServiceTest {

    // --- Mocks for AuthService Dependencies ---
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtUtils jwtUtils;

    // --- Mocks for WishlistService Dependencies ---
    @MockBean
    private WishlistRepository wishlistRepository;
    @MockBean
    private BuyerRepository buyerRepository;
    @MockBean
    private LaptopRepository laptopRepository;

    // --- Services Under Test (indirectly via WishlistService or directly for setup) ---
    @Autowired
    private AuthService authService; // Needed to register/login the user for setup
    @Autowired
    private WishlistService wishlistService; // The main service to test

    // --- Test Data Variables ---
    private Buyer testBuyer;
    private Laptop testLaptop1;
    private Laptop testLaptop2;
    private final String testEmail = "wishlist.tester@univ.ca";
    private final String testPassword = "passwordWishlist123";
    private final Long buyerId = 99L; // Simulated Buyer ID
    private final Long laptopId1 = 101L; // Simulated Laptop ID 1 (e.g., corresponds to DB ID 1)
    private final Long laptopId2 = 102L; // Simulated Laptop ID 2 (e.g., corresponds to DB ID 3)

    // --- Setup method to register and 'log in' the user ---
    @BeforeEach
    void setupAuthenticationAndTestData() {
        System.out.println("=== Setting up test data and authenticated user ===");

        // 1. Create User/Buyer object
        testBuyer = new Buyer();
        testBuyer.setId(buyerId); // Assign the simulated ID
        testBuyer.setEmail(testEmail);
        testBuyer.setPassword(testPassword); // Plain text for mock matching
        testBuyer.setMajor("Testing Major");
        testBuyer.setBudget(2000.00);

        // 2. Mock Authentication Flow
        // Mock findByEmail for registration check (not found initially)
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        // Mock save operation for registration
        when(userRepository.save(any(Buyer.class))).thenAnswer(invocation -> {
            Buyer savedBuyer = invocation.getArgument(0);
            savedBuyer.setId(buyerId); // Ensure the saved buyer gets the ID
            System.out.println("Mock saving registered buyer: " + savedBuyer.getEmail());
            testBuyer = savedBuyer; // Update our testBuyer instance with the saved one
            return savedBuyer;
        });

        // Perform registration (using the actual authService with mocked repo)
        UserRegistrationDTO registrationDto = new UserRegistrationDTO();
        registrationDto.setEmail(testEmail);
        registrationDto.setPassword(testPassword);
        registrationDto.setRole("BUYER");
        authService.registerUser(registrationDto); // Register the user

        // Mock findByEmail for login and for getCurrentUserId
        // This single mock handles both the authService.login() find AND the wishlistService.getCurrentUserId() find
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testBuyer));

        // Mock JWT generation (not strictly needed for service test, but good practice)
        when(jwtUtils.generateToken(any(User.class))).thenReturn("mock.jwt.token.wishlist");

        // Perform login (optional for service test, but confirms setup)
        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setEmail(testEmail);
        loginDto.setPassword(testPassword);
        authService.login(loginDto); // Login the user

        // 3. Setup Laptop Data
        testLaptop1 = new Laptop();
        testLaptop1.setId(laptopId1);
        testLaptop1.setBrand("TestBrand");
        testLaptop1.setModel("TestModelX");
        testLaptop1.setPrice(1500.00);
        // ... set other laptop fields if needed for DTO conversion

        testLaptop2 = new Laptop();
        testLaptop2.setId(laptopId2);
        testLaptop2.setBrand("AnotherBrand");
        testLaptop2.setModel("ModelY");
        testLaptop2.setPrice(1800.00);

        // Mock Laptop Repository finds
        when(laptopRepository.findById(laptopId1)).thenReturn(Optional.of(testLaptop1));
        when(laptopRepository.findById(laptopId2)).thenReturn(Optional.of(testLaptop2));
        when(laptopRepository.findById(eq(999L))).thenReturn(Optional.empty()); // For not found test

        // Mock Buyer Repository find (needed by addToWishlist)
        when(buyerRepository.findById(buyerId)).thenReturn(Optional.of(testBuyer));


        System.out.println("=== Setup complete. Test Buyer ID: " + testBuyer.getId() + " ===");
        // Now, any call to wishlistService.getCurrentUserId(userRepository)
        // inside the test methods will effectively return `buyerId` because
        // userRepository.findByEmail(testEmail) is mocked to return testBuyer.
    }

    @Test
    void addGetUpdateRemove_SuccessfulFlow() {
        System.out.println("=== Starting test: addGetUpdateRemove_SuccessfulFlow ===");

        // --- ARRANGE ---
        WishlistRequestDTO addDto = new WishlistRequestDTO();
        addDto.setLaptopId(laptopId1);
        addDto.setTargetPrice(1400.00);

        WishlistKey key = new WishlistKey(buyerId, laptopId1);

        WishlistItem expectedItem = new WishlistItem();
        expectedItem.setId(key);
        expectedItem.setBuyer(testBuyer);
        expectedItem.setLaptop(testLaptop1);
        expectedItem.setTargetPrice(addDto.getTargetPrice());

        // Mocks for Add
        when(wishlistRepository.existsById(key)).thenReturn(false); // Item does not exist initially
        when(wishlistRepository.save(any(WishlistItem.class))).thenReturn(expectedItem); // Return the saved item

        // --- ACT: Add item ---
        System.out.println("Calling wishlistService.addToWishlist()...");
        // We pass buyerId explicitly here, mimicking how the controller *would* get it
        WishlistResponseDTO addedDto = wishlistService.addToWishlist(testBuyer.getId(), addDto);
        System.out.println("Service returned added DTO: " + addedDto);

        // --- ASSERT: Add item ---
        assertNotNull(addedDto);
        assertEquals(buyerId, addedDto.getBuyerId());
        assertEquals(laptopId1, addedDto.getLaptopId());
        assertEquals(testLaptop1.getBrand(), addedDto.getLaptopBrand());
        assertEquals(testLaptop1.getModel(), addedDto.getLaptopModel());
        assertEquals(addDto.getTargetPrice(), addedDto.getTargetPrice());
        verify(wishlistRepository).existsById(key);
        verify(wishlistRepository).save(argThat(item -> // Use argThat for complex argument matching
                item.getId().equals(key) &&
                        item.getBuyer().getId().equals(buyerId) &&
                        item.getLaptop().getId().equals(laptopId1) &&
                        item.getTargetPrice().equals(addDto.getTargetPrice())
        ));

        // --- ARRANGE: Get Wishlist ---
        when(wishlistRepository.findByBuyerId(buyerId)).thenReturn(Collections.singletonList(expectedItem));

        // --- ACT: Get Wishlist ---
        System.out.println("Calling wishlistService.getWishlistByBuyer()...");
        List<WishlistResponseDTO> wishlist = wishlistService.getWishlistByBuyer(testBuyer.getId());
        System.out.println("Service returned wishlist: " + wishlist);

        // --- ASSERT: Get Wishlist ---
        assertNotNull(wishlist);
        assertEquals(1, wishlist.size());
        assertEquals(addedDto, wishlist.get(0)); // Check if the DTO matches the one from add
        verify(wishlistRepository).findByBuyerId(buyerId);

        // --- ARRANGE: Update Price ---
        Double newPrice = 1350.00;
        WishlistItem updatedItem = new WishlistItem(); // Create a new instance for the updated state
        updatedItem.setId(key);
        updatedItem.setBuyer(testBuyer);
        updatedItem.setLaptop(testLaptop1);
        updatedItem.setTargetPrice(newPrice); // Set the new price

        when(wishlistRepository.findById(key)).thenReturn(Optional.of(expectedItem)); // Find the original item
        // Mock the save operation for the update, return the item with the *new* price
        when(wishlistRepository.save(any(WishlistItem.class))).thenReturn(updatedItem);

        // --- ACT: Update Price ---
        System.out.println("Calling wishlistService.updateWishlistItemPrice()...");
        WishlistResponseDTO updatedDto = wishlistService.updateWishlistItemPrice(testBuyer.getId(), laptopId1, newPrice);
        System.out.println("Service returned updated DTO: " + updatedDto);

        // --- ASSERT: Update Price ---
        assertNotNull(updatedDto);
        assertEquals(buyerId, updatedDto.getBuyerId());
        assertEquals(laptopId1, updatedDto.getLaptopId());
        assertEquals(newPrice, updatedDto.getTargetPrice()); // Verify the new price
        verify(wishlistRepository).findById(key); // Verify findById was called for update
        verify(wishlistRepository, times(2)).save(any(WishlistItem.class)); // Save called for add and update

        // --- ARRANGE: Remove Item ---
        when(wishlistRepository.existsById(key)).thenReturn(true); // Item now exists
        doNothing().when(wishlistRepository).deleteById(key); // Mock void method

        // --- ACT: Remove Item ---
        System.out.println("Calling wishlistService.removeFromWishlist()...");
        boolean removed = wishlistService.removeFromWishlist(testBuyer.getId(), laptopId1);
        System.out.println("Service returned removal status: " + removed);

        // --- ASSERT: Remove Item ---
        assertTrue(removed);
        verify(wishlistRepository, times(2)).existsById(key); // Called for add and remove checks
        verify(wishlistRepository).deleteById(key); // Verify delete was called

        System.out.println("All assertions passed!");
        System.out.println("=== Test completed successfully: addGetUpdateRemove_SuccessfulFlow ===");
    }

    @Test
    void addToWishlist_DuplicateItem_ThrowsException() {
        System.out.println("=== Starting test: addToWishlist_DuplicateItem_ThrowsException ===");
        // --- ARRANGE ---
        WishlistRequestDTO addDto = new WishlistRequestDTO();
        addDto.setLaptopId(laptopId1);
        addDto.setTargetPrice(1400.00);

        WishlistKey key = new WishlistKey(buyerId, laptopId1);

        // Mock that the item *already* exists
        when(wishlistRepository.existsById(key)).thenReturn(true);

        System.out.println("Test data created & Mocks setup (item already exists)");

        // --- ACT & ASSERT ---
        System.out.println("Calling wishlistService.addToWishlist() expecting exception...");
        DuplicateWishlistItemException exception = assertThrows(DuplicateWishlistItemException.class, () -> {
            wishlistService.addToWishlist(testBuyer.getId(), addDto);
        });

        assertEquals("Laptop already exists in your wishlist", exception.getMessage());
        System.out.println("Correct exception thrown: " + exception.getMessage());

        // Verify save was NOT called
        verify(wishlistRepository).existsById(key);
        verify(wishlistRepository, never()).save(any(WishlistItem.class));
        System.out.println("=== Test completed successfully: addToWishlist_DuplicateItem_ThrowsException ===");
    }

    @Test
    void addToWishlist_LaptopNotFound_ThrowsException() {
        System.out.println("=== Starting test: addToWishlist_LaptopNotFound_ThrowsException ===");
        // --- ARRANGE ---
        Long nonExistentLaptopId = 999L;
        WishlistRequestDTO addDto = new WishlistRequestDTO();
        addDto.setLaptopId(nonExistentLaptopId);
        addDto.setTargetPrice(1000.00);

        WishlistKey key = new WishlistKey(buyerId, nonExistentLaptopId);

        // Mock item doesn't exist, but laptop also doesn't exist
        when(wishlistRepository.existsById(key)).thenReturn(false);
        // laptopRepository mock for 999L returning empty is in @BeforeEach

        System.out.println("Test data created & Mocks setup (laptop not found)");

        // --- ACT & ASSERT ---
        System.out.println("Calling wishlistService.addToWishlist() expecting exception...");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            wishlistService.addToWishlist(testBuyer.getId(), addDto);
        });

        assertEquals("Laptop not found", exception.getMessage());
        System.out.println("Correct exception thrown: " + exception.getMessage());

        // Verify save was NOT called
        verify(wishlistRepository).existsById(key); // Existence check happens first
        verify(laptopRepository).findById(nonExistentLaptopId); // Verify laptop find was attempted
        verify(wishlistRepository, never()).save(any(WishlistItem.class));
        System.out.println("=== Test completed successfully: addToWishlist_LaptopNotFound_ThrowsException ===");
    }

    @Test
    void removeFromWishlist_ItemNotFound_ThrowsException() {
        System.out.println("=== Starting test: removeFromWishlist_ItemNotFound_ThrowsException ===");
        // --- ARRANGE ---
        Long laptopToRemoveId = laptopId2; // Use a valid laptop ID, but assume it's not in wishlist
        WishlistKey key = new WishlistKey(buyerId, laptopToRemoveId);

        // Mock that the item does NOT exist in the wishlist
        when(wishlistRepository.existsById(key)).thenReturn(false);

        System.out.println("Test data created & Mocks setup (item not in wishlist)");

        // --- ACT & ASSERT ---
        System.out.println("Calling wishlistService.removeFromWishlist() expecting exception...");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            wishlistService.removeFromWishlist(testBuyer.getId(), laptopToRemoveId);
        });

        assertEquals("Wishlist item not found", exception.getMessage());
        System.out.println("Correct exception thrown: " + exception.getMessage());

        // Verify delete was NOT called
        verify(wishlistRepository).existsById(key);
        verify(wishlistRepository, never()).deleteById(any(WishlistKey.class));
        System.out.println("=== Test completed successfully: removeFromWishlist_ItemNotFound_ThrowsException ===");
    }

    @Test
    void updateWishlistItemPrice_ItemNotFound_ThrowsException() {
        System.out.println("=== Starting test: updateWishlistItemPrice_ItemNotFound_ThrowsException ===");
        // --- ARRANGE ---
        Long laptopToUpdateId = laptopId2; // Use a valid laptop ID, assume it's not in wishlist
        Double newPrice = 1750.00;
        WishlistKey key = new WishlistKey(buyerId, laptopToUpdateId);

        // Mock that findById returns empty for this key
        when(wishlistRepository.findById(key)).thenReturn(Optional.empty());

        System.out.println("Test data created & Mocks setup (item not in wishlist)");

        // --- ACT & ASSERT ---
        System.out.println("Calling wishlistService.updateWishlistItemPrice() expecting exception...");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            wishlistService.updateWishlistItemPrice(testBuyer.getId(), laptopToUpdateId, newPrice);
        });

        assertEquals("Wishlist item not found", exception.getMessage());
        System.out.println("Correct exception thrown: " + exception.getMessage());

        // Verify save was NOT called
        verify(wishlistRepository).findById(key);
        verify(wishlistRepository, never()).save(any(WishlistItem.class));
        System.out.println("=== Test completed successfully: updateWishlistItemPrice_ItemNotFound_ThrowsException ===");
    }

    @Test
    void getWishlistByBuyer_EmptyWishlist() {
        System.out.println("=== Starting test: getWishlistByBuyer_EmptyWishlist ===");
        // --- ARRANGE ---
        // Mock repository to return an empty list for this buyer
        when(wishlistRepository.findByBuyerId(buyerId)).thenReturn(Collections.emptyList());

        System.out.println("Mocks setup (empty wishlist)");

        // --- ACT ---
        System.out.println("Calling wishlistService.getWishlistByBuyer()...");
        List<WishlistResponseDTO> wishlist = wishlistService.getWishlistByBuyer(testBuyer.getId());
        System.out.println("Service returned wishlist: " + wishlist);

        // --- ASSERT ---
        assertNotNull(wishlist);
        assertTrue(wishlist.isEmpty(), "Wishlist should be empty");
        verify(wishlistRepository).findByBuyerId(buyerId);
        System.out.println("Assertions passed!");
        System.out.println("=== Test completed successfully: getWishlistByBuyer_EmptyWishlist ===");
    }
}