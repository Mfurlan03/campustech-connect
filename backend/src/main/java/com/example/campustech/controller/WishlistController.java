package com.example.campustech.controller;

import com.example.campustech.controller.dto.request.WishlistRequestDTO;
import com.example.campustech.controller.dto.response.WishlistResponseDTO;
import com.example.campustech.exceptions.DuplicateWishlistItemException;
import com.example.campustech.model.repository.UserRepository;
import com.example.campustech.model.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserRepository userRepository; // ✅ Declare it properly

    @Autowired
    public WishlistController(WishlistService wishlistService, UserRepository userRepository) {
        this.wishlistService = wishlistService;
        this.userRepository = userRepository; // ✅ Initialize it properly
    }

    // ✅ Add item to wishlist (DTO-based)
    // Add item to wishlist using token-derived buyerId
    @PostMapping
    public ResponseEntity<?> addToWishlist(@Valid @RequestBody WishlistRequestDTO dto) {
        try {
            Long buyerId = wishlistService.getCurrentUserId(userRepository);
            WishlistResponseDTO response = wishlistService.addToWishlist(buyerId, dto);
            return ResponseEntity.ok(response);
        } catch (DuplicateWishlistItemException ex) {
            System.out.println("An error occured here");
            System.out.println("An error occured here" + ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    // ✅ Get wishlist for the currently logged-in user
    @GetMapping("/my-wishlist")
    public ResponseEntity<List<WishlistResponseDTO>> getMyWishlist() {
        Long buyerId = wishlistService.getCurrentUserId(userRepository); // ✅ Now userRepository exists
        List<WishlistResponseDTO> wishlist = wishlistService.getWishlistByBuyer(buyerId);
        return ResponseEntity.ok(wishlist);
    }

    // ✅ DELETE: Remove a laptop from the wishlist of the currently logged-in user
    @DeleteMapping("/my-wishlist/{laptopId}")
    public ResponseEntity<String> removeFromWishlist(@PathVariable Long laptopId) {
        Long buyerId = wishlistService.getCurrentUserId(userRepository);
        wishlistService.removeFromWishlist(buyerId, laptopId);
        return ResponseEntity.ok("Laptop removed from wishlist successfully.");
    }

    // ✅ PUT: Update wishlist item's target price for the currently logged-in user
    // Update target price for a laptop in the user's wishlist
    @PutMapping("/my-wishlist/{laptopId}")
    public ResponseEntity<WishlistResponseDTO> updateWishlistItemPrice(
            @PathVariable Long laptopId,
            @RequestParam Double newPrice
    ) {
        Long buyerId = wishlistService.getCurrentUserId(userRepository);
        WishlistResponseDTO updatedItem = wishlistService.updateWishlistItemPrice(buyerId, laptopId, newPrice);
        return ResponseEntity.ok(updatedItem);
    }
}


