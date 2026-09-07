package com.example.debyempurratudo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class FasesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fases);
        GridView gridView = findViewById(R.id.gridViewFases);

        //Qtd tota de fases e qual a ultima liberada
        LevelManager levelManager = new LevelManager(this);
        int totalFases = levelManager.getTotalFases();
        int faseLiberada = SaveManager.getFaseLiberada(this);

        List<String> listaFases = new ArrayList<>();
        for (int i = 0; i < totalFases; i++) {
            listaFases.add(String.valueOf(i));
        }

        //Ativar/Desativar cada quadrado da grade
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaFases) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                int numeroFase = position + 1;

                tv.setTextSize(20f);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tv.setPadding(20, 30, 20, 30);

                if (numeroFase <= faseLiberada) {
                    // Fase liberada: Fundo verde e texto branco
                    tv.setBackgroundColor(Color.parseColor("#4CAF50"));
                    tv.setTextColor(Color.WHITE);
                    tv.setEnabled(true);
                } else {
                    // Fase bloqueada: Fundo cinza escuro
                    tv.setBackgroundColor(Color.parseColor("#333333"));
                    tv.setTextColor(Color.parseColor("#777777"));
                    tv.setEnabled(false);
                }

                return tv;
            }
        };
        gridView.setAdapter(adapter);

        //Clique fase grade
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int numeroFaseSelecionada = position + 1;

            if (numeroFaseSelecionada <= faseLiberada) {
                Intent intent = new Intent(FasesActivity.this, MainActivity.class);
                intent.putExtra("FASE_INICIAL", numeroFaseSelecionada);
                startActivity(intent);
            }
        });
    }
}