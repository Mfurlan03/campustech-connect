package com.example.campustech.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PriceScraper {

    public Double scrapePriceFromUrl(String url) {
        try {
            if (url.matches("https?://www\\.newegg\\.ca/.*")) {
                return scrapeFromNewegg(url);
            } else if (url.matches("https?://www\\.ebay\\.ca/itm/.*")) {
                return scrapeFromEbay(url);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private Double scrapeFromNewegg(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(5000).get();
        Elements prices = doc.select(".price .price-current, .price-current");
        for (Element priceEl : prices) {
            Double price = extractPrice(priceEl.text());
            if (price != null) return price;
        }
        return null;
    }

    private Double scrapeFromEbay(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(5000).get();
        Elements prices = doc.select(".s-item__price, .x-price-approx__price, .display-price");
        for (Element priceEl : prices) {
            Double price = extractPrice(priceEl.text());
            if (price != null) return price;
        }
        return null;
    }

    private Double extractPrice(String text) {
        if (text == null || text.isEmpty()) return null;
        Pattern pattern = Pattern.compile("\\$([0-9,]+\\.\\d{2})");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1).replace(",", ""));
        }
        return null;
    }
}
