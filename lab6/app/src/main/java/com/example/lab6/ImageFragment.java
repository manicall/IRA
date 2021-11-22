package com.example.lab6;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class ImageFragment extends Fragment implements View.OnClickListener {
    ImageView imageView;
    float scale;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_image, container, false);
        imageView = root.findViewById(R.id.imageView);

        // очищаем изображение установкой нового объекта Bitmap
        setNewBitmap();

        Button minusZoom = root.findViewById(R.id.minusZoom);
        Button plusZoom = root.findViewById(R.id.plusZoom);
        // устанавливаем слушатели на реализацию метода OnClickListener
        minusZoom.setOnClickListener(this);
        plusZoom.setOnClickListener(this);

        scale = 1; // масштаб 1:1

        return root;
    }

    void setNewBitmap(){
        Bitmap bitmap = Bitmap.createBitmap(100, 100,
                Bitmap.Config.ARGB_8888);

        // Выведем bitmap в ImageView
        imageView.setImageBitmap(bitmap);
    }

    // обновление изображения
    public void setSelectedItem(String posititon) {
        switch(posititon){
            case "ноль":
                imageView.setBackgroundResource(R.drawable.ic_zero);
                break;
            case "один":
                imageView.setBackgroundResource(R.drawable.ic_one);
                break;
            case "два":
                imageView.setBackgroundResource(R.drawable.ic_two);
                break;
            case "три":
                imageView.setBackgroundResource(R.drawable.ic_three);
                break;
            case "четыре":
                imageView.setBackgroundResource(R.drawable.ic_four);
                break;
            case "пять":
                imageView.setBackgroundResource(R.drawable.ic_five);
                break;
            case "шесть":
                imageView.setBackgroundResource(R.drawable.ic_six);
                break;
            case "семь":
                imageView.setBackgroundResource(R.drawable.ic_seven);
                break;

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.minusZoom:
                imageScale(scale -= .1f);
                break;
            case R.id.plusZoom:
                imageScale(scale += .1f);
                break;
        }
        Log.d("TAG", "onClick: " + scale);

    }
    // обновление масштаба
    void imageScale(float scale){
        imageView.setScaleX(scale);
        imageView.setScaleY(scale);
    }

}