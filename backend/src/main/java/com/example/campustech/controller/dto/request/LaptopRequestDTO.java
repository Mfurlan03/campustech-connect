package com.example.campustech.controller.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

@Data
public class LaptopRequestDTO {
    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @Positive(message = "Price must be positive")
    @NotNull(message = "Price is required")
    private Double price;

    @NotBlank(message = "RAM specification is required")
    private String ram;

    @NotBlank(message = "Storage specification is required")
    private String storage;

    @NotBlank(message = "CPU specification is required")
    private String cpu;

    private String gpu; // GPU is optional

    @NotNull(message = "Seller ID is required")
    private Long sellerId;
}