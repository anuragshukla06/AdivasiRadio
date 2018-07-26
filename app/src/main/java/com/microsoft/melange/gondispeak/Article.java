package com.microsoft.melange.gondispeak;

public class Article {
    private String title;
    private String content;
    private int thumbnail;
    private String url;

    public Article(String title, String content, int thumbnail, String url) {
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
