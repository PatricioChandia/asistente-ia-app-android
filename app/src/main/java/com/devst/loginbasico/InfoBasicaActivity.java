package com.devst.loginbasico;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class InfoBasicaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_basica);

        Button volver = findViewById(R.id.btnVolverLogin);
        volver.setOnClickListener(v -> finish()); // cierra y vuelve al login
    }
}
