package com.example.debyempurratudo;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;
    private final Paint paint;
    private int playerLin;
    private int playerCol;
    private int tamanhoBloco;
    private boolean venceu;
    private int[][] mapa;
    private int[][] alvo;
    private boolean faseZerada = false;
    private LevelManager levelManager = new LevelManager();

    private int getElementoOriginal(int linha, int coluna) {
        if (alvo != null && alvo[linha][coluna] == 3) return 3;
        return 0;
    }

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        gameThread = new GameThread(getHolder(), this);
        paint = new Paint();
        carregarFaseAtual();
    }

    private void carregarFaseAtual() {
        this.mapa = levelManager.getMapaAtual();
        this.alvo = levelManager.getAlvoAtual();
        this.playerLin = 1;
        this.playerCol = 1;
        this.venceu = false;

        if (getWidth() > 0 && mapa[0].length > 0) tamanhoBloco = getWidth() / mapa[0].length;
    }

    private void proximoNivel() {
        if (levelManager.nextLevel()) {
            carregarFaseAtual();
        }
    }

    public boolean checkVitory() {
        for (int i = 0; i < alvo.length; i++) {
            for (int j = 0; j < alvo[i].length; j++) {
                //Se existe alvo, mas na matriz nao tem caixa - nao venceu
                if (alvo[i][j] == 3 && mapa[i][j] != 2) return false;
            }
        }
        faseZerada = true;
        return true;        //todas as caixas estao nos alvos
    }

    public void tentarMover(int dLin, int dCol) { //Metodo para processos de movimentacao
        int newLin = playerLin + dLin;
        int newCol = playerCol + dCol;

        if (newLin < 0 || newLin >= mapa.length ||
                newCol < 0 || newCol >= mapa[0].length || venceu) return;

        int elementoDestino = mapa[newLin][newCol];

        if (elementoDestino == 1) {             //se for parede nao move
            return;
        } else if (elementoDestino == 2) {      //se for caixa tenta empurrar
            if (alvo != null && alvo[newLin][newCol] == 3) return;

            int caixaNewLine = newLin + dLin;
            int caixaNewCol = newCol + dCol;

            if (caixaNewLine < 0 || caixaNewLine >= mapa.length || caixaNewCol < 0 ||
                    caixaNewCol >= mapa[0].length) return;

            int alemCaixa = mapa[caixaNewLine][caixaNewCol];

            if (alemCaixa == 1 || alemCaixa == 2) {
                return;
            } else if (alemCaixa == 0 || alemCaixa == 3) {
                mapa[caixaNewLine][caixaNewCol] = 2;
                mapa[newLin][newCol] = getElementoOriginal(newLin, newCol);
                mapa[playerLin][playerCol] = getElementoOriginal(playerLin, playerCol);
                playerLin = newLin;
                playerCol = newCol;
                if (checkVitory()) {
                    faseZerada = true;
                    venceu = true;
                }
            }
        } else {                                //se for chao ou o destino vazio move
            mapa[playerLin][playerCol] = getElementoOriginal(playerLin, playerCol);
            playerLin = newLin;
            playerCol = newCol;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (venceu) {
                proximoNivel();
                return true;
            }
            float touchX = event.getX();
            float touchY = event.getY();
            int larguraTela = getWidth();
            int alturaTela = getHeight();
            //Logica quadrantes para navegacao
            //Se tocar metade superior -> sobe
            //Se tocar metade inferior -> desce
            //Se tocar lateral esq -> esq
            //Se tocar lateral dir -> dir
            if (touchY < alturaTela / 3f) {
                tentarMover(-1, 0);
            } else if (touchY > (alturaTela * 2) / 3f) {
                tentarMover(1, 0);;
            } else if (touchX < larguraTela / 2f) {
                tentarMover(0, -1);;
            } else if (touchX > larguraTela / 2f) {
                tentarMover(0, 1);;
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void rendenizar(Canvas canvas) {
        if (canvas != null) {
            canvas.drawColor(Color.BLACK);

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
            if (venceu || faseZerada) {
                float centroX = getWidth() / 2f;
                float centroY = getHeight() / 2f;
                paint.setColor(Color.RED);
                paint.setTextSize(80f);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("🎉 VOCÊ VENCEU!", centroX, centroY - 50f, paint);
                paint.setTextSize(80f);
                paint.setColor(Color.WHITE);
                canvas.drawText("NEXT LEVEL", centroX, centroY + 60f, paint);
            }
        }
    }
}
