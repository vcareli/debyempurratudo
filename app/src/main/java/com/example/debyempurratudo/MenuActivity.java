package com.example.debyempurratudo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    /*int maiorFaseLiberada = SaveManager.getFaseLiberada(this);
    // Para cada botão de fase na tela:
    btnFase1.setEnabled(true); // Fase 1 sempre liberada
    if (maiorFaseLiberada >= 2) {
        btnFase2.setEnabled(true); // Libera o clique no botão da Fase 2
    } else {
        btnFase2.setEnabled(false); // Mantém bloqueado com tom cinza/cadeado
    }

    if (maiorFaseLiberada >= 3) {
        btnFase3.setEnabled(true);
    } else {
        btnFase3.setEnabled(false);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Botao jogar abre ultima fase desbloqueada
        Button btnJogar = findViewById(R.id.btnJogar);
        btnJogar.setOnClickListener(v -> {
            int faseSalva = SaveManager.getFaseLiberada(MenuActivity.this);
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            intent.putExtra("FASE_INICIAL", faseSalva);
            startActivity(intent);
        });

        //Botao fases abre tela para esoclha de fases
        Button btnFases = findViewById(R.id.btnFases);
        btnFases.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, FasesActivity.class);
            startActivity(intent);
        });
    }
}