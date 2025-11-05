package com.devst.loginbasico.network;

import java.io.IOException; // <-- NUEVO
import okhttp3.Interceptor; // <-- NUEVO
import okhttp3.OkHttpClient; // <-- NUEVO
import okhttp3.Request; // <-- NUEVO
import okhttp3.Response; // <-- NUEVO
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // IP del emulador que apunta al localhost de tu PC
    private static final String BASE_URL = "http://10.0.2.2:3000/";

    private static Retrofit retrofitBase = null;

    // Cliente base (para Login y Registro, que no necesitan token)
    public static ApiService getApiService() {
        if (retrofitBase == null) {
            retrofitBase = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitBase.create(ApiService.class);
    }

    // ===================================
    // NUEVO: Cliente "Autenticado"
    // Este cliente adjuntará el token a todas las peticiones
    // ===================================
    public static ApiService getAuthenticatedApiService(final String token) {

        // 1. Creamos un "Interceptor"
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer " + token); // <-- Aquí se adjunta el token

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();

        // 2. Creamos una nueva instancia de Retrofit con este cliente
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // <-- Usamos el cliente con el interceptor
                .build();

        return retrofit.create(ApiService.class);
    }
}