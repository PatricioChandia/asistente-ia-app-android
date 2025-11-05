package com.devst.loginbasico.network;

// Esta clase representa el JSON que recibiremos del servidor tras un login o registro exitoso
public class AuthResponse {
    private String message;
    private String token; // El token JWT que usaremos para futuras peticiones

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}