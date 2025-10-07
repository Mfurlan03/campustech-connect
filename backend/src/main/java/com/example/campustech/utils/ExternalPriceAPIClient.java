package com.example.campustech.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExternalPriceAPIClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public Double getExternalPrice(String model, String retailer) {
        // Mock implementation - replace with actual API calls
        return switch (retailer.toLowerCase()) {
            case "amazon" -> Math.random() * 1000 + 500; // Random price between 500-1500
            case "bestbuy" -> Math.random() * 1200 + 600;
            default -> throw new IllegalArgumentException("Unsupported retailer");
        };
    }
}