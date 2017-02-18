package com.example.youtubesearch;

public class SearchResult {
    public String thumbnailUrl;
    public String title;
    public String subTitle;

    public SearchResult(String thumbnailUrl, String title, String subTitle) {
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.subTitle = subTitle;
    }
}
