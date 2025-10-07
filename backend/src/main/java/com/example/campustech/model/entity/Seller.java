package com.example.campustech.model.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sellers")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter @Setter
public class Seller extends User {

    @Column(name = "seller_rating", precision = 3, scale = 2)
    private Double sellerRating = 0.0;

    @Column(name = "is_admin_verified")
    private Boolean isAdminVerified = false;

    @Column(name = "university_id", nullable = false)
    private String universityId;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private List<Laptop> laptops = new ArrayList<>();
}