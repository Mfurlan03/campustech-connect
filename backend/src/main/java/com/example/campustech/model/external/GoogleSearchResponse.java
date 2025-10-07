package com.example.campustech.model.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleSearchResponse {

    @JsonProperty("items")
    private List<SearchItem> items;

    public List<SearchItem> getItems() {
        return items;
    }

    public void setItems(List<SearchItem> items) {
        this.items = items;
    }

    // 定义 "items" 内的子类
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchItem {
        @JsonProperty("title")
        private String title;

        @JsonProperty("link")
        private String link;

        @JsonProperty("pagemap")
        private PageMap pagemap;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public PageMap getPagemap() {
            return pagemap;
        }

        public void setPagemap(PageMap pagemap) {
            this.pagemap = pagemap;
        }
    }

    // 解析 "pagemap" 部分的更多信息
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PageMap {
        @JsonProperty("cse_image")
        private List<ImageItem> cseImage;

        @JsonProperty("offer")
        private List<OfferItem> offer;

        @JsonProperty("metatags")
        private List<MetaTagItem> metatags;

        @JsonProperty("product")
        private List<ProductItem> product;

        public List<ImageItem> getCseImage() {
            return cseImage;
        }

        public void setCseImage(List<ImageItem> cseImage) {
            this.cseImage = cseImage;
        }

        public List<OfferItem> getOffer() {
            return offer;
        }

        public void setOffer(List<OfferItem> offer) {
            this.offer = offer;
        }

        public List<MetaTagItem> getMetatags() {
            return metatags;
        }

        public void setMetatags(List<MetaTagItem> metatags) {
            this.metatags = metatags;
        }

        public List<ProductItem> getProduct() {
            return product;
        }

        public void setProduct(List<ProductItem> product) {
            this.product = product;
        }
    }

    // "cse_image" 解析
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageItem {
        @JsonProperty("src")
        private String src;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }
    }

    // "offer" 解析 (可能包含价格)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OfferItem {
        @JsonProperty("price")
        private String price;

        @JsonProperty("currency")
        private String currency;

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetaTagItem {
        @JsonProperty("og:price:amount")
        private String priceAmount;

        @JsonProperty("og:title")  // ✅ 解析完整的标题信息
        private String title;

        public String getPriceAmount() {
            return priceAmount;
        }

        public void setPriceAmount(String priceAmount) {
            this.priceAmount = priceAmount;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }




    // "product" 解析 (可能包含产品规格)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductItem {
        @JsonProperty("name")
        private String name;

        @JsonProperty("brand")
        private String brand;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }
    }
}
