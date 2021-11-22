package com.example.lab6;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity  implements ListFragment.OnFragmentSendDataListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onSendData(String selectedItem) {
        // получаем объект фрагмента
        ImageFragment fragment = (ImageFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detailFragment);
        // устанавливаем содержимое выбранного элемента из списка
        if (fragment != null)
            fragment.setSelectedItem(selectedItem);
    }

}