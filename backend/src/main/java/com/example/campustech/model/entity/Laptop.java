package com.example.campustech.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.Positive;

@Entity
@Table(name = "laptops")
@Getter @Setter @NoArgsConstructor
public class Laptop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Positive
    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String ram;

    @Column(nullable = false)
    private String storage;

    @Column(nullable = false)
    private String cpu;

    private String gpu;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Getter
    @Setter
    @Column(name = "is_verified")
    private Boolean isVerified = false;
}