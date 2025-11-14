package com.devst.voicegpt;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy; // <-- ¡NUEVO IMPORT!
import com.bumptech.glide.signature.ObjectKey; // <-- ¡NUEVO IMPORT!
import com.devst.voicegpt.network.ApiService;
import com.devst.voicegpt.network.AuthResponse;
import com.devst.voicegpt.network.ProfileImageResponse;
import com.devst.voicegpt.network.ProfileResponse;
import com.devst.voicegpt.network.ProfileUpdateRequest;
import com.devst.voicegpt.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbarPerfil;
    private TextView tvPerfilEmail;
    private EditText etPerfilNombre;
    private Button btnGuardarPerfil;
    private CircleImageView ivPerfil;

    private ApiService apiService;
    private String authToken;
    private static final String TAG = "PerfilActivity";

    private Uri selectedImageUri = null;

    // Launcher para pedir permiso de galería
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openGallery(); // Si se da permiso, abrir galería
                } else {
                    Toast.makeText(this, "Permiso de galería denegado", Toast.LENGTH_SHORT).show();
                }
            });

    // Launcher para abrir la galería
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Mostrar la imagen seleccionada y subirla
                    // Esta llamada no necesita romper caché, porque es una URI local
                    Glide.with(this).load(selectedImageUri).into(ivPerfil);
                    uploadImageToBackend(selectedImageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Cargar Token e Inicializar API
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        authToken = settings.getString(LoginActivity.TOKEN_KEY, null);
        if (authToken == null) { irALogin(); return; }
        apiService = RetrofitClient.getAuthenticatedApiService(authToken);

        // 3. Vincular Vistas
        toolbarPerfil = findViewById(R.id.toolbarPerfil);
        tvPerfilEmail = findViewById(R.id.tvPerfilEmail);
        etPerfilNombre = findViewById(R.id.etPerfilNombre);
        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil);
        ivPerfil = findViewById(R.id.ivPerfil);

        // Configurar Toolbar
        setSupportActionBar(toolbarPerfil);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPerfil.setNavigationOnClickListener(v -> finish()); // Acción de volver

        // 4. Configurar Listeners
        btnGuardarPerfil.setOnClickListener(v -> guardarCambios());
        ivPerfil.setOnClickListener(v -> checkPermissionAndOpenGallery());

        // 5. Cargar datos del perfil
        cargarDatosPerfil();
    }

    private void cargarDatosPerfil() {
        Call<ProfileResponse> call = apiService.getPerfil();
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse perfil = response.body();
                    tvPerfilEmail.setText(perfil.getEmail());
                    etPerfilNombre.setText(perfil.getNombre());

                    // Cargar foto de perfil
                    String imageUrl = perfil.getProfileImageUrl();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        // ===================================
                        // ¡AQUÍ ESTÁ LA CORRECCIÓN!
                        // Forzamos a Glide a no usar la caché
                        // ===================================
                        Glide.with(ProfileActivity.this)
                                .load(imageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE) // No guardar en disco
                                .skipMemoryCache(true) // No guardar en RAM
                                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis()))) // Firma única
                                .placeholder(R.mipmap.ic_launcher_round)
                                .error(R.mipmap.ic_launcher_round)
                                .into(ivPerfil);
                        // ===================================
                    }
                } else {
                    Log.e(TAG, "Error al cargar perfil: " + response.code());
                    if (response.code() == 401) irALogin();
                }
            }
            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e(TAG, "Fallo al cargar perfil: " + t.getMessage());
            }
        });
    }

    // 1. Revisar permisos
    private void checkPermissionAndOpenGallery() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery(); // Permiso ya dado
        } else {
            requestPermissionLauncher.launch(permission); // Pedir permiso
        }
    }

    // 2. Abrir la galería
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    // 3. Subir la imagen al Backend
    private void uploadImageToBackend(Uri imageUri) {
        Toast.makeText(this, "Subiendo foto...", Toast.LENGTH_SHORT).show();

        File file = createTempFileFromUri(imageUri);
        if (file == null) {
            Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profileImage", file.getName(), requestFile);

        Call<ProfileImageResponse> call = apiService.uploadProfilePicture(body);
        call.enqueue(new Callback<ProfileImageResponse>() {
            @Override
            public void onResponse(Call<ProfileImageResponse> call, Response<ProfileImageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProfileActivity.this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                    String newUrl = response.body().getProfileImageUrl();
                    Log.d(TAG, "Nueva URL de Cloudinary: " + newUrl);
                } else {
                    Toast.makeText(ProfileActivity.this, "Error al subir la foto", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al subir foto: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProfileImageResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Fallo al subir foto: " + t.getMessage());
            }
        });
    }

    // 4. Helper para convertir Uri a File
    private File createTempFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File tempFile = File.createTempFile("profile", ".jpg", getCacheDir());
            tempFile.deleteOnExit();
            OutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return tempFile;
        } catch (Exception e) {
            Log.e(TAG, "Error al crear archivo temporal", e);
            return null;
        }
    }

    // Guardar cambios del nombre
    private void guardarCambios() {
        String nuevoNombre = etPerfilNombre.getText().toString().trim();
        if (nuevoNombre.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardarPerfil.setEnabled(false);
        btnGuardarPerfil.setText("Guardando...");

        ProfileUpdateRequest request = new ProfileUpdateRequest(nuevoNombre);
        Call<AuthResponse> call = apiService.updatePerfil(request);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra la actividad y vuelve al menú
                } else {
                    Toast.makeText(ProfileActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al guardar perfil: " + response.code());
                    if (response.code() == 401) irALogin();
                }
                btnGuardarPerfil.setEnabled(true);
                btnGuardarPerfil.setText("Guardar Cambios");
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Fallo al guardar perfil: " + t.getMessage());
                btnGuardarPerfil.setEnabled(true);
                btnGuardarPerfil.setText("Guardar Cambios");
            }
        });
    }

    private void irALogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}