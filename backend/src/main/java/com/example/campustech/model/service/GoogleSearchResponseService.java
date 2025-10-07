package com.example.campustech.model.service;

import lombok.Getter;

import java.util.List;

@Getter
public class GoogleSearchResponseService {
    private List<SearchItem> items;

    public void setItems(List<SearchItem> items) {
        this.items = items;
    }

    @Getter
    public static class SearchItem {
        private String title;
        private String link;
        private Pagemap pagemap;

        public void setTitle(String title) {
            this.title = title;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public void setPagemap(Pagemap pagemap) {
            this.pagemap = pagemap;
        }
    }

    @Getter
    public static class Pagemap {
        private List<CseImage> cse_image;

        public void setCse_image(List<CseImage> cse_image) {
            this.cse_image = cse_image;
        }
    }

    @Getter
    public static class CseImage {
        private String src;

        public void setSrc(String src) {
            this.src = src;
        }
    }
}
