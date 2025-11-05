package com.devst.loginbasico;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class EvaluacionEnviada extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluacion_enviada);

        Button volverAtras = findViewById(R.id.btnVolverAtras);
        volverAtras.setOnClickListener(v -> finish()); // vuelve atras
    }

}
