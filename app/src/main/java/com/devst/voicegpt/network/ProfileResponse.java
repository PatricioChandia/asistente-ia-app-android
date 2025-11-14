package com.devst.voicegpt.network;

// Representa la respuesta de GET /api/perfil
public class ProfileResponse {
    String nombre;
    String email;
    String profileImageUrl; // <-- ¡NUEVO CAMPO!

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImageUrl() { // <-- ¡NUEVO GETTER!
        return profileImageUrl;
    }
}