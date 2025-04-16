package com.example.androidgame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class InventorySlot {
    private RectF rect;
    private Item item;

    public InventorySlot(float x, float y, float size) {
        this.rect = new RectF(x, y, x + size, y + size);
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(0xFFCCCCCC);
        canvas.drawRect(rect, paint);
        if (item != null && item.getIcon() != null) {
            canvas.drawBitmap(item.getIcon(), null, rect, paint);
        }
    }

    public boolean contains(float x, float y) {
        return rect.contains(x, y);
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public RectF getRect() {
        return rect;
    }
}