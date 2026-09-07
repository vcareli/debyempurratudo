package com.example.debyempurratudo;
import android.content.Context;
import android.content.SharedPreferences;

public class SaveManager {
    private static final String PREF_NAME = "DebyEmpurraTudoSave";
    private static final String KEY_FASE_LIBERADA = "fase_liberada";

    public static int getFaseLiberada(Context context) {            //Retorna numero maior fase desbloqueada
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
        return preferences.getInt(KEY_FASE_LIBERADA, 1);
    }

    public static void salvarProgresso(Context context, int faseConcluida) {    //Salva fase liberada
        int proximaFase = faseConcluida + 1;
        int faseAtual = getFaseLiberada(context);

        if (proximaFase > faseAtual) {
            SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(KEY_FASE_LIBERADA, proximaFase);
            editor.apply();     // Salva em segundo plano de forma segura
        }
    }
}
