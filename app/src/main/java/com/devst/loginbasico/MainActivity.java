package com.devst.loginbasico;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.devst.loginbasico.network.ApiService;
import com.devst.loginbasico.network.AuthResponse;
import com.devst.loginbasico.network.LoginRequest;
import com.devst.loginbasico.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btnLogin;
    private TextView registrar;
    private ApiService apiService;
    private ProgressDialog progressDialog;

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String TOKEN_KEY = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email     = findViewById(R.id.email);
        password  = findViewById(R.id.password);
        btnLogin  = findViewById(R.id.btnLogin);
        registrar = findViewById(R.id.registrar);

        apiService = RetrofitClient.getApiService(); // Obtener el servicio base

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Iniciando sesión...");
        progressDialog.setCancelable(false);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCrear(v);
            }
        });
    }

    public void onClickEntrar(View view) {
        String user = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (user.isEmpty()) {
            Toast.makeText(this,"El campo de usuario está vacío", Toast.LENGTH_SHORT).show();
            email.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            Toast.makeText(this,"El campo de contraseña está vacío", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
            Toast.makeText(this, "Formato de correo inválido", Toast.LENGTH_SHORT).show();
            email.requestFocus();
            return;
        }

        // --- LÓGICA DE RED ---
        progressDialog.show();

        LoginRequest loginRequest = new LoginRequest(user, pass);
        Call<AuthResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    // ¡Login exitoso!
                    String token = response.body().getToken();
                    Log.d("MainActivity", "Token recibido: " + token);

                    // Guardar el token
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(TOKEN_KEY, token);
                    editor.apply();

                    // =======================================================
                    // ¡CAMBIO! Ir al Menú Principal, no al chat
                    // =======================================================
                    Intent intent = new Intent(MainActivity.this, MenuPrincipalActivity.class);
                    // Limpiamos la pila para que no pueda "volver" al login
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // Cierra MainActivity
                    // =======================================================

                } else {
                    // Error (credenciales incorrectas, etc.)
                    Toast.makeText(MainActivity.this, "Credenciales Incorrectas", Toast.LENGTH_SHORT).show();
                    Log.e("MainActivity", "Error en login: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "Fallo de conexión: " + t.getMessage());
            }
        });
    }

    public void onClickCrear(View view) {
        Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
        startActivity(intent);
    }
}