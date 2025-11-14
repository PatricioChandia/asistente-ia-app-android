package com.devst.voicegpt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.devst.voicegpt.network.ApiService;
import com.devst.voicegpt.network.ProfileResponse;
import com.devst.voicegpt.network.RetrofitClient;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private TextView tvBienvenida;
    private Button btnIrAlChat, btnVerPerfil, btnCerrarSesionMenu, btnIrANotas; // <-- ¡NUEVO!
    private CircleImageView ivMenuProfile;
    private ApiService apiService;
    private String authToken;
    private static final String TAG = "MenuPrincipalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 1. Cargar Token
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        authToken = settings.getString(LoginActivity.TOKEN_KEY, null);

        if (authToken == null) {
            irALogin(); // Si no hay token, fuera de aquí
            return;
        }

        // 2. Inicializar API
        apiService = RetrofitClient.getAuthenticatedApiService(authToken);

        // 3. Vincular Vistas
        tvBienvenida = findViewById(R.id.tvBienvenida);
        ivMenuProfile = findViewById(R.id.ivMenuProfile);
        btnIrAlChat = findViewById(R.id.btnIrAlChat);
        btnIrANotas = findViewById(R.id.btnIrANotas); // <-- ¡NUEVO!
        btnVerPerfil = findViewById(R.id.btnVerPerfil);
        btnCerrarSesionMenu = findViewById(R.id.btnCerrarSesionMenu);

        // 4. Configurar Listeners
        btnIrAlChat.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ChatActivity.class));
        });

        // ===================================
        // ¡LISTENER CORREGIDO!
        btnIrANotas.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, NotesActivity.class));
        });
        // ===================================

        btnVerPerfil.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        btnCerrarSesionMenu.setOnClickListener(v -> {
            // Borrar token y volver a Login
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(LoginActivity.TOKEN_KEY);
            editor.apply();
            irALogin();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosPerfil(); // Carga los datos cada vez que la pantalla se vuelve visible
    }

    private void cargarDatosPerfil() {
        Call<ProfileResponse> call = apiService.getPerfil();
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String nombre = response.body().getNombre();
                    String imageUrl = response.body().getProfileImageUrl();

                    tvBienvenida.setText("¡Bienvenido, " + nombre + "!");

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(HomeActivity.this)
                                .load(imageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                .placeholder(R.mipmap.ic_launcher_round)
                                .error(R.mipmap.ic_launcher_round)
                                .into(ivMenuProfile);
                    }

                } else {
                    Log.e(TAG, "Error al cargar perfil: " + response.code());
                    tvBienvenida.setText("¡Bienvenido!");
                    if (response.code() == 401) irALogin(); // Sesión expirada
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e(TAG, "Fallo al cargar perfil: " + t.getMessage());
                tvBienvenida.setText("¡Bienvenido!");
            }
        });
    }

    private void irALogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}