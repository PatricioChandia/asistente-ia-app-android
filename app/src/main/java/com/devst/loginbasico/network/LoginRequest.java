package com.devst.loginbasico.network;

// Esta clase representa el JSON que enviaremos al servidor para el login
public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters y Setters podrían ser necesarios si Gson tiene problemas
}