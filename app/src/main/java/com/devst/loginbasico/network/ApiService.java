package com.devst.loginbasico.network;

import java.util.List; // <-- NUEVO
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET; // <-- NUEVO

public interface ApiService {

    // Ruta para registrar
    @POST("/api/register")
    Call<Void> register(@Body RegisterRequest request);

    // Ruta para iniciar sesión
    @POST("/api/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    // ===================================
    // NUEVAS RUTAS
    // ===================================

    // Ruta para obtener el historial (protegida)
    @GET("/api/historial")
    Call<List<MessageModel>> getHistorial();

    // Ruta para enviar una consulta (protegida)
    @POST("/api/consulta")
    Call<ConsultaResponse> enviarConsulta(@Body ConsultaRequest request);
}