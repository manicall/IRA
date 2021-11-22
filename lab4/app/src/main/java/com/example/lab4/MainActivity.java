package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setScaleX(2);
        imageView.setScaleX(2);
    }
/*      bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_document);
        imageView.setImageBitmap(bitmap);

        // Вычисляем ширину и высоту изображения
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();


        imageView.setImageBitmap(bmUpRightPartial);
    }*/
/*
    Bitmap getBitmap(ImageView imageView){
        // Половинки
        int halfWidth = width / 2;
        int halfHeight = height / 2;

        // Выводим верхнюю левую четвертинку картинки
        Bitmap bmUpRightPartial = Bitmap.createBitmap(halfWidth, halfHeight,
                Bitmap.Config.ARGB_8888);
        int[] pixels = new int[halfWidth * halfHeight];
            bitmap
                    .getPixels(pixels, 0, halfWidth, 0, 0, halfWidth, halfHeight);
            bmUpRightPartial
                    .setPixels(pixels, 0, halfWidth, 0, 0, halfWidth, halfHeight);
    }*/
}