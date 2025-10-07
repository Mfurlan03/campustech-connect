package com.example.campustech.controller.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

@Data
public class RecommendationRequestDTO {
    @NotBlank(message = "Major cannot be blank")
    private String major;

    @NotNull(message = "Minimum budget is required")
    @Positive(message = "Minimum budget must be a positive value")
    private Double minBudget;

    @NotNull(message = "Maximum budget is required")
    @Positive(message = "Maximum budget must be a positive value")
    private Double maxBudget;
}