package com.example.androidgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import java.util.ArrayList;

public class Inventory {
    private ArrayList<InventorySlot> slots = new ArrayList<>();
    private boolean isOpen = false;
    private float slotSize = 120;
    private float padding = 20;

    private Item draggingItem = null;
    private float dragX, dragY;

    public Inventory(Context context) {
        float startX = 100;
        float startY = 100;

        for (int i = 0; i < 5; i++) {
            slots.add(new InventorySlot(startX + i * (slotSize + padding), startY, slotSize));
        }

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 5; col++) {
                float x = startX + col * (slotSize + padding);
                float y = startY + slotSize + padding + row * (slotSize + padding);
                slots.add(new InventorySlot(x, y, slotSize));
            }
        }

        Bitmap itemIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.attack_button);
        slots.get(0).setItem(new Item(itemIcon, "Меч"));
    }

    public void draw(Canvas canvas, Paint paint) {
        if (!isOpen) return;

        for (InventorySlot slot : slots) {
            slot.draw(canvas, paint);
        }

        if (draggingItem != null) {
            canvas.drawBitmap(draggingItem.getIcon(), dragX - slotSize / 2, dragY - slotSize / 2, paint);
        }
    }

    public void handleTouch(MotionEvent event) {
        if (!isOpen) return;

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (InventorySlot slot : slots) {
                    if (slot.contains(x, y) && slot.getItem() != null) {
                        draggingItem = slot.getItem();
                        slot.setItem(null);
                        dragX = x;
                        dragY = y;
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                dragX = x;
                dragY = y;
                break;
            case MotionEvent.ACTION_UP:
                for (InventorySlot slot : slots) {
                    if (slot.contains(x, y) && slot.getItem() == null) {
                        slot.setItem(draggingItem);
                        draggingItem = null;
                        return;
                    }
                }
                draggingItem = null;
                break;
        }
    }

    public void toggle() {
        isOpen = !isOpen;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void addItem(Item item) {
        for (InventorySlot slot : slots) {
            if (slot.getItem() == null) {
                slot.setItem(item);
                return;
            }
        }
    }
} // конец файла Inventory.java