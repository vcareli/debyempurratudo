package com.example.debyempurratudo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recebe o número da fase enviado pelo Menu
        int faseInicial = getIntent().getIntExtra("FASE_INICIAL", 1);

        // Instancia a GameView passando a fase inicial
        gameView = new GameView(this, faseInicial);
        setContentView(gameView);
    }
}