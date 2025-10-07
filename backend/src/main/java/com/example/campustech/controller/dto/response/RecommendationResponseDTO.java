package com.example.campustech.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor  // generates a constructor with all fields
public class RecommendationResponseDTO {
    private String major;
    private double minBudget;
    private double maxBudget;
    private List<LaptopResponseDTO> recommendedLaptops;
    private String matchingCriteria; // e.g., "RAM >= 16GB, Budget <= $2000"
}
