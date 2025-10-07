package com.example.campustech.model.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "buyers")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter @Setter
public class Buyer extends User {

    @Column(nullable = false)
    private String major;

    @Column(name = "preferred_brand")
    private String preferredBrand;

    @Column(nullable = false)
    private Double budget;
}