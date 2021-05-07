package com.example.mygallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GiveBitmap {

    public static Bitmap takeBitmap(File file){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        return  bitmap;
    }

}
