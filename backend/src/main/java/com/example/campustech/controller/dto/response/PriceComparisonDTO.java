package com.example.campustech.controller.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
public class PriceComparisonDTO {

    private MarketplaceLaptopDTO marketplaceLaptop;
    private List<DatabaseListing> similarLaptops;
    private List<ExternalListing> externalListings;


    @Data
    public static class ExternalListing {
        private String title;
        private String link;
        private String image;
        private Double price;

        private String brand;
        private String model;
        private String ram;
        private String storage;
        private String cpu;
        private String gpu;
    }



    @Data
    public static class DatabaseListing {
        private Long id;
        private String model;
        private String brand;
        private BigDecimal price;
        private String seller;
        private String ram;
        private String storage;
        private String cpu;
        private String gpu;
        private BigDecimal priceDifference;
    }


    @Data
    public static class MarketplaceLaptopDTO {
        private Long id;
        private String model;
        private String brand;
        private BigDecimal price;
        private String seller;
        private String ram;
        private String storage;
        private String cpu;
        private String gpu;
    }


}
