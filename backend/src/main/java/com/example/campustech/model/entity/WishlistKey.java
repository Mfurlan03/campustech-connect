package com.example.campustech.model.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WishlistKey implements Serializable {

    @Column(name = "buyer_id")
    private Long buyerId;

    @Column(name = "laptop_id")
    private Long laptopId;

    // Required no-arg constructor
    public WishlistKey() {}

    public WishlistKey(Long buyerId, Long laptopId) {
        this.buyerId = buyerId;
        this.laptopId = laptopId;
    }

    // Getters and Setters
    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
    public Long getLaptopId() { return laptopId; }
    public void setLaptopId(Long laptopId) { this.laptopId = laptopId; }

    // Override equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WishlistKey that = (WishlistKey) o;
        return Objects.equals(buyerId, that.buyerId) && Objects.equals(laptopId, that.laptopId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(buyerId, laptopId);
    }
}