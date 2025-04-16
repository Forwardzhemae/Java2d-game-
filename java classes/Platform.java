package com.example.androidgame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Platform {
    private RectF rect;

    public Platform(float left, float top, float right, float bottom) {
        rect = new RectF(left, top, right, bottom);
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(0xFF654321);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect, paint);
    }

    public RectF getRect() {
        return rect;
    }
}