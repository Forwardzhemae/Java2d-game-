package com.example.androidgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class NPC {
    private Bitmap npcBitmap;
    private Bitmap textSprite;
    private float x, y;
    private boolean showMessage = false;

    public NPC(Context context, float x, float y) {
        this.npcBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.npc);
        this.textSprite = BitmapFactory.decodeResource(context.getResources(), R.drawable.text_box);
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(npcBitmap, x, y, paint);

        if (showMessage && textSprite != null) {
            float spriteX = x + npcBitmap.getWidth() / 2f - textSprite.getWidth() / 2f;
            float spriteY = y - textSprite.getHeight() - 20;

            canvas.drawBitmap(textSprite, spriteX, spriteY, paint);

            float centerX = spriteX + textSprite.getWidth() / 2f;
            float centerY = spriteY + textSprite.getHeight() / 2f;

            paint.setColor(0xFF000000); // чёрный текст
            paint.setTextSize(36);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("ПОМОГИ МОЕЙ ДЕРЕВНЕ", centerX, centerY - 20, paint);
            canvas.drawText("ТУДА НАПАДАЮТ ДЕМОНЫ!", centerX, centerY + 20, paint);
        }
    }

    public boolean contains(float touchX, float touchY) {
        return touchX >= x && touchX <= x + npcBitmap.getWidth() &&
                touchY >= y && touchY <= y + npcBitmap.getHeight();
    }

    public boolean isNearby(float heroX, float heroY) {
        return Math.abs(heroX - x) < 300 && Math.abs(heroY - y) < 300;
    }

    public void toggleMessage() {
        showMessage = !showMessage;
    }

    public void hideMessage() {
        showMessage = false;
    }

    public boolean isMessageShown() {
        return showMessage;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}