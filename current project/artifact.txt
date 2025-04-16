package com.example.androidgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Artifact {
    private float x, y;
    private float speedY = 5;
    private Bitmap image;
    private boolean collected = false;
    private boolean landed = false;
    private int pageIndex;
    private float width, height;

    public Artifact(Context context, float x, float y, int drawableId) {
        this.x = x;
        this.y = y;
        this.pageIndex = pageIndex;
        this.image = BitmapFactory.decodeResource(context.getResources(), drawableId);
        this.width = image.getWidth();
        this.height = image.getHeight();
    }




    public void update(float groundY) {
        if (collected || landed) return;

        if (y + height < groundY) {
            y += speedY;
        } else {
            y = groundY - height;
            landed = true;
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        if (!collected) {
            canvas.drawBitmap(image, x, y, paint);
        }
    }

    public boolean isCollected() {
        return collected;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void collect() {
        collected = true;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Bitmap getImage() {
        return image;
    }

    public boolean isNear(float heroX, float heroY, float threshold) {
        float artifactCenterX = x + width / 2;
        float artifactCenterY = y + height / 2;
        float heroCenterX = heroX + width / 2;
        float heroCenterY = heroY + height / 2;

        return Math.abs(artifactCenterX - heroCenterX) < threshold &&
                Math.abs(artifactCenterY - heroCenterY) < threshold;
    }

    public boolean hasLanded() {
        return landed;
    }
}