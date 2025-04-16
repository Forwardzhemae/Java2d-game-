package com.example.androidgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

public class Enemy {
    private Bitmap walkBitmap;
    private Bitmap attackBitmap;
    private float x, y;
    private boolean isAlive = true;
    private RectF hitBox;
    private float speed = 2f;
    private long lastAttackTime = 0;
    private static final long ATTACK_COOLDOWN = 1000;
    private boolean facingLeft = false;

    private int type;

    public Enemy(Context context, float x, float y, int drawableId) {
        this.x = x;
        this.y = y;
        this.type = getTypeFromDrawable(drawableId);

        this.walkBitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        this.attackBitmap = loadAttackBitmap(context, type);

        this.hitBox = new RectF(x, y, x + walkBitmap.getWidth(), y + walkBitmap.getHeight());
    }

    private int getTypeFromDrawable(int drawableId) {
        if (drawableId == R.drawable.enemy1) return 1;
        if (drawableId == R.drawable.enemy2) return 2;
        if (drawableId == R.drawable.enemy3) return 3;
        return 1;
    }

    private Bitmap loadAttackBitmap(Context context, int type) {
        int id = R.drawable.enemy1_attack;
        switch (type) {
            case 2: id = R.drawable.enemy2_attack; break;
            case 3: id = R.drawable.enemy3_attack; break;
        }
        return BitmapFactory.decodeResource(context.getResources(), id);
    }

    public void update(float heroX, float heroY) {
        if (!isAlive) return;

        float dx = heroX - x;
        facingLeft = dx < 0;

        if (Math.abs(dx) < 100) {
            if (System.currentTimeMillis() - lastAttackTime > ATTACK_COOLDOWN) {
                lastAttackTime = System.currentTimeMillis();
            }
        } else {
            if (dx > 0) {
                x += speed;
            } else {
                x -= speed;
            }
        }

        hitBox.set(x, y, x + walkBitmap.getWidth(), y + walkBitmap.getHeight());
    }

    public void draw(Canvas canvas, Paint paint) {
        if (!isAlive) return;

        Bitmap currentBitmap = isAttacking() ? attackBitmap : walkBitmap;
        Matrix matrix = new Matrix();

        if (facingLeft) {
            matrix.preScale(-1, 1);
            matrix.postTranslate(x + currentBitmap.getWidth(), y);
        } else {
            matrix.postTranslate(x, y);
        }

        canvas.drawBitmap(currentBitmap, matrix, paint);
    }

    public boolean isAttacking() {
        return System.currentTimeMillis() - lastAttackTime < 300;
    }

    public boolean isHit(float touchX, float touchY) {
        return isAlive && hitBox.contains(touchX, touchY);
    }

    public void kill() {
        isAlive = false;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public RectF getHitBox() {
        return hitBox;
    }

    public boolean isCloseToHero(float heroX, float heroY) {
        return isAlive && Math.abs(heroX - x) < 50 && Math.abs(heroY - y) < 50;
    }
}