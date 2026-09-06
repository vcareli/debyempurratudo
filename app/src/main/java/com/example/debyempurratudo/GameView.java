package com.example.debyempurratudo;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private Bitmap imgParede;
    private Bitmap imgChao;
    private Bitmap imgBuraco;
    private Bitmap imgCaixa;
    private Bitmap imgDeby;
    private GameThread gameThread;
    private final Paint paint;
    private int playerLin;
    private int playerCol;
    private int tamanhoBloco;
    private boolean venceu;
    private int[][] mapa;
    private int[][] alvo;
    private boolean faseZerada = false;
    private final RectF btnRestartLevel = new RectF();
    private final RectF btnCima = new RectF();
    private final RectF btnBaixo = new RectF();
    private final RectF btnEsquerda = new RectF();
    private final RectF btnDireita = new RectF();
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
        carregarImg();
        carregarFaseAtual();
    }

    private void carregarImg() {
        imgParede = BitmapFactory.decodeResource(getResources(), R.drawable.stone);
        imgChao = BitmapFactory.decodeResource(getResources(), R.drawable.chao);
        imgBuraco = BitmapFactory.decodeResource(getResources(), R.drawable.poco);
        imgDeby = BitmapFactory.decodeResource(getResources(), R.drawable.deby);
        imgCaixa = BitmapFactory.decodeResource(getResources(), R.drawable.skull);
    }

    private void restartLevel() {
        this.mapa = levelManager.getMapaAtual();
        this.alvo = levelManager.getAlvoAtual();
        this.playerCol = 1;
        this.playerLin = 1;
        this.venceu = false;
        this.faseZerada = false;
    }

    private void carregarFaseAtual() {
        this.mapa = levelManager.getMapaAtual();
        this.alvo = levelManager.getAlvoAtual();
        this.playerLin = 1;
        this.playerCol = 1;
        this.venceu = false;
        this.faseZerada = false;

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
            float touchX = event.getX();
            float touchY = event.getY();

            // 1. Checa se o toque foi no Botão Virtual de Reset
            if (btnRestartLevel.contains(touchX, touchY)) {
                restartLevel();
                return true;
            }

            // 2. Se o jogador já venceu a fase e tocou fora do reset -> Próximo Nível!
            if (venceu || faseZerada) {
                proximoNivel();
                return true;
            }

            // 3. Checa os cliques nos botões do D-Pad para mover a Deby
            if (btnCima.contains(touchX, touchY)) {
                tentarMover(-1, 0); // Sobe 1 linha
                return true;
            } else if (btnBaixo.contains(touchX, touchY)) {
                tentarMover(1, 0); // Desce 1 linha
                return true;
            } else if (btnEsquerda.contains(touchX, touchY)) {
                tentarMover(0, -1); // Volta 1 coluna
                return true;
            } else if (btnDireita.contains(touchX, touchY)) {
                tentarMover(0, 1); // Avança 1 coluna
                return true;
            }
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
                    int left = coluna * tamanhoBloco;
                    int top = linha * tamanhoBloco;
                    int right = left + tamanhoBloco;
                    int bottom = top + tamanhoBloco;
                    Rect destRect = new Rect(left, top, right, bottom);

                    if (imgChao != null)
                        canvas.drawBitmap(imgChao, null, destRect, paint);
                    if (elemento == 1 && imgParede != null) {
                        canvas.drawBitmap(imgParede, null, destRect, paint);
                    } else if(elemento == 2 && imgCaixa != null) {
                        canvas.drawBitmap(imgCaixa, null, destRect, paint);
                    } else if(elemento == 3 && imgBuraco != null) {
                        canvas.drawBitmap(imgBuraco, null, destRect, paint);
                    }

                    //canvas.drawRect(left, top, right, bottom, paint);
                    if (linha == playerLin && coluna == playerCol && imgDeby != null) {
                        canvas.drawBitmap(imgDeby, null, destRect, paint);
                    }
                }
            }
            // Botoes reiniciar e direcionais
            // --- DESENHO DO D-PAD (CONTROLE VIRTUAL) ---
            float tamanhoBtn = 130f; // Tamanho de cada botão em pixels
            float centroControleX = getWidth() / 2f; // Centralizado horizontalmente
            float centroControleY = getHeight() - 220f; // Posicionado na parte inferior da tela

            // Define as posições dos 4 botões na tela em formato de cruz
            btnCima.set(centroControleX - tamanhoBtn / 2f, centroControleY - tamanhoBtn * 1.5f,
                    centroControleX + tamanhoBtn / 2f, centroControleY - tamanhoBtn / 2f);

            btnBaixo.set(centroControleX - tamanhoBtn / 2f, centroControleY + tamanhoBtn / 2f,
                    centroControleX + tamanhoBtn / 2f, centroControleY + tamanhoBtn * 1.5f);

            btnEsquerda.set(centroControleX - tamanhoBtn * 1.5f, centroControleY - tamanhoBtn / 2f,
                    centroControleX - tamanhoBtn / 2f, centroControleY + tamanhoBtn / 2f);

            btnDireita.set(centroControleX + tamanhoBtn / 2f, centroControleY - tamanhoBtn / 2f,
                    centroControleX + tamanhoBtn * 1.5f, centroControleY + tamanhoBtn / 2f);

            // Configuração da pintura dos botões
            paint.setColor(Color.DKGRAY);

            // Desenha o fundo dos 4 botões com cantos arredondados
            canvas.drawRoundRect(btnCima, 15f, 15f, paint);
            canvas.drawRoundRect(btnBaixo, 15f, 15f, paint);
            canvas.drawRoundRect(btnEsquerda, 15f, 15f, paint);
            canvas.drawRoundRect(btnDireita, 15f, 15f, paint);

            // Desenha os ícones/textos dentro dos botões
            paint.setColor(Color.WHITE);
            paint.setTextSize(50f);
            paint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText("▲", btnCima.centerX(), btnCima.centerY() + 15f, paint);
            canvas.drawText("▼", btnBaixo.centerX(), btnBaixo.centerY() + 15f, paint);
            canvas.drawText("◀", btnEsquerda.centerX(), btnEsquerda.centerY() + 15f, paint);
            canvas.drawText("▶", btnDireita.centerX(), btnDireita.centerY() + 15f, paint);
            //Botao reset
            float larguraBotao = 220f;
            float alturaBotao = 80f;
            float margemDireita = getWidth() - 20f;
            float margemTopo = 20f;
            btnRestartLevel.set(margemDireita - larguraBotao, margemTopo, margemDireita, margemTopo + alturaBotao);
            paint.setColor(Color.RED);
            canvas.drawRoundRect(btnRestartLevel, 20f, 20f, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(36f);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("🔄 Reset", btnRestartLevel.centerX(), btnRestartLevel.centerY() + 12f, paint);

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
