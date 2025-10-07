package com.example.campustech.utils;

import com.example.campustech.model.entity.WishlistItem;
import com.example.campustech.model.repository.WishlistRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AlertScheduler {

    private final WishlistRepository wishlistRepository;
    private final ExternalPriceAPIClient priceClient;

    public AlertScheduler(WishlistRepository wishlistRepository,
                          ExternalPriceAPIClient priceClient) {
        this.wishlistRepository = wishlistRepository;
        this.priceClient = priceClient;
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // Run daily
    public void checkPriceAlerts() {
        List<WishlistItem> items = wishlistRepository.findAll();
        items.forEach(item -> {
            double currentPrice = priceClient.getExternalPrice(
                    item.getLaptop().getModel(),
                    "amazon" // Example retailer
            );
            if (currentPrice <= item.getTargetPrice()) {
                sendAlert(item, currentPrice);
            }
        });
    }

    private void sendAlert(WishlistItem item, double currentPrice) {
        // Implement email/SMS notification
        System.out.printf(
                "Alert: %s's price dropped to $%.2f (target: $%.2f)%n",
                item.getLaptop().getModel(),
                currentPrice,
                item.getTargetPrice()
        );
    }
}