package com.example.elderlycareapp;

public class Video {
    public String title;
    public String duration;
    public String difficulty;
    public String channel;
    public int thumbnailResId;
    public String videoId;
    public String category;

    private boolean isFavorite = false;  // private favorite flag

    // Keep your original constructor exactly as you wrote it
    public Video(String title, String duration, String difficulty, String channel,
                 int thumbnailResId, String videoId, String category) {
        this.title = title;
        this.duration = duration;
        this.difficulty = difficulty;
        this.channel = channel;
        this.thumbnailResId = thumbnailResId;
        this.videoId = videoId;
        this.category = category;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getChannel() {
        return channel;
    }

    public int getThumbnailResId() {
        return thumbnailResId;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getCategory() {
        return category;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}