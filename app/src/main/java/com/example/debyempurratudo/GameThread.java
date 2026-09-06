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
        while (isRunning) {
            Canvas canvas = null;
            try {
                // Tenta travar o Canvas para desenho
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    if (canvas != null) gameView.rendenizar(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Garante que o Canvas seja liberado mesmo se der erro
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            // Controle básico de FPS (evita sobrecarregar a CPU)
            try {
                Thread.sleep(16); // Aproximadamente 60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}