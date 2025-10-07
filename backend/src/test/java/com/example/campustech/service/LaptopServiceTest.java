package com.example.campustech.service;

import com.example.campustech.controller.dto.request.LaptopRequestDTO;
import com.example.campustech.model.entity.Laptop;
import com.example.campustech.model.entity.Seller;
import com.example.campustech.model.repository.LaptopRepository;
import com.example.campustech.model.repository.SellerRepository;
import com.example.campustech.model.service.LaptopService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class LaptopServiceTest {

    @MockBean
    private LaptopRepository laptopRepository;

    @MockBean
    private SellerRepository sellerRepository;

    @Autowired
    private LaptopService laptopService;

    @Test
    void createLaptop_ValidSeller_ReturnsLaptop() {
        System.out.println("=== Starting test: createLaptop_ValidSeller_ReturnsLaptop ===");

        // 1. Setup test data
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setIsAdminVerified(true);

        LaptopRequestDTO dto = new LaptopRequestDTO();
        dto.setSellerId(1L);
        dto.setBrand("Dell");
        dto.setModel("XPS 15");
        dto.setPrice(1499.99);
        dto.setRam("12GB");
        dto.setCpu("intel i9");
        dto.setGpu("RT 3090");
        dto.setStorage("1TB");

        System.out.println("Test data created:");
        System.out.println(" - Seller: ID=" + seller.getId() + ", Verified=" + seller.getIsAdminVerified());
        System.out.println(" - Request DTO: " + dto);

        // 2. Mock repository behavior
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(laptopRepository.save(any(Laptop.class))).thenAnswer(invocation -> {
            Laptop savedLaptop = invocation.getArgument(0);
            System.out.println("Mock repository saving laptop: " + savedLaptop);
            return savedLaptop;
        });

        System.out.println("Repository mocking setup complete");

        // 3. Execute the service method
        System.out.println("Calling laptopService.createLaptop()...");
        Laptop laptop = laptopService.createLaptop(dto.getSellerId(), dto);
        System.out.println("Service returned laptop: " + laptop);

        // 4. Verify results
        System.out.println("Performing assertions...");
        assertNotNull(laptop, "Returned laptop should not be null");
        assertEquals("Dell", laptop.getBrand(), "Laptop brand should match");
        assertEquals("XPS 15", laptop.getModel(), "Laptop model should match");
        assertEquals(1499.99, laptop.getPrice(), 0.001, "Laptop price should match");
        assertEquals(1L, laptop.getSeller().getId(), "Seller ID should match");
        assertEquals("12GB", laptop.getRam(), "Laptop Ram should match ");
        assertEquals("intel i9", laptop.getCpu(), "Laptop CPU should match ");
        assertEquals("RT 3090", laptop.getGpu(), "Laptop GPU should match ");
        assertEquals("1TB", laptop.getStorage(), "Laptop storage should match ");
        System.out.println("All assertions passed!");
        System.out.println("=== Test completed successfully ===");
    }
}