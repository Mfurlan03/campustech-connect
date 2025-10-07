package com.example.campustech.model.repository;

import com.example.campustech.model.entity.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaptopRepository extends JpaRepository<Laptop, Long> {
    @Query("SELECT l FROM Laptop l JOIN FETCH l.seller")
    List<Laptop> findAllLaptops();

    List<Laptop> findByBrand(String brand);

    @Query("SELECT l FROM Laptop l WHERE l.price BETWEEN :minPrice AND :maxPrice")
    List<Laptop> findByPriceBetween(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    List<Laptop> findBySellerId(Long sellerId);

    @Query("SELECT DISTINCT l.brand FROM Laptop l")
    List<String> findAllDistinctBrands();


    @Query("SELECT l FROM Laptop l WHERE l.brand = :brand AND l.price BETWEEN :minPrice AND :maxPrice")
    List<Laptop> findByBrandAndPriceRange(@Param("brand") String brand, @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);


    // Fetch laptops with the same brand and model but a different ID
    List<Laptop> findByBrandAndModelAndIdNot(String brand, String model, Long id);

    @Query("SELECT l FROM Laptop l WHERE l.model = :model AND l.id <> :excludeId")
    List<Laptop> findByModelAndIdNot(@Param("model") String model, @Param("excludeId") Long excludeId);

}