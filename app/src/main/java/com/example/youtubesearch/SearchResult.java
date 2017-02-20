package com.example.youtubesearch;

public class SearchResult {
    public String thumbnailUrl;
    public String title;
    public String subTitle;
    public String videoId;

    public SearchResult(String thumbnailUrl, String title, String subTitle, String videoId) {
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.subTitle = subTitle;
        this.videoId = videoId;
    }
}
