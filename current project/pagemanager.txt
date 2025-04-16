package com.example.androidgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class PageManager {
    private Bitmap[] pages;
    private int currentPageIndex = 0;

    public PageManager(Context context) {
        pages = new Bitmap[4];
        pages[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        pages[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.background2);
        pages[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.background3);
        pages[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.background4);
    }

    public void nextPage() {
        currentPageIndex = (currentPageIndex + 1) % pages.length;
    }

    public void previousPage() {
        currentPageIndex = (currentPageIndex - 1 + pages.length) % pages.length;
    }

    public void drawCurrentPage(Canvas canvas, Paint paint) {
        canvas.drawBitmap(pages[currentPageIndex], 0, 0, paint);
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }
}
