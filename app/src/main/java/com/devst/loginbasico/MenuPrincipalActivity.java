package com.devst.loginbasico;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy; // <-- ¡NUEVO IMPORT!
import com.bumptech.glide.signature.ObjectKey; // <-- ¡NUEVO IMPORT!
import com.devst.loginbasico.network.ApiService;
import com.devst.loginbasico.network.ProfileResponse;
import com.devst.loginbasico.network.RetrofitClient;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuPrincipalActivity extends AppCompatActivity {

    private TextView tvBienvenida;
    private Button btnIrAlChat, btnVerPerfil, btnCerrarSesionMenu;
    private CircleImageView ivMenuProfile;
    private ApiService apiService;
    private String authToken;
    private static final String TAG = "MenuPrincipalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        // 1. Cargar Token
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        authToken = settings.getString(MainActivity.TOKEN_KEY, null);

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
        btnVerPerfil = findViewById(R.id.btnVerPerfil);
        btnCerrarSesionMenu = findViewById(R.id.btnCerrarSesionMenu);

        // 4. Configurar Listeners
        btnIrAlChat.setOnClickListener(v -> {
            startActivity(new Intent(MenuPrincipalActivity.this, AccesoActivity.class));
        });

        btnVerPerfil.setOnClickListener(v -> {
            startActivity(new Intent(MenuPrincipalActivity.this, PerfilActivity.class));
        });

        btnCerrarSesionMenu.setOnClickListener(v -> {
            // Borrar token y volver a Login
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(MainActivity.TOKEN_KEY);
            editor.apply();
            irALogin();
        });

        // 5. Cargar datos del perfil (Se movió a onResume)
        // cargarDatosPerfil(); // No es necesario aquí
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

                    // Cargar imagen con Glide
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        // ===================================
                        // ¡AQUÍ ESTÁ LA CORRECCIÓN!
                        // Forzamos a Glide a no usar la caché
                        // ===================================
                        Glide.with(MenuPrincipalActivity.this)
                                .load(imageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE) // No guardar en disco
                                .skipMemoryCache(true) // No guardar en RAM
                                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis()))) // Firma única
                                .placeholder(R.mipmap.ic_launcher_round)
                                .error(R.mipmap.ic_launcher_round)
                                .into(ivMenuProfile);
                        // ===================================
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
        Intent intent = new Intent(MenuPrincipalActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}