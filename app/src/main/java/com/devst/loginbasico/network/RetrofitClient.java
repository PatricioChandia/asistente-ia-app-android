package com.devst.loginbasico.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit; // <-- ¡NUEVO IMPORT!

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.0.6:3000/";

    private static Retrofit retrofitBase = null;

    // Cliente base (para Login y Registro, que no necesitan token)
    // (Este lo dejamos con el timeout por defecto, que es rápido)
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
    // (Este cliente tendrá timeouts LARGOS para la subida de fotos)
    // ===================================
    public static ApiService getAuthenticatedApiService(final String token) {

        // 1. Creamos un "Interceptor"
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // ===================================
        // ¡NUEVA LÓGICA DE TIMEOUT!
        // Aumentamos el tiempo de espera a 2 minutos
        // ===================================
        httpClient.connectTimeout(2, TimeUnit.MINUTES);
        httpClient.readTimeout(2, TimeUnit.MINUTES);
        httpClient.writeTimeout(2, TimeUnit.MINUTES);
        // ===================================

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
                .client(client) // <-- Usamos el cliente con el interceptor y timeouts
                .build();

        return retrofit.create(ApiService.class);
    }
}