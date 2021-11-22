package com.example.lab3;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // объект текстового поля
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // установка иконки на ActionBar
        setActionBarIcon(R.drawable.ic_zero);

        textView = findViewById(R.id.textView);
    }

    void setActionBarIcon(int drawableId){
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | // отображение иконки
                ActionBar.DISPLAY_SHOW_TITLE); // отображение заголовка
        bar.setIcon(drawableId); // установка иконки
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // установить название меню в текстовое поле
        textView.setText(item.getTitle());

        // демонстрация взаимодействия с LogCat
        switch (item.getItemId())
        {
            case R.id.one:
                // Verbose
                Log.v("TAG", item.getTitle().toString());
                break;
            case R.id.two:
                // Debug
                Log.d("TAG", item.getTitle().toString());
                break;
            case R.id.three:
                // Info
                Log.i("TAG", item.getTitle().toString());
                break;
            case R.id.four:
                // Warn
                Log.w("TAG", item.getTitle().toString());
                break;
            case R.id.five:
                // Error
                Log.e("TAG", item.getTitle().toString());
                break;
            case R.id.six:
                // Assert
                Log.wtf("TAG", item.getTitle().toString());
        }

        return true;
    }
}