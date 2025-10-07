package com.example.campustech.controller;

import com.example.campustech.controller.dto.response.PriceComparisonDTO;
import com.example.campustech.model.service.PriceComparisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/compare")
public class PriceComparisonController {

    private final PriceComparisonService priceService;

    @Autowired
    public PriceComparisonController(PriceComparisonService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/{laptopId}")
    public PriceComparisonDTO comparePrices(@PathVariable Long laptopId) {
        return priceService.comparePrices(laptopId);
    }
}