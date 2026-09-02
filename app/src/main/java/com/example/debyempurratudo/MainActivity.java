package com.example.debyempurratudo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Trocamos o layout XML padrão pela nossa GameView do Sokoban
        GameView gameView = new GameView(this);
        setContentView(gameView);
    }
}