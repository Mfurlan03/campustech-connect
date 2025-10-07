package com.example.campustech.model.service;

import com.example.campustech.controller.dto.response.PriceComparisonDTO;
import com.example.campustech.model.entity.Laptop;
import com.example.campustech.model.external.GoogleSearchResponse;
import com.example.campustech.model.repository.LaptopRepository;
import com.example.campustech.utils.PriceScraper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class PriceComparisonService {

    private final RestTemplate restTemplate;
    private final LaptopRepository laptopRepository;

    @Value("${google.cse.id}")
    private String googleCseId;

    @Value("${google.api.key}")
    private String googleApiKey;

    private static final String GOOGLE_SEARCH_API_URL =
            "https://www.googleapis.com/customsearch/v1?q=%s&cx=%s&key=%s&num=%d&start=%d";

    private final PriceScraper priceScraper;

    public PriceComparisonService(RestTemplate restTemplate,
                                  LaptopRepository laptopRepository,
                                  PriceScraper priceScraper) {
        this.restTemplate = restTemplate;
        this.laptopRepository = laptopRepository;
        this.priceScraper = priceScraper;
    }


    public List<PriceComparisonDTO.ExternalListing> searchExternalListings(String model, String brand) {
        if (brand == null) brand = "";
        if (model == null) model = "";

        String fullQuery = String.format("%s %s", brand.trim(), model.trim()).trim();
        String encodedQuery = URLEncoder.encode(fullQuery, StandardCharsets.UTF_8);

        List<PriceComparisonDTO.ExternalListing> finalResults = new ArrayList<>();
        int startIndex = 1;

        while (finalResults.size() < 5 && startIndex <= 90) {
            String searchUrl = String.format(
                    "https://www.googleapis.com/customsearch/v1?q=%s&cx=%s&key=%s&num=10&start=%d",
                    encodedQuery, googleCseId, googleApiKey, startIndex
            );

            try {
                ResponseEntity<GoogleSearchResponse> response = restTemplate.exchange(
                        searchUrl, HttpMethod.GET, null, GoogleSearchResponse.class);

                if (response.getBody() != null && response.getBody().getItems() != null) {
                    for (GoogleSearchResponse.SearchItem item : response.getBody().getItems()) {
                        String link = item.getLink();
                        if (link == null) continue;

                        Double price = priceScraper.scrapePriceFromUrl(link);
                        if (price != null && price > 500) {
                            PriceComparisonDTO.ExternalListing listing = convertToExternalListing(item);
                            listing.setPrice(price);
                            finalResults.add(listing);

                            if (finalResults.size() >= 5) break;
                        }
                    }
                }

            } catch (HttpClientErrorException e) {
                System.err.println("❌ Google API error: " + e.getMessage());
                break;
            }

            startIndex += 10;
        }

        return finalResults;
    }

    private PriceComparisonDTO.ExternalListing convertToExternalListing(GoogleSearchResponse.SearchItem item) {
        PriceComparisonDTO.ExternalListing listing = new PriceComparisonDTO.ExternalListing();
        listing.setLink(item.getLink());

        if (item.getPagemap() != null && item.getPagemap().getMetatags() != null
                && !item.getPagemap().getMetatags().isEmpty()) {
            GoogleSearchResponse.MetaTagItem metaTag = item.getPagemap().getMetatags().get(0);
            if (metaTag.getTitle() != null) {
                listing.setTitle(metaTag.getTitle());
            } else {
                listing.setTitle(item.getTitle());
            }
        } else {
            listing.setTitle(item.getTitle());
        }

        if (item.getPagemap() != null && item.getPagemap().getCseImage() != null
                && !item.getPagemap().getCseImage().isEmpty()) {
            listing.setImage(item.getPagemap().getCseImage().get(0).getSrc());
        }

        extractBrandAndModel(listing.getTitle(), listing);
        extractSpecifications(listing.getTitle(), listing);

        return listing;
    }

    private Double parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) return null;
        try {
            return Double.parseDouble(priceStr.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parsePriceFromTitle(String title) {
        Pattern pricePattern = Pattern.compile("\\$([0-9,]+\\.\\d{2})");
        Matcher priceMatcher = pricePattern.matcher(title);
        if (priceMatcher.find()) {
            return Double.parseDouble(priceMatcher.group(1).replace(",", ""));
        }
        return null;
    }

    private void extractBrandAndModel(String title, PriceComparisonDTO.ExternalListing listing) {
        Pattern brandModelPattern = Pattern.compile("(?i)(Apple|Dell|HP|Lenovo|Asus|Acer|MSI|Razer|Microsoft)\\s*([\\w\\d\\- ]+)");
        Matcher matcher = brandModelPattern.matcher(title);
        if (matcher.find()) {
            listing.setBrand(matcher.group(1));
            listing.setModel(matcher.group(2).trim());
        }
    }

    private void extractSpecifications(String title, PriceComparisonDTO.ExternalListing listing) {
        Pattern ramPattern = Pattern.compile("(\\d{2,}GB) RAM|RAM (\\d{2,}GB)");
        Matcher ramMatcher = ramPattern.matcher(title);
        if (ramMatcher.find()) {
            listing.setRam(ramMatcher.group(1) != null ? ramMatcher.group(1) : ramMatcher.group(2));
        }

        Pattern storagePattern = Pattern.compile("(\\d+[TBGB]+) (SSD|HDD)|Storage: (\\d+[TBGB]+)");
        Matcher storageMatcher = storagePattern.matcher(title);
        if (storageMatcher.find()) {
            listing.setStorage(
                    (storageMatcher.group(1) != null ? storageMatcher.group(1) : storageMatcher.group(3)) +
                            (storageMatcher.group(2) != null ? " " + storageMatcher.group(2) : "")
            );
        }

        Pattern cpuPattern = Pattern.compile("(Intel|AMD|M1|M2)\\s*(Core)?\\s*(i\\d{1,2}|Ryzen \\d{1,2}|Pro)?[- ]?(\\d{4,5}[A-Za-z]*)?");
        Matcher cpuMatcher = cpuPattern.matcher(title);
        if (cpuMatcher.find()) {
            listing.setCpu(cpuMatcher.group());
        }

        Pattern gpuPattern = Pattern.compile("(NVIDIA|AMD|M1|M2)\\s*(GeForce|Radeon|Pro)?\\s*(RTX|GTX|RX|core)?[- ]?(\\d{3,4}[A-Za-z]*)?");
        Matcher gpuMatcher = gpuPattern.matcher(title);
        if (gpuMatcher.find()) {
            listing.setGpu(gpuMatcher.group());
        }
    }

    public PriceComparisonDTO comparePrices(Long laptopId) {
        Laptop laptop = laptopRepository.findById(laptopId)
                .orElseThrow(() -> new IllegalArgumentException("Laptop not found"));

        BigDecimal currentPrice = BigDecimal.valueOf(laptop.getPrice());

        PriceComparisonDTO.MarketplaceLaptopDTO marketplaceLaptop = new PriceComparisonDTO.MarketplaceLaptopDTO();
        marketplaceLaptop.setId(laptop.getId());
        marketplaceLaptop.setModel(laptop.getModel());
        marketplaceLaptop.setBrand(laptop.getBrand());
        marketplaceLaptop.setPrice(currentPrice);
        marketplaceLaptop.setSeller(laptop.getSeller() != null ? laptop.getSeller().getUniversityId() : "Unknown Seller");
        marketplaceLaptop.setRam(laptop.getRam());
        marketplaceLaptop.setStorage(laptop.getStorage());
        marketplaceLaptop.setCpu(laptop.getCpu());
        marketplaceLaptop.setGpu(laptop.getGpu());

        List<PriceComparisonDTO.DatabaseListing> similarLaptops = getSimilarLaptops(laptop.getModel(), laptop.getId(), currentPrice);

        List<PriceComparisonDTO.ExternalListing> externalListings = searchExternalListings(
                laptop.getModel(), laptop.getBrand()
        );

        PriceComparisonDTO dto = new PriceComparisonDTO();
        dto.setMarketplaceLaptop(marketplaceLaptop);
        dto.setSimilarLaptops(similarLaptops);
        dto.setExternalListings(externalListings);

        return dto;
    }

    public List<PriceComparisonDTO.DatabaseListing> getSimilarLaptops(String model, Long excludeId, BigDecimal marketplacePrice) {
        return laptopRepository.findByModelAndIdNot(model, excludeId).stream()
                .map(laptop -> {
                    PriceComparisonDTO.DatabaseListing listing = new PriceComparisonDTO.DatabaseListing();
                    listing.setId(laptop.getId());
                    listing.setModel(laptop.getModel());
                    listing.setBrand(laptop.getBrand());
                    listing.setPrice(BigDecimal.valueOf(laptop.getPrice()));
                    listing.setSeller(laptop.getSeller() != null ? laptop.getSeller().getUniversityId() : null);
                    listing.setRam(laptop.getRam());
                    listing.setStorage(laptop.getStorage());
                    listing.setCpu(laptop.getCpu());
                    listing.setGpu(laptop.getGpu());
                    listing.setPriceDifference(BigDecimal.valueOf(laptop.getPrice()).subtract(marketplacePrice));
                    return listing;
                })
                .collect(Collectors.toList());
    }
}
