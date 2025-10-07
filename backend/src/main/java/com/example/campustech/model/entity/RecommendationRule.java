package com.example.campustech.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "recommendation_rules")
@Getter @Setter @NoArgsConstructor
public class RecommendationRule {

    @Id
    @Column(nullable = false)
    private String major;

    @Lob
    @Column(name = "required_specs", nullable = false)
    private String requiredSpecs;

    @Column(name = "min_budget", nullable = false)
    private Double minBudget;

    @Column(name = "max_budget", nullable = false)
    private Double maxBudget;
}