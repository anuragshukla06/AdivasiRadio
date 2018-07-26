package com.microsoft.melange.gondispeak;

public class SavedText {

    private String text;
    private int rating;
    private int thumbnail;

    public SavedText(String text, int rating, int thumbnail) {
        this.text = text;
        this.rating = rating;
        this.thumbnail = thumbnail;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
