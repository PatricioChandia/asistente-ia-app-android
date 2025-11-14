package com.devst.voicegpt.network;

// Esta clase representa el JSON que enviaremos al servidor para el registro
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;

    public RegisterRequest(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }
}