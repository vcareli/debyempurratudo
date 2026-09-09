package com.example.debyempurratudo;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

public class SoundManager {
    private SoundPool soundPool;
    private int soundPassos;
    private int soundCaixa;
   private int soundWin;
    private boolean carregado = false;
    // Flags de controle individual para cada áudio
    private static final String TAG = "DEBY_SOUND";
    private boolean passosPronto = false;
    private boolean caixaPronto = false;
    private boolean winPronto = false;

    public SoundManager(Context context) {
        //Configura audio para jogos
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        // Cria o SoundPool permitindo reproduzir até 4 sons simultâneos
        soundPool = new SoundPool.Builder().setMaxStreams(4).setAudioAttributes(audioAttributes).build();
        /*
        soundPool.setOnLoadCompleteListener((soundPool1, sampleId, status) -> {
            if (status == 0) {
                if (sampleId == soundPassos) passosPronto = true;
                else if (sampleId == soundCaixa) caixaPronto = true;
                else if (sampleId == soundWin) winPronto = true;
                }
            });*/
        soundPool.setOnLoadCompleteListener((soundPool1, sampleId, status) -> {
                    if (status == 0) {
                        if (sampleId == soundPassos) {
                            passosPronto = true;
                            Log.d(TAG, "Som dos PASSOS pronto!");
                        } else if (sampleId == soundCaixa) {
                            caixaPronto = true;
                            Log.d(TAG, "Som da CAIXA pronto!");
                        } else if (sampleId == soundWin) {
                            winPronto = true;
                            Log.d(TAG, "Som de VITORIA pronto!");
                        }
                    } else {
                        Log.e(TAG, "Erro ao carregar o som ID: " + sampleId + " Status: " + status);
                    }
        });
        //Carrega os audios
        soundPassos = soundPool.load(context, R.raw.foot, 1);
        soundCaixa = soundPool.load(context, R.raw.box, 1);
        soundWin = soundPool.load(context, R.raw.win, 1);
    }

    public void tocarPassos() {
        if (carregado) soundPool.play(soundPassos, 1f, 1f, 1, 0, 1f);
    }

    public void tocarCaixa() {
        if (carregado) soundPool.play(soundCaixa, 1f, 1f, 1, 0, 1f);
    }

    public void tocarWin() {
        if (carregado) soundPool.play(soundWin, 1f, 1f, 1, 0, 1f);
    }

    public void liberar() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
