package com.devst.loginbasico;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkTokenAndNavigate();
            }
        }, SPLASH_DURATION);
    }

    private void checkTokenAndNavigate() {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        String authToken = settings.getString(MainActivity.TOKEN_KEY, null);

        Intent intent;

        // =======================================================
        // ¡CAMBIO! Ahora navegamos al Menú Principal
        // =======================================================
        if (authToken != null) {
            // Si SÍ hay token, vamos al Menú Principal
            intent = new Intent(SplashActivity.this, MenuPrincipalActivity.class); // <-- CAMBIO
        } else {
            // Si NO hay token, vamos al Login
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        // =======================================================

        startActivity(intent);
        finish();
    }
}