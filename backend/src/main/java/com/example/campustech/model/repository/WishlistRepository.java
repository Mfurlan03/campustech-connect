package com.example.campustech.model.repository;

import com.example.campustech.model.entity.WishlistItem;
import com.example.campustech.model.entity.WishlistKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistItem, WishlistKey> {

    @Query("SELECT w FROM WishlistItem w WHERE w.id.buyerId = :buyerId")
    List<WishlistItem> findByBuyerId(@Param("buyerId") Long buyerId);
}