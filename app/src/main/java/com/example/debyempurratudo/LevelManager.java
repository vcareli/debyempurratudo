package com.example.debyempurratudo;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// 1 - Parede
// 0 - Chao vazio
// 2 - Caixa
// 3 - Destino
// OBS:
// O jogador NAO esta representado com 4.
// A posicao inicial do jogador deve ser definida
// separadamente pela classe do jogo.


public class LevelManager {
    private int nivelAtual = 0;
    private final List<int[][]> mapas = new ArrayList<>();
    private final List<int[][]> alvos = new ArrayList<>();
    private final List<int[]> posicoesIniciais = new ArrayList<>();

    public LevelManager(Context context) {
        chargeLevelsJSON(context);
    }

    private void chargeLevelsJSON(Context context) {
        try {
            // 1. Abre o arquivo fases.json da pasta assets
            InputStream is = context.getAssets().open("fases.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonString);

            // 2. Percorre cada fase cadastrada no JSON
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objFase = jsonArray.getJSONObject(i);

                int pLin = objFase.getInt("playerLin");
                int pCol = objFase.getInt("playerCol");
                posicoesIniciais.add(new int[]{pLin, pCol});

                // Converte a matriz mapa
                JSONArray jsonMapa = objFase.getJSONArray("mapa");
                int[][] matrizMapa = jsonArrayParaMatriz(jsonMapa);
                mapas.add(matrizMapa);

                // Converte a matriz alvo
                JSONArray jsonAlvo = objFase.getJSONArray("alvo");
                int[][] matrizAlvo = jsonArrayParaMatriz(jsonAlvo);
                alvos.add(matrizAlvo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int[][] jsonArrayParaMatriz(JSONArray jsonArray) throws Exception {
        int linhas = jsonArray.length();
        int colunas = jsonArray.getJSONArray(0).length();
        int[][] matriz = new int[linhas][colunas];

        for (int i = 0; i < linhas; i++) {
            JSONArray linhaArray = jsonArray.getJSONArray(i);
            for (int j = 0; j < colunas; j++) {
                matriz[i][j] = linhaArray.getInt(j);
            }
        }
        return matriz;
    }

    public int[][] getMapaAtual() {
        return clonarMatrix(mapas.get(nivelAtual));
    }

    public int[][] getAlvoAtual() {
        return alvos.get(nivelAtual);
    }

    public int getPlayerLinhaInicial() {
        return posicoesIniciais.get(nivelAtual)[0];
    }

    public int getPlayerColunaInicial() {
        return posicoesIniciais.get(nivelAtual)[1];
    }

    public boolean nextLevel() {
        if (nivelAtual + 1 < mapas.size()) {
            nivelAtual++;
            return true;
        }
        return false;
    }

    public void restartNivel() {
        int[][] mapa = this.getMapaAtual();
    }

    public int getNivelAtual() {
        return nivelAtual;
    }

    public int[][] clonarMatrix(int[][] origin) {
        int[][] copia = new int[origin.length][origin[0].length];
        for (int i = 0; i < origin.length; i++) {
            System.arraycopy(origin[i], 0, copia[i], 0, origin[i].length);
        }
        return copia;
    }
}