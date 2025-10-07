package com.example.campustech.model.service;

import com.example.campustech.controller.dto.request.LaptopRequestDTO;
import com.example.campustech.controller.dto.response.LaptopResponseDTO;
import com.example.campustech.model.entity.Laptop;
import com.example.campustech.model.entity.Seller;
import com.example.campustech.model.repository.LaptopRepository;
import com.example.campustech.model.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LaptopService {

    @Autowired
    private LaptopRepository laptopRepository;

    @Autowired
    private SellerRepository sellerRepository;

    // ✅ Retrieve all laptops
    public List<LaptopResponseDTO> getAllLaptops() {
        return laptopRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Create a new laptop listing
    // LaptopService.java
    @Transactional
    public Laptop createLaptop(Long sellerId, LaptopRequestDTO dto) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

//        if (!seller.getIsAdminVerified()) {
//            throw new IllegalStateException("Seller not verified");
//        }

        Laptop laptop = new Laptop();
        laptop.setBrand(dto.getBrand());
        laptop.setModel(dto.getModel());
        laptop.setPrice(dto.getPrice());
        laptop.setRam(dto.getRam());
        laptop.setStorage(dto.getStorage());
        laptop.setCpu(dto.getCpu());
        laptop.setGpu(dto.getGpu());
        laptop.setSeller(seller);

        return laptopRepository.save(laptop);
    }

    // ✅ Update laptop verification status
    public Laptop updateLaptopVerification(Long laptopId, boolean isVerified) {
        Laptop laptop = laptopRepository.findById(laptopId)
                .orElseThrow(() -> new IllegalArgumentException("Laptop not found"));
        laptop.setIsVerified(isVerified);
        return laptopRepository.save(laptop);
    }

    // ✅ Find laptops by brand
    public List<LaptopResponseDTO> getLaptopsByBrand(String brand) {
        return laptopRepository.findByBrand(brand).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Find laptops within a price range
    public List<LaptopResponseDTO> getLaptopsByPriceRange(Double minPrice, Double maxPrice) {
        return laptopRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Find laptops by seller ID
    public List<LaptopResponseDTO> getLaptopsBySeller(Long sellerId) {
        return laptopRepository.findBySellerId(sellerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper method to convert entity to DTO
    public LaptopResponseDTO convertToDTO(Laptop laptop) {
        return new LaptopResponseDTO(
                laptop.getId(),
                laptop.getBrand(),
                laptop.getModel(),
                laptop.getPrice(),
                laptop.getRam(),
                laptop.getStorage(),
                laptop.getCpu(),
                laptop.getGpu(),
                laptop.getIsVerified()
        );
    }

    @Transactional
    // LaptopService.java
    public void deleteLaptop(Long sellerId, Long laptopId) {
        Laptop laptop = laptopRepository.findById(laptopId)
                .orElseThrow(() -> new IllegalArgumentException("Laptop not found"));

        if (!laptop.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("You do not own this laptop");
        }

        laptopRepository.delete(laptop);
    }
    public List<String> getAllLaptopBrands() {
        return laptopRepository.findAllDistinctBrands();
    }


    public List<LaptopResponseDTO> getLaptopsByBrandAndPrice(String brand, Double minPrice, Double maxPrice) {
        return laptopRepository.findByBrandAndPriceRange(brand, minPrice, maxPrice).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LaptopService(LaptopRepository laptopRepository) {
        this.laptopRepository = laptopRepository;
    }

    public List<Laptop> findLaptopsByModel(String model, Long excludeId) {
        return laptopRepository.findByModelAndIdNot(model, excludeId);
    }




}
