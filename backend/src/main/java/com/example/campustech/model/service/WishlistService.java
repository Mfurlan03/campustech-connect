package com.example.campustech.model.service;

import com.example.campustech.controller.dto.request.WishlistRequestDTO;
import com.example.campustech.controller.dto.response.WishlistResponseDTO;
import com.example.campustech.exceptions.DuplicateWishlistItemException;
import com.example.campustech.model.entity.*;
import com.example.campustech.model.repository.BuyerRepository;
import com.example.campustech.model.repository.LaptopRepository;
import com.example.campustech.model.repository.UserRepository;
import com.example.campustech.model.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final BuyerRepository buyerRepository;
    private final LaptopRepository laptopRepository;

    public WishlistService(WishlistRepository wishlistRepository,
                           BuyerRepository buyerRepository,
                           LaptopRepository laptopRepository) {
        this.wishlistRepository = wishlistRepository;
        this.buyerRepository = buyerRepository;
        this.laptopRepository = laptopRepository;
    }

    public Long getCurrentUserId(UserRepository userRepository) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    // ✅ Add item to wishlist (Using DTO)
    @Transactional
    public WishlistResponseDTO addToWishlist(Long buyerId, WishlistRequestDTO dto) {
        // Check if the item already exists
        WishlistKey wishlistKey = new WishlistKey(buyerId, dto.getLaptopId());
        if (wishlistRepository.existsById(wishlistKey)) {
            throw new DuplicateWishlistItemException("Laptop already exists in your wishlist");
        }

        // Proceed if no duplicate
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));

        Laptop laptop = laptopRepository.findById(dto.getLaptopId())
                .orElseThrow(() -> new IllegalArgumentException("Laptop not found"));

        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setId(wishlistKey);
        wishlistItem.setBuyer(buyer);
        wishlistItem.setLaptop(laptop);
        wishlistItem.setTargetPrice(dto.getTargetPrice());

        WishlistItem savedItem = wishlistRepository.save(wishlistItem);
        return convertToDTO(savedItem);
    }

    // ✅ Fetch wishlist items by buyer (DTO conversion)
    @Transactional(readOnly = true)
    public List<WishlistResponseDTO> getWishlistByBuyer(Long buyerId) {
        return wishlistRepository.findByBuyerId(buyerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Convert entity to DTO
    private WishlistResponseDTO convertToDTO(WishlistItem wishlistItem) {
        return new WishlistResponseDTO(
                wishlistItem.getId().getBuyerId(),
                wishlistItem.getLaptop().getId(),
                wishlistItem.getLaptop().getBrand(),
                wishlistItem.getLaptop().getModel(),
                wishlistItem.getTargetPrice()
        );
    }
    // ✅ Remove an item from wishlist
    public boolean removeFromWishlist(Long buyerId, Long laptopId) {
        WishlistKey wishlistKey = new WishlistKey(buyerId, laptopId);
        if (!wishlistRepository.existsById(wishlistKey)) {
            throw new IllegalArgumentException("Wishlist item not found");
        }
        wishlistRepository.deleteById(wishlistKey);
        return true;
    }

    // ✅ Update wishlist item's target price
    public WishlistResponseDTO updateWishlistItemPrice(Long buyerId, Long laptopId, Double newPrice) {
        WishlistKey wishlistKey = new WishlistKey(buyerId, laptopId);
        WishlistItem wishlistItem = wishlistRepository.findById(wishlistKey)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist item not found"));

        wishlistItem.setTargetPrice(newPrice);
        wishlistRepository.save(wishlistItem);

        return new WishlistResponseDTO(
                wishlistItem.getId().getBuyerId(),
                wishlistItem.getLaptop().getId(),
                wishlistItem.getLaptop().getBrand(),
                wishlistItem.getLaptop().getModel(),
                wishlistItem.getTargetPrice()
        );

    }

}
