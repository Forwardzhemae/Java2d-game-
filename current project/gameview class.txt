package com.example.androidgame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private PageManager pageManager;
    private NPC npc;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Artifact> artifacts = new ArrayList<>();

    private boolean isLeftPressed = false;
    private boolean isRightPressed = false;
    private boolean isJumping = false;
    private boolean isAttackPressed = false;
    private boolean isDefending = false;

    private float jumpVelocity = -20;
    private float gravity = 1;
    private float verticalVelocity = 0;

    private float heroX, heroY;
    private float buttonSize;
    private float screenWidth, screenHeight;

    private Bitmap[] runFrames;
    private Bitmap attackSprite;
    private Bitmap defendSprite;
    private int frameIndex = 0;
    private long lastFrameTime;
    private long attackStartTime = 0;
    private int frameWidth, frameHeight;
    private boolean isFacingLeft = false;

    private RectF leftButtonRect, rightButtonRect, jumpButtonRect, attackButtonRect;
    private RectF defendButtonRect;
    private Inventory inventory;
    private RectF inventoryButtonRect;

    private Random random = new Random();

    private int heroMaxHP = 100;
    private int heroCurrentHP = 100;

    private boolean isGameOver = false;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        gameThread = new GameThread(getHolder(), this);
        pageManager = new PageManager(context);

        Bitmap runSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.run_sprite);
        loadRunFrames(runSpriteSheet, 1);

        attackSprite = BitmapFactory.decodeResource(getResources(), R.drawable.attack_button);
        defendSprite = BitmapFactory.decodeResource(getResources(), R.drawable.shield_sprite); // Указатель спрайт

        enemies.add(new Enemy(context, 800, 600, R.drawable.enemy1));
        enemies.add(new Enemy(context, 1100, 600, R.drawable.enemy2));
        enemies.add(new Enemy(context, 1400, 600, R.drawable.enemy3));

        inventory = new Inventory(context);
    }

    private void loadRunFrames(Bitmap spriteSheet, int frameCount) {
        frameWidth = spriteSheet.getWidth() / frameCount;
        frameHeight = spriteSheet.getHeight();
        runFrames = new Bitmap[frameCount];
        for (int i = 0; i < frameCount; i++) {
            runFrames[i] = Bitmap.createBitmap(spriteSheet, i * frameWidth, 0, frameWidth, frameHeight);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;

        heroX = screenWidth / 4 - frameWidth / 4;
        heroY = screenHeight - frameHeight - 150;

        npc = new NPC(getContext(), 1700, 660);

        buttonSize = screenWidth / 16;

        leftButtonRect = new RectF(20, screenHeight - buttonSize - 20, 20 + buttonSize, screenHeight - 20);
        rightButtonRect = new RectF(40 + buttonSize, screenHeight - buttonSize - 20, 40 + 2 * buttonSize, screenHeight - 20);

        float centerX = (leftButtonRect.right + rightButtonRect.left) / 2;
        jumpButtonRect = new RectF(centerX - buttonSize / 2, screenHeight - buttonSize - 40 - buttonSize,
                centerX + buttonSize / 2, screenHeight - 40 - buttonSize);

        float margin = 20;
        attackButtonRect = new RectF(screenWidth - buttonSize - margin, screenHeight - buttonSize - margin,
                screenWidth - margin, screenHeight - margin);

        defendButtonRect = new RectF(screenWidth - 2 * buttonSize - margin - 20, screenHeight - buttonSize - margin,
                screenWidth - buttonSize - margin - 20, screenHeight - margin);

        inventoryButtonRect = new RectF(screenWidth - 150, 50, screenWidth - 50, 150);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        gameThread.setRunning(false);
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
        float touchX = event.getX();
        float touchY = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (inventoryButtonRect.contains(touchX, touchY)) {
                inventory.toggle();
                return true;
            }
        }

        inventory.handleTouch(event);
        if (inventory.isOpen())
            return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isDefending = defendButtonRect.contains(touchX, touchY);

                if (leftButtonRect.contains(touchX, touchY)) {
                    isLeftPressed = true;
                    isRightPressed = false;
                } else if (rightButtonRect.contains(touchX, touchY)) {
                    isRightPressed = true;
                    isLeftPressed = false;
                } else if (jumpButtonRect.contains(touchX, touchY)) {
                    if (!isJumping) {
                        isJumping = true;
                        verticalVelocity = jumpVelocity;//                  кнопка прыжка.
                    }
                } else if (attackButtonRect.contains(touchX, touchY)) {
                    isAttackPressed = true;
                    attackStartTime = System.currentTimeMillis();
                } else if (pageManager.getCurrentPageIndex() == 0 &&
                        npc.contains(touchX, touchY) &&
                        npc.isNearby(heroX, heroY)) {
                    npc.toggleMessage();
                }
                break;

            case MotionEvent.ACTION_UP:
                isLeftPressed = false;
                isRightPressed = false;
                isAttackPressed = false;
                isDefending = false;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas != null) {
            Paint paint = new Paint();
            pageManager.drawCurrentPage(canvas, paint);

            if (pageManager.getCurrentPageIndex() == 0) npc.draw(canvas, paint);
            if (pageManager.getCurrentPageIndex() == 1) {
                for (Enemy enemy : enemies) {
                    if (enemy.isAlive()) {
                        enemy.update(heroX, heroY);
                        enemy.draw(canvas, paint);
                    }
                }
            }

            for (Artifact artifact : artifacts) {
                artifact.update(heroY + frameHeight);
                if (!artifact.isCollected() && Math.abs(artifact.getX() - heroX) < 50 && Math.abs(artifact.getY() - heroY) < 100) {
                    artifact.collect();
                    inventory.addItem(new Item(BitmapFactory.decodeResource(getResources(), R.drawable.attack_button), "Артефакт"));
                }
                artifact.draw(canvas, paint);
            }

            Bitmap currentFrame;
            if (isDefending) {
                currentFrame = defendSprite;
            } else if (isAttackPressed || (System.currentTimeMillis() - attackStartTime < 300)) {
                currentFrame = attackSprite;
            } else {
                currentFrame = runFrames[frameIndex];
            }

            Matrix matrix = new Matrix();
            if (isFacingLeft) {
                matrix.preScale(-1, 1);
                matrix.postTranslate(heroX + frameWidth, heroY);
            } else {
                matrix.postTranslate(heroX, heroY);
            }
            canvas.drawBitmap(currentFrame, matrix, paint);

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFrameTime > 100) {
                frameIndex = (frameIndex + 1) % runFrames.length;
                lastFrameTime = currentTime;
            }

            int barWidth = 300;
            int barHeight = 30;
            float hpPercentage = (float) heroCurrentHP / heroMaxHP;

            paint.setColor(0xFF000000);
            canvas.drawRect(30, 30, 30 + barWidth, 30 + barHeight, paint);
            paint.setColor(0xFFFF0000);
            canvas.drawRect(30, 30, 30 + barWidth * hpPercentage, 30 + barHeight, paint);
            paint.setColor(0xFFFFFFFF);
            paint.setTextSize(26);
            canvas.drawText("HP: " + heroCurrentHP + "/" + heroMaxHP, 40, 30 + barHeight - 6, paint);

            paint.setColor(0xFF888888);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(leftButtonRect, 20, 20, paint);
            canvas.drawRoundRect(rightButtonRect, 20, 20, paint);
            canvas.drawRoundRect(jumpButtonRect, 20, 20, paint);
            canvas.drawRoundRect(attackButtonRect, 20, 20, paint);
            canvas.drawRoundRect(defendButtonRect, 20, 20, paint); // ← Защитная кнопка

            paint.setColor(0xFFFFFFFF);
            paint.setTextSize(buttonSize / 3);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("←", leftButtonRect.centerX(), leftButtonRect.centerY() + buttonSize / 6, paint);
            canvas.drawText("→", rightButtonRect.centerX(), rightButtonRect.centerY() + buttonSize / 6, paint);
            canvas.drawText("⇧", jumpButtonRect.centerX(), jumpButtonRect.centerY() + buttonSize / 6, paint);
            canvas.drawText("🗡", attackButtonRect.centerX(), attackButtonRect.centerY() + buttonSize / 6, paint);
            canvas.drawText("🛡", defendButtonRect.centerX(), defendButtonRect.centerY() + buttonSize / 6, paint);

            paint.setColor(0xAA888888);
            canvas.drawRoundRect(inventoryButtonRect, 20, 20, paint);
            paint.setColor(0xFFFFFFFF);
            paint.setTextSize(40);
            canvas.drawText("📦", inventoryButtonRect.centerX(), inventoryButtonRect.centerY() + 15, paint);

            inventory.draw(canvas, paint);
        }
    }

    private boolean isEnemyInHitRange(Enemy enemy) {
        float hitRange = 80;
        float hitBoxX = isFacingLeft ? heroX - hitRange : heroX + frameWidth;
        float hitBoxY = heroY;
        return enemy.getHitBox().intersects(hitBoxX, hitBoxY, hitBoxX + hitRange, hitBoxY + frameHeight);
    }

    private class GameThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private GameView gameView;
        private boolean running;

        public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
            this.surfaceHolder = surfaceHolder;
            this.gameView = gameView;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @SuppressLint("WrongCall")
        @Override
        public void run() {
            while (running) {
                Canvas canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        gameView.onDraw(canvas);

                        if (isGameOver) continue;

                        if (isLeftPressed) {
                            heroX -= 10;
                            isFacingLeft = true;
                        }
                        if (isRightPressed) {
                            heroX += 10;
                            isFacingLeft = false;
                        }

                        if (heroX < -frameWidth / 2) {
                            pageManager.previousPage();
                            heroX = screenWidth - frameWidth - 1;
                        } else if (heroX > screenWidth - frameWidth / 2) {
                            pageManager.nextPage();
                            heroX = 1;
                        }

                        if (isJumping) {
                            heroY += verticalVelocity;
                            verticalVelocity += gravity;

                            if (heroY >= screenHeight - frameHeight - 150) {
                                heroY = screenHeight - frameHeight - 150;
                                isJumping = false;
                                verticalVelocity = 0;
                            }
                        }

                        if (isAttackPressed || (System.currentTimeMillis() - attackStartTime < 300)) {
                            for (Enemy enemy : enemies) {
                                if (enemy.isAlive() && isEnemyInHitRange(enemy)) {
                                    enemy.kill();
                                    if (random.nextFloat() < 0.5f) {
                                        int artId = random.nextBoolean() ? R.drawable.artifact1 : R.drawable.artifact2;
                                        artifacts.add(new Artifact(getContext(), enemy.getHitBox().centerX(), enemy.getHitBox().centerY(), artId));
                                    }
                                }
                            }
                        }

                        for (Enemy enemy : enemies) {
                            if (enemy.isAlive() && enemy.isAttacking()) {
                                RectF eBox = enemy.getHitBox();
                                if (eBox.intersects(heroX, heroY, heroX + frameWidth, heroY + frameHeight)) {
                                    if (!isDefending) {
                                        heroCurrentHP -= 20;
                                        if (heroCurrentHP <= 0) {
                                            heroCurrentHP = 0;
                                            isGameOver = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
