package com.devst.loginbasico.network;

// Representa el body que enviamos a PUT /api/perfil
public class ProfileUpdateRequest {
    String nombre;

    public ProfileUpdateRequest(String nombre) {
        this.nombre = nombre;
    }
}