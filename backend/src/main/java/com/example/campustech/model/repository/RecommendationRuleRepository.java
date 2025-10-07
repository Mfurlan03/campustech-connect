package com.example.campustech.model.repository;

import com.example.campustech.model.entity.RecommendationRule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface RecommendationRuleRepository extends CrudRepository<RecommendationRule, String> {

    @Query("SELECT DISTINCT r.major FROM RecommendationRule r")
    List<String> findDistinctMajors();
}
