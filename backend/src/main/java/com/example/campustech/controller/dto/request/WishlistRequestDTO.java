package com.example.campustech.controller.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

@Data
public class WishlistRequestDTO {
    @NotNull(message = "Laptop ID is required")
    private Long laptopId;

    @Positive(message = "Target price must be positive")
    @NotNull(message = "Target price is required")
    private Double targetPrice;
}