package com.devst.voicegpt.db;

// Esta es una clase 'POJO' (Plain Old Java Object)
// que representa una sola nota.
public class Note {
    private long id;
    private String title;
    private String content;
    private String timestamp;

    // Constructor para crear una nota
    public Note(long id, String title, String content, String timestamp) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }
}