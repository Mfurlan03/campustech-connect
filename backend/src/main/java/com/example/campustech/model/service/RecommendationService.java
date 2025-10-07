package com.example.campustech.model.service;

import com.example.campustech.controller.dto.request.RecommendationRequestDTO;
import com.example.campustech.controller.dto.response.LaptopResponseDTO;
import com.example.campustech.controller.dto.response.RecommendationResponseDTO;
import com.example.campustech.model.entity.Laptop;
import com.example.campustech.model.entity.RecommendationRule;
import com.example.campustech.model.repository.LaptopRepository;
import com.example.campustech.model.repository.RecommendationRuleRepository;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final RecommendationRuleRepository ruleRepository;
    private final LaptopRepository laptopRepository;

    public RecommendationService(RecommendationRuleRepository ruleRepository,
                                 LaptopRepository laptopRepository) {
        this.ruleRepository = ruleRepository;
        this.laptopRepository = laptopRepository;
    }

    public List<Laptop> getRecommendations(String major, double minBudget, double maxBudget) {
        RecommendationRule rule = ruleRepository.findById(major)
                .orElseThrow(() -> new IllegalArgumentException("No rules for major: " + major));

        return laptopRepository.findAll().stream()
                .filter(laptop -> laptop.getPrice() >= minBudget && laptop.getPrice() <= maxBudget)
                .filter(laptop -> meetsSpecRequirements(laptop, rule.getRequiredSpecs()))
                .collect(Collectors.toList());
    }

    private boolean meetsSpecRequirements(Laptop laptop, String requiredSpecs) {
        // Implement spec parsing logic (e.g., "RAM >= 16GB, Storage >= 512GB")
        // Simplified example:
        return requiredSpecs.contains("RAM >= 16GB") ?
                laptop.getRam().equals("16GB") || laptop.getRam().equals("32GB") :
                true;
    }
    public List<String> getAllMajors() {
        return ruleRepository.findDistinctMajors();
    }

    public RecommendationResponseDTO generateRecommendations(@Valid RecommendationRequestDTO request) {
        List<Laptop> recommendedLaptops = getRecommendations(
                request.getMajor(),
                request.getMinBudget(),
                request.getMaxBudget()
        );

        List<LaptopResponseDTO> responseLaptops = recommendedLaptops.stream()
                .map(laptop -> new LaptopResponseDTO(
                        laptop.getId(),
                        laptop.getBrand(),
                        laptop.getModel(),
                        laptop.getPrice(),
                        laptop.getRam(),
                        laptop.getStorage(),
                        laptop.getCpu(),
                        laptop.getGpu(),
                        laptop.getIsVerified())) // ✅ Convert Laptop to LaptopResponseDTO
                .collect(Collectors.toList());

        return new RecommendationResponseDTO(
                request.getMajor(),
                request.getMinBudget(),
                request.getMaxBudget(),
                responseLaptops,
                "Matching based on budget and specs"
        );
    }

    public RecommendationRule getRuleByMajor(String major) {
        return ruleRepository.findById(major)
                .orElseThrow(() -> new IllegalArgumentException("No rules found for major: " + major));
    }


}