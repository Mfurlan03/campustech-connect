package com.example.campustech.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "wishlist")
@Getter
@Setter
@NoArgsConstructor
public class WishlistItem {

    @EmbeddedId
    private WishlistKey id; // ✅ Keeps composite key structure

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("buyerId") // ✅ Maps buyerId from WishlistKey
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("laptopId") // ✅ Maps laptopId from WishlistKey
    @JoinColumn(name = "laptop_id", nullable = false)
    private Laptop laptop;

    @Column(name = "target_price", nullable = false)
    private Double targetPrice; // ✅ Keeps the targetPrice field from my previous changes
}
