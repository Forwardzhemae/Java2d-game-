package com.example.androidgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.content.Context;

public class AttackButton {
    private Bitmap buttonBitmap;
    private RectF buttonRect;
    private boolean isPressed;
    private float x, y, size;

    public AttackButton(Context context, float x, float y, float size) {
        this.buttonBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.attack_button);
        this.x = x;
        this.y = y;
        this.size = size;
        this.buttonRect = new RectF(x, y, x + size, y + size);
        this.isPressed = false;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (isPressed) {
            paint.setAlpha(150);
        } else {
            paint.setAlpha(255);
        }
        canvas.drawBitmap(buttonBitmap, null, buttonRect, paint);
        paint.setAlpha(255);
    }

    public boolean contains(float touchX, float touchY) {
        return buttonRect.contains(touchX, touchY);
    }

    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public RectF getRect() {
        return buttonRect;
    }
}