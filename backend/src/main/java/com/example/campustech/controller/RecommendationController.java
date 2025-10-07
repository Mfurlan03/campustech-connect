package com.example.campustech.controller;

import com.example.campustech.controller.dto.request.RecommendationRequestDTO;
import com.example.campustech.controller.dto.response.RecommendationResponseDTO;
import com.example.campustech.model.entity.RecommendationRule;
import com.example.campustech.model.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public RecommendationResponseDTO getRecommendations(
            @Valid @RequestBody RecommendationRequestDTO request
    ) {
        return recommendationService.generateRecommendations(request);
    }

    @GetMapping("/majors")
    public List<String> getAllMajors() {
        return recommendationService.getAllMajors();
    }

    @GetMapping("/specs/{major}")
    public ResponseEntity<String> getSpecsByMajor(@PathVariable String major) {
        try {
            RecommendationRule rule = recommendationService.getRuleByMajor(major);
            return ResponseEntity.ok(
                    "For " + rule.getMajor() +
                            ": We need the following specs: " + rule.getRequiredSpecs()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}