package com.example.decocheck.model;

public class Post {
    private int id;
    private String title;
    private String content;
    private String excerpt;
    private String date;
    private String featuredImageUrl;
    private String author;

    public Post() {}

    public Post(int id, String title, String content, String excerpt, String date, String featuredImageUrl, String author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.excerpt = excerpt;
        this.date = date;
        this.featuredImageUrl = featuredImageUrl;
        this.author = author;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getDate() {
        return date;
    }

    public String getFeaturedImageUrl() {
        return featuredImageUrl;
    }

    public String getAuthor() {
        return author;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFeaturedImageUrl(String featuredImageUrl) {
        this.featuredImageUrl = featuredImageUrl;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}