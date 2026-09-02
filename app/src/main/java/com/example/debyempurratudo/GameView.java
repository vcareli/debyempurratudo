package com.example.debyempurratudo;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;
    private Paint paint;
    private int playerLin = 1;
    private int playerCol = 1;

    //Matriz do mapa inicial
    // 1 - Parede, 0 - Chao vazio
    int[][] mapa = {
            {1, 1, 1, 1, 1},
            {1, 0, 0, 0, 1},
            {1, 0, 0, 0, 1},
            {1, 1, 1, 1, 1},
    };
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        gameThread = new GameThread(getHolder(), this);
        paint = new Paint();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        int tamanhoBloco = 120;
        tamanhoBloco = width / mapa[0].length;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //Encerra Thread de forma segura
        boolean retry = true;
        gameThread.setRunning(true);
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void rendenizar(Canvas canvas) {
        if (canvas != null) {
            // Fundo da tela
            canvas.drawColor(Color.BLACK);

            int tamanhoBloco = 120; // Tamanho em pixels de cada quadrado

            // Desenhar a matriz linha por linha e coluna por coluna
            for (int linha = 0; linha < mapa.length; linha++) {
                for (int coluna = 0; coluna < mapa[linha].length; coluna++) {
                    int elemento = mapa[linha][coluna];

                    if (elemento == 1) {
                        paint.setColor(Color.GRAY); // Parede é cinza
                    } else if(elemento == 2) {
                        paint.setColor(Color.YELLOW);
                    } else if(elemento == 3) {
                        paint.setColor(Color.RED);
                    } else {
                        paint.setColor(Color.DKGRAY); // Chão é cinza escuro
                    }
                    float left = coluna * tamanhoBloco;
                    float top = linha * tamanhoBloco;
                    float right = left + tamanhoBloco;
                    float bottom = top + tamanhoBloco;
                    canvas.drawRect(left, top, right, bottom, paint);
                    if (linha == playerLin && coluna == playerCol) {
                        paint.setColor(Color.BLUE);
                        canvas.drawCircle(left + tamanhoBloco/2f,
                                top + tamanhoBloco/2f,
                                tamanhoBloco/3f,
                                paint);
                    }
                }
            }
        }
    }
}
