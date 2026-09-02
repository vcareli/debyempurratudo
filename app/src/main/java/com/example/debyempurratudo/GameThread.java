package com.example.debyempurratudo;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean isRunning = false;
    private static final int FPS_ALVO = 60;
    private static final long TEMPO_FRAME = 1000 / FPS_ALVO;


    public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    @Override
    public void run() {
        long tempoInicio = 0;
        long tempoDecorrido = 0;
        long tempoEspera = 0;

        while (isRunning) {
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    if (canvas != null) {
                        gameView.rendenizar(canvas);
                    }
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            //Calcula tempo para desenhar quadro
            tempoDecorrido = System.currentTimeMillis() - tempoInicio;

            //Calcula tempo falta
            tempoEspera = TEMPO_FRAME - tempoDecorrido;
            if (tempoEspera > 0) {
                try {
                    Thread.sleep(tempoEspera);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}