package org.example;

import java.time.LocalDateTime;

public class DiaryEntry {
    private final int id;
    private String title;
    private String content;
    private final LocalDateTime createdAt;

    public DiaryEntry(int id, String title, String content, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Entry ID: " + id +
               "\nTitle: " + title +
               "\nCreated: " + createdAt +
               "\nContent:\n" + content;
    }
}