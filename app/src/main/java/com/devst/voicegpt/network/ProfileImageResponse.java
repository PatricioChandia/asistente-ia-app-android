package com.devst.voicegpt.network;

// Representa la respuesta de POST /api/perfil/foto
public class ProfileImageResponse {
    String message;
    String profileImageUrl;

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}