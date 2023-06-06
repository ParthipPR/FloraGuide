package com.example.FloraGuide;

public class Item {
    private int id;
    private String title;
    private String description;
    private String imageUrl;

    public Item(int id, String title, String description, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;

    }

    public int getImageResId() {
        return id;
    }

    public void setImageResId(int imageResId) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
