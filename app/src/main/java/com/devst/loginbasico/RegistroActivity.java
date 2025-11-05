package com.devst.loginbasico;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.app.ProgressDialog; // Importar
import android.util.Log; // Importar

// =======================================
// Imports de red
// =======================================
import com.devst.loginbasico.network.ApiService;
// Se quita AuthResponse porque ya no se usa aquí
import com.devst.loginbasico.network.RegisterRequest;
import com.devst.loginbasico.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// =======================================

public class RegistroActivity extends AppCompatActivity {
    private EditText etNombre, etCorreo, etPassword, etConfirmar;
    private CheckBox checkBox;
    private ApiService apiService; // Añadir
    private ProgressDialog progressDialog; // Añadir

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        etConfirmar = findViewById(R.id.etConfirmar);
        checkBox = findViewById(R.id.checkbox);

        // Inicializar ApiService
        apiService = RetrofitClient.getApiService();

        // Inicializar ProgressDialog (para mostrar "cargando")
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando...");
        progressDialog.setCancelable(false);


        findViewById(R.id.btnRegistrar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        findViewById(R.id.btnVolver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registrarUsuario() {
        String nombre = etNombre.getText().toString().trim();

        // ======================================================
        // CAMBIO: de 'correo' a 'email' para ser consistente
        // ======================================================
        String email = etCorreo.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirmar = etConfirmar.getText().toString().trim();

        // CAMBIO: de 'correo' a 'email'
        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty() || confirmar.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }
        // CAMBIO: de 'correo' a 'email'
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(confirmar)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkBox.isChecked()) {
            Toast.makeText(this, "Debes aceptar los términos para continuar", Toast.LENGTH_SHORT).show();
            return;
        }

        // ===================================================================
        // N U E V O: Lógica de red (Actualizada)
        // ===================================================================

        progressDialog.show(); // Mostrar diálogo "cargando"

        // 1. Crear el objeto de la petición (con 'email')
        RegisterRequest registerRequest = new RegisterRequest(nombre, email, pass);

        // 2. Hacer la llamada asíncrona (esperando 'Void')
        // CAMBIO: de 'Call<AuthResponse>' a 'Call<Void>'
        Call<Void> call = apiService.register(registerRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss(); // Ocultar diálogo "cargando"

                if (response.isSuccessful()) {
                    // ¡Registro exitoso!
                    Toast.makeText(RegistroActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                    // Cerramos esta actividad para volver al Login (MainActivity)
                    finish();
                } else {
                    // Error del servidor (ej. 400 El correo ya existe)
                    Toast.makeText(RegistroActivity.this, "Error: El correo ya está en uso", Toast.LENGTH_SHORT).show();
                    Log.e("RegistroActivity", "Error en registro: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Error de red
                progressDialog.dismiss(); // Ocultar diálogo "cargando"
                Toast.makeText(RegistroActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("RegistroActivity", "Fallo de conexión: " + t.getMessage());
            }
        });
    }
}