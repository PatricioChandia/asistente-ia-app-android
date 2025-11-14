package com.devst.voicegpt.network;

// Representa un mensaje en la base de datos
public class MessageModel {
    String role;
    String content;
    String timestamp;
    String _id; // ID de MongoDB

    // ===================================
    // NUEVO: Constructor
    public MessageModel(String role, String content) {
        this.role = role;
        this.content = content;
    }
    // ===================================

    // Getters
    public String getRole() { return role; }
    public String getContent() { return content; }
}