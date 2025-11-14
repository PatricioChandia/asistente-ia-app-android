package com.devst.voicegpt.network;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT; // <-- NUEVO IMPORT
import retrofit2.http.Multipart; // <-- NUEVO
import retrofit2.http.Part; // <-- NUEVO
import okhttp3.MultipartBody; // <-- NUEVO

public interface ApiService {

    // ... (Rutas register, login, getHistorial, enviarConsulta sin cambios) ...
    @POST("/api/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("/api/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @GET("/api/historial")
    Call<List<MessageModel>> getHistorial();

    @POST("/api/consulta")
    Call<ConsultaResponse> enviarConsulta(@Body ConsultaRequest request);

    // ===================================
    // NUEVAS RUTAS DE PERFIL
    // ===================================

    @GET("/api/perfil")
    Call<ProfileResponse> getPerfil();

    @PUT("/api/perfil")
    Call<AuthResponse> updatePerfil(@Body ProfileUpdateRequest request); // Reutilizamos AuthResponse

    @Multipart
    @POST("/api/perfil/foto")
    Call<ProfileImageResponse> uploadProfilePicture(
            @Part MultipartBody.Part profileImage
    );
}