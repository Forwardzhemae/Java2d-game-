package com.example.androidgame;

import android.graphics.Bitmap;

public class Item {
    private Bitmap icon;
    private String name;

    public Item(Bitmap icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }
}