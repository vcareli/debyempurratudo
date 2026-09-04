package com.example.debyempurratudo;
import java.util.ArrayList;
import java.util.List;

public class LevelManager {
    private int nivelAtual = 0;
    private final List<int[][]> mapas = new ArrayList<>();
    private final List<int[][]> alvos = new ArrayList<>();

    public LevelManager() {
        chargeLevels();
    }

    private void chargeLevels() {
        // 1 - Parede, 0 - Chao vazio, 2 - Caixa, 3 - destino, 4 - player
        //Nivel 1
        int[][] mapa1 = {
                {1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 1},
                {1, 0, 0, 2, 0, 1},
                {1, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 1},
                {1, 0, 3, 0, 0, 1},
                {1, 1, 1, 1, 1, 1}
        };

        int[][] alvo1 = {
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 3, 0, 0, 0},
                {0, 0, 0, 0, 0, 0}
        };
        mapas.add(mapa1);
        alvos.add(alvo1);

        //Nivel 2
        int[][] mapa2 = {
                {1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 2, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 1, 0, 1, 0, 1},
                {1, 0, 0, 1, 3, 1, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1}
        };

        int[][] alvo2 = {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 3, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0}
        };
        mapas.add(mapa2);
        alvos.add(alvo2);
    }

    public int[][] getMapaAtual() {
        return clonarMatrix(mapas.get(nivelAtual));
    }

    public int[][] getAlvoAtual() {
        return alvos.get(nivelAtual);
    }

    public boolean nextLevel() {
        if (nivelAtual + 1 < mapas.size()) {
            nivelAtual++;
            return true;
        }
        return false;
    }

    public void restartNivel() {}

    public int getNivelAtual() {
        return nivelAtual++;
    }

    public int[][] clonarMatrix(int[][] origin) {
        int[][] copia = new int[origin.length][origin[0].length];
        for (int i = 0; i < origin.length; i++) {
            System.arraycopy(origin[i], 0, copia[i], 0, origin[i].length);
        }
        return copia;
    }
}
