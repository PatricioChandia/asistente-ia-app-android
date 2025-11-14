package com.devst.voicegpt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager; // <-- NUEVO
import androidx.recyclerview.widget.RecyclerView;

import com.devst.voicegpt.network.ApiService;
import com.devst.voicegpt.network.ConsultaRequest;
import com.devst.voicegpt.network.ConsultaResponse;
import com.devst.voicegpt.network.MessageModel;
import com.devst.voicegpt.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnCerrarSesion;
    private RecyclerView rvHistorialChat;
    private EditText etMensaje;
    private FloatingActionButton btnEnviar;

    private ApiService apiService;
    private String authToken;
    private static final String TAG = "AccesoActivity";

    // ===================================
    // NUEVO: El Adaptador
    private ChatAdapter chatAdapter;
    // ===================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // --- 1. Cargar el Token ---
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        authToken = settings.getString(LoginActivity.TOKEN_KEY, null);

        if (authToken == null) {
            Log.e(TAG, "¡Token no encontrado! Volviendo al Login.");
            irALogin();
            return;
        }
        Log.d(TAG, "Token encontrado: " + authToken);

        // --- 2. Inicializar el Cliente de API Autenticado ---
        apiService = RetrofitClient.getAuthenticatedApiService(authToken);

        // --- 3. Encontrar los componentes de la UI ---
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        rvHistorialChat = findViewById(R.id.rvHistorialChat);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        // --- 4. Configurar Listeners ---
        btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(LoginActivity.TOKEN_KEY);
            editor.apply();
            Log.d(TAG, "Token borrado. Cerrando sesión.");
            irALogin();
        });

        btnEnviar.setOnClickListener(v -> enviarConsulta());

        // ===================================
        // --- 5. Configurar el RecyclerView (¡NUEVO!) ---
        setupRecyclerView();
        // ===================================

        // --- 6. Cargar el historial de chat ---
        cargarHistorial();
    }

    // ===================================
    // NUEVO: Método para configurar el RecyclerView
    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Hace que la lista empiece desde abajo
        rvHistorialChat.setLayoutManager(layoutManager);
        rvHistorialChat.setAdapter(chatAdapter);
    }
    // ===================================

    private void irALogin() {
        Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void cargarHistorial() {
        Log.d(TAG, "Llamando a /api/historial...");
        Call<List<MessageModel>> call = apiService.getHistorial();

        call.enqueue(new Callback<List<MessageModel>>() {
            @Override
            public void onResponse(Call<List<MessageModel>> call, Response<List<MessageModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MessageModel> historial = response.body();
                    Log.d(TAG, "¡Historial recibido! Cantidad de mensajes: " + historial.size());

                    // ===================================
                    // CAMBIO: ¡Entregar historial al adapter!
                    chatAdapter.setMessages(historial);
                    rvHistorialChat.scrollToPosition(historial.size() - 1); // Ir al final
                    // ===================================

                } else {
                    Log.e(TAG, "Error al cargar historial: " + response.code());
                    if (response.code() == 401) {
                        Toast.makeText(ChatActivity.this, "Sesión expirada", Toast.LENGTH_SHORT).show();
                        irALogin();
                    }
                }
            }
            @Override
            public void onFailure(Call<List<MessageModel>> call, Throwable t) {
                Log.e(TAG, "Fallo al cargar historial: " + t.getMessage());
            }
        });
    }

    private void enviarConsulta() {
        String consulta = etMensaje.getText().toString().trim();
        if (consulta.isEmpty()) {
            return;
        }
        etMensaje.setText(""); // Limpiar el campo de texto

        // ===================================
        // CAMBIO: Añadir mensaje de 'user' al adapter
        MessageModel userMessage = new MessageModel("user", consulta);
        chatAdapter.addMessage(userMessage);
        rvHistorialChat.scrollToPosition(chatAdapter.getItemCount() - 1);
        // ===================================

        Log.d(TAG, "Enviando consulta a /api/consulta: " + consulta);

        ConsultaRequest request = new ConsultaRequest(consulta);
        Call<ConsultaResponse> call = apiService.enviarConsulta(request);

        call.enqueue(new Callback<ConsultaResponse>() {
            @Override
            public void onResponse(Call<ConsultaResponse> call, Response<ConsultaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String respuestaIA = response.body().getResponse();
                    Log.d(TAG, "Respuesta de IA recibida: " + respuestaIA);

                    // ===================================
                    // CAMBIO: Añadir respuesta de 'assistant' al adapter
                    MessageModel assistantMessage = new MessageModel("assistant", respuestaIA);
                    chatAdapter.addMessage(assistantMessage);
                    rvHistorialChat.scrollToPosition(chatAdapter.getItemCount() - 1);
                    // ===================================

                } else {
                    Log.e(TAG, "Error al enviar consulta: " + response.code());
                    if (response.code() == 401) irALogin();
                }
            }
            @Override
            public void onFailure(Call<ConsultaResponse> call, Throwable t) {
                Log.e(TAG, "Fallo al enviar consulta: " + t.getMessage());
            }
        });
    }
}