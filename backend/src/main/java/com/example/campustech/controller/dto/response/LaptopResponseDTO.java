package com.example.campustech.controller.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@NoArgsConstructor
@Data
public class LaptopResponseDTO {
    private Long id;
    private String brand;
    private String model;
    private Double price;
    private String ram;
    private String storage;
    private String cpu;
    private String gpu;
    private boolean isVerified;

    // ✅ constructor that matches the 5 fields in getAllLaptops()
    public LaptopResponseDTO(Long id, String brand, String model,
                             Double price, String ram, String storage, String cpu, String gpu, boolean isVerified) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.ram = ram;
        this.storage = storage;
        this.cpu = cpu;
        this.gpu = gpu;
        this.isVerified = isVerified;
    }
}
