package com.devst.loginbasico;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.util.Log;

import com.devst.loginbasico.network.ApiService;
import com.devst.loginbasico.network.AuthResponse;
import com.devst.loginbasico.network.RegisterRequest;
import com.devst.loginbasico.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {
    private EditText etNombre, etCorreo, etPassword, etConfirmar;
    private CheckBox checkBox;
    private ApiService apiService;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        etConfirmar = findViewById(R.id.etConfirmar);
        checkBox = findViewById(R.id.checkbox);

        apiService = RetrofitClient.getApiService();

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
        String email = etCorreo.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirmar = etConfirmar.getText().toString().trim();

        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty() || confirmar.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }
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

        progressDialog.show();

        RegisterRequest registerRequest = new RegisterRequest(nombre, email, pass);
        Call<AuthResponse> call = apiService.register(registerRequest);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegistroActivity.this, "¡Registro exitoso! Iniciando sesión...", Toast.LENGTH_SHORT).show();

                    String token = response.body().getToken();
                    Log.d("RegistroActivity", "Token recibido: " + token);

                    // 1. Guardar el token
                    SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(MainActivity.TOKEN_KEY, token);
                    editor.apply();

                    // =======================================================
                    // ¡CAMBIO! Ir al Menú Principal, no al chat
                    // =======================================================
                    Intent i = new Intent(RegistroActivity.this, MenuPrincipalActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                    // =======================================================

                } else {
                    Toast.makeText(RegistroActivity.this, "Error: El correo ya está en uso", Toast.LENGTH_SHORT).show();
                    Log.e("RegistroActivity", "Error en registro: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RegistroActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("RegistroActivity", "Fallo de conexión: " + t.getMessage());
            }
        });
    }
}