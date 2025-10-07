package com.example.campustech.controller;

import com.example.campustech.controller.dto.request.LaptopRequestDTO;
import com.example.campustech.controller.dto.response.LaptopResponseDTO;
import com.example.campustech.model.entity.Laptop;
import com.example.campustech.model.entity.Seller;
import com.example.campustech.model.repository.SellerRepository;
import com.example.campustech.model.service.LaptopService;
import com.example.campustech.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/laptops")
public class LaptopController {

    private final LaptopService laptopService;
    private final SellerRepository sellerRepository;
    private final JwtUtils jwtUtils; // Inject JwtUtils

    public LaptopController(LaptopService laptopService, SellerRepository sellerRepository, JwtUtils jwtUtils) {
        this.laptopService = laptopService;
        this.sellerRepository = sellerRepository;
        this.jwtUtils = jwtUtils; // ✅ Initialize JwtUtils
    }

    @GetMapping
    public List<LaptopResponseDTO> getAllLaptops() {
        return laptopService.getAllLaptops();
    }

    // LaptopController.java
    @PostMapping
    public ResponseEntity<LaptopResponseDTO> createLaptop(@RequestBody LaptopRequestDTO dto) {
        // Get seller ID from token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        // Create laptop
        Laptop laptop = laptopService.createLaptop(seller.getId(), dto);
        LaptopResponseDTO responseDTO = laptopService.convertToDTO(laptop);
        return ResponseEntity.ok(responseDTO);
    }

    // LaptopController.java
    @GetMapping("/seller")
    public ResponseEntity<List<LaptopResponseDTO>> getSellerLaptops() {
        // Get seller ID from token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        // Fetch seller's laptops
        List<LaptopResponseDTO> laptops = laptopService.getLaptopsBySeller(seller.getId());
        return ResponseEntity.ok(laptops);
    }

    // LaptopController.java
    @PutMapping("/{id}/verify")
    public ResponseEntity<LaptopResponseDTO> verifyListing(
            @PathVariable Long id,
            @RequestParam boolean isVerified
    ) {
        // Optional: Check if user has admin role here
        Laptop laptop = laptopService.updateLaptopVerification(id, isVerified);
        LaptopResponseDTO responseDTO = laptopService.convertToDTO(laptop);
        return ResponseEntity.ok(responseDTO);
    }
    // LaptopController.java
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLaptop(@PathVariable Long id) {
        // Get seller ID from token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        // Delete laptop (service checks ownership)
        laptopService.deleteLaptop(seller.getId(), id);
        return ResponseEntity.ok("Laptop deleted successfully.");
    }


//    @GetMapping("/brand/{brand}")
//    public ResponseEntity<List<LaptopResponseDTO>> getLaptopsByBrand(@PathVariable String brand) {
//        return ResponseEntity.ok(laptopService.getLaptopsByBrand(brand));
//    }

//    @GetMapping("/brand/{brand}/price-range")
//    public ResponseEntity<List<LaptopResponseDTO>> getLaptopsByBrandAndPriceRange(
//            @PathVariable String brand,
//            @RequestParam Double minPrice,
//            @RequestParam Double maxPrice
//    ) {
//        return ResponseEntity.ok(laptopService.getLaptopsByBrandAndPrice(brand, minPrice, maxPrice));
//    }

//    @GetMapping("/price-range")
//    public ResponseEntity<List<LaptopResponseDTO>> getLaptopsByPriceRange(
//            @RequestParam Double minPrice,
//            @RequestParam Double maxPrice
//    ) {
//        return ResponseEntity.ok(laptopService.getLaptopsByPriceRange(minPrice, maxPrice));
//    }
//
//    @GetMapping("/brands")
//    public ResponseEntity<List<String>> getAllLaptopBrands() {
//        return ResponseEntity.ok(laptopService.getAllLaptopBrands());
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteLaptop(@PathVariable Long id) {
//        laptopService.deleteLaptop(id);
//        return ResponseEntity.ok("Laptop with ID " + id + " has been deleted successfully.");
//    }

}
